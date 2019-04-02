package com.leyou.user.dao;

import com.leyou.user.pojo.User;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface UserMapper extends Mapper<User>,IdListMapper<User,Long> {
}
