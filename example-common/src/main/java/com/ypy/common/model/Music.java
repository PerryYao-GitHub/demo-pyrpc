package com.ypy.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class Music implements Serializable {
    private long id;
    private String title;
    private String composer;
    private String player;
}
