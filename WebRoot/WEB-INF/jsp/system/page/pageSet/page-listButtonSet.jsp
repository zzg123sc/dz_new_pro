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
	<link rel="stylesheet" type="text/css" href="static/formSelects/formSelects-v4.css">
</head>
<body layadmin-themealias="default">
  <form class="layui-form" id='contentForm'>
    <div class="layui-form-item">
	   <div class="layui-inline">
	      <div class="layui-input-inline">
	      	 <input type="hidden" id="tableId" name="tableId" value='${tableId}'/>
      		 <input type="hidden" id="pageId" name="pageId" value='${pageId}'/>
      		 <input type="hidden" id="delReSize" name="delReSize" value='${delReSize}'/>
	         <button type="button" class="layui-btn" onclick='delBtn()'>清空字段</button>
	     </div>
	   </div>
	</div>
  	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	  <legend align="center">按钮设置</legend>
	</fieldset>
	  <table class="layui-table" id='buttonSetTable'>
	    <tr>
        <td class="drage canDrop"></td>
        <td class="drage canDrop"></td>
        <td class="drage canDrop"></td>
        <td class="drage canDrop"></td>
      </tr>
      </table>
    <div class="layui-form-item layui-hide">
	      <button  type='button' id='savePage' class="layui-btn" lay-submit lay-filter="saveContentSet">立即提交</button>
	</div>
  </form>
  <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	  <legend align="center">按钮选择</legend>
  </fieldset>
  <div class="layui-form">
  	<div class="layui-btn-container"  align="center">
  		<span class='drage'><button type='button' class="layui-btn" buttonType='7'><span id='btnName' name='btnName'>删除</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='8'><span id='btnName' name='btnName'>关联添加</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='9'><span id='btnName' name='btnName'>修改</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='10'><span id='btnName' name='btnName'>查看</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='11'><span id='btnName' name='btnName'>打印</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='12'><span id='btnName' name='btnName'>直接操作</></button></span>
  		<span class='drage'><button type='button' class="layui-btn" buttonType='13'><span id='btnName' name='btnName'>自定义</></button></span>
  	</div>
	<div id='commonDeleteSet' class="layui-hide">
	  	<table class='layui-table'>
	    <colgroup>
		  <col width='50%'>
		  <col width='50%'>
		</colgroup>
		<thead>
		  <tr>
		    <th>关联表名称</th>
		    <th>操作选择:
		    	<select onchange='batchChangeDelSet(this)'>
	    		<option value='0'>有数据不可删除</option>
	    		<option value='1'>联动删除</option>
	    		<option value='2'>无操作</option>
		      </select>
		    </th>
		  </tr> 
		</thead>
		 <tbody>
		 <c:forEach items='${relationTables}' var='rbs'>
		 <tr>
		 <td>${rbs.relationTableCnName}(${rbs.relationTableName})</td>
		 <td>
		  <input type='hidden' name='delTableId' value='${rbs.relationTableId }'/>
		  <input type='hidden' name='delReFieldId' value='${rbs.relationFieldId }'/>
		  <select tmpId='delSet_${rbs.relationTableId }' id='deleteOType' name='deleteOType'>
	    		<option value='0'>有数据不可删除</option>
	    		<option value='1'>联动删除</option>
	    		<option value='2'>无操作</option>
	      </select>
		 </td>
		 </tr>
		 </c:forEach>
		 </tbody></table>
	</div>
  </div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/formSelects/formSelects-v4.js"></script>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="static/operamask-ui.js"></script>
<script type="text/javascript" src="zdyJs/page-commonset.js"></script>
<script type="text/javascript" src="zdyJs/wqy-method.js"></script>
<script>
	var layer = layui.layer;
	var element = layui.element;
	var layuiForm = layui.form;
	var parent$ = window.parent.layui.jquery;//父页面jquery对象
	var formSelects = layui.formSelects;
    layer.config({
    	skin:'layui-layer-molv'
    });
    layuiForm.on("submit(saveContentSet)",function (data) {
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'page_saveListButtonSet',
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

  	//初始化设置数据
    inintButtonTable ($("#buttonSetTable"),${listButtonSet}); 
  	//按钮设置初始化
    btnSetInit (layer,element);
  	//按钮拖拽事件处理
	initBtnDropOrDrage();
	//初始化表格右键事件
    intiRightMouseEvent($("#buttonSetTable"),2);
</script>
<div id='pageSetDiv'></div>
</body>
</html>
