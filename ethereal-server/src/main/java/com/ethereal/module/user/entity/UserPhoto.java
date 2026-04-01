package com.ethereal.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_photo")
public class UserPhoto {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String photoUrl;
    private Integer sortOrder;
    private Integer auditStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
