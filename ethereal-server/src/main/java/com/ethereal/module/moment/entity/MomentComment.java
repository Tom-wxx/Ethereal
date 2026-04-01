package com.ethereal.module.moment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_moment_comment")
public class MomentComment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long momentId;
    private Long userId;
    private String content;
    private Long parentId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
