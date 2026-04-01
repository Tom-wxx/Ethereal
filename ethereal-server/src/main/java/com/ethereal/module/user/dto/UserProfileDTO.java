package com.ethereal.module.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileDTO {
    @Size(max = 50, message = "昵称最多50个字符")
    private String nickname;

    private Integer gender;
    private String birthday;

    @Size(max = 500, message = "简介最多500个字符")
    private String bio;

    @Size(max = 100, message = "职业最多100个字符")
    private String profession;

    @Size(max = 50, message = "城市最多50个字符")
    private String city;
}
