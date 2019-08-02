package com.mbfw.controller.system.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.ShardedJedis;
import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
import com.mbfw.createPage.PageUtils;
import com.mbfw.service.pageSet.PageSetService;
import com.mbfw.service.system.basic.ObjectService;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.PageData;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Controller
public class PageSetController extends BaseController{
	
	@Resource(name = "pageSetService")
	public PageSetService pageSetService;
	
	/**
	 * 去栏目列表
	 * @return
	 */
	@RequestMapping(value = "/page_toList")
	public ModelAndView toPageList () {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/page/page-list");
		mv.addObject("pd", pd);
		return mv;
	}
	
	/**
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/page_ajaxList")
	public @ResponseBody Object ajaxList()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("page");
		String pageId = getParam("pageId");
		String pageName = getParam("pageName");
		String tableId = getParam("tableId");
		String tableCnName = getParam("tableCnName");
		DBObject args = new BasicDBObject();
		if (MyStringUtils.notBlank(pageId)) {
			args.put("pageId", pageId);
		}
		if (MyStringUtils.notBlank(pageName)) {
			Pattern pattern = Pattern.compile(".*"+pageName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("pageName", pattern);
		}
		if (MyStringUtils.notBlank(tableId)) {
			args.put("tableId", pageId);
		}
		if (MyStringUtils.notBlank(tableCnName)) {
			Pattern pattern = Pattern.compile(".*"+tableCnName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("tableCnName", pattern);
		}
		DBCursor tableCur = menuDbc.find(args);
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "");
		tabMap.put("count", tableCur.count());
		int page = MyNumberUtils.toInt(getParam("page"));
		int limit = MyNumberUtils.toInt(getParam("limit"));
		tableCur = tableCur.skip((page-1)*limit).limit(limit);
		List<DBObject> list = tableCur.toArray();
		tabMap.put("data", list);
		return JSON.toJSON(tabMap);
	}
	
	/**
	 * 页面刷新
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/page_refReshPage")
	public @ResponseBody int refReshPage() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		String pageIds = getParam("pageId");
		String[] pageIdArr = pageIds.split(",");
		ShardedJedis jds = RedisUtil.getJedis();
		for (String pageId : pageIdArr) {
			DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", MyNumberUtils.toInt(pageId)));
			//刷新页面重新生成
			PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"), MyNumberUtils.toInt(pageDbo.get("tableId")), MyNumberUtils.toInt(pageDbo.get("pageType")));
		}
		RedisUtil.returnJedis(jds);
		return 1;
	}
	
	/**
	 * 页面删除
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/page_delete")
	public @ResponseBody int deletePage() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		String pageIds = getParam("pageId");
		String[] pageIdArr = pageIds.split(",");
		ShardedJedis jds = RedisUtil.getJedis();
		for (String pageId : pageIdArr) {
			DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", MyNumberUtils.toInt(pageId)));
			pageDbc.remove(pageDbo);
			String domId = pageDbo.get("tableId")+"_"+pageId;
			jds.del(domId+"_page");
			jds.del(domId+"_sql");
		}
		RedisUtil.returnJedis(jds);
		return 1;
	}
	
	/**
	 * 去页面内容设置
	 * @return
	 */
	@RequestMapping(value = "/page_contentSet")
	public ModelAndView toPageContentSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		mv.addObject("fixedRowNumber", MyNumberUtils.toInt(pageDbo.get("fixedRowNumber")));
		List<DBObject> showFieldList = (List<DBObject>)pageDbo.get("showFieldList");//显示字段
		mv.addObject("showFieldList", showFieldList);
		//获取对象所有字段
		DBObject args = new BasicDBObject();
		args.put("tableId", tableId);
		String[] noShowType =  "0,13,14,19".split(",");
		List<Integer> noTypeList = new ArrayList<Integer>();
		for (String ft : noShowType) {
			noTypeList.add(MyNumberUtils.toInt(ft));
		}
		args.put("fieldType", new BasicDBObject("$nin", noTypeList));
		DBCursor fieldCur = fieldDbc.find(args);
		mv.addObject("tableFieldList", fieldCur.toArray());
		mv.setViewName("system/page/pageSet/page-contentSet");
		return mv;
	}
	
	/**
	 * 去页面搜索条件设置
	 * @return
	 */
	@RequestMapping(value = "/page_searchSet")
	public ModelAndView toPageSearchSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		List<DBObject> searchFieldList = (List<DBObject>)pageDbo.get("searchFieldList");//搜索字段
		mv.addObject("searchFieldList", searchFieldList);
		//获取对象所有字段
		DBObject args = new BasicDBObject();
		args.put("tableId", tableId);
		String[] noShowType =  "0,13,14,19".split(",");
		List<Integer> noTypeList = new ArrayList<Integer>();
		for (String ft : noShowType) {
			noTypeList.add(MyNumberUtils.toInt(ft));
		}
		args.put("fieldType", new BasicDBObject("$nin", noTypeList));
		DBCursor fieldCur = fieldDbc.find(args);
		mv.addObject("tableFieldList", fieldCur.toArray());
		mv.addObject("dicParentList", getDicParentList());
		mv.setViewName("system/page/pageSet/page-searchSet");
		return mv;
	}
	/**
	 * 去页面sql设置
	 * @return
	 */
	@RequestMapping(value = "/page_sqlSet")
	public ModelAndView toPageSqlSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		mv.addObject("sql", pageDbo.get("sql"));
		mv.addObject("zdySql", pageDbo.get("zdySql"));
		mv.setViewName("system/page/pageSet/page-sqlSet");
		return mv;
	}
	
	/**
	 * 去页面html设置
	 * @return
	 */
	@RequestMapping(value = "/page_htmlSet")
	public ModelAndView toPageHtmlSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		mv.addObject("pageHtml", pageDbo.get("pageHtml"));
		mv.setViewName("system/page/pageSet/page-htmlSet");
		return mv;
	}
	
	/**
	 * 去列表数据权限设置
	 * @return
	 */
	@RequestMapping(value = "/page_limitSet")
	public ModelAndView toPageLimitSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection limitDbc = db.getCollection("limitSet");
		//获取page对象
		DBCursor limitCur = limitDbc.find(new BasicDBObject("pageId", pageId));
		mv.addObject("limitList", limitCur.toArray());
		mv.setViewName("system/page/pageSet/page-limitSet");
		return mv;
	}
	
	/**
	 * 去列表按钮设置
	 * @return
	 */
	@RequestMapping(value = "/page_buttonSet")
	public ModelAndView toPageButtonSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		List<DBObject> buttonSet = (List<DBObject>)pageDbo.get("buttonSet");
		mv.addObject("buttonSet", MyCollectionUtils.notEmpty(buttonSet)?buttonSet:new ArrayList<DBObject>());
		
		//获取关联表信息
		DBCollection fieldDbc = db.getCollection("field");
		DBObject args = new BasicDBObject();
		args.put("tableId", tableId);
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(12);
		typeList.add(14);
		typeList.add(16);
		args.put("fieldType", new BasicDBObject("$in", typeList));
		DBCursor tabCur = fieldDbc.find(args);
		List<DBObject> rebList = tabCur!=null?tabCur.toArray():new ArrayList<DBObject>();
		mv.addObject("relationTables", rebList);
		mv.addObject("delReSize", rebList.size());
		mv.setViewName("system/page/pageSet/page-buttonSet");
		return mv;
	}
	
	/**
	 * 去列表数据按钮设置
	 * @return
	 */
	@RequestMapping(value = "/page_listButtonSet")
	public ModelAndView toPageListButtonSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		List<DBObject> listButtonSet = (List<DBObject>)pageDbo.get("listButtonSet");
		mv.addObject("listButtonSet", MyCollectionUtils.notEmpty(listButtonSet)?listButtonSet:new ArrayList<DBObject>());
		
		//获取关联表信息
		DBCollection fieldDbc = db.getCollection("field");
		DBObject args = new BasicDBObject();
		args.put("tableId", tableId);
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(12);
		typeList.add(14);
		typeList.add(16);
		args.put("fieldType", new BasicDBObject("$in", typeList));
		DBCursor tabCur = fieldDbc.find(args);
		List<DBObject> rebList = tabCur!=null?tabCur.toArray():new ArrayList<DBObject>();
		mv.addObject("relationTables", rebList);
		mv.addObject("delReSize", rebList.size());
		mv.setViewName("system/page/pageSet/page-listButtonSet");
		return mv;
	}
	
	/**
	 * 修改内容设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveContentSet")
	public @ResponseBody Integer saveContentSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","fixedRowNumber","fieldId","fieldCnName","zdyName","zdyJs","ifShow","ifOrder","width"};
		Class[] classType = {Integer.class,Integer.class,Integer.class,Integer[].class,String[].class,String[].class,String[].class,Integer[].class,Integer[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveContentSet(args);
	}
	
	/**
	 * 修改sql
	 * @return
	 */
	@RequestMapping(value = "/page_saveSqlSet")
	public @ResponseBody Integer saveSqlSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","lastSql","zdySql"};
		Class[] classType = {Integer.class,Integer.class,String.class,String.class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveSqlSet(args);
	}
	
	/**
	 * 权限设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveLimitSet")
	public @ResponseBody Integer saveLimitSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","removeIds","limitId","limitName","limitSql"};
		Class[] classType = {Integer.class,Integer.class,String.class,Long[].class,String[].class,String[].class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveLimitSet(args);
	}
	

	/**
	 * 搜索条件设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveSearchSet")
	public @ResponseBody Integer saveSearchSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","fieldId","fieldCnName","zdyName","searchType","dateType","dicParentId"};
		Class[] classType = {Integer.class,Integer.class,Integer[].class,String[].class,String[].class,Integer[].class,String[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveSearchSet(args);
	}
	
	/**
	 * html设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveHtmlSet")
	public @ResponseBody Integer saveHtmlSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","pageHtml"};
		Class[] classType = {Integer.class,Integer.class,String.class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveHtmlSet(args);
	}
	
	/**
	 * button设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveButtonSet")
	public @ResponseBody Integer saveButtonSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","buttonId","buttonType","buttonName","buttonPageId","ifDisRole"
				,"openStyle","pageWidth","pageHeight","pageUrl","urlParam","btnBeforeJs","btnAfterJs","delReSize","delReFieldId","delTableId","deleteOType"};
		Class[] classType = {Integer.class,Integer.class,Long[].class,Integer[].class,String[].class,Integer[].class,Integer[].class
				,Integer[].class,Integer[].class,Integer[].class,String[].class,String[].class,String[].class,String[].class,Integer.class,Integer[].class,Integer[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveButtonSet(args);
	}
	
	/**
	 * button设置
	 * @return
	 */
	@RequestMapping(value = "/page_saveListButtonSet")
	public @ResponseBody Integer saveListButtonSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","pageId","buttonId","buttonType","buttonName","buttonPageId","ifDisRole"
				,"openStyle","pageWidth","pageHeight","pageUrl","urlParam","btnBeforeJs","btnAfterJs","delReSize","delReFieldId","delTableId","deleteOType"};
		Class[] classType = {Integer.class,Integer.class,Long[].class,Integer[].class,String[].class,Integer[].class,Integer[].class
				,Integer[].class,Integer[].class,Integer[].class,String[].class,String[].class,String[].class,String[].class,Integer.class,Integer[].class,Integer[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
				
		return pageSetService.saveListButtonSet(args);
	}
	
	/**
	 * 去添加页面设置
	 * @return
	 */
	@RequestMapping(value = "/page_addPageSet")
	public ModelAndView toAddPageSet () {
		ModelAndView mv = this.getModelAndView();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		int pageId = MyNumberUtils.toInt(getParam("pageId"));//页面id
		mv.addObject("tableId", tableId);
		mv.addObject("pageId", pageId);
		mv.addObject("buttonType", getParam("buttonType"));
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		//获取page对象
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", pageId));
		List<DBObject> showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
		List<DBObject> hideFieldList = (List<DBObject>)pageDbo.get("hideFieldList");
		mv.addObject("showFieldList", MyCollectionUtils.notEmpty(showFieldList)?showFieldList:new ArrayList<DBObject>());
		mv.addObject("hideFieldList", MyCollectionUtils.notEmpty(hideFieldList)?hideFieldList:new ArrayList<DBObject>());
		//获取所有的字段
		String[] noShowType =  "0,22,23,24,25".split(",");
		List<Integer> noTypeList = new ArrayList<Integer>();
		for (String ft : noShowType) {
			noTypeList.add(MyNumberUtils.toInt(ft));
		}
		DBCollection fieldDbc = db.getCollection("field");
		DBObject args = new BasicDBObject();
		args.put("tableId", tableId);
		args.put("fieldType", new BasicDBObject("$nin", noTypeList));
		DBCursor fields = fieldDbc.find(args);
		mv.addObject("fieldList", fields!=null?fields.toArray():new ArrayList<DBObject>());
		mv.setViewName("system/page/pageSet/page-addPageSet");
		return mv;
	}
	
	/**
	 * 保存添加页面设置
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/page_saveAddPageSet")
	public @ResponseBody Long saveAddPageSet () {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"mainTableId","pageId","buttonType","pageName","customerJs","developScript","isChild"
				,"fieldId","fieldName","fieldCnName","tableId","fieldType","zdyName"
				,"ifMust","ifUpdate","ifShowDefVal","ifNullShowDefVal","defaultShow"
				,"defaultSelect","defaultVal","inputStyle","dateStyle","relationTableId"
				,"relationTableCnName","addStyle","dataMaxNum","ifCanAdd","ifOnlyAdd"
				,"dialogField","chooseType","dialogPage","zdyVerify","fieldDescript"
				,"childSelectSql","childZdySelectSql","childPageId","showNum"
				};
		Class[] classType = {Integer.class,Integer.class,Integer.class,String.class,String.class,String.class,Integer.class
				,Integer[].class,String[].class,String[].class,Integer[].class,Integer[].class,String[].class
				,Integer[].class,Integer[].class,Integer[].class,Integer[].class,String[].class
				,String[].class,String[].class,Integer[].class,String[].class,Integer[].class
				,String[].class,Integer[].class,Integer[].class,Integer[].class,Integer[].class
				,String[].class,Integer[].class,Integer[].class,String[].class,String[].class
				,String[].class,String[].class,Integer[].class,String.class
		};
		getAllParam(args, paramName, paramName, classType, true);
				
		try {
			return pageSetService.saveAddPageSet(args);
		} catch (Exception e) {
			e.printStackTrace();
			return 0l;
		}
	}

	
	/**
	 * 根据页面类型获取
	 * @return
	 */
	@RequestMapping(value = "/page_getAllPage")
	public @ResponseBody Object getAllPageByType () {
		DBObject args = new BasicDBObject();
		int tableId = MyNumberUtils.toInt(getParam("tableId"));//对象id
		if (tableId>0) {
			args.put("tableId", tableId);
		}
		int pageType = MyNumberUtils.toInt(getParam("pageType"));//页面类型
		if (pageType==0) {
			int buttonType = MyNumberUtils.toInt(getParam("buttonType"));//按钮类型
			if (buttonType==1 || buttonType==8) {//添加
				pageType=2;
			} else if (buttonType==9) {//修改
				pageType=3;
			} else if (buttonType==2) {//批量修改
				pageType=4;
			} else {
				pageType=-1;
			}
		}
		if (pageType>0) {
			args.put("pageType", pageType);
		}
		DBCollection pageDbc = MongoDbFileUtil.getDbcCollection("page");
		DBCursor pageCur = pageDbc.find(args);
		List<Map<String, Object>> tmpPageList = new ArrayList<Map<String,Object>>();
		if (pageCur!=null) {
			List<DBObject> pageList = pageCur.toArray();
			for (DBObject page : pageList) {
				Map<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("name", page.get("pageName"));
				tmp.put("value", page.get("pageId"));
				tmp.put("tableId", page.get("tableId"));
				tmpPageList.add(tmp);
			}
		}
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "success");
		tabMap.put("data", tmpPageList);
		return JSON.toJSON(tabMap);
	}
	
	/**
	 * 根据页面类型获取
	 * @return
	 */
	@RequestMapping(value = "/page_getDicChildren")
	public @ResponseBody Object getDicChildren () {
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "success");
		System.out.println("==page_getDicChildren="+getParam("dicParent"));
		List<DBObject> dicList = getAllDicChild(getParam("dicParent"));
		tabMap.put("data", dicList);
		return JSON.toJSON(tabMap);
	}
	
	/**
	 * 获取子页面设置
	 * @return
	 */
	@RequestMapping(value = "/page_getChildPageSet")
	public @ResponseBody Object getChildPageSet () {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		int childPageId = MyNumberUtils.toInt(getParam("childPageId"));//子页面id
		int addStyle = MyNumberUtils.toInt(getParam("addStyle"));//添加形式
		int mainTableId = MyNumberUtils.toInt(getParam("mainTableId"));//主表对象
		int wjTableId = MyNumberUtils.toInt(getParam("wjTableId"));//外键对象
		int ifInit = MyNumberUtils.toInt(getParam("ifInit"));//是否是初始化
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		if (ifInit==0) {//判断
			List<DBObject> wjFieldList = new ArrayList<DBObject>();
			//获取当前对象的外键字段组
			DBObject args = new BasicDBObject();
			args.put("tableId", MyNumberUtils.toInt(getParam("tableId")));
			args.put("fieldType",15);
			if (MyNumberUtils.toInt(getParam("tableId"))!=mainTableId) {
				args.put("relationTableId", new BasicDBObject("$ne", mainTableId));
			}
			DBCursor wjCur = fieldDbc.find(args);
			wjFieldList = wjCur!=null?wjCur.toArray():new ArrayList<DBObject>();
			if (MyCollectionUtils.notEmpty(wjFieldList) && wjTableId==0 && addStyle!=1) {//赋值第一个外键字段
				wjTableId = MyNumberUtils.toInt(wjFieldList.get(0).get("relationTableId"));
			}
			fieldMap.put("wjFieldList", wjFieldList);
			
			List<DBObject> showFieldList = new ArrayList<DBObject>();
			List<DBObject> hideFieldList = new ArrayList<DBObject>();
			if (childPageId>0) {//初始化
				DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", childPageId));
				if (pageDbo!=null) {
					if (pageDbo.get("showFieldList")!=null) {
						showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
					}
					if (pageDbo.get("hideFieldList")!=null) {
						hideFieldList = (List<DBObject>)pageDbo.get("hideFieldList");
					}
				}
			}
			fieldMap.put("showFieldList", showFieldList);
			fieldMap.put("hideFieldList", hideFieldList);
		}
		
		//获取当前表字段
		DBObject args = new BasicDBObject();
		args.put("tableId", MyNumberUtils.toInt(getParam("tableId")));
		//获取所有的字段
		String[] noShowType =  "0,22,23,24,25".split(",");
		List<Integer> noTypeList = new ArrayList<Integer>();
		for (String ft : noShowType) {
			noTypeList.add(MyNumberUtils.toInt(ft));
		}
		args.put("fieldType", new BasicDBObject("$nin", noTypeList));
		List<Integer> noRtabList = new ArrayList<Integer>();
		if (mainTableId!=MyNumberUtils.toInt(getParam("tableId"))) {
			noRtabList.add(mainTableId);
		}
		if (wjTableId>0 && addStyle!=1) {
			noRtabList.add(wjTableId);
		}
		if (MyCollectionUtils.notEmpty(noRtabList) ) {
			args.put("relationTableId", new BasicDBObject("$nin", noRtabList));
		}
		//获取当前对象的非外键字段数据
		DBCursor fieldCur = fieldDbc.find(args);
		List<DBObject> fieldList = fieldCur!=null?fieldCur.toArray():new ArrayList<DBObject>();
		
		//获取外键表字段
		if (addStyle!=1 && wjTableId>0) {//非点击+添加则获取外键字段关联的字段数据(主要用于子页面显示使用)		
			args = new BasicDBObject();
			args.put("tableId", wjTableId);
			//获取所有的字段
			String[] showType =  "1,3,4,5,6,7,8,9,10".split(",");
			List<Integer> typeList = new ArrayList<Integer>();
			for (String ft : showType) {
				typeList.add(MyNumberUtils.toInt(ft));
			}
			args.put("fieldType", new BasicDBObject("$in", typeList));
			fieldCur = fieldDbc.find(args);
			if (fieldCur!=null) {
				fieldList.addAll(fieldCur.toArray());
			}
		}
		fieldMap.put("fieldList", fieldList);
		return JSON.toJSON(fieldMap);
	}
}
