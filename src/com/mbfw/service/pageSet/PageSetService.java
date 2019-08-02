package com.mbfw.service.pageSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mbfw.createPage.PageUtils;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Service("pageSetService")
public class PageSetService {
	
	/**
	 * 保存内容设置
	 * @param args
	 * @return
	 */
	public int saveContentSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			pageDbo.put("fixedRowNumber", args.get("fixedRowNumber"));
			Integer[] fieldIds = (Integer[])args.get("fieldId");
			String[] fieldCnNames = (String[])args.get("fieldCnName");
			String[] zdyNames = (String[])args.get("zdyName");
			String[] zdyJs = (String[])args.get("zdyJs");
			Integer[] ifShow = (Integer[])args.get("ifShow");
			Integer[] ifOrder = (Integer[])args.get("ifOrder");
			Integer[] width = (Integer[])args.get("width");
			List<DBObject> fieldList = new ArrayList<DBObject>();
			for (int i=0;i<fieldIds.length;i++) {
				DBObject field = new BasicDBObject();
				if (fieldIds[i]>0) {
					field = fieldDbc.findOne(new BasicDBObject("fieldId",fieldIds[i]));
					if (field==null) {
						continue;
					}
				} else {
					field = new BasicDBObject();
				}
				field.put("fieldId", fieldIds[i]);
				field.put("fieldCnName", fieldCnNames[i]);
				field.put("zdyName", zdyNames[i]);
				field.put("ifShow", ifShow[i]);
				field.put("ifOrder", ifOrder[i]);
				field.put("zdyJs", zdyJs[i]);
				field.put("width", width[i]);
				fieldList.add(field);
			}
			pageDbo.put("showFieldList", fieldList);
			pageDbc.save(pageDbo);
			PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
		}
		return 1;
	}
	
	/**
	 * 保存sql设置
	 * @param args
	 * @return
	 */
	public int saveSqlSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			String zdySql = (String)args.get("zdySql");//最近的zdySql
			pageDbo.put("lastSql", pageDbo.get("sql"));
			if (MyStringUtils.notBlank(zdySql)) {
				pageDbo.put("zdySql", zdySql);
				pageDbo.put("sql", zdySql);
				//刷新redissql
				RedisUtil.addString(pageDbo.get("tableId")+"_"+pageDbo.get("pageId")+"_sql", zdySql);
			} else {
				pageDbo.put("zdySql", "");
				PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
			}
			pageDbc.save(pageDbo);
		}
		return 1;
	}
	
	/**
	 * 保存内容设置
	 * @param args
	 * @return
	 */
	public int saveLimitSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection limitDbc = db.getCollection("limitSet");
		Long[] limitIds = (Long[])args.get("limitId");
		String[] limitNames = (String[])args.get("limitName");
		String[] limitSqls = (String[])args.get("limitSql");
		String removeIds = (String)args.get("removeIds");
		if (MyStringUtils.notBlank(removeIds)) {
			String[] removeIdArr = removeIds.split(",");
			for (String id : removeIdArr) {
				//删除权限
				limitDbc.remove(new BasicDBObject("limitId", MyNumberUtils.toInt(id)));
			}
		}
		System.out.println(limitIds.length);
		if (limitIds!=null && limitIds.length>0) {
			for (int i=0;i<limitIds.length;i++) {
				long limitId = limitIds[i];
				DBObject limitDbo = null;
				if (limitId>0) {
					limitDbo = limitDbc.findOne(new BasicDBObject("limitId", limitId));
				} else {
					limitId = MongoDbFileUtil.getIncr("limitSet");
				}
				limitDbo = limitDbo == null?new BasicDBObject():limitDbo;
				limitDbo.put("tableId", args.get("tableId"));
				limitDbo.put("pageId", args.get("pageId"));
				limitDbo.put("limitId", limitId);
				limitDbo.put("limitName", limitNames[i]);
				limitDbo.put("limitSql", limitSqls[i]);
				limitDbc.save(limitDbo);
			}
		}
		return 1;
	}
	
	/**
	 * 保存搜索设置
	 * @param args
	 * @return
	 */
	public int saveSearchSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			Integer[] fieldIds = (Integer[])args.get("fieldId");
			String[] fieldCnNames = (String[])args.get("fieldCnName");
			String[] zdyNames = (String[])args.get("zdyName");
			Integer[] searchType = (Integer[])args.get("searchType");
			String[] dateType = (String[])args.get("dateType");
			Integer[] dicParentId = (Integer[])args.get("dicParentId");
			List<DBObject> fieldList = new ArrayList<DBObject>();
			for (int i=0;i<fieldIds.length;i++) {
				DBObject field = new BasicDBObject();
				int fieldType = 0;
				if (fieldIds[i]>0) {
					field = fieldDbc.findOne(new BasicDBObject("fieldId",fieldIds[i]));
					if (field==null) {
						continue;
					}
					fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
				} else {
					field = new BasicDBObject();
				}
				field.put("fieldId", fieldIds[i]);
				field.put("fieldCnName", fieldCnNames[i]);
				field.put("zdyName", zdyNames[i]);
				if (fieldIds[i]==0) {
					field.put("searchType", searchType[i]);
					if (searchType[i]==3) {//日期
						field.put("dateType", dateType[i]);
					} else if (searchType[i]==4) {//数据字典
						field.put("dicParent", dicParentId[i]);
					}
				} else if (",10,23,25,".indexOf(","+fieldType+",")>=0){//日期
					field.put("dateType", dateType[i]);
				}
				fieldList.add(field);
			}
			pageDbo.put("searchFieldList", fieldList);
			pageDbc.save(pageDbo);
			PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
		}
		return 1;
	}
	
	/**
	 * 保存html设置
	 * @param args
	 * @return
	 */
	public int saveHtmlSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			String pageHtml = (String)args.get("pageHtml");//最近的pageHtml
			pageDbo.put("lastPageHtml", pageDbo.get("zdyPageHtml"));
			if (MyStringUtils.notBlank(pageHtml)) {
				pageDbo.put("zdyPageHtml", pageHtml);
				pageDbo.put("pageHtml", pageHtml);
				//刷新redissql
				RedisUtil.addString(pageDbo.get("tableId")+"_"+pageDbo.get("pageId")+"_page", pageHtml);
			} else {//重新生成html
				pageDbo.put("zdyPageHtml", "");
				PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
			}
			pageDbc.save(pageDbo);
		}
		return 1;
	}
	
	/**
	 * 保存按钮设置
	 * @param args
	 * @return
	 */
	public int saveButtonSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			Long[] buttonId = (Long[])args.get("buttonId");//按钮id
			Integer[] buttonType = (Integer[])args.get("buttonType");//按钮类型
			Integer[] buttonPageId = (Integer[])args.get("buttonPageId");//按钮页面id
			Integer[] ifDisRole = (Integer[])args.get("ifDisRole");//按钮是否区分角色
			String[] buttonName = (String[])args.get("buttonName");//按钮名称
			Integer[] openStyle = (Integer[])args.get("openStyle");//按钮打开方式
			Integer[] pageWidth = (Integer[])args.get("pageWidth");//按钮宽度
			Integer[] pageHeight = (Integer[])args.get("pageHeight");//按钮高度
			String[] pageUrl = (String[])args.get("pageUrl");//按钮url
			String[] urlParam = (String[])args.get("urlParam");//按钮url参数
			String[] btnBeforeJs = (String[])args.get("btnBeforeJs");//按钮打开前js
			String[] btnAfterJs = (String[])args.get("btnAfterJs");//按钮打开之后js
			int delReSize = MyNumberUtils.toInt(args.get("delReSize"));//关联删除的对象个数
			Integer[] delTableId = (Integer[])args.get("delTableId");//关联删除的对象id
			Integer[] delReFieldId = (Integer[])args.get("delReFieldId");//关联删除的对象字段id
			Integer[] deleteOType = (Integer[])args.get("deleteOType");//删除的操作
			List<DBObject> buttonList = new ArrayList<DBObject>();
			if (buttonId!=null && buttonId.length>0) {
				for (int i=0;i<buttonId.length;i++) {
					DBObject button = new BasicDBObject();
					button.put("buttonId", buttonId[i]>0?buttonId[i]:MongoDbFileUtil.getIncr("button"));
					button.put("buttonType", buttonType[i]);
					button.put("buttonPageId", buttonPageId[i]);
					//获取页面对应id
					if (buttonPageId[i]>0) {
						DBObject tmpPage = pageDbc.findOne(new BasicDBObject("pageId",buttonPageId[i]));
						button.put("buttonTableId", tmpPage.get("tableId"));
						button.put("buttonPageName", tmpPage.get("pageName"));
					}
					button.put("buttonName", buttonName[i]);
					button.put("ifDisRole", ifDisRole[i]);
					button.put("openStyle", openStyle[i]);
					button.put("pageWidth", pageWidth[i]);
					button.put("pageHeight", pageHeight[i]);
					if (buttonType[i]==6 || buttonType[i]==13){//自定义按钮
						button.put("pageUrl", pageUrl[i]);
						button.put("urlParam", urlParam[i]);
					}
					button.put("btnBeforeJs", btnBeforeJs[i]);
					button.put("btnAfterJs", btnAfterJs[i]);
					if (buttonType[i]==3 || buttonType[i]==7) {
						List<DBObject> delReList = new ArrayList<DBObject>();
						for (int j=delReSize*i;j<delReSize*(i+1);j++) {
							DBObject delRb = new BasicDBObject();
							delRb.put("tableId", delTableId[j]);
							delRb.put("relationFieldId", delReFieldId[j]);
							delRb.put("deleteType", deleteOType[j]);
							delReList.add(delRb);
						}
						button.put("delReList", delReList);
					}
					buttonList.add(button);
				}
			}
			pageDbo.put("buttonSet", buttonList);
			pageDbc.save(pageDbo);
			PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
		}
		return 1;
	}
	
	/**
	 * 保存列表数据按钮设置
	 * @param args
	 * @return
	 */
	public int saveListButtonSet (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBObject pageDbo = pageDbc.findOne(new BasicDBObject("pageId", args.get("pageId")));
		if (pageDbo!=null) {
			Long[] buttonId = (Long[])args.get("buttonId");//按钮id
			Integer[] buttonType = (Integer[])args.get("buttonType");//按钮类型
			Integer[] buttonPageId = (Integer[])args.get("buttonPageId");//按钮页面id
			Integer[] ifDisRole = (Integer[])args.get("ifDisRole");//按钮是否区分角色
			String[] buttonName = (String[])args.get("buttonName");//按钮名称
			Integer[] openStyle = (Integer[])args.get("openStyle");//按钮打开方式
			Integer[] pageWidth = (Integer[])args.get("pageWidth");//按钮宽度
			Integer[] pageHeight = (Integer[])args.get("pageHeight");//按钮高度
			String[] pageUrl = (String[])args.get("pageUrl");//按钮url
			String[] urlParam = (String[])args.get("urlParam");//按钮url参数
			String[] btnBeforeJs = (String[])args.get("btnBeforeJs");//按钮打开前js
			String[] btnAfterJs = (String[])args.get("btnAfterJs");//按钮打开之后js
			int delReSize = MyNumberUtils.toInt(args.get("delReSize"));//关联删除的对象个数
			Integer[] delTableId = (Integer[])args.get("delTableId");//关联删除的对象id
			Integer[] delReFieldId = (Integer[])args.get("delReFieldId");//关联删除的对象字段id
			Integer[] deleteOType = (Integer[])args.get("deleteOType");//删除的操作
			List<DBObject> buttonList = new ArrayList<DBObject>();
			if (buttonId!=null && buttonId.length>0) {
				for (int i=0;i<buttonId.length;i++) {
					DBObject button = new BasicDBObject();
					button.put("buttonId", buttonId[i]>0?buttonId[i]:MongoDbFileUtil.getIncr("button"));
					button.put("buttonType", buttonType[i]);
					button.put("buttonPageId", buttonPageId[i]);
					button.put("buttonName", buttonName[i]);
					button.put("ifDisRole", ifDisRole[i]);
					button.put("openStyle", openStyle[i]);
					button.put("pageWidth", pageWidth[i]);
					button.put("pageHeight", pageHeight[i]);
					if (buttonType[i]==6 || buttonType[i]==13){//自定义按钮
						button.put("pageUrl", pageUrl[i]);
						button.put("urlParam", urlParam[i]);
					}
					button.put("btnBeforeJs", btnBeforeJs[i]);
					button.put("btnAfterJs", btnAfterJs[i]);
					if (buttonType[i]==3 || buttonType[i]==7) {
						List<DBObject> delReList = new ArrayList<DBObject>();
						for (int j=delReSize*i;j<delReSize*(i+1);j++) {
							DBObject delRb = new BasicDBObject();
							delRb.put("tableId", delTableId[j]);
							delRb.put("relationFieldId", delReFieldId[j]);
							delRb.put("deleteType", deleteOType[j]);
							delReList.add(delRb);
						}
						button.put("delReList", delReList);
					}
					buttonList.add(button);
				}
			}
			pageDbo.put("listButtonSet", buttonList);
			pageDbc.save(pageDbo);
			PageUtils.createPage(pageDbo, (String)pageDbo.get("pageName"),MyNumberUtils.toInt(args.get("tableId")), 1);
		}
		return 1;
	}
	
	/**
	 * 保存添加修改页面的设置
	 * @return
	 */
	public Long saveAddPageSet (Map<String, Object> args) throws Exception{
		int isChild = MyNumberUtils.toInt(args.get("isChild"));//是否是子页面的设置
		Integer[] fieldId = (Integer[])args.get("fieldId");
		String[] fieldCnName = (String[])args.get("fieldCnName");
		String[] zdyName = (String[])args.get("zdyName");
		Integer[] fieldType = (Integer[])args.get("fieldType");
		Integer[] ifMust = (Integer[])args.get("ifMust");
		Integer[] ifUpdate = (Integer[])args.get("ifUpdate");
		Integer[] ifShowDefVal = (Integer[])args.get("ifShowDefVal");
		Integer[] ifNullShowDefVal = (Integer[])args.get("ifNullShowDefVal");
		String[] defaultShow = (String[])args.get("defaultShow");
		String[] defaultSelect = (String[])args.get("defaultSelect");
		String[] defaultVal = (String[])args.get("defaultVal");
		Integer[] inputStyle = (Integer[])args.get("inputStyle");
		Integer[] relationTableId = (Integer[])args.get("relationTableId");
		String[] relationTableCnName = (String[])args.get("relationTableCnName");
		Integer[] addStyle = (Integer[])args.get("addStyle");
		Integer[] dataMaxNum = (Integer[])args.get("dataMaxNum");
		Integer[] ifCanAdd = (Integer[])args.get("ifCanAdd");
		Integer[] ifOnlyAdd = (Integer[])args.get("ifOnlyAdd");
		String[] dialogField = (String[])args.get("dialogField");
		Integer[] chooseType = (Integer[])args.get("chooseType");
		Integer[] dialogPage = (Integer[])args.get("dialogPage");
		String[] zdyVerify = (String[])args.get("zdyVerify");
		String[] fieldDescript = (String[])args.get("fieldDescript");
		String[] childSelectSql = (String[])args.get("childSelectSql");
		String[] childZdySelectSql = (String[])args.get("childZdySelectSql");
		Integer[] childPageId = (Integer[])args.get("childPageId");
		int showNum = MyNumberUtils.toInt(args.get("showNum"));//显示字段个数
		List<DBObject> showFieldList = new ArrayList<DBObject>();
		List<DBObject> hideFieldList = new ArrayList<DBObject>();
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		for (int i=0;i<fieldId.length;i++) {
			DBObject field = new BasicDBObject();
			if (fieldType[i]>0) {
				field = fieldDbc.findOne(new BasicDBObject("fieldId", fieldId[i]));
				if (field==null) {
					continue;
				}
			} else {
				field.put("fieldId", fieldId[i]>0?fieldId[i]:MongoDbFileUtil.getIncr("zdyField"));
				field.put("fieldType", fieldType[i]);
			}
			field.put("fieldCnName", fieldCnName[i]);
			field.put("zdyName", zdyName[i]);
			field.put("ifMust", ifMust[i]);
			field.put("ifUpdate", ifUpdate[i]);
			field.put("ifShowDefVal", ifShowDefVal[i]);
			field.put("ifNullShowDefVal", ifNullShowDefVal[i]);
			if (fieldType[i]==11 || fieldType[i]==12) {//数据字典
				field.put("defaultShow", defaultShow[i]);
				field.put("defaultSelect", defaultSelect[i]);
			}
			field.put("defaultVal", defaultVal[i]);
			field.put("inputStyle", inputStyle[i]);
			if (fieldType[i]==15 || fieldType[i]==16 || fieldType[i]==21) {//内部对象/多值字段
				field.put("relationTableId", relationTableId[i]);
				field.put("relationTableCnName", relationTableCnName[i]);
				field.put("addStyle", addStyle[i]);
				field.put("dataMaxNum", dataMaxNum[i]);
				field.put("ifCanAdd", ifCanAdd[i]);
				field.put("ifOnlyAdd", ifOnlyAdd[i]);
				field.put("dialogField", dialogField[i]);
				field.put("chooseType", chooseType[i]);
				field.put("dialogPage", dialogPage[i]);
				field.put("childPageId", childPageId[i]);
				field.put("childSelectSql", childSelectSql[i]);
				field.put("childZdySelectSql", childZdySelectSql[i]);
			}
			field.put("zdyVerify", zdyVerify[i]);
			field.put("fieldDescript", fieldDescript[i]);
			if (i<showNum) {
				showFieldList.add(field);
			} else {
				hideFieldList.add(field);
			}
		}
		Long pageId =  MyNumberUtils.toLong(args.get("pageId"),0);
		int buttonType = MyNumberUtils.toInt(args.get("buttonType"));
		DBObject pageDbo = null;
		if (pageId>0) {
			pageDbo =  pageDbc.findOne(new BasicDBObject("pageId", pageId));
		} 
		if (isChild==1 && pageDbo==null) {
			pageDbo = new BasicDBObject();
			pageId = MongoDbFileUtil.getIncr("page");
			pageDbo.put("pageId", pageId);
			int pageType = 2;
			if (buttonType==1 || buttonType==8) {//添加
				pageType = 2;
			} else if (buttonType==9) {//修改
				pageType = 3;
			} else if (buttonType==10) {//查看
				pageType = 8;
			}
			pageDbo.put("pageType", pageType);
			pageDbo.put("isChild", 1);
			pageDbo.put("pageName", args.get("pageName"));
		}
		pageDbo.put("tableId", args.get("mainTableId"));
		pageDbo.put("buttonType", buttonType);
		pageDbo.put("showFieldList", showFieldList);
		pageDbo.put("hideFieldList", hideFieldList);
		pageDbo.put("customerJs", args.get("customerJs"));
		pageDbc.save(pageDbo);
		//刷新页面
		if (isChild!=1) {
			PageUtils.refreashPage(pageId);
		}
		return pageId;
	}

}
 