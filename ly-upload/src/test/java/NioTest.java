import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

@Log4j2
public class NioTest {
    /**
     * 基本nio 操作
     */
    public void nioTest() {
        try {
            RandomAccessFile file = new RandomAccessFile("", "rw");
            // 获取通道
            FileChannel channel = file.getChannel();
            // 定义缓冲区大小 48 bytes
            // buffer 中的 position limit capacity
            // capacity 是 定义是 缓冲区的大小
            // 在写模式中 position 是 初始位置 limit 是数据写入后的末位置
            // 当从写模式切换到读模式时，position 会重置为0,limit 表示最多能读取多少数据
            // 在读模式中 position 是 初始位置 limit 就是 capacity
            ByteBuffer buffer = ByteBuffer.allocate(48);
            // 将数据从通道写入到buffer
            int byteRead = channel.read(buffer);
            // 如果通道中没有数据返回0，如果通道已经达到流的末尾返回 -1
            while (byteRead != -1) {
                System.out.println("Read " + byteRead);
                //将写模式 切换到 读模式
                buffer.flip();
                // 从缓冲区获取数据的两种方式
                while (buffer.hasRemaining()) {
                    //将数据从缓冲区写入到通道中
                    channel.write(buffer);
                    //直接从缓冲区获取数据
                    System.out.println(buffer.get());
                }
                //clear 方法 清空整个缓冲区
                //buffer.clear();
                //清除已经读取过的数据，position 设置到最后一个未读元素的后面
                buffer.compact();
                byteRead = channel.read(buffer);
            }
            file.close();
        } catch (Exception e) {
            log.error("文件读取出错!");
        }
    }

