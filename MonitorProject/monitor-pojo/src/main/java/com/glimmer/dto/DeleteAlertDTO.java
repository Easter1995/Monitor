package com.glimmer.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteAlertDTO implements Serializable {
    private String[] pathVideos;
    private String[] pathPhotos;
}
