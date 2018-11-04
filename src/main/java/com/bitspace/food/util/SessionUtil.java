package com.bitspace.food.util;


import com.bitspace.food.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class SessionUtil {
    private static Logger log = LoggerFactory.getLogger(SessionUtil.class);

    private static Map<Long, Map<String, HttpSession>> userSessions = new HashMap<Long, Map<String, HttpSession>>();
    private static ConcurrentMap<String, Long> sessionToUid = new ConcurrentHashMap<String, Long>();

    public static String AUTH_TYPE = "STORM_TYPE";
    public static String AUTH_UID = "STORM_UID";

    private SessionUtil() {
    }



    public static Long getUid(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            return DbDataConvertUtil.toLong(session.getAttribute(AUTH_UID));
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static Long getUid(HttpServletRequest request) {
        return getUid(request.getSession(false));
    }

    public static boolean hasUserLogin(HttpSession session) {
        Long uid = getUid(session);
        if (uid == null || uid < 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean hasUserLogin(HttpServletRequest request) {
        return hasUserLogin(request.getSession(false));
    }

    public static void invalidCurrentSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                LoggerUtil.error(log, e);
            }
        }
    }

    public static void setAdminInfoToSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(AUTH_TYPE, 1);
        session.setAttribute(AUTH_UID, user.getUid());
    }

    public static void setUserInfoToSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(AUTH_TYPE, 2);
        session.setAttribute(AUTH_UID, user.getUid());
    }

}
