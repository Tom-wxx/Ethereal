package com.ethereal.module.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String birthday;
    private String bio;
    private String profession;
    private String city;
    private Integer verified;
    private Integer vibeScore;
    private Integer vipLevel;
    private List<TagVO> tags;
    private List<PhotoVO> photos;

    @Data
    public static class TagVO {
        private Long id;
        private String tagName;
        private String tagCategory;
    }

    @Data
    public static class PhotoVO {
        private Long id;
        private String photoUrl;
        private Integer sortOrder;
    }
}
