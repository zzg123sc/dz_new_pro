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
	      <label class="layui-form-label">固定前几列</label>
	      <div class="layui-input-inline">
	      	 <input type="hidden" name="tableId" value='${tableId}'/>
      		 <input type="hidden" name="pageId" value='${pageId}'/>
	         <input type="text" name="fixedRowNumber" value='${fixedRowNumber}' lay-verify="number"  class="layui-input">
	     </div>
	   </div>
	   <div class="layui-inline">
	      <div class="layui-input-inline">
	         <button type="button" class="layui-btn" onclick='delField()'>清空字段</button>
	     </div>
	   </div>
	</div>
  	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	  <legend>显示字段</legend>
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
	      url:'page_saveContentSet',
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
    inintFieldTable($("#showFieldTable"),${showFieldList},true);
    $("#showFieldTable").find('td').dblclick(function (){fieldDbClick(this);});
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
        
        var $from = null;
    	layer.open({
			  title:'显示字段-'+titleName,
			  type:1,
			  anim: 5
			  ,area:['670px','400px']
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
            zdyName = zdyName==null || zdyName==undefined?'AFM_'+field.fieldId:zdyName;
            zdyJs = zdyJs==null || zdyJs==undefined?'':zdyJs;
            var dqTd = showFieldTable.find("tr").eq(trIndex).find("td").eq(tdIndex);
            var fieldDiv = fieldSetHtml(false,ifFsetTable);
            dqTd.append(fieldDiv);
            dqTd.find("#fname").text(field.fieldCnName);
            dqTd.find("#fieldId").val(field.fieldId);
            dqTd.find("#fieldCnName").val(field.fieldCnName);
            dqTd.find("#zdyName").val(zdyName);
            dqTd.find("#width").val(field.width!=null?field.width:0);
            dqTd.find("#ifShow").find("option[value='"+field.ifShow+"']").attr("selected",true);
            dqTd.find("#ifOrder").find("option[value='"+field.ifOrder+"']").attr("selected",true);
            dqTd.find("#zdyJs").val(zdyJs);
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
        fieldDiv+="<div class=\"layui-inline\">";
        fieldDiv+="<label class=\"layui-form-label\">是否显示</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <select id='ifShow' name='ifShow'>";
        fieldDiv+="		<option value='0'>是</option>";
        fieldDiv+="		<option value='1'>否</option>";
        fieldDiv+="</select>";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline\">";
        fieldDiv+="<label class=\"layui-form-label\">是否可排序</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <select id='ifOrder' name='ifOrder'>";
        fieldDiv+="		<option value='1'>是</option>";
        fieldDiv+="		<option value='0'>否</option>";
        fieldDiv+="</select>";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="<div class=\"layui-inline\">";
        fieldDiv+="<label class=\"layui-form-label\">宽度</label>";
        fieldDiv+="<div class=\"layui-input-inline\">";
        fieldDiv+="  <input type=\"text\" id=\"width\" name=\"width\" placeholder=\"宽度\" lay-verify=\"number\" value='0'  class=\"layui-input\">";
        fieldDiv+="</div>";
        fieldDiv+="</div>";
        fieldDiv+="</div><div class=\"layui-collapse\">";
        fieldDiv+="  <div class=\"layui-colla-item\">";
        fieldDiv+="    <h2 class=\"layui-colla-title\">自定义显示js</h2>";
        fieldDiv+="    <div class=\"layui-colla-content layui-show\">";
        fieldDiv+="  	<textarea id='zdyJs' name='zdyJs' placeholder=\"取值用rowData.别名...\" class=\"layui-textarea\" style='width:100%'></textarea>";
        fieldDiv+="</div></div></div>";
        fieldDiv+="	<button  type='button' id='saveFieldSet' class=\"layui-btn layui-hide\" lay-submit lay-filter=\"saveFieldSet\">立即提交</button>";
        if (!ifNullSet) {
        	fieldDiv+="</div>";
        }
        return fieldDiv;
    }

  	//初始化字段拖拽
    initFieldDropOrDrage();
    //初始化表格右键事件
    intiRightMouseEvent($("#showFieldTable"),1);
</script>
<div id='fsetDiv'></div>
</body>
</html>
