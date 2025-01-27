package com.ypy.consumer;

import com.ypy.common.model.Music;
import com.ypy.common.model.User;
import com.ypy.common.service.MusicService;
import com.ypy.common.service.UserService;
import com.ypy.pyrpc.bootstrap.ConsumerBootstrap;
import com.ypy.pyrpc.proxy.ServiceProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        ConsumerBootstrap.init();

        MusicService musicService = ServiceProxyFactory.getProxy(MusicService.class);
        Music bach = musicService.getMusicById(0L);
        System.out.println(bach);
        Music mozart = musicService.getMusicById(1L);
        System.out.println(mozart);

        MusicService musicMockService = ServiceProxyFactory.getMockProxy(MusicService.class);
        Music mockMusic = musicMockService.getMusicById(0L);
        System.out.println(mockMusic);

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User dxp = userService.getUserById(8964L);
        System.out.println(dxp);
        User mzd = userService.getUserById(1966L);
        System.out.println(mzd);

        UserService userServiceMock = ServiceProxyFactory.getMockProxy(UserService.class);
        User mockUser = userServiceMock.getUserById(8964L);
        System.out.println(mockUser);
    }
}
