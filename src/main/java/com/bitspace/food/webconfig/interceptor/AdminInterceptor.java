package com.bitspace.food.webconfig.interceptor;


import com.bitspace.food.entity.Admin;
import com.bitspace.food.memdb.AdminMemDB;
import com.bitspace.food.memdb.SessionMemDB;
import com.bitspace.food.util.CommonUtil;
import com.bitspace.food.util.JsonUtil;
import com.bitspace.food.util.JwtUtil;
import com.bitspace.food.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zham on 18-1-20.
 */
public class AdminInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(AdminInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = CommonUtil.getIp(request);
        String token = JwtUtil.getToken(request);
        Admin admin = null;
        if (!StringUtils.isEmpty(token)) {
            Long aid = null;
            try {
                aid = JwtUtil.getUidByJwt(token);
            } catch (Exception e) {
                //token 非法直接报错
                LoggerUtil.warn(log, token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            log.info(String.format("%s %s %s %s %s", aid, ip, request.getMethod(), request.getRequestURI(), JsonUtil.toJson(request.getParameterMap())));
            if (aid != null) {
                String accessToken = SessionMemDB.getSessionByUid(aid);
                //防止失效token调用
                if (!token.equals(accessToken)) {
                    LoggerUtil.debug(log, "invalid token aid:" + aid + ",token is:" + token + ",accessToken is:" + accessToken);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                admin = AdminMemDB.getAdminByAid(aid);
            }
            
            if (null == admin) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
//			else if(!AccessMem.checkApiAccess(user.getAppId(),user.getAppUid(),request.getMethod(), request.getRequestURI())){
//				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//				return false;
//			}
            else {
                return true;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
    
    
}
