package com.mbfw.controller.system.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
import com.mbfw.service.system.basic.DicService;
import com.mbfw.servlet.InitDataServlet;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.PageData;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Controller
public class DicController extends BaseController {
	
	@Resource(name = "dicService")
	public DicService dicService;
	
	@Autowired
	public ServletContext servletContext;
	/**
	 * 去数据字典列表
	 * @return
	 */
	@RequestMapping(value = "/dic_toList")
	public ModelAndView toDicList () {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/dictionary/dic-list");
		mv.addObject("pd", pd);
		return mv;
	}
	
	/**
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_ajaxList")
	public @ResponseBody Object ajaxList()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("dictionary");
		DBObject args = new BasicDBObject();
		String dicName = getParam("dicName");
		String parentDicName = getParam("parentDicName");
		int parentId = MyNumberUtils.toInt(getParam("parentId"));
		if (MyStringUtils.notBlank(dicName)) {
			Pattern pattern = Pattern.compile(".*"+dicName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("DIC_NAME", pattern);
		}
		if (MyStringUtils.notBlank(parentDicName)) {
			Pattern pattern = Pattern.compile(".*"+parentDicName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("DIC_PARENT_NAME", pattern);
		}
		if (parentId>0) {
			List<DBObject> seaList = new ArrayList<DBObject>();
			DBObject args2 = new BasicDBObject();
			args2.put("DIC_ID", parentId);
			seaList.add(args2);
			args2 = new BasicDBObject();
			args2.put("DIC_PARENT_ID", parentId);
			seaList.add(args2);
			args.put("$or", seaList);
		}
		DBObject sortDbo = new BasicDBObject();
		sortDbo.put("DIC_ID", -1);
		sortDbo.put("DIC_PARENT_ID", -1);
		sortDbo.put("DIC_ORDER", -1);
		DBCursor tableCur = menuDbc.find(args).sort(sortDbo);
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
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_ajaxTreeDic")
	public @ResponseBody Object ajaxTreeData()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		String dicName = getParam("treeName");
		DBObject args = new BasicDBObject();
		if (MyStringUtils.notBlank(dicName)) {
			Pattern pattern = Pattern.compile(".*"+dicName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("DIC_NAME", pattern);
		}
		DBCursor dicCur = dicDbc.find(args);
		List<DBObject> dicList = new ArrayList<DBObject>();
		if (dicCur!=null){
			dicCur.sort(new BasicDBObject("DIC_ORDER",1));
			dicList = dicCur.toArray();
			for (DBObject dic : dicList) {
				dic.put("id", dic.get("DIC_ID"));
				dic.put("pId", dic.get("DIC_PARENT_ID"));
				dic.put("name", dic.get("DIC_NAME"));
				dic.removeField("_id");
			}
		}
		return JSON.toJSON(dicList);
	}

	/**
	 * 数据字典添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_toAdd")
	public ModelAndView toAddDic() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/dictionary/dic-add");
		mv.addObject("pd", pd);
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		DBCursor dicCur = dicDbc.find(new BasicDBObject("DIC_PARENT_ID",0));
		mv.addObject("dicList", dicCur==null?new ArrayList<DBObject>():dicCur.toArray());
		return mv;
	}
	
	/**
	 * 数据字典修改
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_toUpdate")
	public ModelAndView toUpdateDic() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("system/dictionary/dic-update");
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		int dicId = MyNumberUtils.toInt(getParam("dicId"));
		DBCursor dicCur = dicDbc.find(new BasicDBObject("DIC_PARENT_ID",0));
		mv.addObject("dicList", dicCur==null?new ArrayList<DBObject>():dicCur.toArray());
		DBObject dicDbo = dicDbc.findOne(new BasicDBObject("DIC_ID", dicId));
		mv.addObject("dicDbo",dicDbo);
		int dicParentId = MyNumberUtils.toInt(dicDbo.get("DIC_PARENT_ID"));
		if (dicParentId==0) {//判断是否是父级字典修改
			dicCur = dicDbc.find(new BasicDBObject("DIC_PARENT_ID", dicDbo.get("DIC_ID")));
			DBObject sortDbo = new BasicDBObject();
			sortDbo.put("DIC_ORDER", 1);
			mv.addObject("dicChildList",dicCur.sort(sortDbo).toArray());
		}
		return mv;
	}
	
	/**
	 * 数据字典添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_add")
	public @ResponseBody int addDic() {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] paramName = {"dicType","parentDicName","parentDicName","parentDicId","rgDicId","dicName"};
		Class[] classType = {Integer.class,String.class,String.class,Long.class,Integer[].class,String[].class};
		getAllParam(args, paramName, paramName, classType, true);
		
		//去保存对象和字段
		dicService.addDic(args);
		//刷新数据字典
		InitDataServlet.loadAllDicToApplication(servletContext);
		return 1;
	}
	
	/**
	 * 数据字典修改
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_update")
	public @ResponseBody int updateDic() {
		int parentId = MyNumberUtils.toInt(getParam("parentId"));
		if (parentId>0) {//判断是否是父级修改
			Map<String, Object> args = new HashMap<String, Object>();
			String[] paramName = {"parentId","dicType","parentDicName","parentDicName","parentDicId","dicId","rgDicId","dicName","removeIds"};
			Class[] classType = {Integer.class,Integer.class,String.class,String.class,Integer.class,Integer[].class,Integer[].class,String[].class,String.class};
			getAllParam(args, paramName, paramName, classType, true);
			
			//去保存对象和字段
			dicService.updateDic(args);
		} else {//单子级修改
			DB db = MongoDbFileUtil.getDb();
			DBCollection dicDbc = db.getCollection("dictionary");
			DBObject dicDbo = dicDbc.findOne(new BasicDBObject("DIC_ID", MyNumberUtils.toInt(getParam("dicId"))));
			dicDbo.put("RG_DIC_ID", MyNumberUtils.toInt(getParam("rgDicId")));
			dicDbo.put("DIC_NAME", getParam("dicName"));
			dicDbo.put("DIC_PARENT_ID", MyNumberUtils.toInt(getParam("parentDicId")));
			dicDbo.put("DIC_PARENT_NAME", getParam("cparentDicName"));
			dicDbc.save(dicDbo);
		}
		//刷新数据字典
		InitDataServlet.loadAllDicToApplication(servletContext);
		return 1;
	}
	
	/**
	 * 数据字典删除
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/dic_delete")
	public @ResponseBody int deleteDic() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		String dicIds = getParam("dicId");
		String[] dicArr = dicIds.split(",");
		for (String dicId : dicArr) {
			dicDbc.remove(new BasicDBObject("DIC_ID", MyNumberUtils.toInt(dicId)));
		}
		//刷新数据字典
		InitDataServlet.loadAllDicToApplication(servletContext);
		return 1;
	}
}
