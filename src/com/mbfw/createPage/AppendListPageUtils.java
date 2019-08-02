package com.mbfw.createPage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class AppendListPageUtils {
	
	/**
	 * 拼接头部
	 * @param headerBufer
	 */
	public static void appendHeader (StringBuffer headerBufer) {
		headerBufer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		headerBufer.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		headerBufer.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"layuiadmin/layui/css/layui.css\">");
		headerBufer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/formSelects/formSelects-v4.css\">");
		headerBufer.append("</head>");
	}
	
	/**
	 * 拼接js引用
	 * @param bodyJsBuffer
	 */
	public static void appendBasicJs (StringBuffer bodyJsBuffer) {
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"layuiadmin/layui/layui.all.js\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/js/jquery-1.7.2.js\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/page-commonset.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/formSelects/formSelects-v4.js\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/common.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/ztree/js/jquery.ztree.core.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/ztree/js/jquery.ztree.excheck.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/ztree/js/jquery.ztree.exedit.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"static/operamask-ui.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/wqy-dialog.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/wqy-addChild.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/wqy-treeDialog.js?version=${verson}\"></script>");
		bodyJsBuffer.append("<script type=\"text/javascript\" src=\"zdyJs/wqy-select.js?version=${verson}\"></script>");
	}
	
	/**
	 * 拼接搜索条件html
	 * @param bodyBuffer
	 * @param searchList
	 */
	public static void appendPageHtml (DBObject pageDbo,StringBuffer bodyBuffer, List<DBObject> searchList,DBCollection fieldDbc, String domId) {
		String tableId = domId.split("_")[0];
		String pageId = domId.split("_")[1];
		bodyBuffer.append("<div class=\"layui-fluid\">");
		bodyBuffer.append("<div class=\"layui-card\">");
		bodyBuffer.append("<div class=\"layui-form layuiadmin-card-header-auto\">");
		bodyBuffer.append("<form class=\"layui-form\" id='listForm_"+domId+"'>");
		bodyBuffer.append("<div class=\"layui-collapse\">");
		bodyBuffer.append("  <div class=\"layui-colla-item\">");
		bodyBuffer.append("    <h2 class=\"layui-colla-title\">搜索条件</h2>");
		bodyBuffer.append("    <div class=\"layui-colla-content\">");
		bodyBuffer.append("<div class=\"layui-form-item\">");
		if (MyCollectionUtils.notEmpty(searchList)) {
			for (int i=0;i<searchList.size();i++) {
				DBObject field = searchList.get(i);
				Integer fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
				Integer searchType = MyNumberUtils.toInt(field.get("searchType"));//搜索条件字段类型
				String  dicParent ="";//数据字典父级
				String zdyName = (String)field.get("zdyName");//自定义别名
				zdyName = MyStringUtils.notBlank(zdyName)?zdyName:"AFM_"+field.get("fieldId");
				if (",1,3,4,5,6,7,8,18,15,16,21,22,24,".indexOf(","+fieldType+",")>=0 || searchType==1) {//字符类型
					searchType = 1;
				} else if (",9,".indexOf(","+fieldType+",")>=0 || searchType==2) {//数据
					searchType = 2;
				} else if (",10,23,25,".indexOf(","+fieldType+",")>=0 || searchType==3) {//日期
					searchType = 3;
				} else if (",11,12,17,".indexOf(","+fieldType+",")>=0 || searchType==4 || searchType==5) {//下拉框
					dicParent = field.get("dicParent")+"";
					if (fieldType==12) {//获取多值选择项对应的数据字典值
						DBObject args1 = new BasicDBObject();
						args1.put("fieldType", 11);
						args1.put("tableId", field.get("relationTableId"));
						DBObject dzInnerdzDbo = fieldDbc.findOne(args1);
						dicParent = dzInnerdzDbo.get("dicParent")+"";
					}
					dicParent = fieldType == 17 || searchType==5?"XQ":dicParent;
					searchType = 4;
				}
				field.put("zdyName", zdyName);
				bodyBuffer.append("<div class=\"layui-inline\">");
				bodyBuffer.append("<input type=\"hidden\" name=\"searchName\" value=\""+zdyName+"\">");
				bodyBuffer.append("<input type=\"hidden\" name=\""+zdyName+"_searchType\" value=\""+searchType+"\">");
				bodyBuffer.append("<label class=\"layui-form-label\">"+field.get("fieldCnName")+"</label>");
				if (searchType == 1) {//字符类型
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 140px;\">");
					bodyBuffer.append("<select name='"+zdyName+"_oneType'>");
					bodyBuffer.append("<option value='1'>包含</option>");
					bodyBuffer.append("<option value='2'>不包含</option>");
					bodyBuffer.append("<option value='3'>等于</option>");
					bodyBuffer.append("<option value='4'>起始</option>");
					bodyBuffer.append("<option value='5'>终止</option>");
					bodyBuffer.append("<option value='6'>为空</option>");
					bodyBuffer.append("<option value='7'>不为空</option>");
					bodyBuffer.append("</select>");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 200px;\">");
					bodyBuffer.append("<input type=\"text\" id=\""+zdyName+"\" name=\""+zdyName+"\" placeholder=\"请输入\" autocomplete=\"off\" class=\"layui-input\">");
					bodyBuffer.append("</div>");
				} else if (searchType==2) {//数据
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 80px;\">");
					bodyBuffer.append("<select name='"+zdyName+"_oneType'>");
					bodyBuffer.append("<option value='1'>大于</option>");
					bodyBuffer.append("<option value='2'>大于等于</option>");
					bodyBuffer.append("<option value='3'>等于</option>");
					bodyBuffer.append("<option value='4'>为空</option>");
					bodyBuffer.append("<option value='5'>不为空</option>");
					bodyBuffer.append("</select>");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 80px;\">");
					bodyBuffer.append("<input type=\"text\" id=\""+zdyName+"_one\" name=\""+zdyName+"_one\" placeholder=\"请输入\" autocomplete=\"off\" class=\"layui-input\">");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 80px;\">");
					bodyBuffer.append("<select name='"+zdyName+"_twoType'>");
					bodyBuffer.append("<option value='1'>小于</option>");
					bodyBuffer.append("<option value='2'>小于等于</option>");
					bodyBuffer.append("<option value='3'>等于</option>");
					bodyBuffer.append("<option value='4'>为空</option>");
					bodyBuffer.append("<option value='5'>不为空</option>");
					bodyBuffer.append("</select>");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 80px;\">");
					bodyBuffer.append("<input type=\"text\" id=\""+zdyName+"_two\" name=\""+zdyName+"_two\" placeholder=\"请输入\" autocomplete=\"off\" class=\"layui-input\">");
					bodyBuffer.append("</div>");
				} else if (searchType==3) {//日期
					String dateType = (String)field.get("dateType");//字段类型
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
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 170px;\">");
					bodyBuffer.append("<input type=\"text\" timeType='"+timeType+"' dateType='"+dateType+"' max=\"#"+zdyName+"_end\" id=\""+zdyName+"_start\" name=\""+zdyName+"_start\" placeholder=\"请选择\" autocomplete=\"off\" class=\"layui-input date\">");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\"  style=\"width: 170px;\">");
					bodyBuffer.append("<input type=\"text\" timeType='"+timeType+"' dateType='"+dateType+"' min=\"#"+zdyName+"_start\" id=\""+zdyName+"_end\" name=\""+zdyName+"_end\" placeholder=\"请选择\" autocomplete=\"off\" class=\"layui-input date\">");
					bodyBuffer.append("</div>");
				} else if (searchType==4) {//下拉框
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 140px;\">");
					bodyBuffer.append("<select name='"+zdyName+"_oneType'>");
					bodyBuffer.append("<option value='1'>包含</option>");
					bodyBuffer.append("<option value='2'>不包含</option>");
					bodyBuffer.append("</select>");
					bodyBuffer.append("</div>");
					bodyBuffer.append("<div class=\"layui-input-inline\" style=\"width: 200px;\">");
					bodyBuffer.append("<select name='"+zdyName+"'  lay-search xm-select='"+zdyName+"' xm-select-show-count='2'>");
					bodyBuffer.append("DIC_{"+(fieldType==17?"XQ":field.get("dicParent"))+"}");
					bodyBuffer.append("</select>");
					bodyBuffer.append("</div>");
				}
				bodyBuffer.append("</div>");
			}
		}
		bodyBuffer.append("</div>");
		bodyBuffer.append("</div></div></div>");
		bodyBuffer.append("</form>");
		
		//按钮设置区域
		bodyBuffer.append("<div class=\"layui-collapse\">");
		bodyBuffer.append("  <div class=\"layui-colla-item\">");
		bodyBuffer.append("    <h2 class=\"layui-colla-title\">页面设置</h2>");
		bodyBuffer.append("    <div class=\"layui-colla-content\">");
		bodyBuffer.append("        <div class=\"layui-btn-container\">");
		bodyBuffer.append("	      	<button class=\"layui-btn\" onclick='pageContentSet("+tableId+","+pageId+")'>列表显示设置</button> ");
		bodyBuffer.append("	      	<button class=\"layui-btn\" onclick='pageSearchSet("+tableId+","+pageId+")'>搜索条件设置</button> ");
		bodyBuffer.append("	    	<button class=\"layui-btn\" onclick='pageSqlSet("+tableId+","+pageId+")'>列表sql设置</button>");
		bodyBuffer.append("	      	<button class=\"layui-btn\" onclick='pageHtmlSet("+tableId+","+pageId+")'>列表html编辑</button> ");
		bodyBuffer.append("	    	<button class=\"layui-btn\" onclick='pageLimitSet("+tableId+","+pageId+")'>数据权限设置</button>");
		bodyBuffer.append("	      	<button class=\"layui-btn\" onclick='pageButtonSet("+tableId+","+pageId+")'>列表按钮条设置</button> ");
		bodyBuffer.append("	      	<button class=\"layui-btn\" onclick='pageListButton("+tableId+","+pageId+")'>数据按钮设置</button> ");
		bodyBuffer.append("</div></div></div></div>");
		
		bodyBuffer.append("</div>");
		
		bodyBuffer.append("<div class=\"layui-card-body\">");
		bodyBuffer.append("<script type=\"text/html\" id=\"batchToolbar\" lay-filter=\"batchToolbar\">");
		//搜索块按钮
		bodyBuffer.append("		<div class=\"layui-btn-container\">");
		bodyBuffer.append("        <button class=\"layui-btn layuiadmin-btn-forum-list\" lay-submit=\"\" lay-filter=\"LAY-app-forumlist-search\" onclick=\"initTable()\">");
		bodyBuffer.append("	      <i class=\"layui-icon layui-icon-search layuiadmin-button-btn\"></i>");
		bodyBuffer.append("	    </button>");
		bodyBuffer.append(appendButtonHtml((List<DBObject>)pageDbo.get("buttonSet"), domId, false));
		bodyBuffer.append("		</div>");
		bodyBuffer.append("</script>");
		bodyBuffer.append("<table id=\"listTable_"+domId+"\" lay-filter=\"listTable_"+domId+"\" lay-filter=\"listTable_"+domId+"\"></table>");
		bodyBuffer.append("</div>");
		
		bodyBuffer.append("</div>");
		bodyBuffer.append("</div>");
			
	}
	
	/**
	 * 展示数据列表
	 * @param bodyBuffer
	 * @param showFieldList
	 */
	public static void appendPageJs (DBObject pageDbo,StringBuffer jsBuffer, List<DBObject> showFieldList,Object tableId,Object pageId) {
		jsBuffer.append("<script>");
		jsBuffer.append("var table = layui.table; ");
		jsBuffer.append("var layer = layui.layer;");
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
		
		jsBuffer.append("initTable ();");//初始化表格
		
		//回车查询
		jsBuffer.append("$(\"#listForm_"+tableId+"_"+pageId+"\").keydown(function(event){");
		jsBuffer.append("	if(event.keyCode == 13){ ");
		jsBuffer.append("		initTable ();");
		jsBuffer.append("	}");
		jsBuffer.append("});");
		
		jsBuffer.append("function initTable () {");
		jsBuffer.append("table.render({");
		jsBuffer.append("elem: '#listTable_"+tableId+"_"+pageId+"'");
		jsBuffer.append(",height: 'full-100'");
		jsBuffer.append(",cellMinWidth: 80");
		jsBuffer.append(" ,toolbar: '#batchToolbar'");
		jsBuffer.append(" ,url: 'listCore_ajaxList?tableId="+tableId+"&pageId="+pageId+"&'+$(\"#listForm_"+tableId+"_"+pageId+"\").serialize()");
		jsBuffer.append(" ,page: true ");
		jsBuffer.append(",cols: [[ ");
		jsBuffer.append(" {type: 'checkbox', fixed: 'left'}    ");
		jsBuffer.append(",{type: 'numbers', title: '序号', fixed: 'left'}    ");
		if (MyCollectionUtils.notEmpty(showFieldList)) {
			int fixedRowNumber=MyNumberUtils.toInt(pageDbo.get("fixedRowNumber"));//固定前几列
			for (int i=0;i<showFieldList.size();i++) {
				DBObject field = showFieldList.get(i);
				Integer ifShow = MyNumberUtils.toInt(field.get("ifShow"));//是否显示
				if (ifShow==1) {
					continue;
				}
				String zdyName = (String)field.get("zdyName");//自定义别名
				zdyName = MyStringUtils.notBlank(zdyName)?zdyName:"AFM_"+field.get("fieldId");
				String zdyJs = (String)field.get("zdyJs");//自定义js
				int fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
				if (fieldType==11 || fieldType==12) {
					zdyName = "DIC_"+field.get("dicParent")+"_"+zdyName;
				} else if (fieldType==17) {
					zdyName = "XQ_"+zdyName;
				}
				int width = MyNumberUtils.toInt(field.get("width"));//自定义宽度
				Integer ifOrder = MyNumberUtils.toInt(field.get("ifOrder"));//是否可以排序
				jsBuffer.append(",{field: '"+zdyName+"', title: '"+field.get("fieldCnName")+"'");
				if (ifOrder==1) {
					jsBuffer.append(",sort: true");
				}
				if (width>0) {
					jsBuffer.append(", width:"+width+"");
				} else {
					jsBuffer.append(", width:150");
				}
				if (i<fixedRowNumber) {
					jsBuffer.append(", fixed: 'left'");
				}
				if (MyStringUtils.notBlank(zdyJs)) {
					jsBuffer.append(",templet: function(rowData){");
					jsBuffer.append(zdyJs);
					jsBuffer.append("}");
				}
				jsBuffer.append("}");	
			}
		}
		List<DBObject> listButtonSet = (List<DBObject>)pageDbo.get("listButtonSet");
		if (MyCollectionUtils.notEmpty(listButtonSet)) {
			jsBuffer.append(",{fixed: 'right', width: 165, title:'操作', align:'left',templet: function(rowData){");
			//列表按钮
			jsBuffer.append("return '"+appendButtonHtml(listButtonSet, tableId+"_"+pageId, true)+"';");
			jsBuffer.append("}}");
		}
		jsBuffer.append("]]");
		jsBuffer.append(",done:function () {");
    	//给操作按钮赋值下标
		jsBuffer.append("	$('.operaBtn').each(function (i,one) {");
		jsBuffer.append("$(one).attr('index',$(one).closest('tr').attr('data-index'));");
		jsBuffer.append("});	");
		jsBuffer.append("}");
		jsBuffer.append("		  });");
		jsBuffer.append("}");
		
		//拼接按钮html
		StringBuffer buttonJsBuffer = new StringBuffer();
		appendAllButtonJs((List<DBObject>)pageDbo.get("buttonSet"), tableId+"_"+pageId, false, buttonJsBuffer);
		appendAllButtonJs(listButtonSet, tableId+"_"+pageId, true, buttonJsBuffer);
		jsBuffer.append(buttonJsBuffer);
		jsBuffer.append("</script>");
		jsBuffer.append("<div id='openPageDiv'></div>");
		jsBuffer.append("</body>");
		jsBuffer.append("</html>");
	}
	
	/**
	 * 拼接button
	 * @param buttonList
	 * @param domId
	 * @return
	 */
	public static String appendButtonHtml (List<DBObject> buttonList,String domId,boolean ifList) {
		StringBuffer btnBuffer = new StringBuffer();
		if (MyCollectionUtils.notEmpty(buttonList)) {
			for (DBObject button : buttonList) {
				if (ifList) {
					btnBuffer.append("<a class=\"layui-btn layui-btn-xs operaBtn\" onclick=\"btn_click_"+button.get("buttonId")+"(this)\">"+button.get("buttonName")+"</a>");
				} else {
					btnBuffer.append("<button class=\"layui-btn layuiadmin-btn-forum-list\" onclick=\"btn_click_"+button.get("buttonId")+"()\">"+button.get("buttonName")+"</button>");
				}
			}
		}
		return btnBuffer.toString();
	}
	
	/**
	 * 拼接buttonJs
	 * @param buttonList
	 * @param domId
	 * @return
	 */
	public static String appendAllButtonJs (List<DBObject> buttonList,String domId,boolean ifList,StringBuffer buttonJsBuffer) {
		if (MyCollectionUtils.notEmpty(buttonList)) {
			DB db = MongoDbFileUtil.getDb();
			DBCollection tableDbc = db.getCollection("table");
			DBCollection fieldDbc = db.getCollection("field");
			for (DBObject button : buttonList) {
				//拼接按钮js
				appendButtonJs(tableDbc,fieldDbc,button, domId, ifList, buttonJsBuffer);
			}
		}
		return buttonJsBuffer.toString();
	}
	
	/**
	 * 拼接操作按钮的js
	 * @param button
	 * @param domId
	 * @param buttonBuffer
	 */
	public static void appendButtonJs (DBCollection tableDbc,DBCollection fieldDbc,DBObject button,String domId,boolean ifList,StringBuffer buttonJsBuffer) {
		int buttonType = MyNumberUtils.toInt(button.get("buttonType"));//按钮类型
		buttonJsBuffer.append("function btn_click_"+button.get("buttonId")+" (obj) {");
		if (ifList) {//列表的拼接获取rowData
			buttonJsBuffer.append("var rowIndex = $(obj).attr('index');");
			buttonJsBuffer.append("var rowData = table.cache.listTable_"+domId+"[rowIndex];");
		}
		String url = "";//URL地址
		String urlParam = null;//自定义参数;
		boolean ifOnlyAjax = MyNumberUtils.toInt(button.get("openStyle"))==2?true:false;//是否只走ajax操作
		boolean ifNeedPageSetBtn = false;//是否需要页面设置按钮
		boolean ifBatch = false;//是否是批量操作
		String pageSetFunName = "";//页面设置的方法名
		String buttonKey = domId+"_"+button.get("buttonId");
		if (buttonType==1) {//添加
			ifNeedPageSetBtn = true;
			pageSetFunName = "toAddPageSet("+button.get("buttonTableId")+","+button.get("buttonPageId")+","+buttonType+")";
			url = "addPage_toAdd?pageId="+button.get("buttonPageId")+"&tableId="+button.get("buttonTableId");
		} else if (buttonType==2) {//批量修改
			ifBatch = true;
			ifNeedPageSetBtn = true;
			pageSetFunName = "toBatchUpdatePageSet("+button.get("buttonTableId")+","+button.get("buttonPageId")+")";
			url = "addPage_toBatchUpdate?pageId="+button.get("buttonPageId")+"&tableId="+button.get("buttonTableId");
		} else if (buttonType==3) {//批量删除
			ifBatch = true;
			ifOnlyAjax = true;
			url = "delete_delete?buttonKey="+buttonKey;
		} else if (buttonType==4) {//导入
			url = "export_toImport?buttonKey="+buttonKey;
		} else if (buttonType==5) {//导出
			ifOnlyAjax = true;
			url = "export_toExport?buttonKey="+buttonKey;
		} else if (buttonType==6) {//批量自定义
			urlParam = (String)button.get("urlParam");//自定义参数;
			url = (String)button.get("pageUrl");
			url += (url.indexOf("?")>0?"&":"?")+"domId="+domId; 
			ifBatch = MyStringUtils.notBlank(urlParam)?true:false;//有参数需要判断
		} else if (buttonType==7) {//删除
			ifOnlyAjax = true;
			url = "delete_delete?buttonKey="+buttonKey;
		} else if (buttonType==8) {//关联添加
			ifNeedPageSetBtn = true;
			pageSetFunName = "toAddPageSet("+button.get("buttonTableId")+","+button.get("buttonPageId")+","+buttonType+")";
			url = "addPage_toRelationAdd?pageId="+button.get("buttonPageId")+"&tableId="+button.get("buttonTableId");
		} else if (buttonType==9) {//修改
			ifNeedPageSetBtn = true;
			pageSetFunName = "toUpdatePageSet("+button.get("buttonTableId")+","+button.get("buttonPageId")+","+buttonType+")";
			url = "updatePage_toUpdate?pageId="+button.get("buttonPageId")+"&tableId="+button.get("buttonTableId");
		} else if (buttonType==10) {//查看
			ifNeedPageSetBtn = true;
			pageSetFunName = "toUpdatePageSet("+button.get("buttonTableId")+","+button.get("buttonPageId")+","+buttonType+")";
			url = "seePage_toSee?pageId="+button.get("buttonPageId")+"&tableId="+button.get("buttonTableId");
		} else if (buttonType==11) {//打印
			url = "print_toPrint?buttonKey="+buttonKey;
		} else if (buttonType==12) {//直接操作
			url = "direct_directOpera?buttonKey="+buttonKey;
		} else if (buttonType==13) {//列表自定义
			urlParam = (String)button.get("urlParam");//自定义参数;
			url = (String)button.get("pageUrl");
			url += (url.indexOf("?")>0?"&":"?")+"domId="+domId; 
			if (MyStringUtils.notBlank(urlParam)) {
				Pattern fieldPattern = Pattern.compile("\\{[a-zA-Z0-9_-]+\\}");
		        Matcher fieldMatch =  fieldPattern.matcher(urlParam);
		        while(fieldMatch.find()) {
		            String group = fieldMatch.group();
		            group = group.trim();
		            urlParam = urlParam.replace(group, "'+rowData."+(group.substring(1, group.length()-1))+"+'");
		        }
		        url=url+"&"+urlParam;
			}
		}
		int buttonPageId = MyNumberUtils.toInt(button.get("buttonPageId"));
		if (buttonPageId>0) {
			//刷新页面
			PageUtils.refreashPage(buttonPageId);
		}
		//刷新删除sql
		if (buttonType==3 || buttonType==7) {
			SqlUtils.appendDeleteSql(tableDbc, fieldDbc, domId, button);
		}
		
		buttonJsBuffer.append("var httpUrl='"+url+"';");
		if (ifBatch) {//添加批量获取选中行
			buttonJsBuffer.append("var checkStatus = table.checkStatus('listTable_"+domId+"')");
			buttonJsBuffer.append(",data = checkStatus.data,batchIds='';");
			buttonJsBuffer.append("if (data.length>0) {");
			//处理批量自定义参数
			if (MyStringUtils.notBlank(urlParam)) {
				Pattern fieldPattern = Pattern.compile("\\{[a-zA-Z0-9_-]+\\}");
		        Matcher fieldMatch =  fieldPattern.matcher(urlParam);
		        while(fieldMatch.find()) {
		            String group = fieldMatch.group();
		            group = group.trim();
		            buttonJsBuffer.append("var tmp"+group.substring(1, group.length()-1)+"='';");
		        }
			}
			buttonJsBuffer.append("   for (var i=0;i<data.length;i++) {");
			//拼接批量自定义参数值
			if (MyStringUtils.notBlank(urlParam)) {
				Pattern fieldPattern = Pattern.compile("\\{[a-zA-Z0-9_-]+\\}");
		        Matcher fieldMatch =  fieldPattern.matcher(urlParam);
		        while(fieldMatch.find()) {
		            String group = fieldMatch.group();
		            group = group.trim();
		            group = group.substring(1, group.length()-1);
		            buttonJsBuffer.append("tmp"+group+"+=(i>0?',':'')+(data[i]."+group+"==null?'#':data[i]."+group+");");
		        }
			} else {
				buttonJsBuffer.append("    	  batchIds+=(i>0?',':'')+data[i].ID;");
			}
			buttonJsBuffer.append("	  }");
			if (MyStringUtils.notBlank(urlParam)) {
				Pattern fieldPattern = Pattern.compile("\\{[a-zA-Z0-9_-]+\\}");
		        Matcher fieldMatch =  fieldPattern.matcher(urlParam);
		        while(fieldMatch.find()) {
		            String group = fieldMatch.group();
		            group = group.trim();
		            urlParam = urlParam.replace(group, "'+tmp"+(group.substring(1, group.length()-1))+"+'");
		        }
		        buttonJsBuffer.append("httpUrl+='&"+urlParam+"'");
			} else {
				buttonJsBuffer.append("httpUrl+='&batchIds='+batchIds");
			}
			buttonJsBuffer.append("  } else {");
			buttonJsBuffer.append("layer.msg('请至少选择一条数据！',{icon: 5,offset: 'rt'});return;");
			buttonJsBuffer.append("}");
		} else if (ifList){
			buttonJsBuffer.append("httpUrl+='&dataId='+rowData.ID;");
		}
		//操作前js
		buttonJsBuffer.append(button.get("btnBeforeJs"));
		if (ifOnlyAjax) {//只走ajax
			buttonJsBuffer.append("layer.confirm(\"您当前执行的是<font color='red'>"+button.get("buttonName")+"</font>操作,确定要执行吗？\", function(index){");
			buttonJsBuffer.append("	layer.close(index);");
			buttonJsBuffer.append("$.ajax({");
			buttonJsBuffer.append("      url:httpUrl,");
			buttonJsBuffer.append("      type:'post',");
			buttonJsBuffer.append("		 data:{},");
			buttonJsBuffer.append("		 success:function(result){");
			buttonJsBuffer.append("			alertMessage(result);");
			buttonJsBuffer.append("     	initTable ();");
			buttonJsBuffer.append("      },error:function (result) {");
			buttonJsBuffer.append("	  		alertMessage(result!=null?result:'操作失败！');");
			buttonJsBuffer.append("	  	 }");
			buttonJsBuffer.append("    });");
			buttonJsBuffer.append("});");
		} else {
			int pageWidth = MyNumberUtils.toInt(button.get("pageWidth"));
			pageWidth=pageWidth>0?pageWidth:1000;
			int pageHeight = MyNumberUtils.toInt(button.get("pageHeight"));
			pageHeight=pageHeight>0?pageHeight:500;
			buttonJsBuffer.append("			layer.open({");
			buttonJsBuffer.append("			 title:'"+button.get("buttonName")+"',");
			buttonJsBuffer.append("			 type:2,");
			buttonJsBuffer.append("			 anim: 5");
			buttonJsBuffer.append("			 ,area:['"+pageWidth+"px','"+pageHeight+"px']");
			buttonJsBuffer.append("			 ,content: httpUrl");
			buttonJsBuffer.append("			 ,btn: ['保存', '取消'"+(ifNeedPageSetBtn?",'页面设置'":"")+"]");
			buttonJsBuffer.append("			 ,btnAlign: 'c'");
			buttonJsBuffer.append("			 ,offset: 'auto'");
			buttonJsBuffer.append("			 ,yes: function(index, layero){");
    		buttonJsBuffer.append("				var submit = layero.find('iframe').contents().find('#savePage');");
			buttonJsBuffer.append("				submit.trigger('click');");
			buttonJsBuffer.append("			 },btn2: function(index, layero){");
			buttonJsBuffer.append("				 $('#operaDiv').children().remove();  ");
			if (ifNeedPageSetBtn) {
				buttonJsBuffer.append("			 },btn3: function(index, layero){");
				buttonJsBuffer.append(pageSetFunName);
			}
			buttonJsBuffer.append("			 },cancel: function(){ ");
			buttonJsBuffer.append("				$('#operaDiv').children().remove()");;
			buttonJsBuffer.append("			 },success: function(layero, index){");
			//操作后js
			buttonJsBuffer.append(button.get("btnAfterJs"));
			buttonJsBuffer.append("			}});");
		}
		buttonJsBuffer.append("}");
	}
	
	public static void main(String[] args) {
		Pattern fieldPattern = Pattern.compile("\\{[a-zA-Z0-9_-]+\\}");
        Matcher fieldMatch =  fieldPattern.matcher("id={ID}&ab={a_a}&ab={AFM_2-5}");
        while(fieldMatch.find()) {
            String group = fieldMatch.group();
            System.out.println(group+"--"+group.substring(1, group.length()-1));
        }
	}

}
