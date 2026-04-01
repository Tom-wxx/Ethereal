package com.ethereal.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_tag")
public class UserTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String tagName;
    private String tagCategory;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
