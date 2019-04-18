package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leyou.common.pojo.Validator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
@Data
@ApiModel("用户信息类")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;
    @Pattern(regexp = Validator.REGEX_USERNAME,message = "用户名格式不正确")
    @ApiModelProperty(value = "用户名",required = true,dataType = "String",example = "用户名")
    private String username;
    //在json序列化时将java bean中的一些属性忽略掉，序列化和反序列化都受影响
    //一般标记在属性或者方法上，返回的json数据即不包含该属性
    //为了安全考虑。这里对password和salt添加了注解@JsonIgnore，这样在json序列化时，就不会把password和salt返回
    @JsonIgnore
    @Pattern(regexp = Validator.REGEX_PASSWORD,message = "密码格式不正确")
    @ApiModelProperty(value = "密码",required = true,dataType = "String",example = "密码")
    private String password;
    @ApiModelProperty(value = "手机号",required = true,dataType = "String",example = "手机号")
    @Pattern(regexp = Validator.REGEX_MOBILE,message = "手机号格式不正确")
    private String phone;
    @ApiModelProperty(hidden = true)
    private Date created;
    /**
     * 密码的盐值
     */
    @JsonIgnore
    private String salt;
}
