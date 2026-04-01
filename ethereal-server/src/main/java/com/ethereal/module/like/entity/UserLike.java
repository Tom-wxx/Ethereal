package com.ethereal.module.like.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_like")
public class UserLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long targetUserId;
    private Integer actionType; // 1喜欢 2超级喜欢 3跳过
    private Integer isMutual;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
