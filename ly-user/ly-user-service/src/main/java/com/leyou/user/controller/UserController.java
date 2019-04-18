package com.leyou.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping
@Log4j2
@Api("user 服务接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     @Api: 修改整个类，描述Controller的作用
     @ApiOperation: 描述一个类的一个方法，或者说一个接口
     @ApiParam: 单个参数描述
     @ApiModel: 用对象来接受参数
     @ApiModelProperty: 用对象来接受参数时，描述对象的一个字段
     @ApiResponse: HTTP响应的其中一个描述
     @ApiResponses: HTTP响应的整体描述
     @ApiIgnore: 使用该注解忽略这个Api
     @ApiImplicitParam: 一个请求参数
     @ApiImplicitParams: 多个请求参数
     */

    @GetMapping("check/{data}/{type}")
    @ApiOperation(value = "校验用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", required = true, value = "用户名或者手机号", paramType = "String"),
            @ApiImplicitParam(name = "type", required = true, value = "登录类型 1 用户名 2 手机号", paramType = "Integer")
    })
    public ResponseEntity<Object> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type", required = false) Integer type) {
        int type_ = 1;
        if (type != null) {
            type_ = type;
        }
        Boolean flag = userService.checkUserData(data, type);
        if (flag == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(flag);
    }

    /**
     * 发送手机短信码
     *
     * @param phone
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<Object> getCode(@RequestParam("phone") String phone) {
        if (StringUtils.isEmpty(phone)) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Boolean flag = userService.getCode(phone);
        if (flag == null || !flag) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code",required = true,type = "String",value = "验证码")
    })
    public ResponseEntity<Object> register(@RequestBody @Valid User user, @RequestParam("code") String code) {
        Boolean flag = userService.register(user, code);
        if (flag == null || !flag) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/query")
    @ApiOperation("用户查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username",required = true,type = "String",value = "用户名"),
            @ApiImplicitParam(name = "password",required = true,type = "String",value = "密码")
    })
    @ApiResponses({
            @ApiResponse(code = 200,message = "操作成功",response = User.class)
    })
    public ResponseEntity<Object> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        log.info(username + password);
        User user = userService.queryUser(username, password);
        try {
            log.info(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }


}
