package com.leyou.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Object> checkUserData(@PathVariable("data") String data, @PathVariable(value = "type",required = false) Integer type){
        int type_ = 1;
        if (type != null){
            type_ = type;
        }
        Boolean flag = userService.checkUserData(data,type);
        if (flag == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(flag);
    }

    /**
     * 发送手机短信码
     * @param phone
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<Object> getCode(@RequestParam("phone") String phone){
        if (StringUtils.isEmpty(phone)){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Boolean flag = userService.getCode(phone);
        if (flag == null || !flag){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid User user , @RequestParam("code") String code){
        Boolean flag = userService.register(user,code);
        if (flag == null || !flag){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/query")
    public ResponseEntity<Object> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        log.info(username+password);
        User user = userService.queryUser(username,password);
        try {
            log.info(objectMapper.writeValueAsString(user));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }


}
