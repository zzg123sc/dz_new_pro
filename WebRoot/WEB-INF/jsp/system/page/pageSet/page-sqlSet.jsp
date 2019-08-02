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
</head>
<body layadmin-themealias="default">
<form class="layui-form" id='contentForm'>
    <div class="layui-collapse">
	  <div class="layui-colla-item">
	    <h2 class="layui-colla-title">当前SQL</h2>
	    <div class="layui-colla-content layui-show">
   	      	 <textarea id='lastSql' name='lastSql' class="layui-textarea" readonly="readonly" style='width:100%'>${sql }</textarea>
		</div>
	  </div>
	</div>
	<div class="layui-collapse">
	  <div class="layui-colla-item">
	    <h2 class="layui-colla-title">修改SQL</h2>
	    <div class="layui-colla-content">
	      	 <textarea id='zdySql' name='zdySql' placeholder="修改sql..."  class="layui-textarea" style='width:100%'>${zdySql }</textarea>
		</div>
	  </div>
	</div>
	<div class="layui-form-item layui-hide">
		  <input type="hidden" name="tableId" value='${tableId}'/>
      	  <input type="hidden" name="pageId" value='${pageId}'/>
	      <button  type='button' id='savePage' class="layui-btn" lay-submit lay-filter="savePage">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script>
	var layuiForm = layui.form;
	var layer = layui.layer;
	var $ = layui.jquery; 
	var parent$ = window.parent.layui.jquery;//父页面jquery对象
	layuiForm.on("submit(savePage)",function (data) {
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'page_saveSqlSet',
	      type:'post',
	      data:$("#contentForm").serialize(),
	      success:function(result){
	       if(result==1){
	    	    parent.layer.closeAll();
		        parent.location.reload();
		        parent.layer.msg("保存成功！",{icon: 6,offset: 't'});
	       }
	      },error:function (result) {
	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	    	  saveBtn.removeClass("layui-btn-disabled");
		  }
	    });
	    return false;
	});
</script>
</body>
</html>