package com.leyou.test;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JWTUtils;
import com.leyou.auth.utils.RSAUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(SpringRunner.class)

public class JWTTest {
    private static final String pubKeyPath = "E:\\JWT\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\JWT\\rsa\\rsa.pri";
    /**
     * 公钥
     */
    private PublicKey publicKey;
    /**
     * 私钥
     */
    private PrivateKey privateKey;

    /**
     * RSA 进行非对称加密,生成公钥，私钥文件
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RSAUtils.generateKey(pubKeyPath,priKeyPath,"234");
    }

    /**
     * 获取rsa文件中的公钥和私钥
     * @throws Exception
     */
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RSAUtils.getPublicKey(pubKeyPath);
        this.privateKey = RSAUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     */
    @Test
    public void testGenerateToken() throws Exception {
        String token = JWTUtils.generateToken(new UserInfo(20L,"jack"),privateKey,20);
        System.out.println("toekn: "+token);
    }

    /**
     * 解析token
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU1NDE5NDQyNH0.cpD2nczqRjH3jI8_VkBfL2lqAQZ0UH0fZYUtqyu1HoXvucqrP7XfbShJOEZ7u82a_3Xp6zMo84ceIn5bMUsCuYkEylc13YsXLtcDXHY_lYcOfoLp9ik0VTFAkP5sbYHA9QXwxVTeDIQdjHVAKp_YgIesaaUw6gh3uKE5uv9Q55M";

        // 解析token
        UserInfo user = JWTUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
