package com.ethereal.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId != null && loginId.toString().startsWith("admin:")) {
            return List.of("admin");
        }
        return Collections.emptyList();
    }
}
