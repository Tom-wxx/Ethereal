package com.ethereal.module.moment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "t_moment", autoResultMap = true)
public class Moment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private BigDecimal longitude;
    private BigDecimal latitude;
    private String locationName;
    private Integer likeCount;
    private Integer commentCount;
    private Integer auditStatus;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
