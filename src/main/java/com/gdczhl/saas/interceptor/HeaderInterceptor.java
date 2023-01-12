package com.gdczhl.saas.interceptor;


import com.gdczhl.saas.exceptions.LoginException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hkx
 */
public class HeaderInterceptor implements HandlerInterceptor {

    public static final String OPERATOR_UUID = "operatorUuid";
    public static final String INSTITUTION_UUID = "institutionUuid";
    public static final String JTI = "jti";
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String operatorUuid = request.getHeader(OPERATOR_UUID);
        if (StringUtils.isBlank(operatorUuid)) {
            throw new LoginException();
        }

        ContextCache.putAttribute(OPERATOR_UUID, operatorUuid);

        if (!pathMatcher.match("/**/operating/**", request.getRequestURI())) {
            String institutionUuid = request.getHeader(INSTITUTION_UUID);
            ContextCache.putAttribute(INSTITUTION_UUID, institutionUuid);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextCache.remove();
    }
}
