package com.mbfw.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;

import com.mbfw.createPage.SqlUtils;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * @author wangqiyu
 *
 */
@SuppressWarnings("unchecked")
@Component("initDataServlet")
public class InitDataServlet extends HttpServlet{
	private static final long serialVersionUID = 2468041426284362899L;
	public InitDataServlet() {
		
	}
	public void init(ServletConfig config) throws ServletException {
		ServletContext servletContext = config.getServletContext();
		//加载所有的数据字典到application
		loadAllDicToApplication(servletContext);
		//刷新登陆sql
		refreshLoginSql();
		super.init(config);
	}
	
	/**
	 * 加载所有的数据字典到application
	 * @param servletContext
	 */
	public static void loadAllDicToApplication (ServletContext servletContext) {
		System.out.println("****************加载所有的数据字典到application*************");
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		//先获取所有父级字典
		DBObject args = new BasicDBObject();
		args.put("DIC_PARENT_ID", 0);
		DBCursor dicPCur = dicDbc.find(args);
		Map<Object,String> dicMap=new HashMap<Object,String>();
		Map<Object,  List<DBObject>> childMap=new HashMap<Object,  List<DBObject>>();
		List<DBObject> dicPList = new ArrayList<DBObject>();
		if (dicPCur!=null && dicPCur.length()>0) {
			dicPList = dicPCur.toArray();
			for (DBObject dicP : dicPList) {
				//获取对应的子集字典
				args.put("DIC_PARENT_ID", dicP.get("DIC_ID"));
				DBCursor dicdCur = dicDbc.find(args).sort(new BasicDBObject("DIC_ORDER",1));
				if (dicdCur!=null && dicdCur.length()>0) {
					List<DBObject> dicdList = dicdCur.toArray();
					for (DBObject dicd : dicdList) {
						dicd.put("name", dicd.get("DIC_NAME"));
						dicd.put("value", dicd.get("RG_DIC_ID"));
						dicMap.put(dicP.get("DIC_ID")+"_"+dicd.get("RG_DIC_ID"), dicd.get("DIC_NAME")+"");
					}
					childMap.put(dicP.get("DIC_ID")+"", dicdList);
				}
			}
		}
		List<DBObject> xqList = new ArrayList<DBObject>();
		int[] xqId = new int[]{1,2,3,4,5,6,7};
		String[] xqName = new String[]{"星期一","星期二","星期三","星期四","星期五","星期六","星期天"};
		for (int i=0;i<xqId.length;i++) {
			DBObject tmp = new BasicDBObject();
			dicMap.put("XQ_"+xqId, xqName[i]);
			tmp.put("name", xqName[i]);
			tmp.put("value", xqId[i]);
			xqList.add(tmp);
		}
		childMap.put("XQ", xqList);
		servletContext.setAttribute("dicParent", dicPList);
		servletContext.setAttribute("chidDic", dicMap);
		servletContext.setAttribute("childDicTree", childMap);
	}
	
	/**
     * 
     * refreshLoginSql  {刷新登录sql}
     * 
     * @Description {刷新登录sql} void
     * @see
     */
    public static void refreshLoginSql () {
    	System.out.println("****************刷新登陆sql*************");
    	DB db = MongoDbFileUtil.getDb();
    	DBCollection tabDbc = db.getCollection("table");
    	DBCollection fieldDbc = db.getCollection("field");
    	DBCursor tabCur = tabDbc.find(new BasicDBObject("tableType", 2));
    	//判断用户表是否存在
    	if (tabCur == null) {
    		return;
    	}
    	ShardedJedis jds = RedisUtil.getJedis();
    	List<DBObject> tabDboList = tabCur.toArray();
    	for (DBObject tabDbo : tabDboList) {
	    	DBObject args = new BasicDBObject();
	    	args.put("tableId", tabDbo.get("tableId"));
	    	//获取用户名字段信息
	    	args.put("fieldType", 18);
	    	DBObject uNameDbo = fieldDbc.findOne(args);
	    	if(uNameDbo == null) {
	    		//如果账号字段为空 则 获取手机号作为登录账号
	    		args.put("fieldType", 5);
		    	uNameDbo = fieldDbc.findOne(args);
		    	if(uNameDbo == null) {
		    		//如果手机号为空则 获取身份证号为登录账号
		    		args.put("fieldType", 3);
			    	uNameDbo = fieldDbc.findOne(args);
		    	}
	    	}
	    	//获取密码字段信息
	    	args.put("fieldType", 19);
	    	DBObject psDbo = fieldDbc.findOne(args);
	    	//获取名称字段信息
	    	args.put("fieldType", 8);
	    	DBObject nameDbo = fieldDbc.findOne(args);
	    	
	    	
	    	//判断两个字段是否都存在
	    	if (uNameDbo==null || psDbo==null || nameDbo == null) {
	    		return;
	    	}
	    	nameDbo.put("zdyName", "USER_NAME");
	    	//获取人员表所有的普通字段
	    	DBObject args2 = new BasicDBObject();
	    	args2.put("tableId", tabDbo.get("tableId"));
	    	args2.put("fieldType", new BasicDBObject("$nin", new Integer[]{13,14,19,22,23,24,25}));
	    	List<DBObject> fieldList = fieldDbc.find(args2).toArray();
	    	//拼接登录查询sql
	    	String loginSql = SqlUtils.appendListSql(fieldList, tabDbc, fieldDbc, MyNumberUtils.toInt(tabDbo.get("tableId")));
	    	if (MyStringUtils.notBlank(loginSql)) {
	    		StringBuffer loginWhere = new StringBuffer();
	    		loginWhere.append(" AND T_0."+uNameDbo.get("fieldName")+"='#{USER_NAME}' AND T_0."+psDbo.get("fieldName")+"=#{passWord}");
	    		loginSql = loginSql.replace("${WHERE}", loginWhere.toString());
	    		jds.set("loginSql", loginSql);
	    	}
    	}
    	RedisUtil.returnJedis(jds);
    }
}
