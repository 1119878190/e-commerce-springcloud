package com.imooc.ecommerce.filter;


import com.imooc.ecommerce.constant.CommonConstant;
import com.imooc.ecommerce.util.TokenParseUtil;
import com.imooc.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <h1 >用户身份统一拦截</h1>
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class LoginUserInterceptor implements HandlerInterceptor {

    /**
     * 执行方法前，将用户信息存入 ThreadLocal 中
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        // 部分请求不需要带有身份信息，即白名单
        if (checkWhiteListUrl(request.getRequestURI())) {
            return true;
        }

        // 先从 http header 中拿到 token
        String token = request.getHeader(CommonConstant.JWT_USER_INFO_KEY);

        // 解析 token
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception e) {
            log.error("parse login user info error: [{}]", e.getMessage(), e);
        }

        // 这里只是演示，不可能出现loginUserInfo为null的情况，因为请求先经过gateway，在gateway中已经判断了loginUserInfo为空的情况
        if (Objects.isNull(loginUserInfo)) {
            throw new RuntimeException("can not parse current login user");
        }

        log.info("set login user info is : [{}]", request.getRequestURI());

        // 设置当前请求上下文，把用户信息填充进去
        AccessContext.setLoginUserInfoThreadLocal(loginUserInfo);

        return true;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 请求完全结束后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除用户信息
        if (Objects.nonNull(AccessContext.getLoginUserInfo())) {
            AccessContext.clearLoginUserInfo();
        }
    }


    /**
     * 白名单，若在白名单中不用拦截校验
     * <p>
     * 过滤 Swagger 请求
     *
     * @param url
     * @return
     */
    private boolean checkWhiteListUrl(String url) {
        return StringUtils.containsAny(
                url,
                "springfox", "swagger", "v2", "webjars", "doc.html", "error"
        );
    }
}
