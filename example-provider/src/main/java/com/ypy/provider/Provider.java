package com.ypy.provider;

import com.ypy.common.service.MusicService;
import com.ypy.common.service.UserService;
import com.ypy.provider.service.impl.MusicServiceImpl;
import com.ypy.provider.service.impl.UserServiceImpl;
import com.ypy.pyrpc.bootstrap.ProviderBootstrap;
import com.ypy.pyrpc.model.ServiceRegisterInfo;

import java.util.List;

public class Provider {
    public static void main(String[] args) {
        ServiceRegisterInfo<MusicService> musicServiceServiceRegisterInfo = new ServiceRegisterInfo<>();
        musicServiceServiceRegisterInfo.setServiceInterfaceName(MusicService.class.getName());
        musicServiceServiceRegisterInfo.setServiceImplClass(MusicServiceImpl.class);

        ServiceRegisterInfo<UserService> userServiceServiceRegisterInfo = new ServiceRegisterInfo<>();
        userServiceServiceRegisterInfo.setServiceInterfaceName(UserService.class.getName());
        userServiceServiceRegisterInfo.setServiceImplClass(UserServiceImpl.class);

        ProviderBootstrap.init(List.of(musicServiceServiceRegisterInfo, userServiceServiceRegisterInfo));
    }
}
