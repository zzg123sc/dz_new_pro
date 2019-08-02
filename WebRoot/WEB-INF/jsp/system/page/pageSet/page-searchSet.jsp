<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
	<link rel="stylesheet" type="text/css" href="static/operamask-ui.css">
</head>
<body layadmin-themealias="default">
  <form class="layui-form" id='contentForm'>
    <div class="layui-form-item">
	   <div class="layui-inline">
	      <div class="layui-input-inline">
	      	 <input type="hidden" name="tableId" value='${tableId}'/>
      		 <input type="hidden" name="pageId" value='${pageId}'/>
	         <button type="button" class="layui-btn" onclick='delField()'>清空字段</button>
	     </div>
	   </div>
	</div>
  	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	  <legend>搜索条件字段</legend>
	</fieldset>
	  <table class="layui-table" id='showFieldTable'>
	    <tr>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      </table>
    <div class="layui-form-item layui-hide">
	      <button  type='button' id='savePage' class="layui-btn" lay-submit lay-filter="saveContentSet">立即提交</button>
	</div>
  </form>
  <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	  <legend>选择字段</legend>
  </fieldset>
  <div class="layui-form">
  	<table class="layui-table" id='allFieldTable'>
    	<tr>
	       <td></td>
	       <td></td>
	       <td></td>
	       <td></td>
	    </tr>
    </table>
  </div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="static/operamask-ui.js"></script>
