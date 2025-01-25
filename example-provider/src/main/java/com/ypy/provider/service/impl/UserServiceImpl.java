package com.ypy.provider.service.impl;

import com.ypy.common.model.User;
import com.ypy.common.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(long id) {
        User user = new User();
        user.setId(id);
        if (id == 8964L) {
            user.setUsername("邓小平");
            user.setPassword("8964dxp");
            List<Long> friendsIds = List.of(1966L, 422L, 516L, 720L);
            user.setFriendsIds(friendsIds);
        } else if (id == 1966L) {
            user.setUsername("毛泽东");
            user.setPassword("1966mzd");
            List<Long> friendsIds = List.of(8964L, 422L, 516L, 720L);
            user.setFriendsIds(friendsIds);
        }
        return user;
    }
}
