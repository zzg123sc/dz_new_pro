package com.mbfw.createPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class AppendAddPageUtils {
	
	/**
	 * 拼接页面html
	 * @param pageDbo
	 * @param pageType
	 */
	public static void appendAddPageHtml (DBObject pageDbo,String domId,int pageType,StringBuffer bodyBuffer,StringBuffer jsBuffer) {
		bodyBuffer.append("<form class='layui-form' id='pageForm_"+domId+"'>");
		//显示字段
		List<DBObject> showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
		appendBodyHtml(domId, showFieldList, pageType, bodyBuffer,jsBuffer, true);
		//隐藏字段
		List<DBObject> hideFieldList = (List<DBObject>)pageDbo.get("hideFieldList");
		appendBodyHtml(domId, hideFieldList, pageType, bodyBuffer,jsBuffer, false);
		bodyBuffer.append("<div class='layui-form-item layui-hide'>");
		bodyBuffer.append("  <button  type='button' id='savePage' class='layui-bt' lay-submit lay-filter='savePage'>立即提交</button>");
		bodyBuffer.append("</div>");
		bodyBuffer.append("</form>");
	}
	
	/**
	 * 拼接主体html
	 * @param pageDbo
	 * @param fieldList
	 * @param pageType
	 * @param bodyBuffer
	 * @param ifShow
	 */
	public static void appendBodyHtml (String domId,List<DBObject> fieldList,int pageType,StringBuffer bodyBuffer,StringBuffer jsBuffer,boolean ifShow) {
		if (MyCollectionUtils.notEmpty(fieldList)) {
			if (ifShow) {
				bodyBuffer.append("<div id='showField' name='showField'>");
			} else {
				bodyBuffer.append("<div id='hideField' name='hideField' class='layui-hide'>");
			}
			int dqRow = 0;
			int tableId = MyNumberUtils.toInt(domId.split("_")[0]);
			int pageId = MyNumberUtils.toInt(domId.split("_")[1]);
			//bodyBuffer.append("<div class='layui-form-item'>");
			for (int i=0;i<fieldList.size();i++) {
				DBObject field = fieldList.get(i);//字段
				int row = MyNumberUtils.toInt(field.get("row"));//行
				if ((row==0 && i%3==0) || dqRow<row) {//如果没有划分行,则默认三列为一行
					bodyBuffer.append("<div class='layui-form-item'>");
				}
				//拼接字段内容
				appendFieldHtml(pageId, tableId, field, pageType, false, bodyBuffer,jsBuffer);
				if ((row==0 && i%3==2) || dqRow<row) {
					bodyBuffer.append("</div>");
					dqRow = row;//赋值当前行
				}
			}
			//bodyBuffer.append("</div>");
			bodyBuffer.append("</div>");
		}
	}
	
	/**
	 * 拼接子页面内容
	 * @param pageDbo
	 * @param domId
	 * @param pageType
	 * @param bodyBuffer
	 * @param jsBuffer
	 */
	public static void appendChildBodyHtml (DBObject pageDbo,String domId,int pageType,StringBuffer thBuffer,StringBuffer trBuffer,StringBuffer jsBuffer) {
		//显示字段
		List<DBObject> showFieldList = (List<DBObject>)pageDbo.get("showFieldList");
		//隐藏字段
		List<DBObject> hideFieldList = (List<DBObject>)pageDbo.get("hideFieldList");
		//隐藏字段（子表格关联字段）
		List<DBObject> parentRelationField = (List<DBObject>)pageDbo.get("parentRelationField");
		int tableId = MyNumberUtils.toInt(domId.split("_")[0]);
		int pageId = MyNumberUtils.toInt(domId.split("_")[1]);
		thBuffer.append("<tr>");
		trBuffer.append("<tr class='ptr'>");
		int parentAddStyle = MyNumberUtils.toInt(pageDbo.get("parentAddStyle"));//父级页面添加形式
		for (int i=0;i<showFieldList.size();i++) {
			DBObject field = showFieldList.get(i);//字段
			//拼接字段内容
			trBuffer.append("<td>");
			thBuffer.append("<th>"+field.get("fieldCnName"));
			appendFieldHtml(pageId, tableId, field, pageType, true, trBuffer,jsBuffer);
			//隐藏字段全部放到第一行
			if (i==0) {
				trBuffer.append("<div class='layui-hide'>");
				if (MyCollectionUtils.notEmpty(hideFieldList)) {
					for (DBObject hideField : hideFieldList) {
						appendFieldHtml(pageId, tableId, hideField, pageType, true, trBuffer,jsBuffer);
					}
				}
				if (MyCollectionUtils.notEmpty(parentRelationField) && parentAddStyle!=1) {//非点击+添加才需要放置此字段
					int index = 0;
					for (DBObject hideField : parentRelationField) {
						System.out.println(index+"==="+hideField);
						if (index==0) {//第一个是主页面的关联字段，不用放置
							index++;
							continue;
						}
						appendFieldHtml(pageId, tableId, hideField, pageType, true, trBuffer,jsBuffer);
					}
				}
				trBuffer.append("<div>");
			}
			trBuffer.append("</td>");
			thBuffer.append("</th>");
		}
		thBuffer.append("</tr>");
		trBuffer.append("</tr>");
	}
	
	/**
	 * 
	 * @param fieldDbo
	 * @param pageType
	 * @param bodyBuffer
	 */
	public static void appendFieldHtml (int pageId,int tableId,DBObject fieldDbo,int pageType,boolean ifChild,StringBuffer bodyBuffer,StringBuffer jsBuffer) {
		int fieldType = MyNumberUtils.toInt(fieldDbo.get("fieldType"));//字段类型
		int fieldId = MyNumberUtils.toInt(fieldDbo.get("fieldId"));//字段id
		int fieldTableId = MyNumberUtils.toInt(fieldDbo.get("tableId"));//字段tableid
		int addStyle = MyNumberUtils.toInt(fieldDbo.get("addStyle"));//添加形式
		fieldType = fieldId>0?fieldType:-1;
		String zdyName = (String)fieldDbo.get("zdyName");//字段别名
		String dataName = zdyName;//数据别
		int ifMust = MyNumberUtils.toInt(fieldDbo.get("ifMust"));//是否必填
		int ifUpdate = MyNumberUtils.toInt(fieldDbo.get("ifUpdate"));//是否可修改
		int inputStyle = MyNumberUtils.toInt(fieldDbo.get("inputStyle"));//输入框类型（text/textarea）
		String domId = tableId+"_"+pageId;
		int childPageId = MyNumberUtils.toInt(fieldDbo.get("childPageId"));//子頁面id
		int relationTableId = MyNumberUtils.toInt(fieldDbo.get("relationTableId"));//管理對象id
		if (childPageId>0 && fieldType==16) {
			domId = relationTableId+"_"+childPageId;
		}
		if (MyStringUtils.isBlank(zdyName)) {
			dataName = "AFM_"+fieldId;
			zdyName = "field_"+domId+"_"+fieldId;
		}
		String dicParent = fieldDbo.get("dicParent")+"";
		if (fieldType==12) {//获取多值选择项对应的数据字典值
			DBObject args1 = new BasicDBObject();
			args1.put("fieldType", 11);
			args1.put("tableId", fieldDbo.get("relationTableId"));
			DB db = MongoDbFileUtil.getDb();
			DBCollection fieldDbc = db.getCollection("field");
			DBObject dzInnerdzDbo = fieldDbc.findOne(args1);
			if (dzInnerdzDbo!=null) {
				String tmpDom = "field_"+domId+"_"+fieldId;
				dicParent = dzInnerdzDbo.get("dicParent")+"";
				String childDomArr = RedisUtil.getStringVal(tableId+"_"+pageId+"_childDom");
				//存放子页面的dom数据
				if (MyStringUtils.notBlank(childDomArr)) {
					childDomArr+=','+zdyName;
				} else {
					childDomArr = zdyName;
				}
				RedisUtil.addString(tableId+"_"+pageId+"_childDom", childDomArr);
				StringBuffer dzSqlBuffer = new StringBuffer();
				args1.put("fieldType", 15);
				args1.put("tableId", dzInnerdzDbo.get("tableId"));
				args1.put("relationTableId", tableId);
				DBObject otherInnerdzDbo = fieldDbc.findOne(args1);//另一个当前对象的外键字段
				
				dzSqlBuffer.append("insert into "+dzInnerdzDbo.get("tableName")+"(");
				dzSqlBuffer.append(otherInnerdzDbo.get("fieldName")+","+dzInnerdzDbo.get("fieldName")+",creater,create_date)");
				dzSqlBuffer.append(" values(#{mainId},#{"+tmpDom+"},#{userId},now())");
				RedisUtil.addString(tmpDom+"_insertSql", dzSqlBuffer.toString());
				RedisUtil.addString(tmpDom+"_fieldName",zdyName);
			}
		}
		dicParent = fieldType==17?"XQ":dicParent;
		if (fieldType==16) {
			String childDomArr = RedisUtil.getStringVal(tableId+"_"+pageId+"_childDom");
			//存放子页面的dom数据
			if (MyStringUtils.notBlank(childDomArr)) {
				childDomArr+=','+domId;
			} else {
				childDomArr = domId;
			}
			RedisUtil.addString(tableId+"_"+pageId+"_childDom", childDomArr);
			RedisUtil.addString(domId+"_fieldName",zdyName);
		}
		if (!ifChild) {
			bodyBuffer.append("<div class='layui-inline'>");
			bodyBuffer.append("<label class='layui-form-label'>"+fieldDbo.get("fieldCnName")+"</label>");
			bodyBuffer.append("<div class='layui-input-inline'>");
		}
		StringBuffer verifyBuffer = new StringBuffer();
		if (ifMust==1 && fieldTableId==tableId && fieldType>0) {//是否必填
			verifyBuffer.append("required");
		}
		
		//字段基础信息
		String fieldBasic = " fieldType='"+fieldType+"' id='"+zdyName+"' name='"+zdyName+"' "+appendLayVerify(fieldType,verifyBuffer);
		if (ifUpdate==1 || fieldTableId!=tableId || fieldType<0) {//不可修改
			fieldBasic+=" readonly='readonly' ";
		}
		if (ifChild) {
			String fieldAlias = (String)fieldDbo.get("fieldAlias");//字段别名
			fieldAlias = MyStringUtils.isBlank(fieldAlias)?dataName:fieldAlias;
			fieldBasic+=" dataName='"+fieldAlias+"' ";
		}
		switch (fieldType) {
		case 0:
			bodyBuffer.append("<input type='hidden'  autocomplete='off' class='layui-input' "+fieldBasic+" value='val_{"+dataName+"}'>");
			break;
		case -1:
		case 1://字符
		case 3://身份证
		case 4://email
		case 5://手机
		case 6://电话
		case 7://规则生成
		case 8://名称
		case 9://数据
		case 18://用户名
			if (inputStyle==0) {
				bodyBuffer.append("<input type='text' "+fieldBasic+"  autocomplete='off' class='layui-input'  value='val_{"+dataName+"}'>");
			} else {
				bodyBuffer.append("<textarea  class='editor' "+fieldBasic+"></textarea>");
			}
			break;
		case 19://密码
			bodyBuffer.append("<input type='password' "+fieldBasic+"  autocomplete='off' class='layui-input' value='val_{"+dataName+"}'>");
			break;
		case 2://富文本
			bodyBuffer.append("<textarea  class='editor' "+fieldBasic+" style='display: none;'></textarea>");
			break;
		case 10://日期
			String dateType = (String)fieldDbo.get("dateType");
			dateType = MyStringUtils.isBlank(dateType)?"yyyy-MM-dd":dateType;
			String timeType = "datetime";
			if (dateType.endsWith("yy")) {
				timeType = "year";
			} else if (dateType.endsWith("MM")) {
				timeType = "month";
			} else if (dateType.endsWith("dd")) {
				timeType = "date";
			} else if (dateType.startsWith("HH")) {
				timeType = "time";
			}
			bodyBuffer.append("<input type='text' "+fieldBasic+" timeType='"+timeType+"' dateType='"+dateType+"'  autocomplete='off' class='layui-input date' fieldType='"+fieldType+"' id='"+zdyName+"' name='"+zdyName+"' value='val_{"+dataName+"}'>");
			break;
		case 11://单值选择项
		case 17://星期
			bodyBuffer.append("<select "+fieldBasic+"  lay-search>");
			bodyBuffer.append("<option value=''>--请选择--</option>");
			bodyBuffer.append("DIC_{"+(fieldType==17?"XQ":fieldDbo.get("dicParent"))+"}");
			bodyBuffer.append("</select>");
			break;
		case 12://多值选择项
			bodyBuffer.append("<select "+fieldBasic+"  lay-search xm-select='"+zdyName+"' xm-select-show-count='2'>");
			bodyBuffer.append("DIC_{"+(fieldType==17?"XQ":dicParent)+"}");
			bodyBuffer.append("</select>");
			break;
		case 13://单文档
		case 14://多文档
			bodyBuffer.append("<button type='button' class='layui-btn fileUpload' "+fieldBasic+"><i class='layui-icon'>&#xe67c;</i>上传图片</button>");
			break;
		case 15://内部对象
		case 21://父级引用
		case 16://内部对象多值
			fieldBasic = " fieldType='"+fieldType+"' id='"+zdyName+"' name='"+zdyName+"' ";
			if (addStyle==1) {
				bodyBuffer.append("<button type='button' "+fieldBasic+" class='layui-btn'><i class='layui-icon'>&#xe608;</i>添加</button>");
			} else {
				if (ifChild) {
					String fieldAlias = (String)fieldDbo.get("fieldAlias");//字段别名
					fieldAlias = MyStringUtils.isBlank(fieldAlias)?dataName:fieldAlias;
					fieldBasic+=" dataName='"+fieldAlias+"' ";
				}
				bodyBuffer.append("<input type='hidden' "+fieldBasic+"  autocomplete='off' class='layui-input'  value='val_{"+dataName+"}'>");
				fieldBasic = " id='"+zdyName+"_choose' name='"+zdyName+"_choose' "+appendLayVerify(fieldType,verifyBuffer);
				bodyBuffer.append("<input type='text' "+fieldBasic+" placeholder='点击选择' title='点击选择' autocomplete='off' class='layui-input'  value='val_{"+dataName+"}'>");
			}
			appendInnerObjJs(fieldDbo,fieldType,tableId,pageId,zdyName,jsBuffer);
			break;
		default:
			bodyBuffer.append("<input type='text' "+fieldBasic+"  autocomplete='off' class='layui-input'  value='val_{"+dataName+"}'>");
			break;
		}
		if (!ifChild) {
			bodyBuffer.append("</div></div>");
		}
	}
	
	/**
	 * 拼接内部對象内容js
	 * @param field
	 * @param inputName
	 * @param jsBuffer
	 */
	public static void appendInnerObjJs (DBObject field,int fieldType,int tableId,int pageId,String inputName,StringBuffer jsBuffer) {
		//获取弹层对象
		DB db = MongoDbFileUtil.getDb();
		DBCollection pageDbc = db.getCollection("page");
		DBCollection fieldDbc = db.getCollection("field");
		//初始化js
		int addStyle = MyNumberUtils.toInt(field.get("addStyle"));//添加形式
		int chooseType = MyNumberUtils.toInt(field.get("chooseType"));//选择形式
		int childPageId = MyNumberUtils.toInt(field.get("childPageId"));//子页面id
		if (childPageId<=0) {
			return;
		}
		DBObject childPageDbo = pageDbc.findOne(new BasicDBObject("pageId",childPageId));//子页面
		List<DBObject> parentRelationField = new ArrayList<DBObject>();
		if (fieldType==16) {//内部对象多值
			//获取内部对象多值字段
			DBObject dzObj = fieldDbc.findOne(new BasicDBObject("fieldId",field.get("fieldId")));
			//获取内部对象多值关联字段
			DBObject dzGlObj = fieldDbc.findOne(new BasicDBObject("fieldId",dzObj.get("relationFieldId")));
			dzGlObj.put("preveDataId", "mainId");
			parentRelationField.add(dzGlObj);
		}
		childPageDbo.put("parentAddStyle", addStyle);
		childPageDbo.put("parentPageId", pageId);
		if (addStyle==0 || addStyle==2) {//选择添加
			//将子表格字段追加到弹层
			int dialogPage = MyNumberUtils.toInt(field.get("dialogPage"));//弹层id
			DBObject dialogPageDbo = pageDbc.findOne(new BasicDBObject("pageId",dialogPage));
			String firstAlias = "";//第一个字段别名
			if (dialogPageDbo!=null && childPageDbo!=null) {
				int diaTableId =  MyNumberUtils.toInt(dialogPageDbo.get("tableId")); 
				List<DBObject> diaList = (List<DBObject>)dialogPageDbo.get("showFieldList");//弹层字段
				List<String> diaIdList = new ArrayList<String>();
				Map<String,String> diaZdyNames = new HashMap<String, String>();
				for (DBObject fd : diaList) {
					int tmpFieldType = MyNumberUtils.toInt(fd.get("fieldType"));
					diaIdList.add((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId")));
					String zdyName = (String)fd.get("zdyName");
					diaZdyNames.put((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId")),MyStringUtils.notBlank(zdyName)?zdyName:"AFM_"+fd.get("fieldId"));
				}
				List<DBObject> needAddDialog = new ArrayList<DBObject>();//需要往弹层追加的字段
				List<DBObject> childList = (List<DBObject>)childPageDbo.get("showFieldList");//子表格字段
				List<DBObject> childHideList = (List<DBObject>)childPageDbo.get("hideFieldList");//子表格字段
				//处理显示字段
				for (int i=0;i<childList.size();i++) {
					DBObject fd = childList.get(i);
					int tmpFieldType = MyNumberUtils.toInt(fd.get("fieldType"));
					int tmpTableId =  MyNumberUtils.toInt(fd.get("tableId"));
					if (tmpTableId!=diaTableId && tmpTableId>0) {
						continue;
					}
					//只记录第一个字段
					if (MyStringUtils.isBlank(firstAlias)) {
						String zdyName = (String)fd.get("zdyName");//自定义别名
						firstAlias = MyStringUtils.notBlank(zdyName)?zdyName:("AFM_"+fd.get("fieldId"));
					}
					//判断弹层是否已包含此字段
					if(!diaIdList.contains((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId")))) {
						fd.put("ifShow", 1);//追加字段默认为不显示
						needAddDialog.add(fd);
					} else {
						fd.put("fieldAlias", diaZdyNames.get((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId"))));
					}
				}
				//处理隐藏的字段
				for (int i=0;i<childHideList.size();i++) {
					DBObject fd = childHideList.get(i);
					int tmpFieldType = MyNumberUtils.toInt(fd.get("fieldType"));
					int tmpTableId =  MyNumberUtils.toInt(fd.get("tableId"));
					if (tmpTableId!=diaTableId && tmpTableId>0) {
						continue;
					}
					//只记录第一个字段
					if (MyStringUtils.isBlank(firstAlias)) {
						String zdyName = (String)fd.get("zdyName");//自定义别名
						firstAlias = MyStringUtils.notBlank(zdyName)?zdyName:("AFM_"+fd.get("fieldId"));
					}
					//判断弹层是否已包含此字段
					if(!diaIdList.contains((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId")))) {
						fd.put("ifShow", 1);//追加字段默认为不显示
						needAddDialog.add(fd);
					} else {
						fd.put("fieldAlias", diaZdyNames.get((tmpFieldType<0?"tmp_":"")+MyNumberUtils.toInt(fd.get("fieldId"))));
					}
				}
				childPageDbo.put("showFieldList", childList);
				childPageDbo.put("hideFieldList", childHideList);
				//将需要回显的列追加到弹层
				diaList.addAll(needAddDialog);
				//刷新弹层
				PageUtils.refreashPage(dialogPage);
				
				if (fieldType==16) {//内部对象多值
					String dialogField = (String)field.get("dialogField");
					//获取外键字段
					DBObject wjField = fieldDbc.findOne(new BasicDBObject("fieldId",MyNumberUtils.toInt(dialogField.split("_")[0])));
					wjField.put("fieldAlias", "ID");
					parentRelationField.add(wjField);
					childPageDbo.put("parentRelationField", parentRelationField);
					pageDbc.save(childPageDbo);
				}
				//刷新子表格
				PageUtils.refreashPage(childPageId);
			}
			if (chooseType==0) {//弹层
				jsBuffer.append("$(\"input[name='"+inputName+"_choose']\").wqyDialog({");
				jsBuffer.append("tableId:"+dialogPageDbo.get("tableId"));
				jsBuffer.append(",pageId:"+dialogPage);
				jsBuffer.append(",isMulti:"+(fieldType==16?"true":"false"));
				jsBuffer.append(",ifUpdate:"+field.get("ifUpdate"));
				jsBuffer.append(",dataMaxNum:"+field.get("dataMaxNum"));
				jsBuffer.append(",firstAlias:'"+firstAlias+"'");
				appendChildParam(pageDbc, childPageId, jsBuffer);
				jsBuffer.append("});");
			} else if (chooseType==1) {//下拉树
				
			} else if (chooseType==2) {//下拉框
				
			}
		} else {//点击+添加
			childPageDbo.put("parentRelationField", parentRelationField);
			pageDbc.save(childPageDbo);
			//刷新子表格
			PageUtils.refreashPage(childPageId);
			
			jsBuffer.append("$(\"button[name='"+inputName+"']\").wqyAddChild({");
			jsBuffer.append("ifUpdate:"+field.get("ifUpdate"));
			jsBuffer.append(",dataMaxNum:"+field.get("dataMaxNum"));
			appendChildParam(pageDbc, childPageId, jsBuffer);
			jsBuffer.append("});");
		}
	}

	
	/**
	 * 拼接子页面的回调
	 * @param childPageId
	 * @param jfBuffer
	 */
	public static void appendChildParam (DBCollection pageDbc,int childPageId,StringBuffer jsBuffer) {
		DBObject childPageDbo = pageDbc.findOne(new BasicDBObject("pageId",childPageId));
		List<DBObject> showFieldList = (List<DBObject>)childPageDbo.get("showFieldList");
		if (MyCollectionUtils.notEmpty(showFieldList) && showFieldList.size()>1) {//至少有两个字段才出现子表格
			jsBuffer.append(",ifChildTable:true");
			jsBuffer.append(",thBody:\""+childPageDbo.get("thBody")+"\"");
			jsBuffer.append(",trBody:\""+childPageDbo.get("trBody")+"\"");
			//拼接回调js
			String jsBody = (String)childPageDbo.get("jsBody");
			jsBody=MyStringUtils.isBlank(jsBody)?"":jsBody;
			jsBuffer.append(",jsCallBack:function(data,ele) {"+jsBody+"}");
		}
	}
	
	/**
	 * 拼接验证的html
	 * @param verifyBuffer
	 * @return
	 */
	public static String appendLayVerify (int fieldType,StringBuffer verifyBuffer) {
		if (fieldType == 3) {//身份证
			verifyBuffer.append("|identity");
		} else if (fieldType == 4) {//email
			verifyBuffer.append("|email");
		} else if (fieldType == 5) {//手机
			verifyBuffer.append("|phone");
		} else if (fieldType == 6) {//电话
			verifyBuffer.append("|tel");
		} else if (fieldType == 9) {
			verifyBuffer.append("|number");
		} else if (fieldType == 19) {//密码
			verifyBuffer.append("|password");
		}
		String verifyStr = verifyBuffer.toString();
		if (MyStringUtils.notBlank(verifyStr)) {
			verifyStr = verifyStr.startsWith("|")?verifyStr.substring(verifyStr.indexOf("|")):verifyStr;
			verifyStr = "lay-verify='"+verifyStr+"'";
		}
		return verifyStr;
	}
	
	/**
	 * 拼接页面js
	 * @param pageDbo
	 */
	public static void appendAddPageJs (DBObject pageDbo,String  domId,StringBuffer jsBuffer){
		jsBuffer.append("<script>");
		jsBuffer.append("var layer = layui.layer;");
		jsBuffer.append("var parent$ = window.parent.layui.jquery;");
		jsBuffer.append("var layuiForm = layui.form;");
		jsBuffer.append("var laydate = layui.laydate;");
		jsBuffer.append("var formSelects = layui.formSelects;");
		jsBuffer.append("layer.config({");
		jsBuffer.append("	skin:'layui-layer-molv'");
		jsBuffer.append("});");
		//初始化日期选择框
		jsBuffer.append("$('.date').each(function (i,one) {" +
				"laydate.render({" +
				"elem:'#'+$(one).attr('id')," +
				"type: $(one).attr('timeType'),"+
				"format:$(one).attr('dateType')" +
				"});});");
		
		
		//监听下拉框选中事件
		jsBuffer.append("layuiForm.on('select',function (data) {");
		jsBuffer.append("	$(this).parent().parent().parent().find('select').change();");
		jsBuffer.append("});");
		
		//添加保存监听
		jsBuffer.append("layuiForm.on('submit(savePage)',function (data) {");
		jsBuffer.append("	var saveBtn = $(parent$.find('.layui-layer-btn0'));");
		jsBuffer.append("if (saveBtn.hasClass('layui-btn-disabled')) {");
		jsBuffer.append("layer.msg('正在提交中，请勿重复提交！',{icon: 5});");
		jsBuffer.append("return false;");
		jsBuffer.append("	}");
		jsBuffer.append("	saveBtn.addClass('layui-btn-disabled');");
		jsBuffer.append("$.ajax({");
		jsBuffer.append("url:'addPage_save?tableId="+domId.split("_")[0]+"&pageId="+domId.split("_")[1]+"',");
		jsBuffer.append("type:'post',");
		jsBuffer.append("      data:wqySerSerialize($('#pageForm_"+domId+"')),");
		jsBuffer.append("     success:function(result){");
		jsBuffer.append("      if(result==1){");
		jsBuffer.append("       parent.layer.closeAll();");
		jsBuffer.append("       parent.layer.msg('保存成功！',{icon: 6,offset: 't'});");
		jsBuffer.append("       parent.initTable ();");
		jsBuffer.append("       parent.refreshTree();");
		jsBuffer.append("      } else {");
		jsBuffer.append("  	  layer.msg('保存失败！',{icon: 5,offset: 'rt'});");
		jsBuffer.append("  	  saveBtn.removeClass('layui-btn-disabled');");
		jsBuffer.append("	  }");
		jsBuffer.append("    },error:function (result) {");
		jsBuffer.append("  	  layer.msg('保存失败！',{icon: 5,offset: 'rt'});");
		jsBuffer.append("  	  saveBtn.removeClass('layui-btn-disabled');");
		jsBuffer.append("	  }");
		jsBuffer.append("  });");
		jsBuffer.append("  return false;");
		jsBuffer.append("});");
	}

}
