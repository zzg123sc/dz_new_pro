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
import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
import com.mbfw.createPage.PageUtils;
import com.mbfw.service.system.basic.ObjectService;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.PageData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;



/*
 * 总入口
 */
@Controller
public class objectController extends BaseController {

	@Resource(name = "objectService")
	public ObjectService objectService;
	/**
	 * 对象列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_toList")
	public ModelAndView toList() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.addObject("allObjTypes", JSON.toJSON(getAllObjectType()));
		mv.setViewName("system/objectManage/obj-list");
		return mv;
	}
	
	/**
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_ajaxList")
	public @ResponseBody Object ajaxList()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("table");
		String tableCnName = getParam("tableCnName");
		String tableName = getParam("tableName");
		String tableType = getParam("tableType");
		DBObject args = new BasicDBObject();
		if (MyStringUtils.notBlank(tableCnName)) {
			Pattern pattern = Pattern.compile(".*"+tableCnName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("tableCnName", pattern);
		}
		if (MyStringUtils.notBlank(tableName)) {
			Pattern pattern = Pattern.compile(".*"+tableName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("tableName", pattern);
		}
		if (MyStringUtils.notBlank(tableType)) {
			args.put("tableType", MyNumberUtils.toInt(tableType));
		}
		
		DBCursor tableCur = tableDbc.find(args).sort(new BasicDBObject("tableId",-1));
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "");
		tabMap.put("count", tableCur.count());
		int page = MyNumberUtils.toInt(getParam("page"));
		int limit = MyNumberUtils.toInt(getParam("limit"));
		tableCur = tableCur.skip((page-1)*limit).limit(limit);
		List<DBObject> list = tableCur.toArray();
		for (DBObject table : list) {
			table.put("_id", table.get("_id").toString());
		}
		tabMap.put("data", list);
		return JSON.toJSON(tabMap);
	}
	
	/**
	 * 对象列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_toLookField")
	public ModelAndView toLookField() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.addObject("allFieldTypes", JSON.toJSON(getAllFieldType("")));
		mv.addObject("tableId",getParam("tableId"));
		mv.setViewName("system/objectManage/field-list");
		return mv;
	}
	
	/**
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_getFields")
	public @ResponseBody Object getFieldList()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("field");
		DBCursor tableCur = tableDbc.find(new BasicDBObject("tableId",MyNumberUtils.toInt(getParam("tableId"))));
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "");
		tabMap.put("count", tableCur.count());
		int page = MyNumberUtils.toInt(getParam("page"));
		int limit = MyNumberUtils.toInt(getParam("limit"));
		tableCur = tableCur.skip((page-1)*limit).limit(limit);
		List<DBObject> list = tableCur.toArray();
		for (DBObject table : list) {
			table.put("_id", table.get("_id").toString());
		}
		tabMap.put("data", list);
		return JSON.toJSON(tabMap);
	}
	
	/**
	 * 对象添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_toAdd")
	public ModelAndView toAddobject() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.addObject("allObjTypes", JSON.toJSON(getAllObjectType()));
		mv.addObject("allFieldTypes", JSON.toJSON(getAllFieldType("0,12,14,16,21,22,23,24,25")));
		mv.setViewName("system/objectManage/obj-add");
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("table");
		DBCursor tableCur = tableDbc.find();
		mv.addObject("tableList", tableCur==null?new ArrayList<DBObject>():tableCur.toArray());
		mv.addObject("dicParentList", getDicParentList());
		return mv;
	}
	
	/**
	 * 对象添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_toUpdate")
	public ModelAndView toUpdateObject() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.addObject("allObjTypes", JSON.toJSON(getAllObjectType()));
		String noshowTypes = "0,12,14,16,21,22,23,24,25";
		mv.addObject("allFieldTypes", JSON.toJSON(getAllFieldType(noshowTypes)));
		mv.setViewName("system/objectManage/obj-update");
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("table");
		DBCursor tableCur = tableDbc.find();
		mv.addObject("tableList", tableCur==null?new ArrayList<DBObject>():tableCur.toArray());
		int tableId = MyNumberUtils.toInt(getParam("tableId"));
		mv.addObject("table",tableDbc.findOne(new BasicDBObject("tableId", tableId)));
		DBCollection fieldDbc = db.getCollection("field");
		DBObject args = new BasicDBObject();
		List<Integer> noshowTypeList = new ArrayList<Integer>();
		String[] ntypes = noshowTypes.split(",");
		for (String nt : ntypes) {
			noshowTypeList.add(MyNumberUtils.toInt(nt));
		}
		args.put("fieldType", new BasicDBObject("$nin",noshowTypeList));
		args.put("tableId", tableId);
		mv.addObject("fieldList",fieldDbc.find(args).toArray());
		mv.addObject("dicParentList", getDicParentList());
		return mv;
	}
	
	/**
	 * 对象添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_add")
	public @ResponseBody int addobject() {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableName","tableCnName","tableType","ifParent","remark",
				"fieldCnName","fieldName","fieldType","relationTableId","dicParent","ifOnly","length","numType"};
		Class[] classType = {String.class,String.class,Integer.class,Integer.class,String.class,
				String[].class,String[].class,Integer[].class,Integer[].class,Integer[].class,Integer[].class,Integer[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
		
		//去保存对象和字段
		objectService.addObject(args);
		return 1;
	}
	
	/**
	 * 对象添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_update")
	public @ResponseBody int updateObject() {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"tableId","tableName","tableCnName","tableType","ifParent","remark",
				"fieldId","fieldCnName","fieldName","fieldType","relationTableId","dicParent","ifOnly","removeField","length","numType"};
		Class[] classType = {Integer.class,String.class,String.class,Integer.class,Integer.class,String.class,
				Integer[].class,String[].class,String[].class,Integer[].class,Integer[].class,Integer[].class,Integer[].class,String.class,Integer[].class,Integer[].class};
		getAllParam(args, paramName, paramName, classType, true);
		
		//去保存对象和字段
		return objectService.updateObject(args);
	}
	
	/**
	 * 对象添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_delete")
	public @ResponseBody Object deleteObject() {
		return objectService.deleteObject(getParam("tableId"));
	}
	
	/**
	 * 去生成页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_toCreatePage")
	public ModelAndView toCreatePage() throws Exception {
		ModelAndView mv = this.getModelAndView();
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		List<DBObject> argsList = new ArrayList<DBObject>();
		DBObject args = new BasicDBObject();
		args.put("menuType", 0);
		args.put("pageId", "");
		argsList.add(args);
		
		args = new BasicDBObject();
		args.put("menuType", 1);
		args.put("pageUrl", "");
		argsList.add(args);
		args = new BasicDBObject();
		args.put("$or", argsList);
		DBCursor menuCur = menuDbc.find(args);
		mv.addObject("menuList", menuCur==null?new ArrayList<DBObject>():menuCur.toArray());
		mv.addObject("tableId",getParam("tableId"));
		mv.addObject("tableCnName",getParam("tableCnName"));
		mv.setViewName("system/objectManage/obj-createPage");
		return mv;
	}
	
	/**
	 * 页面生成
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/object_createPage")
	public @ResponseBody Object createPage() {
		//生成页面
		Long pageId = PageUtils.createPage(null, getParam("pageName"), MyNumberUtils.toInt(getParam("tableId")), MyNumberUtils.toInt(getParam("pageType")));
		int ifCreateMenu = MyNumberUtils.toInt(getParam("ifCreateMenu"));//是否生成栏目
		if (ifCreateMenu==1 && MyNumberUtils.toInt(getParam("pageType"))==1) {
			//生成栏目
			DB db = MongoDbFileUtil.getDb();
			DBCollection menuDbc = db.getCollection("menu");
			DBObject menu = new BasicDBObject();
			menu.put("menuId", MongoDbFileUtil.getIncr("menuId"));
			menu.put("menuName", getParam("pageName"));
			menu.put("parentMenuId", MyNumberUtils.toInt(getParam("parentMenu")));
			menu.put("parentMenuName", getParam("parentMenuName"));
			menu.put("menuType", 0);
			menu.put("pageId", pageId);
			menu.put("tableId", MyNumberUtils.toInt(getParam("tableId")));
			menu.put("ifShow", 1);
			menu.put("ifDisRole", 1);
			menuDbc.save(menu);
		}
		return pageId;
	}
}
