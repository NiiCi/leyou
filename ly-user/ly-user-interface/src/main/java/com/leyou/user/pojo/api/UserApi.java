package com.leyou.user.pojo.api;

import com.leyou.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping
public interface UserApi {
    @GetMapping("/query")
    public ResponseEntity<User> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}

