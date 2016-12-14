package com.beetle.component.security.session;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;

import com.beetle.framework.util.encrypt.Coder;

public class RedisSessionFactory implements SessionFactory {

	@Override
	public Session createSession(SessionContext initData) {
		StateSession session = new StateSession();
		if (initData != null) {
			HttpServletRequest request = (HttpServletRequest) initData
					.get(DefaultWebSessionContext.class.getName() + ".SERVLET_REQUEST");
			session.setHost(getIpAddress(request));
		}
		return session;
	}

	public static String getIpAddress(HttpServletRequest req) {
		String ip1 = req.getHeader("X-Forwarded-For");
		String ip2 = req.getHeader("x-real-ip");
		String ip3 = req.getRemoteAddr();
		String ip4 = req.getHeader("X-Client-Id");
		StringBuilder sb = new StringBuilder();
		if (ip1 == null) {
			ip1 = "";
		}
		if (ip2 == null) {
			ip2 = "";
		}
		if (ip3 == null) {
			ip3 = "";
		}
		if (ip4 == null) {
			ip4 = "";
		}
		sb.append(ip1);
		sb.append(ip2);
		sb.append(ip3);
		sb.append(ip4);
		if (sb.toString().length() <= 1) {
			return "127.0.0.1";
		}
		return Coder.md5(sb.toString());
	}
}
