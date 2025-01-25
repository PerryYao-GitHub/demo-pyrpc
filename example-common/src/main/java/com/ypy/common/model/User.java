package com.ypy.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private List<Long> friendsIds;
}
