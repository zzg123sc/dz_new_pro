package com.mbfw.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mbfw.util.Const;

/**
 * 类名称：WebAppContextListener.java 作者：研发中心
 * 
 * @version 1.0
 */
public class WebAppContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		Const.WEB_APP_CONTEXT = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		// System.out.println("========获取Spring WebApplicationContext");
	}

}
