package com.ypy.provider;

import com.ypy.common.service.MusicService;
import com.ypy.provider.service.impl.MusicServiceImpl;
import com.ypy.pyrpc.bootstrap.ProviderBootstrap;
import com.ypy.pyrpc.model.ServiceRegisterInfo;

import java.util.List;

public class Provider {
    public static void main(String[] args) {
        ServiceRegisterInfo<MusicService> musicServiceServiceRegisterInfo = new ServiceRegisterInfo<>();
        musicServiceServiceRegisterInfo.setServiceName(MusicService.class.getName());
        musicServiceServiceRegisterInfo.setImplClass(MusicServiceImpl.class);

        ProviderBootstrap.init(List.of(musicServiceServiceRegisterInfo));
    }
}
