package com.proflaut.dms.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.proflaut.dms.service.impl.AccessServiceImpl;

@Component
public class LogInterceptor implements HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(LogInterceptor.class);

	AccessServiceImpl accessServiceImpl;

	@Autowired
	public LogInterceptor(AccessServiceImpl accessServiceImpl) {
		this.accessServiceImpl = accessServiceImpl;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		logger.info("PATH --> {}", path);
		if (path.equals("/access/signup") || path.equals("/access/login") || path.equals("/access/forgotPassword")
				|| path.equals("/access/verifyOtp") || path.equals("/access/savePassword")
				|| path.equals("/licence/path") || path.equals("/file/compression")) {
			return true;
		} else {

			String token = request.getHeader("token");
			logger.info("hEadER token --> {} ", token);
			Map<String, String> userData = accessServiceImpl.validateToken(token);
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
		// This method is intentionally left empty.
		// No post-processing logic is required at the moment.
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// This method is intentionally left empty.
		// No post-processing logic is required at the moment.
	}

}
