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
<title>栏目添加</title>
<head>
	<link rel="stylesheet" type="text/css" href="static/ztree/css/zTreeStyle/zTreeStyle.css">
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body layadmin-themealias="default">
<form class="layui-form" id='menuForm'>
	<div class="layui-form-item">
	  <div class="layui-inline">
	    <label class="layui-form-label">栏目名称</label>
	    <div class="layui-input-inline">
	      <input type="text" name="menuName" lay-verify="required"  autocomplete="off" class="layui-input">
	    </div>
	  </div>
	  <div class="layui-inline">
	    <label class="layui-form-label">父级栏目</label>
	    <div class="layui-input-inline">
	       <input type="hidden" id="parentMenuName" name="parentMenuName"/>
	       <select name="parentMenuId" lay-search onchange="getParentName(this)">
         	 <option value="">--请选择--</option>
	         <c:forEach items="${menuList}" var="menu">
		       <option value="${ menu.menuId }">${ menu.menuName }</option>
		     </c:forEach>
		   </select>
	    </div>
	  </div>
	 </div>
	 <div class="layui-form-item">
	  <div class="layui-inline">
	      <label class="layui-form-label">调用方式</label>
	      <div class="layui-input-inline">
	         <select name="menuType" lay-search onchange="pageOrUrl(this)">
		       <option value="0">页面</option>
		       <option value="1">地址</option>
		     </select>
	     </div>
	   </div>
	  <div class="layui-inline">
	      <div id='pageDiv'>
		      <label class="layui-form-label">页面</label>
		      <div class="layui-input-inline">
		         <input type="hidden" id="tableId" name="tableId"/>
		         <select id="pageId" name="pageId" lay-search onchange='getTableId (this)'>
	      		   <option value="">--请选择--</option>
			       <c:forEach items="${pageList}" var="page">
				       <option value="${ page.pageId }" tableId='${page.tableId }' >${ page.pageName }</option>
				   </c:forEach>
			     </select>
		      </div>
		  </div>
		  <div id='pageUrlDiv' class='layui-hide'>
		      <label class="layui-form-label">地址url</label>
		      <div class="layui-input-inline">
		         <input type="text" id="pageUrl" name="pageUrl" autocomplete="off" class="layui-input">
		      </div>
	      </div>
	   </div>
	  </div>
	  <div class="layui-form-item">
	   <div class="layui-inline">
		    <label class="layui-form-label">是否在菜单显示</label>
		    <div class="layui-input-inline">
			    <select name="ifShow">
			       <option value="1">是</option>
			       <option value="0">否</option>
			     </select>
		     </div>
	   </div>
	   <div class="layui-inline">
		    <label class="layui-form-label">是否区分角色权限</label>
		    <div class="layui-input-inline">
			    <select name="ifDisRole">
			       <option value="1">是</option>
			       <option value="0">否</option>
			     </select>
		     </div>
	   </div>
	 </div>
	<div class="layui-form-item layui-hide">
	      <button  type='button' id='saveMenu' class="layui-btn" lay-submit lay-filter="saveMenu">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.exedit.js"></script>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script>
    var layuiForm = layui.form;
    var layer = layui.layer;
    var parent$ = window.parent.layui.jquery;//父页面jquery对象

	//监听下拉框选中事件
	layuiForm.on("select",function (data) {
		//触发change事件
		$(this).parent().parent().parent().find("select").change();
	});
	layuiForm.on("submit(saveMenu)",function (data) {
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'menu_add',
	      type:'post',
	      data:$("#menuForm").serialize(),
	      success:function(result){
	       if(result==1){
	        parent.layer.closeAll();
	        parent.layer.msg("保存成功！",{icon: 6,offset: 't'});
	        parent.initTable ();
	        parent.refreshTree();
	       }
	      },error:function (result) {
	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	    	  saveBtn.removeClass("layui-btn-disabled");
		  }
	    });
	    return false;
	});

	//切换页面和url的选择
	function pageOrUrl(obj) {
		if ($(obj).val()==0) {
			$("#pageUrlDiv").addClass("layui-hide");
			$("#pageDiv").removeClass("layui-hide");
			$("#pageUrl").removeAttr("lay-verify");
		} else {
			$("#pageDiv").addClass("layui-hide");
			$("#pageUrlDiv").removeClass("layui-hide");
			$("#pageUrl").attr("lay-verify","required");
		}
	}

	//获取栏目
	function getParentName(obj) {
		$("#parentMenuName").val($(obj).val()>0?$(obj).find("option:selected").text():"");
	}

	//获取tableId
	function getTableId (obj) {
		$("#tableId").val($(obj).val()>0?$(obj).find("option:selected").attr("tableId"):"");
	}
</script>
</body>
</html>
