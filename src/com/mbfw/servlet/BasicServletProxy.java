package com.mbfw.servlet;


import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 在web.xml中定义此servlet,且将name属性设置为真正的servlet的{@code @component}属性定义的name<br>
 * 定义此类是为了能够在servlet中使用spring的注入<br>
 * 可以多次使用(即:在web.xml中可以多次定义)
 * @author lichao.lu
 *
 */
public class BasicServletProxy extends GenericServlet{
	private static final long serialVersionUID = 9027988470572963499L;
	private String targetBean;
	private Servlet proxy;
	public BasicServletProxy() {
		
	}
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		proxy.service(req,res);
	}
	public void init() throws ServletException {
		this.targetBean=getServletName();
		getServletBean();
		proxy.init(getServletConfig());
	}
	private void getServletBean(){
		WebApplicationContext wac=WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		this.proxy=(Servlet)wac.getBean(targetBean);
	}
}
