package com.ethereal.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ethereal.common.result.PageResult;
import com.ethereal.common.result.R;
import com.ethereal.module.moment.entity.Moment;
import com.ethereal.module.moment.mapper.MomentMapper;
import com.ethereal.module.user.entity.UserPhoto;
import com.ethereal.module.user.mapper.UserPhotoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理后台-内容审核")
@RestController
@RequestMapping("/admin-api")
@RequiredArgsConstructor
public class AdminMomentController {

    private final MomentMapper momentMapper;
    private final UserPhotoMapper userPhotoMapper;

    @Operation(summary = "待审核动态列表")
    @GetMapping("/moments/audit")
    public R<PageResult<Moment>> getMomentAuditList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Moment> p = momentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Moment>()
                        .eq(Moment::getAuditStatus, 0)
                        .orderByDesc(Moment::getCreatedAt));
        PageResult<Moment> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());
        return R.ok(result);
    }

    @Operation(summary = "审核动态")
    @PutMapping("/moments/{id}/audit")
    public R<Void> auditMoment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = Integer.parseInt(body.get("status").toString());
        Moment moment = momentMapper.selectById(id);
        if (moment != null) {
            moment.setAuditStatus(status);
            momentMapper.updateById(moment);
        }
        return R.ok();
    }

    @Operation(summary = "待审核图片列表")
    @GetMapping("/photos/audit")
    public R<PageResult<UserPhoto>> getPhotoAuditList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserPhoto> p = userPhotoMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserPhoto>().eq(UserPhoto::getAuditStatus, 0));
        PageResult<UserPhoto> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());
        return R.ok(result);
    }

    @Operation(summary = "审核图片")
    @PutMapping("/photos/{id}/audit")
    public R<Void> auditPhoto(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = Integer.parseInt(body.get("status").toString());
        UserPhoto photo = userPhotoMapper.selectById(id);
        if (photo != null) {
            photo.setAuditStatus(status);
            userPhotoMapper.updateById(photo);
        }
        return R.ok();
    }
}
