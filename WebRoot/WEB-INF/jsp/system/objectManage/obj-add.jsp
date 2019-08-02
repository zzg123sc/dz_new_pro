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
<title>对象管理</title>
<head>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body layadmin-themealias="default">
<form class="layui-form" id='objAddForm'>
	<div class="layui-form-item">
	  <div class="layui-inline">
	    <label class="layui-form-label">名称_中文</label>
	    <div class="layui-input-inline">
	      <input type="text" name="tableCnName" lay-verify="required"  class="layui-input">
	    </div>
	  </div>
	  <div class="layui-inline">
	    <label class="layui-form-label">名称_英文</label>
	    <div class="layui-input-inline">
	      <input type="text" name="tableName" lay-verify="required" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-inline">
	      <label class="layui-form-label">对象类型</label>
	      <div class="layui-input-inline">
	         <select id="tableType" name="tableType" lay-search>
	         <c:forEach items="${allObjTypes}" var="types">
		       <option value="${ types.id }">${ types.name }</option>
		     </c:forEach>
		     </select>
	     </div>
	   </div>
	</div>
	<div class="layui-form-item">
	  <div class="layui-inline">
	      <label class="layui-form-label">是否支持父子级</label>
	      <div class="layui-input-inline">
	         <select name="ifParent" width='50%'>
		       <option value="0">否</option>
		       <option value="1">是</option>
		     </select>
	     </div>
	   </div>
	   <div class="layui-inline">
		    <label class="layui-form-label">备注</label>
		    <div class="layui-input-inline">
		      <textarea name='remark' placeholder="请输入内容" class="layui-textarea"></textarea>
		    </div>
	   </div>
	 </div>
     <div class="layui-form">
	  <table class="layui-table" id='fieldTable'>
	    <colgroup>
	      <col width="200">
	      <col width="200">
	      <col width="300">
	      <col width="200">
	      <col>
	    </colgroup>
	    <thead>
	      <tr>
	        <th>中文</th>
	        <th>英文</th>
	        <th>类型</th>
	        <th>是否唯一</th>
	        <th>操作<button type="button" class="layui-btn layui-btn-sm" onclick='copyNewCol()'><i class="layui-icon"></i></button></th>
	      </tr> 
	    </thead>
	    <tbody>
	      <tr>
	        <td><input type="text" name="fieldCnName" lay-verify="required" class="layui-input"></td>
	        <td><input type="text" name="fieldName" lay-verify="required" class="layui-input"></td>
	        <td>
	        	<select lay-filter="fieldType" name="fieldType" onchange='changeFieldType(this)' lay-search>
	         		<c:forEach items="${allFieldTypes}" var="types">
		      		 <option value="${ types.id }">${ types.name }</option>
	     			</c:forEach>
		     	</select>
		     	<div style="display:none" id="innerObjDiv">
			     	<select lay-filter="relationTableId" name="relationTableId" lay-search>
			     		<option value="">--请选择--</option>
		         		<c:forEach items="${tableList}" var="tables">
			      		 <option value="${ tables.tableId}">${ tables.tableCnName }(${tables.tableName})</option>
		     			</c:forEach>
			     	</select>
		     	</div>
		     	<div style="display:none" id="dicSelectDiv">
			     	<select lay-filter="dicParent" name="dicParent" lay-search>
			     		<option value="">--请选择--</option>
		         		<c:forEach items="${dicParentList}" var="dicParent">
			      		 <option value="${ dicParent.DIC_ID}">${ dicParent.DIC_NAME }</option>
		     			</c:forEach>
			     	</select>
		     	</div>
		     	<div style="display:none" id="numSelectDiv">
			     	<select lay-filter="numType" name="numType" lay-search>
			     		<option value="0">int</option>
		      		    <option value="1">float</option>
		      		    <option value="2">double</option>
		      		    <option value="3">decimal</option>
			     	</select>
		     	</div>
		     	<input type="text" class="layui-input" lay-verify="required|number"  id="length" name="length">
		     </td>
		     <td>
		         <select name="ifOnly" width='50%'>
			       <option value="0">否</option>
			       <option value="1">是</option>
			     </select>
	   		</td>
	        <td><button type="button" class="layui-btn layui-btn-sm" onclick='delTr(this,0)'><i class="layui-icon"></i></button></td>
	      </tr>
	    </tbody>
	  </table>
	</div>
	<div class="layui-form-item layui-hide">
	      <button  type='button' id='saveObj' class="layui-btn" lay-submit lay-filter="addObj">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="zdyJs/obj-addOrUpdate.js"></script>
