package com.ethereal.module.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_conversation")
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userIdA;
    private Long userIdB;
    private String lastMsgContent;
    private LocalDateTime lastMsgTime;
    private Integer unreadCountA;
    private Integer unreadCountB;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
