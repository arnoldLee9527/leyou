package com.leyou.user.service;

import com.leyou.config.User;

public interface UserService {
    Boolean CheckUser(String data, Integer type);

    Boolean Register(User user, String code);

    Boolean sendVerifyCode(String phone);
}
