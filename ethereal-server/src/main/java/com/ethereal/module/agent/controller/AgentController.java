package com.ethereal.module.agent.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.R;
import com.ethereal.module.agent.service.AgentConciergeService;
import com.ethereal.module.agent.service.AgentRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AI Agent模块")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentConciergeService conciergeService;
    private final AgentRecommendService recommendService;

    @Operation(summary = "AI管家对话")
    @PostMapping("/concierge")
    public R<Map<String, Object>> conciergeChat(@RequestBody Map<String, Object> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        String message = (String) body.get("message");
        Long targetUserId = body.get("targetUserId") != null
                ? Long.parseLong(body.get("targetUserId").toString()) : null;
        return R.ok(conciergeService.chat(userId, message, targetUserId));
    }

    @Operation(summary = "获取破冰建议")
    @GetMapping("/ice-breaker/{targetUserId}")
    public R<List<String>> getIceBreaker(@PathVariable Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(conciergeService.generateIceBreakers(userId, targetUserId));
    }

    @Operation(summary = "AI匹配度分析")
    @GetMapping("/compatibility/{targetUserId}")
    public R<String> analyzeCompatibility(@PathVariable Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(recommendService.analyzeCompatibility(userId, targetUserId));
    }
}
