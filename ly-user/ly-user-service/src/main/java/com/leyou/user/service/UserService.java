package com.leyou.user.service;

import com.leyou.user.pojo.User;

public interface UserService {
    public Boolean checkUserData(String data, Integer type);

    public Boolean getCode(String phone);

    public Boolean register(User user, String code);

    public User queryUser(String username, String password);
}
