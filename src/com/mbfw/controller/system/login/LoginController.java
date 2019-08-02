package com.mbfw.controller.system.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.ShardedJedis;
import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
import com.mbfw.util.Const;
import com.mbfw.util.DbDatautils;
import com.mbfw.util.MD5;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.PageData;
import com.mbfw.util.RedisUtil;
import com.mbfw.util.Tools;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/*
 * 总入口
 */
@Controller
public class LoginController extends BaseController {

	@Resource(name = "dbDataUtils")
	public DbDatautils dbDatautils;


	/**
	 * 访问登录页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login_toLogin")
	public ModelAndView toLogin() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("SYSNAME", Tools.readTxtFile(Const.SYSNAME)); // 读取系统名称
		mv.setViewName("system/admin/login");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 请求登录，验证用户
	 */
	@RequestMapping(value = "/login_login", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object login() throws Exception {
		ShardedJedis jds = RedisUtil.getJedis();
		String errInfo = "usererror";
		Map<String, Object>  map = new HashMap<String, Object>();
		try {
			String loginName = getParam("loginname");
			String passWord = getParam("password");
			String code = getParam("code");
			loginName = loginName.trim().replace("'", "");
			passWord = passWord.trim().replace("'","");
			Session session = getSession();
			String sessionCode = (String) session.getAttribute(Const.SESSION_SECURITY_CODE); // 获取session中的验证码
			if (MyStringUtils.notBlank(loginName) && MyStringUtils.notBlank(passWord) && MyStringUtils.notBlank(code)) {
				code = code.toUpperCase();
				sessionCode = MyStringUtils.isBlank(sessionCode)?"":sessionCode.toUpperCase();
				if (sessionCode.equals(code)) {
					// shiro加入身份验证
					Subject subject = SecurityUtils.getSubject();
					UsernamePasswordToken token = new UsernamePasswordToken(loginName, passWord);
					try {
						subject.login(token);
					} catch (AuthenticationException e) {
						errInfo = "身份验证失败！";
						map.put("result", errInfo);
						return JSON.toJSON(map);
					}
					Map<String, Object> userInfo = new HashMap<String, Object>();
					String loginSql = jds.get("loginSql");//登录sql  
					if ("admin".equals(loginName) && "000000".equals(passWord)) {//超级管理员
						errInfo = "success";
						userInfo.put(Const.SESSION_USERID, 0);
						userInfo.put(Const.SESSION_USERNAME, "超级管理员");
					} else if (MyStringUtils.notBlank(loginSql)) {
						passWord = MD5.md5(passWord);
						loginSql = loginSql.replace("#{USER_NAME}", loginName).replace("#{passWord}", passWord);
						List<Map<String, Object>> list = dbDatautils.select(loginSql);
						if (MyCollectionUtils.notEmpty(list)) {
							errInfo = "success";
							userInfo = list.get(0);
							userInfo.put(Const.SESSION_USERID, userInfo.get("ID"));
							userInfo.put(Const.SESSION_USERNAME, userInfo.get("USER_NAME"));
						}
					}
					System.out.println("===userInfo=="+userInfo);
					if (userInfo!=null && !userInfo.isEmpty()) {
						session.setAttribute(Const.SESSION_USERID, userInfo.get(Const.SESSION_USERID));
						session.setAttribute(Const.SESSION_USERNAME, userInfo.get(Const.SESSION_USERNAME));
						session.setAttribute(Const.SESSION_USER, userInfo);
					}
					session.removeAttribute(Const.SESSION_SECURITY_CODE);
				} else {
					errInfo = "codeerror";
				}
			} else {
				errInfo = "errorParam";
			}
		} catch (Exception e) {
			logger.error("登录错误", e);
		} finally {
			RedisUtil.returnJedis(jds);
		}
		map.put("result", errInfo);
		return JSON.toJSON(map);
	}

	/**
	 * 访问系统首页
	 */
	@RequestMapping(value = "/login_index", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ModelAndView login_index() {
		ModelAndView mv = this.getModelAndView();
		ShardedJedis jds = RedisUtil.getJedis();
		try {
			DB db = MongoDbFileUtil.getDb();
			DBCollection menuDbc = db.getCollection("menu");
			DBCursor menuCur = menuDbc.find(new BasicDBObject("ifShow",1));
			menuCur.sort(new BasicDBObject("menuSort",1));
			Session session = getSession();
			mv.addObject("loginUserId",session.getAttribute(Const.SESSION_USERID));
			mv.addObject("loginUserName",session.getAttribute(Const.SESSION_USERNAME));
			mv.addObject("menuList", menuCur==null?new ArrayList<DBObject>():menuCur.toArray());
			mv.setViewName("system/admin/index");
		} catch (Exception e) {
			mv.setViewName("system/admin/login");
			logger.error(e.getMessage(), e);
		} finally {
			RedisUtil.returnJedis(jds);
		}
		return mv;
	}


	/**
	 * 用户注销
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logout() {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();

		// shiro管理的session
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();

		session.removeAttribute(Const.SESSION_USER);
		session.removeAttribute(Const.SESSION_USERID);
		session.removeAttribute(Const.SESSION_USERNAME);

		// shiro销毁登录
		Subject subject = SecurityUtils.getSubject();
		subject.logout();

		pd = this.getPageData();
		String msg = pd.getString("msg");
		pd.put("msg", msg);
		mv.setViewName("system/admin/login");
		mv.addObject("pd", pd);
		return mv;
	}

	
	/**
	 * 进入首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login_home")
	public ModelAndView toHomePage() {
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("system/admin/home");
		return mv;
	}

}
