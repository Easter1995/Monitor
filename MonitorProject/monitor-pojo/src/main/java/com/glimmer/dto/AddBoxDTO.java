package com.glimmer.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddBoxDTO implements Serializable {
    private Integer caId;
    private String leftUp;
    private String rightDown;
}
