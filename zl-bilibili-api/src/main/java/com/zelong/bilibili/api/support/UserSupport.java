package com.zelong.bilibili.api.support;

import com.zelong.bilibili.exception.ConditionException;
import com.zelong.bilibili.service.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {

    public Long getCurrentUserId() {
        // get token from frontend local storage
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String token = requestAttributes.getRequest().getHeader("token");
        Long  userId = TokenUtil.verifyToken(token);
        if (userId < 0) {
            throw new ConditionException("Invalid user!");
        }
        return userId;

    }
}