<script type="text/javascript" src="zdyJs/page-commonset.js"></script>
<script type="text/javascript" src="zdyJs/wqy-method.js"></script>
<script>
	var layer = layui.layer;
	var element = layui.element;
	var layuiForm = layui.form;
	var parent$ = window.parent.layui.jquery;//父页面jquery对象
    layer.config({
    	skin:'layui-layer-molv'
    });
    layuiForm.on("submit(saveContentSet)",function (data) {
        if($("#showFieldTable").find("input[name='fieldId']").length==0) {
        	layer.msg("请至少放置一个字段！",{icon: 5,offset: 'rt'});
        	return false;
        }
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'page_saveSearchSet',
	      type:'post',
	      data:$("#contentForm").serialize(),
	      success:function(result){
	       if(result==1){
	        parent.layer.closeAll();
	        parent.location.reload();
	        parent.layer.msg("保存成功！",{icon: 6,offset: 't'});
	       } else {
	          saveBtn.removeClass("layui-btn-disabled");
	          parent.layer.msg("保存失败！",{icon: 6,offset: 't'});
	       }
	      },error:function (result) {
	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	    	  saveBtn.removeClass("layui-btn-disabled");
		  }
	    });
	    return false;
	});

    //初始化显示字段
    inintFieldTable($("#showFieldTable"),${searchFieldList},true);
    $("#showFieldTable").find('td').dblclick(function () {fieldDbClick(this)});
    $("#showFieldTable").find('td').addClass("drage canDrop");
  	//字段双击设置	    
    function fieldDbClick(obj) {
        var dqTd = $(obj);
        var ifNullSet = false;
        var titleName = dqTd.find("#fname").text();
        var contentHtml = dqTd.find("#fieldSetDiv").children().clone(false);
        if (contentHtml==null || contentHtml.length==0) {//是否是自定义字段设置
        	titleName = "自定义";
        	ifNullSet = true;
        	contentHtml = fieldSetHtml (ifNullSet);
        }
        $("#fsetDiv").append("<form class=\"layui-form\" id='fieldSetForm'></form>");
        $("#fsetDiv").find("#fieldSetForm").append(contentHtml);
        if (ifNullSet) {
        	$("#fsetDiv").find("#searchType").closest('.layui-inline').removeClass('layui-hide');
        }
        
        var $from = null;
    	layer.open({
			  title:'字段-'+titleName,
			  type:1,
			  anim: 5
			  ,area:['690px','260px']
			  ,content: $("#fsetDiv")
			  ,btn: ['保存', '取消']
			  ,btnAlign: 'c'
			  ,offset: 'auto'
			  ,yes: function(index, layero){
				//调用保存
	            var submit = layero.find('#saveFieldSet');
	            $from = layero.find("#fieldSetForm");
				submit.trigger('click');
			  },btn2: function(index, layero){
				 $("#fsetDiv").children().remove();  
			  },cancel: function(){ 
				$("#fsetDiv").children().remove();
			  }
			});
		    element.render();
		    layui.form.render('select');
	    	//监听下拉框选中事件
	    	layui.form.on("select",function (data) {
		    	//当前select元素
	    		var dqSelect = $(this).parent().parent().parent().find("select");
	    		//触发change事件
	    		dqSelect.change();
	    	});
		    layui.form.on("submit(saveFieldSet)",function (data) {
		    	var selectOptions = $from.find("option:selected");//选中的下拉框
		    	$from.find("option").removeAttr("selected");
		    	selectOptions.attr("selected",true);
				//保存设置到隐藏域
				if (ifNullSet) {
					dqTd.append("<span id='fname'></span><a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i></a><div id='fieldSetDiv' class='layui-hide'></div>"); 
				}
				dqTd.find("#fname").text($from.find("#fieldCnName").val());
				dqTd.find("#fieldSetDiv").children().remove();
				dqTd.find("#fieldSetDiv").append($from.children().clone(false));
				layer.closeAll();
				$("#fsetDiv").children().remove();
			});
    }
    //初始化选择字段表格
    inintFieldTable($("#allFieldTable"),${tableFieldList},false);
    $("#allFieldTable").find('td').dblclick(function () {
    	 var dqFieldId = $(this).find("#fieldId").val();//当前fieldID
    	 //判断是否已经存在此字段
         if ($("#showFieldTable").find("input[name='fieldId'][value='"+dqFieldId+"']").length>0) {
         	layer.msg("已经存在此字段了！",{icon: 5,offset: 't'});
 			return;
         }
     	 var maxTdIndex = $("#showFieldTable").find('td').length-1;//td的行数
         var lastField = $("#showFieldTable").find("input[name='fieldId']:last");
         var lastFieldTrIndex = lastField.length==0?0:lastField.closest('tr').index();
         var lastFieldTdIndex = lastField.length==0?0:lastField.closest('td').index();
         lastFieldTdIndex = lastFieldTrIndex*4+lastFieldTdIndex;
         if (lastFieldTdIndex==maxTdIndex) {
        	var $lastTr = $("<tr><td></td><td></td><td></td><td></td></tr>");
      	    $("#showFieldTable").append($lastTr);
      	    $lastTr.find('td').addClass("drage canDrop");
      	    $lastTr.find('td').dblclick(function (){fieldDbClick(this);});
         }
         lastFieldTdIndex = lastField.length==0?lastFieldTdIndex:lastFieldTdIndex+1;
         var cloneTd = $(this).clone(false);//克隆当前字段
         cloneTd.find("#fname").after("<a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i></a>"); 
         $("#showFieldTable").find('td').eq(lastFieldTdIndex).append(cloneTd.children());
       	 //初始化字段拖拽
         initFieldDropOrDrage();
    });

    //初始化字典表格
    function inintFieldTable (showFieldTable,showFieldList,ifFsetTable) {
    	var trHtml = "<tr><td></td><td></td><td></td><td></td></tr>";
        var tdIndex = 0;
        var trIndex = 0;
        for (var i=0;i<showFieldList.length;i++) {
            var field = showFieldList[i];
            var zdyName = field.zdyName;//自定义别名
            var zdyJs = field.zdyJs;//自定义js
            var fieldId = field.fieldId;//字段id
            var fieldType = field.fieldType;//字段类型
            var searchType = field.searchType;//搜索类型
            zdyName = zdyName==null || zdyName==undefined?'AFM_'+field.fieldId:zdyName;
            zdyJs = zdyJs==null || zdyJs==undefined?'':zdyJs;
            var dqTd = showFieldTable.find("tr").eq(trIndex).find("td").eq(tdIndex);
            var fieldDiv = fieldSetHtml(false,ifFsetTable);
            dqTd.append(fieldDiv);
            dqTd.find("#fname").text(field.fieldCnName);
            dqTd.find("#fieldId").val(field.fieldId);
            dqTd.find("#fieldCnName").val(field.fieldCnName);
            dqTd.find("#zdyName").val(zdyName);
            if (fieldId==0) {//自定义字段设置
            	dqTd.find("#searchType").closest('.layui-inline').removeClass('layui-hide');
            	dqTd.find("#searchType").find("option[value='"+field.searchType+"']").attr("selected",true);
            	if (searchType == 3) {//日期类型
            		dqTd.find("#dateType").closest('.layui-inline').removeClass('layui-hide');
                	dqTd.find("#dateType").find("option[value='"+field.dateType+"']").attr("selected",true);
            	} else if (searchType == 4) {//字典类型
            		dqTd.find("#dicParentId").closest('.layui-inline').removeClass('layui-hide');
                	dqTd.find("#dicParentId").find("option[value='"+field.dicParent+"']").attr("selected",true);
            	}
            } else {
            	if (fieldType==10 || fieldType==23 || fieldType==25) {//日期类型
            		dqTd.find("#dateType").closest('.layui-inline').removeClass('layui-hide');
            		dqTd.find("#dateType").find("option[value='"+field.dateType+"']").attr("selected",true);
            	}
            }
            if (!ifFsetTable) {
            	dqTd.addClass("drage");
            }
           	tdIndex++;
        	if (i>0 && i%4==3) {
        		showFieldTable.append(trHtml);
        		tdIndex = 0;
        		trIndex ++;
            }
        }
    }

    //字段设置的html
    function fieldSetHtml (ifNullSet,ifSetTable) {
    	var fieldDiv = "";
    	if (!ifNullSet) {
    		fieldDiv =  "<span id='fname'></span>";
    		if (ifSetTable) {
    			fieldDiv += "<a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i>  </a>";
    		}
    		fieldDiv += "	<div id='fieldSetDiv' class='layui-hide'>";
        }
        fieldDiv+="<div class=\"layui-form-item\">";
        fieldDiv+="<div class=\"layui-inline\">";
        fieldDiv+="<label class=\"layui-form-label\">名称_中文</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="	 <input type='hidden' id='fieldId' name='fieldId' value='0'>";
        fieldDiv+="  <input type=\"text\" id=\"fieldCnName\" name=\"fieldCnName\" placeholder=\"中文名称\" lay-verify=\"required\" class=\"layui-input\">";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline\">";
        fieldDiv+="<label class=\"layui-form-label\">数据别名</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <input type=\"text\" id=\"zdyName\" name=\"zdyName\" placeholder=\"大写数据别名\" lay-verify=\"required\"   class=\"layui-input\">";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline layui-hide\">";
        fieldDiv+="<label class=\"layui-form-label\">字段类型</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <select id='searchType' name='searchType' onchange='changeFsetHide(this)'>";
        fieldDiv+="		<option value='1'>字符</option>";
        fieldDiv+="		<option value='2'>数字</option>";
        fieldDiv+="		<option value='3'>日期</option>";
        fieldDiv+="		<option value='4'>数据字典</option>";
        fieldDiv+="		<option value='5'>星期</option>";
        fieldDiv+="</select>";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline layui-hide\">";
        fieldDiv+="<label class=\"layui-form-label\">日期格式</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <select id='dateType' name='dateType'>";
        fieldDiv+="		<option value='yyyy-MM-dd'>yyyy-MM-dd</option>";
        fieldDiv+="		<option value='yyyy'>yyyy</option>";
        fieldDiv+="		<option value='yyyy-MM'>yyyy-MM</option>";
        fieldDiv+="		<option value='yyyy-MM-dd HH:mm:ss'>yyyy-MM-dd HH:mm:ss</option>";
        fieldDiv+="		<option value='HH:mm:ss'>HH:mm:ss</option>";
        fieldDiv+="</select>";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline layui-hide\">";
        fieldDiv+="<label class=\"layui-form-label\">数据字典</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <select id='dicParentId' name='dicParentId'>";
        fieldDiv+="		<c:forEach items='${dicParentList}' var='dicParent'>";
        fieldDiv+="		<option value='${ dicParent.DIC_ID}'>${ dicParent.DIC_NAME }</option>";
        fieldDiv+="		</c:forEach>";
        fieldDiv+="</select>";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="	<button  type='button' id='saveFieldSet' class=\"layui-btn layui-hide\" lay-submit lay-filter=\"saveFieldSet\">立即提交</button>";
        if (!ifNullSet) {
        	fieldDiv+="</div>";
        }
        return fieldDiv;
    }

    //切换设置字段的隐藏显示
    function changeFsetHide (obj) {
		var searchType = $(obj).val();//搜索类型
		var parnetForm = $(obj).closest('form');
		parnetForm.find("#dateType").closest('.layui-inline').addClass('layui-hide');
		parnetForm.find("#dicParentId").closest('.layui-inline').addClass('layui-hide');
		if (searchType==3) {//日期
			parnetForm.find("#dateType").closest('.layui-inline').removeClass('layui-hide');
		} else if (searchType==4) {//字典类型
			parnetForm.find("#dicParentId").closest('.layui-inline').removeClass('layui-hide');
	    }
    }

  	//初始化字段拖拽
    initFieldDropOrDrage();
  	//初始化表格右键事件
    intiRightMouseEvent($("#showFieldTable"),1);
</script>
<div id='fsetDiv'></div>
</body>
</html>
