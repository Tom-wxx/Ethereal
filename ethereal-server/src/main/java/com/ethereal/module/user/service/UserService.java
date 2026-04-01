package com.ethereal.module.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ethereal.common.exception.BusinessException;
import com.ethereal.module.user.dto.UserProfileDTO;
import com.ethereal.module.user.dto.UserProfileVO;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.entity.UserPhoto;
import com.ethereal.module.user.entity.UserTag;
import com.ethereal.module.user.mapper.UserMapper;
import com.ethereal.module.user.mapper.UserPhotoMapper;
import com.ethereal.module.user.mapper.UserTagMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserTagMapper userTagMapper;
    private final UserPhotoMapper userPhotoMapper;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toProfileVO(user);
    }

    @Transactional
    public void updateProfile(Long userId, UserProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getBirthday() != null) user.setBirthday(LocalDate.parse(dto.getBirthday()));
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getProfession() != null) user.setProfession(dto.getProfession());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        userMapper.updateById(user);
    }

    public String uploadAvatar(Long userId, MultipartFile file) {
        String filename = "avatar/" + userId + "/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new BusinessException(500, "头像上传失败");
        }
        String url = minioEndpoint + "/" + bucket + "/" + filename;
        User user = new User();
        user.setId(userId);
        user.setAvatar(url);
        userMapper.updateById(user);
        return url;
    }

    public void updateLocation(Long userId, BigDecimal longitude, BigDecimal latitude) {
        User user = new User();
        user.setId(userId);
        user.setLongitude(longitude);
        user.setLatitude(latitude);
        userMapper.updateById(user);
    }

    @Transactional
    public void updateTags(Long userId, List<String> tags) {
        userTagMapper.delete(new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId));
        for (String tagName : tags) {
            UserTag tag = new UserTag();
            tag.setUserId(userId);
            tag.setTagName(tagName);
            tag.setTagCategory("custom");
            userTagMapper.insert(tag);
        }
    }

    public UserProfileVO getUserDetail(Long userId) {
        return getProfile(userId);
    }

    private UserProfileVO toProfileVO(User user) {
        UserProfileVO vo = new UserProfileVO();
        BeanUtil.copyProperties(user, vo);
        if (user.getBirthday() != null) {
            vo.setBirthday(user.getBirthday().toString());
        }

        List<UserTag> tags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, user.getId()));
        vo.setTags(tags.stream().map(t -> {
            UserProfileVO.TagVO tv = new UserProfileVO.TagVO();
            tv.setId(t.getId());
            tv.setTagName(t.getTagName());
            tv.setTagCategory(t.getTagCategory());
            return tv;
        }).collect(Collectors.toList()));

        List<UserPhoto> photos = userPhotoMapper.selectList(
                new LambdaQueryWrapper<UserPhoto>()
                        .eq(UserPhoto::getUserId, user.getId())
                        .eq(UserPhoto::getAuditStatus, 1)
                        .orderByAsc(UserPhoto::getSortOrder));
        vo.setPhotos(photos.stream().map(p -> {
            UserProfileVO.PhotoVO pv = new UserProfileVO.PhotoVO();
            pv.setId(p.getId());
            pv.setPhotoUrl(p.getPhotoUrl());
            pv.setSortOrder(p.getSortOrder());
            return pv;
        }).collect(Collectors.toList()));

        return vo;
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : ".jpg";
    }
}