    /**
     * nio scatter 分散 操作
     * 将 通道中的数据 写入到 多个buffer中
     */
    public void scatterTest() {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("", "rw");
            // 获取通道
            FileChannel channel = file.getChannel();
            // 声明一个 消息头，和 消息体
            ByteBuffer header = ByteBuffer.allocate(128);
            ByteBuffer body = ByteBuffer.allocate(1024);

            ByteBuffer[] bufferArray = {header, body};
            // read方法按照buffer在数组中的顺序将从通道中读取的数据写入到buffer中，当一个buffer被写满之后，写入到另一个buffer中
            // 无法处理动态消息
            channel.read(bufferArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * nio gather 聚集操作
     * 将 多个buffer 中的数据 写入到 一个通道中
     */
    public void gatherTest() {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("", "rw");
            // 获取通道
            FileChannel channel = file.getChannel();
            // 声明一个 消息头，和 消息体
            ByteBuffer header = ByteBuffer.allocate(128);
            ByteBuffer body = ByteBuffer.allocate(1024);

            ByteBuffer[] bufferArray = {header, body};
            // write 方法按照buffer数组的顺序将数据写入到通道中，只有在position到limit之间的数据会被写入，能较好的处理动态消息
            channel.write(bufferArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通道间的数据传输
     */
    public void channelToChannelTest() {
        //在nio中，如果两个通道中有一个是 FileChannel，那么可以直接将数据从一个channel传输到另一个channel
        RandomAccessFile fromFile = null;
        RandomAccessFile toFile = null;
        try {
            fromFile = new RandomAccessFile("", "rw");
            // 获取通道
            FileChannel fromChannel = fromFile.getChannel();
            FileChannel toChannel = toFile.getChannel();
            // 定义 初始位置和大小
            long position = 0;
            long count = fromChannel.size();
            // transferTo 和 transFerFrom 都可以进行数据的传输
            fromChannel.transferTo(position, count, toChannel);
            toChannel.transferFrom(fromChannel, position, count);
            //在socketChannel 中,socketChannel 只会传输此时准备好的数据(可能不足count字节)。
            //因此 SocketChannel 可能不会将请求的所有数据全部传输到FileChannel中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * nio 操作之 Selector
     */
    public void selectorTest() {
        try {
            // 创建一个多路复用器
            Selector selector = Selector.open();
            //为了将 channel 和 selector 配合使用,需要将channel 注册到selector上
            //通过 SelectableChannel.register 方法来实现
            SelectableChannel channel = ServerSocketChannel.open();
            //与Selector一起使用事,channel 必须处于非阻塞模式下,这意味着不能将FileChannel与Selector一起使用
            //因为FileChannel 不能切换到 非阻塞模式，而套接字通道都可以
            channel.configureBlocking(false);
            //register方法的第二个参数，是一个"interest"集合,表示通过Selector监听channel时对什么事件感兴趣。
            // 1.connect 2.accept 3.read 4.write
            // 分别表示 连接就绪 接受就绪 读就绪 写就绪
            SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
            // 如果 你对不止一种事件感兴趣，那么可以用 “位或” 符将常量连接起来
            int interestSet = SelectionKey.OP_ACCEPT | SelectionKey.OP_READ;
            channel.register(selector, interestSet);
            //当向 Selector 注册 channel，会返回一个 SelectorKey对象。这个对象包含了一些你感兴趣的属性
            // 1.interest 集合 2.ready 集合 3.channel 4.selector 5.附加的对象

            //通过 select 方法选择通道
            // 1.select 2.select(long timeout) 3.selectNow()
            //select 阻塞到至少有一个通道在你注册的事件上就绪了，timeout 表示最长会阻塞多少毫秒
            // selectNow 不会阻塞，不管什么通道就绪都立刻返回，如果前一次选择操作后，没有通道变成可选择的，则会直接返回0
            int count = selector.select(); //返回值表示有多少通道就绪
            Set<SelectionKey> selectionKeys = selector.selectedKeys();//返回一个就绪的通道set集
            //线程调用select方法后阻塞了,即使没有通道已经就绪，也有办法让其从select方法返回
            //只要让其他线程在第一个线程调用select方法的那个selector对象上调用 wakeup 方法，阻塞的线程就会立马返回
            selector.wakeup();
            //关闭selector 多路复用器,关闭之后 注册到selector 上的SelectionKey 实例失效
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * nio 操作之 FileChannel
     */
    public void fileChannelTest() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(""));
            //获取通道
            FileChannel channel = fileInputStream.getChannel();
            // 定义一个 48 bytes的 buffer
            ByteBuffer buffer = ByteBuffer.allocate(48);
            //将通道中的数据 写入buffer,返回值代表有多少字节被读到了buffer中，如果返回-1，表示到了文件末尾
            int byteRead = channel.read(buffer);


            String newData = "New String to write to file..." + System.currentTimeMillis();
            ByteBuffer buffer1 = ByteBuffer.allocate(48);
            buffer1.clear();
            buffer1.put(newData.getBytes("UTF-8"));
            //将buffer1 的写模式切换到读模式
            buffer1.flip();
            //fileChannel.write 方法无法保证write方法一次能向FileChannel写入多少字节,因此需要重复调用,知道缓冲区没有未写入通道的字节
            while (buffer1.hasRemaining()) {
                channel.write(buffer1);
            }

            //position 获取 或者 设置 fileChannel 的当前位置
            //获取当前位置
            long pos = channel.position();
            //设置当前位置
            //如果将位置设置到文件结束符之后，然后想通道中写数据，文件将撑大到当前位置并写入数据，这将导致"文件空洞"，磁盘上物理文件中吸入的数据间有空隙
            channel.position(pos+123);

            // size 方法返回该通道所关联的文件的大小
            long fileSize = channel.size();

            // truncate 方法截取文件,截取文件时，文件将指定长度后的部分删除
            channel.truncate(1024);

            // force 方法将通道里尚未写入磁盘的数据强制写入磁盘
            channel.force(true);

            //关闭通道
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * nio 操作之 socketChannel
     * socketChannel 是一个连接到TCP网络套接字的通道，可以通过2种方式创建 socketChannel
     * 打开一个SocketChannel 并连接到互联网上的某台服务器
     * 一个新连接到达 ServerSocketChannel时，会创建一个SocketChannel
     */
    public void socketChannelTest(){
        try {
            //打开 socketChannel
            SocketChannel socketChannel = SocketChannel.open();
            // 设置为非阻塞模式，异步调用 connect read write 方法
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("http://www.baidu.com",80));
            //非阻塞模式下, write方法在尚未写出任何内容时可能就返回了，所以需要在循环中调用 write
            //非阻塞模式下，read方法在尚未读取到任何数据时可能就返回了，所以需要关注他的int返回值，即读取了多少字节
            //非阻塞模式与选择器搭配会工作的更好，通过将一或多个SocketChannel注册到Selector，可以询问选择器哪个通道已经准备好了读取，写入等。
            if (!socketChannel.finishConnect()){
                //wait , or do something else...
            }


            // 定义一个 buffer
            ByteBuffer buffer = ByteBuffer.allocate(48);
            int byteRead = socketChannel.read(buffer);
            while(byteRead != -1){
                System.out.println("Read " + byteRead);
                //将写模式 切换到 读模式
                buffer.flip();
                // 从缓冲区获取数据的两种方式
                while (buffer.hasRemaining()) {
                    //将数据从缓冲区写入到通道中
                    socketChannel.write(buffer);
                    //直接从缓冲区获取数据
                    System.out.println(buffer.get());
                    byteRead = socketChannel.read(buffer);
                }
            }
            //关闭 socketChannel
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * nio 操作之 serversocketChannel
     * nio 中的 ServerSocketChannel 是一个可以监听新进来的tcp连接的通道
     */
    public void serverSocketChannelTest(){
        try {
            //打开通道
            ServerSocketChannel channel = ServerSocketChannel.open();
            //定义为非阻塞通道
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress("https://www.baidu.com",80));
            while(true){
                //监听新进来的连接,返回一个新进来的连接的 socketChannel,因此 accept 方法会一直阻塞有新的连接到达
                SocketChannel socketChannel = channel.accept();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
