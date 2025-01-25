package com.ypy.consumer;

import com.ypy.common.model.Music;
import com.ypy.common.service.MusicService;
import com.ypy.pyrpc.bootstrap.ConsumerBootstrap;
import com.ypy.pyrpc.proxy.ServiceProxyFactory;

public class Consumer {
    public static void main(String[] args) {
        ConsumerBootstrap.init();
        MusicService musicService = ServiceProxyFactory.getProxy(MusicService.class);
        Music music = musicService.getMusicById(0L);
        System.out.println(music);
    }
}
