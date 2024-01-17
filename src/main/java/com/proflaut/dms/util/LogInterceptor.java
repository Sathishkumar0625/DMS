package com.proflaut.dms.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.proflaut.dms.service.impl.UserRegisterServiceImpl;

public class LogInterceptor implements HandlerInterceptor {
	 	@Autowired
	    UserRegisterServiceImpl userService;
//	    private Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

	    @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	            throws Exception {
	        String path = request.getRequestURI().substring(request.getContextPath().length());
	        System.out.println(path);
	        if (path.equals("/dmsCheck/signup") || path.equals("/dmsCheck/login")) {
	            return true;
	        } else {

	            String token = request.getHeader("token");
	            System.out.println("hEadER token -- " + token);
	            Map<String, String> userData = userService.validateToken(token);
	            if (userData.get("status").equals("success")) {
	                return true;
	            } else {
	                response.reset();
	                response.getWriter().write("Unauthorized");
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                return false;
	            }
	        }

	    }

	    @Override
	    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
	            ModelAndView modelAndView) throws Exception {
//	        System.out.println("Hello durai");
	    }

	    @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
	            throws Exception {
//	        System.out.println("Hello durai");
	    }

}
