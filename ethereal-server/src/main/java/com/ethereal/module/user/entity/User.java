package com.ethereal.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String bio;
    private String profession;
    private String city;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer verified;
    private Integer vibeScore;
    private Integer vipLevel;
    private Integer status;
    private String wxOpenid;
    private String qqOpenid;
    private LocalDateTime lastLoginTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
