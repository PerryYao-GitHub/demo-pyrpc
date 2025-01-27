package com.ypy.pyrpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo<T> {
    private String serviceInterfaceName;
    private Class<? extends T> serviceImplClass;
}
