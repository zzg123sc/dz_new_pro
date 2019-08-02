package com.mbfw.controller.system.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
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
public class MenuController extends BaseController {
	
	/**
	 * 去栏目列表
	 * @return
	 */
	@RequestMapping(value = "/menu_toList")
	public ModelAndView toMenuList () {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/menu/menu-list");
		mv.addObject("pd", pd);
		return mv;
	}
	
	/**
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_ajaxList")
	public @ResponseBody Object ajaxList()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		String menuName = getParam("menuName");
		String parentMenuName = getParam("parentMenuName");
		int parentMenuId = MyNumberUtils.toInt(getParam("parentMenuId"));
		DBObject args = new BasicDBObject();
		if (MyStringUtils.notBlank(menuName)) {
			Pattern pattern = Pattern.compile(".*"+menuName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("menuName", pattern);
		}
		if (MyStringUtils.notBlank(parentMenuName)) {
			Pattern pattern = Pattern.compile(".*"+parentMenuName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("parentMenuName", pattern);
		}
		if (parentMenuId>0) {
			List<DBObject> seaList = new ArrayList<DBObject>();
			DBObject args2 = new BasicDBObject();
			args2.put("menuId", parentMenuId);
			seaList.add(args2);
			args2 = new BasicDBObject();
			args2.put("parentMenuId", parentMenuId);
			seaList.add(args2);
			args.put("$or", seaList);
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
	 * 获取列表数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_ajaxTreeData")
	public @ResponseBody Object ajaxTreeData()  {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		String menuName = getParam("treeName");
		DBObject args = new BasicDBObject();
		if (MyStringUtils.notBlank(menuName)) {
			Pattern pattern = Pattern.compile(".*"+menuName+".*$",Pattern.CASE_INSENSITIVE);
			args.put("menuName", pattern);
		}
		DBCursor menuCur = menuDbc.find(args);
		List<DBObject> menuList = new ArrayList<DBObject>();
		if (menuCur!=null){
			menuCur.sort(new BasicDBObject("menuSort",1));
			menuList = menuCur.toArray();
			for (DBObject menu : menuList) {
				menu.put("id", menu.get("menuId"));
				menu.put("pId", menu.get("parentMenuId"));
				menu.put("name", menu.get("menuName"));
				menu.removeField("_id");
			}
		}
		return JSON.toJSON(menuList);
	}

	/**
	 * 栏目添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_toAdd")
	public ModelAndView toAddmenu() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("system/menu/menu-add");
		mv.addObject("pd", pd);
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
		
		//获取所有列表页面
		DBCollection pageDbc = db.getCollection("page");
		DBCursor pageCur = pageDbc.find(new BasicDBObject("pageType",1));
		mv.addObject("pageList", pageCur==null?new ArrayList<DBObject>():pageCur.toArray());
		return mv;
	}
	
	/**
	 * 栏目修改
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_toUpdate")
	public ModelAndView toUpdateMenu() throws Exception {
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("system/menu/menu-update");
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		int menuId = MyNumberUtils.toInt(getParam("menuId"));
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
		args.put("menuId", new BasicDBObject("$ne",menuId));
		DBCursor menuCur = menuDbc.find(args);
		mv.addObject("menuList", menuCur==null?new ArrayList<DBObject>():menuCur.toArray());
		mv.addObject("menu",menuDbc.findOne(new BasicDBObject("menuId", menuId)));
		
		//获取所有列表页面
		DBCollection pageDbc = db.getCollection("page");
		DBCursor pageCur = pageDbc.find(new BasicDBObject("pageType",1));
		mv.addObject("pageList", pageCur==null?new ArrayList<DBObject>():pageCur.toArray());
		return mv;
	}
	
	/**
	 * 栏目添加
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_add")
	public @ResponseBody int addMenu() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		DBObject menu = new BasicDBObject();
		int menuType = MyNumberUtils.toInt("menuType");
		menu.put("menuId", MongoDbFileUtil.getIncr("menuId"));
		menu.put("menuName", getParam("menuName"));
		menu.put("parentMenuId", MyNumberUtils.toInt(getParam("parentMenuId")));
		menu.put("parentMenuName", getParam("parentMenuName"));
		menu.put("menuType", menuType);
		menu.put(menuType==0?"pageId":"pageUrl", getParam(menuType==0?"pageId":"pageUrl"));
		if (menuType==0) {
			menu.put("tableId", getParam("tableId"));
		}
		menu.put("ifShow", MyNumberUtils.toInt(getParam("ifShow")));
		menu.put("ifDisRole", MyNumberUtils.toInt(getParam("ifDisRole")));
		menuDbc.save(menu);
		return 1;
	}
	
	/**
	 * 栏目修改
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_update")
	public @ResponseBody int updateMenu() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		DBObject menu = menuDbc.findOne(new BasicDBObject("menuId", MyNumberUtils.toInt(getParam("menuId"))));
		int menuType = MyNumberUtils.toInt(getParam("menuType"));
		menu.put("menuName", getParam("menuName"));
		menu.put("parentMenuId", MyNumberUtils.toInt(getParam("parentMenuId")));
		menu.put("parentMenuName", getParam("parentMenuName"));
		menu.put("menuType", menuType);
		if (menuType==0) {
			menu.removeField("pageUrl");
			menu.put("pageId", getParam("pageId"));
			menu.put("tableId", getParam("tableId"));
		} else {
			menu.removeField("pageId");
			menu.removeField("tableId");
			menu.put("pageUrl", getParam("pageUrl"));
		}
		menu.put("ifShow", MyNumberUtils.toInt(getParam("ifShow")));
		menu.put("ifDisRole", MyNumberUtils.toInt(getParam("ifDisRole")));
		menuDbc.save(menu);
		//更改子栏目的父栏目名称
		DBCursor childMenuCur = menuDbc.find(new BasicDBObject("parentMenuId", MyNumberUtils.toInt(getParam("menuId"))));
		if (childMenuCur!=null && childMenuCur.size()>0) {
			List<DBObject> childList = childMenuCur.toArray();
			for (DBObject child : childList) {
				child.put("parentMenuName", getParam("menuName"));
				menuDbc.save(child);
			}
		}
		return 1;
	}
	
	/**
	 * 栏目删除
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu_delete")
	public @ResponseBody int deleteMenu() {
		DB db = MongoDbFileUtil.getDb();
		DBCollection menuDbc = db.getCollection("menu");
		String menuIds = getParam("menuId");
		String[] menuIdArr = menuIds.split(",");
		for (String menuId : menuIdArr) {
			menuDbc.remove(new BasicDBObject("menuId", MyNumberUtils.toInt(menuId)));
			//更改子栏目的父栏目名称
			DBCursor childMenuCur = menuDbc.find(new BasicDBObject("parentMenuId", MyNumberUtils.toInt(menuId)));
			if (childMenuCur!=null && childMenuCur.size()>0) {
				List<DBObject> childList = childMenuCur.toArray();
				for (DBObject child : childList) {
					child.put("parentMenuId", "");
					child.put("parentMenuName", "");
					menuDbc.save(child);
				}
			}
		}
		return 1;
	}
	
	/**
	 * 
	 * batchUpdateMenuSort  {批量更新栏目排序}
	 * 
	 * @Description {批量更新栏目排序} void
	 * @see
	 */
	@RequestMapping(value = "/menu_batchUpdateMenuSort")
	public @ResponseBody int batchUpdateMenuSort() {
		//获取传入的所以栏目id
		String sortMenuIds = getParam("menuIds");
		if (MyStringUtils.notBlank(sortMenuIds)) {
			DB db=MongoDbFileUtil.getDb();
			DBCollection menuDbc = db.getCollection("menu");
			String[] menuIds = sortMenuIds.split(",");
			for (int i=0;i<menuIds.length;i++) {
				Integer menuId = MyNumberUtils.toInt(menuIds[i].split("_")[0]);
				Integer parentId = MyNumberUtils.toInt(menuIds[i].split("_")[1]);
				DBObject menu = menuDbc.findOne(new BasicDBObject("menuId", menuId));
				if (menu==null) {
					continue;
				}
				menu.put("parentMenuId", parentId);
				menu.put("parentMenuName", parentId>0?menuIds[i].split("_")[2]:"");
				menu.put("menuSort", i);
				menuDbc.save(menu);
			}
		}
		return 1;
	}
}
