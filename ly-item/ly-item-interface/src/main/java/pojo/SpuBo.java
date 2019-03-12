package pojo;

import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class SpuBo extends Spu {
    /**
     * 商品分类名称
     */
    @Transient
    private String cname;
    /**
     * 品牌名称
     */
    @Transient
    private String bname;
    /**
     * 商品详情引用
     */
    @Transient
    private SpuDetail spuDetail;
    /**
     * 商品特殊信息引用
     */
    @Transient
    private List<Sku> skus;

}
