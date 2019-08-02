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
<title>生成页面</title>
<head>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body layadmin-themealias="default">
<form class="layui-form" id='objAddForm'>
	<div class="layui-form-item">
	  <div class="layui-inline">
	      <label class="layui-form-label">页面类型</label>
	      <div class="layui-input-inline">
	      	 <input type="hidden" name="tableId"  value="${tableId}">
	         <select id="pageType" name="pageType" width='50%' onchange='changeTypeShow(this)'>
		       <option value="1">列表</option>
		       <option value="2">添加</option>
		       <option value="3">修改</option>
		       <option value="4">批量修改</option>
		     </select>
	     </div>
	   </div>
	   <div class="layui-inline">
	    <label class="layui-form-label">页面名称</label>
	    <div class="layui-input-inline">
	      <input type="text" id="pageName" name="pageName" placeholder="请输入页面名称" value="${tableCnName}列表" lay-verify="required"  class="layui-input">
	    </div>
	   </div>
	   <div class="layui-inline">
	      <label class="layui-form-label">是否生成栏目</label>
	      <div class="layui-input-inline">
	         <select id="ifCreateMenu" name="ifCreateMenu" width='50%'>
		       <option value="0">否</option>
		       <option value="1">是</option>
		     </select>
	     </div>
	   </div>
	   <div class="layui-inline">
	      <label class="layui-form-label">父级栏目</label>
	      <div class="layui-input-inline">
	         <input type="hidden" id="parentMenuName" name="parentMenuName"/>
	         <select id="parentMenu" name="parentMenu" onchange="getParentName(this)" lay-search>
	           <option value="">--请选择-</option>
	         	<c:forEach items="${menuList}" var="menu">
		       		<option value="${ menu.menuId }">${ menu.menuName }</option>
		     	</c:forEach>
		     </select>
	     </div>
	   </div>
	</div>
	<div class="layui-form-item layui-hide">
	      <button  type='button' id='saveObj' class="layui-btn" lay-submit lay-filter="addObj">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
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
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'object_createPage',
	      type:'post',
	      data:$("#objAddForm").serialize(),
	      success:function(result){
	       if(result>0){
	    	   layer.confirm($("#pageName").val()+'已经生成成功，是否继续生成？', {
	    		   btn: ['继续生成', '取消'] 
	    		   ,cancel:function() {
		    	      saveBtn.removeClass("layui-btn-disabled");
			       }
	    	    },function(index, layero){
	    		   saveBtn.removeClass("layui-btn-disabled");
	    		   layer.close(index);
	    	   	},function(index){
	    	   	   parent.layer.closeAll();
	    	    }
	    	    );
	       } else {
	    	   saveBtn.removeClass("layui-btn-disabled");
	    	   layer.msg("保存失败！",{icon: 5,offset: 'rt'});
		   }
	      },error:function (result) {
	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	    	  saveBtn.removeClass("layui-btn-disabled");
		  }
	    });
	    return false;
	});

	//切换类型显示
	function changeTypeShow(obj) {
		var pageType = $(obj).val();
		var pageName = "${tableCnName}";
		$("#ifCreateMenu").closest('.layui-inline').hide();
		$("#parentMenu").closest('.layui-inline').hide();
		if (pageType==1) {//列表
			pageName +="列表";
			$("#ifCreateMenu").closest('.layui-inline').show();
			$("#parentMenu").closest('.layui-inline').show();
		} else if (pageType==2) {//添加
			pageName +="-添加";
		} else if (pageType==3) {//修改
			pageName +="-修改";
		} else if (pageType==4) {//查看
			pageName +="-查看";
		} else if (pageType==5) {//批量修改
			pageName +="-批量修改";
		}
		$("#pageName").val(pageName);
	}

	//获取栏目
	function getParentName(obj) {
		$("#parentMenuName").val($(obj).val()>0?$(obj).find("option:selected").text():"");
	}
</script>
</body>
</html>
