package com.ethereal.module.auth.dto;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private Boolean isNew;
}
