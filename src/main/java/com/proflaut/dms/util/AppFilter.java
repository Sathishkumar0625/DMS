//package com.proflaut.dms.util;
//
//import java.io.IOException;
//import java.util.Map;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.proflaut.dms.service.impl.UserRegisterServiceImpl;
//
//@Component
//public class AppFilter implements Filter {
//
//	@Autowired
//	UserRegisterServiceImpl userService;
//
//	@Override
//	public void destroy() {
//   // TODO document why this method is empty
// }
//
//	@Override
//	public void init(FilterConfig filterconfig) throws ServletException {
//		//TODO document why this method is empty
//	}
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		HttpServletRequest request1 = (HttpServletRequest) request;
//		System.out.println("Remote Host:" + request.getRemoteHost());
//		System.out.println("Remote Address:" + request.getRemoteAddr());
//		
//		String path = request1.getRequestURI().substring(request1.getContextPath().length());
//		System.out.println(path);
//		if (path.equals("/dmsCheck/signup") || path.equals("/dmsCheck/login")) {
//			chain.doFilter(request, response);
//		} else {
//			String token = request1.getHeader("token");
//			Map<String, String> userData = userService.validateToken(token);
//			if (userData.get("status").equals("success")) {	
//				
//				chain.doFilter(request1, response);
//			} else {
//				HttpServletResponse resp = (HttpServletResponse) response;
//				resp.reset();
//				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//			}
//		}
//
//
//	}
//}