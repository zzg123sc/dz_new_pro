package com.mbfw.createPage;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.ShardedJedis;

import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PageUtils {
	
	/**
	 * 刷新页面
	 * @param pageId
	 */
	public static void refreashPage (Object pageId) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId",MyNumberUtils.toInt(pageId)));
		if (pageDbo!=null) {
			createPage(pageDbo, null,MyNumberUtils.toInt(pageDbo.get("tableId")), MyNumberUtils.toInt(pageDbo.get("pageType")));
		}
	}
	
	/**
	 * 生成页面
	 * @param pageDbo
	 * @param tableId
	 */
	public static Long createPage (DBObject pageDbo,String pageName, Integer tableId, Integer pageType) {
		if (pageType==1) {//列表
			return appendListPage(pageDbo,pageName ,tableId, pageType);
		} else if (pageType==2) {//添加
			return appendAdddPage(pageDbo,pageName, tableId, pageType);
		} else if (pageType==3) {//修改
			
		} else if (pageType==4) {//批量修改
			
		} else if (pageType==5) {//弹层
			return appendListPage(pageDbo,pageName ,tableId, pageType);
		} else if (pageType==6) {//下拉树
			
		} else if (pageType==7) {//查看
			
		}
		return 1l;
	}
	
	
	/**
	 * 拼接列表页面
	 */
	public static Long appendListPage (DBObject pageDbo ,String pageName , Integer tableId, Integer pageType) {
		//拼接搜索条件
		List<DBObject> searchList = null;
		//列表显示字段
		List<DBObject> showFieldList = null;
		//自定义sql
		String zdySql = "";
		//自定义html
		String zdyPageHtml = "";
		Long  pageId = null;
		DB db = MongoDbFileUtil.getDb();
		DBCollection fieldDbc = db.getCollection("field");
		DBCollection tableDbc = db.getCollection("table");
		DBCollection pageDbc = db.getCollection("page");
		if (pageDbo!=null) {
			searchList = (List<DBObject>)pageDbo.get("searchFieldList");
			showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
			pageId = MyNumberUtils.toLong(pageDbo.get("pageId"),0);
			zdySql = (String)pageDbo.get("zdySql");
			zdyPageHtml = (String)pageDbo.get("zdyPageHtml");
		} else {
			pageDbo = new BasicDBObject();
			pageId = MongoDbFileUtil.getIncr("page");
			pageDbo.put("pageId", pageId);
			pageDbo.put("tableId", tableId);
			DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId", tableId));
			pageName = MyStringUtils.notBlank(pageName)?pageName:(tableDbo.get("tableCnName")+"列表");
			pageDbo.put("pageName", pageName);
			String[] noShowType =  "0,12,13,14,16,19,21,22,24,25".split(",");
			List<Integer> noTypeList = new ArrayList<Integer>();
			for (String ft : noShowType) {
				noTypeList.add(MyNumberUtils.toInt(ft));
			}
			//查询搜索条件字段
			DBObject args = new BasicDBObject();
			args.put("tableId", tableId);
			args.put("fieldType", new BasicDBObject("$nin", noTypeList));
			DBCursor fieldCur = fieldDbc.find(args);
			searchList = fieldCur.toArray();
			pageDbo.put("pageType", pageType);
			
			noShowType =  "0,13,14,19".split(",");
			noTypeList = new ArrayList<Integer>();
			for (String ft : noShowType) {
				noTypeList.add(MyNumberUtils.toInt(ft));
			}
			args.put("fieldType", new BasicDBObject("$nin", noTypeList));
			fieldCur = fieldDbc.find(args);
			showFieldList = fieldCur.toArray();
		}
		
		String domId = tableId +"_"+pageId;
		if (MyStringUtils.isBlank(zdyPageHtml)) {
			StringBuffer hederBuffer = new StringBuffer();//头部拼接
			if (pageType==1){
				AppendListPageUtils.appendHeader(hederBuffer);
			}
			StringBuffer bodyBuffer = new StringBuffer();//内容拼接
			bodyBuffer.append("<body layadmin-themealias=\"default\">");
			AppendListPageUtils.appendPageHtml(pageDbo,bodyBuffer, searchList, fieldDbc, domId);
			
			bodyBuffer.append("<div id='operaDiv'></div>");
			bodyBuffer.append("</body>");
			
			StringBuffer jsBuffer = new StringBuffer();//js拼接
			if (pageType==1){
				AppendListPageUtils.appendBasicJs(jsBuffer);
			}
			AppendListPageUtils.appendPageJs(pageDbo,jsBuffer, showFieldList,tableId,pageId);
			StringBuffer holePage = new StringBuffer();
			holePage.append(hederBuffer);
			holePage.append(bodyBuffer);
			holePage.append(jsBuffer);
			zdyPageHtml = holePage.toString();
			pageDbo.put("pageHtml", zdyPageHtml);
		}
		//获取列表sql
		pageDbo.put("searchFieldList", searchList);
		pageDbo.put("showFieldList", showFieldList);
		if (MyStringUtils.isBlank(zdySql)) {
			List<DBObject>  tmpList = new ArrayList<DBObject>();
			List<Integer> fidList = new ArrayList<Integer>();
			for (DBObject field : showFieldList) {
				fidList.add(MyNumberUtils.toInt(field.get("fieldId")));
				tmpList.add(field);
			}
			if (MyCollectionUtils.notEmpty(searchList)) {
				for (DBObject field : searchList) {
					//将搜索条件字段添加到查询列里
					if(!fidList.contains(MyNumberUtils.toInt(field.get("fieldId")))){
						tmpList.add(field);
					}
				}
			}
			zdySql = SqlUtils.appendListSql(tmpList, tableDbc, fieldDbc, tableId);
			pageDbo.put("sql", zdySql);
		}
		pageDbc.save(pageDbo);
		//将页面放到redis暂存
		ShardedJedis jds = RedisUtil.getJedis();
		jds.set(domId+"_page", zdyPageHtml);
		jds.set(domId+"_sql", zdySql);
		RedisUtil.returnJedis(jds);
		return pageId;
	}
	
	/**
	 * 拼接添加修改页面
	 */
	public static long appendAdddPage (DBObject pageDbo , String pageName, Integer tableId, Integer pageType) {
		Long  pageId = null;
		int isChild = pageDbo==null?0:MyNumberUtils.toInt(pageDbo.get("isChild"));//是否是子页面
		DB db = MongoDbFileUtil.getDb();
		DBCollection fieldDbc = db.getCollection("field");
		DBCollection tableDbc = db.getCollection("table");
		DBCollection pageDbc = db.getCollection("page");
		List<DBObject> addFieldList = new ArrayList<DBObject>();
		if (pageDbo!=null) {
			pageId = MyNumberUtils.toLong(pageDbo.get("pageId"),0);
			//显示字段
			List<DBObject> showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
			if (MyCollectionUtils.notEmpty(showFieldList)) {
				addFieldList.addAll(showFieldList);
			}
			//隐藏字段
			List<DBObject> hideFieldList = (List<DBObject>)pageDbo.get("hideFieldList");
			if (MyCollectionUtils.notEmpty(hideFieldList)) {
				addFieldList.addAll(hideFieldList);
			}
		} else {
			pageDbo = new BasicDBObject();
			pageId = MongoDbFileUtil.getIncr("page");
			pageDbo.put("pageId", pageId);
			pageDbo.put("tableId", tableId);
			DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId", tableId));
			pageName = MyStringUtils.notBlank(pageName)?pageName:(tableDbo.get("tableCnName")+"--");
			if (pageType==2) {
				pageName += "添加";
			} else if (pageType==3) {
				pageName += "修改";
			} if (pageType==7) {
				pageName += "查看";
			}
			pageDbo.put("pageName", pageName);
			pageDbo.put("addType", 0);//默认普通添加
			String[] noShowType =  "0,11,12,13,14,15,16,21,22,23,24,25".split(",");
			List<Integer> noTypeList = new ArrayList<Integer>();
			for (String ft : noShowType) {
				noTypeList.add(MyNumberUtils.toInt(ft));
			}
			//查询搜索条件字段
			DBObject args = new BasicDBObject();
			args.put("tableId", tableId);
			args.put("fieldType", new BasicDBObject("$nin", noTypeList));
			DBCursor fieldCur = fieldDbc.find(args);
			addFieldList = fieldCur.toArray();
			pageDbo.put("pageType", pageType);
			pageDbo.put("showFieldList", addFieldList);
		}
		String domId = tableId +"_"+pageId;
		StringBuffer jsBuffer = new StringBuffer();
		ShardedJedis jds = RedisUtil.getJedis();
		//删除当前层级关联的子dom信息
		jds.del(domId+"_childDom");
		List<DBObject> parentRelationField = (List<DBObject>)pageDbo.get("parentRelationField");
		if (MyCollectionUtils.notEmpty(parentRelationField)) {
			addFieldList.addAll(parentRelationField);
		}
		//生成添加sql
		String insertSql = SqlUtils.appendInsertSql(tableDbc, fieldDbc, addFieldList, tableId, pageId);
		
		//生成修改sql
		String updateSql = "";
		if (pageType==3 || pageType==7) {
			updateSql = SqlUtils.appendUpdateSql(tableDbc, fieldDbc, addFieldList, tableId, pageId);
			pageDbo.put("updateSql", updateSql);
			jds.set(domId+"_updateSql", updateSql);
		}
		
		if (isChild==1) {
			//刷新页面内容
			StringBuffer thBuffer = new StringBuffer();
			StringBuffer trBuffer = new StringBuffer();
			AppendAddPageUtils.appendChildBodyHtml(pageDbo, domId, pageType, thBuffer, trBuffer, jsBuffer);
			pageDbo.put("thBody", thBuffer.toString());
			pageDbo.put("trBody", trBuffer.toString());
			pageDbo.put("jsBody", jsBuffer.toString());
			
			//存放用于修改递归使用
			jds.set(pageId+"_thBody", thBuffer.toString());
			jds.set(pageId+"_trBody", trBuffer.toString());
			jds.set(pageId+"_jsBody", jsBuffer.toString());
		} else {
			//刷新页面内容
			StringBuffer bodyBuffer = new StringBuffer();
			AppendListPageUtils.appendHeader(bodyBuffer);
			bodyBuffer.append("<body layadmin-themealias=\"default\">");
			AppendAddPageUtils.appendAddPageHtml(pageDbo,domId, pageType, bodyBuffer, jsBuffer);
			AppendListPageUtils.appendBasicJs(bodyBuffer);
			AppendAddPageUtils.appendAddPageJs(pageDbo, domId,bodyBuffer);
			bodyBuffer.append(jsBuffer);
			String customerJs = (String)pageDbo.get("customerJs");
			if (MyStringUtils.notBlank(customerJs)) {
				bodyBuffer.append(customerJs);
			}
			bodyBuffer.append("</script>");
			bodyBuffer.append("<div id='operaDiv'></div>");
			bodyBuffer.append("</body>");
			pageDbo.put("paegHtml", bodyBuffer.toString());
			jds.set(domId+"_page",  bodyBuffer.toString());
		}
		
		System.out.println("==insertSql="+insertSql);
		pageDbo.put("inserSql", insertSql);
		jds.set(domId+"_insertSql", insertSql);
		RedisUtil.returnJedis(jds);
		pageDbc.save(pageDbo);
		return pageId;
	}
	
	public static void main(String[] args) {
		System.out.println("=======");
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId",37));
		createPage(pageDbo, null,2, 2);
	}

}
