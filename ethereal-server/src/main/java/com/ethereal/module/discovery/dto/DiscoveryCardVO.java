package com.ethereal.module.discovery.dto;

import lombok.Data;

import java.util.List;

@Data
public class DiscoveryCardVO {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer age;
    private Integer gender;
    private String profession;
    private String city;
    private Double distance;
    private Integer verified;
    private Integer vibeScore;
    private String bio;
    private List<String> tags;
    private List<String> photos;
}