<script>
    var layuiForm = layui.form;
    var layer = layui.layer;
    var $ = layui.jquery;
    var parent$ = window.parent.layui.jquery;//父页面jquery对象
	//监听下拉框选中事件
	layuiForm.on("select",function (data) {
		//触发change事件
		$(this).parent().parent().parent().find("select").change();
	});
	layuiForm.on("submit(addObj)",function (data) {
		if (!canSave()) {//判断是否可保存
			return;
		}
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'object_add',
	      type:'post',
	      data:$("#objAddForm").serialize(),
	      success:function(result){
	       if(result==1){
	        parent.layer.closeAll();
	        parent.layer.msg("保存成功！",{icon: 6,offset: 't'});
	        parent.initTable ();
	       }
	      },error:function (result) {
	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	    	  saveBtn.removeClass("layui-btn-disabled");
		  }
	    });
	    return false;
	});

	 //复制一行
	function copyNewCol() {
		var fieldHtml = '<tr><td><input type="text" name="fieldCnName" lay-verify="required" class="layui-input"></td>'+
	        '<td><input type="text" name="fieldName" lay-verify="required" class="layui-input"></td>'+
	        '<td><select name="fieldType" onchange="changeFieldType(this)" lay-search>'+
	        '<c:forEach items="${allFieldTypes}" var="types">'+
		     '  <option value="${ types.id }">${ types.name }</option>'+
		    ' </c:forEach>'+
		     '</select>'+
		     '<div style="display:none" id="innerObjDiv"><select lay-filter="relationTableId" name="relationTableId" lay-search>'+
		     '<option value="">--请选择--</option>'+
      		'<c:forEach items="${tableList}" var="tables">'+
	      		 '<option value="${ tables.tableId}">${ tables.tableCnName }(${tables.tableName})</option>'+
  			'</c:forEach>'+
	     	'</select></div>'+
	     	'<div style="display:none" id="dicSelectDiv">'+
		    ' 	<select lay-filter="dicParent" name="dicParent" lay-search>'+
		    ' 		<option value="">--请选择--</option>'+
	        ' 		<c:forEach items="${dicParentList}" var="dicParent">'+
		    '  		 <option value="${ dicParent.DIC_ID}">${ dicParent.DIC_NAME }</option>'+
	     	'		</c:forEach>'+
		    ' 	</select>'+
	     	'</div>'+
			'<div style="display:none" id="numSelectDiv">'+
			'     	<select lay-filter="numType" name="numType" lay-search>'+
			'     		<option value="0">int</option>'+
		    '  		    <option value="1">float</option>'+
		    '  		    <option value="2">double</option>'+
		    '  		    <option value="3">decimal</option>'+
			'     	</select>'+
		    '	</div>'+
	     	'<input type="text" class="layui-input" lay-verify="required|number"  id="length" name="length">'+
		     '</td>'+
		     '<td>'+
		     '    <select name="ifOnly" width="50%">'+
			 '      <option value="0">否</option>'+
			 '      <option value="1">是</option>'+
			 '    </select>'+
	   		'</td>'+
	        '<td><button type="button" class="layui-btn layui-btn-sm" onclick="delTr(this,0)"><i class="layui-icon"></i></button></td>'+
	         '</tr>';
		$("#fieldTable").children('tbody').append(fieldHtml);
		layui.form.render('select');
	}

	//给table绑定回车事件
    $("#fieldTable").keydown(function(event){
        if(event.keyCode == 13){ //绑定回车 
          copyNewCol(); 
        } else if(event.keyCode == 46){ //绑定delete
        	delTr(event.target);
        } 
    });
</script>
</body>
</html>
