package com.authenhub.config.security;

import com.authenhub.entity.User;
import com.authenhub.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UserRoleService userRoleService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        if (authentication.getPrincipal() instanceof User user) {
            String permissionName = (String) permission;
            
            return userRoleService.hasPermission(user, permissionName);
        }
        
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        if (authentication.getPrincipal() instanceof User user) {
            String permissionName = (String) permission;
            
            return userRoleService.hasPermission(user, permissionName);
        }
        
        return false;
    }
}
