package com.ethereal.module.discovery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserActionDTO {
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;

    @NotBlank(message = "操作类型不能为空")
    private String action; // like, pass, super_like
}
