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
	<div class="layui-form-item">
		<div class="layui-inline">
		    <input type="hidden" name="tableId" value='${tableId}'/>
		    <input type="hidden" name="pageId" value='${pageId}'/>
			<button class="layui-btn" type="button" onclick='addChild()'><i class="layui-icon">&#xe608;</i>添加权限设置</button>
		</div>
	</div>
    <input type="hidden" id="removeIds" name="removeIds"/>
	<div id='limitDiv'>
		<c:forEach items="${limitList}" var="limit">
		<div id='limitSet'>
			<div class="layui-form-item">
			  <div class="layui-inline">
			    <label class="layui-form-label">权限名称</label>
			    <div class="layui-input-inline">
			      <input type="hidden" id="limitId"  name="limitId" value="${limit.limitId }"/>
			      <input type="text" name="limitName" lay-verify="required" placeholder="权限名称"  value="${limit.limitName }"  autocomplete="off" class="layui-input">
			    </div>
			    <div class="layui-input-inline">
				    <button type="button" class="layui-btn layui-btn-sm" onclick='delTr(this,0)'><i class="layui-icon"></i></button>
				  	<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,0)'><i class="layui-icon layui-icon-up"></i></button>
		    		<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,1)'><i class="layui-icon layui-icon-down"></i></button>
			    </div>
			  </div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">权限sql</label>
			    <div class="layui-input-block">
			      <textarea id='limitSql' name='limitSql' lay-verify="required" placeholder="权限sql..." class="layui-textarea" style='width:100%'>${limit.limitSql}</textarea>
			    </div>
			</div>
		</div>
		</c:forEach>
	</div>
	<div class="layui-form-item layui-hide">
	      <button  type='button' id='savePage' class="layui-btn" lay-submit lay-filter="savePage">立即提交</button>
	</div>
</form>
<div class='layui-hide' id='tmpLimitDiv'>
	<div id='limitSet'>
		<div class="layui-form-item">
		  <div class="layui-inline">
		    <label class="layui-form-label">权限名称</label>
		    <div class="layui-input-inline">
		      <input type="hidden" id="limitId"  name="limitId" value="0"/>
		      <input type="text" name="limitName" lay-verify="required" placeholder="权限名称"  autocomplete="off" class="layui-input">
		    </div>
		    <div class="layui-input-inline">
		      	<button type="button" class="layui-btn layui-btn-sm" onclick='delTr(this,0)'><i class="layui-icon"></i></button>
		  		<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,0)'><i class="layui-icon layui-icon-up"></i></button>
	   			<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,1)'><i class="layui-icon layui-icon-down"></i></button>
		    </div>
		  </div>
		</div>
		<div class="layui-form-item">
		    <label class="layui-form-label">权限sql</label>
		    <div class="layui-input-block">
		      <textarea id='limitSql' name='limitSql' placeholder="权限sql..." lay-verify="required" class="layui-textarea" style='width:100%'></textarea>
		    </div>
		</div>
	</div>
</div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script>
    var layuiForm = layui.form;
    var layer = layui.layer;
    var $ = layui.jquery; 
    var parent$ = window.parent.layui.jquery;//父页面jquery对象
	//默认添加一条
	if ($("#limitDiv").children().length==0) {
		addChild();
	}
	layuiForm.on("submit(savePage)",function (data) {
		var saveBtn = $(parent$.find(".layui-layer-btn0"));//保存按钮
		if (saveBtn.hasClass("layui-btn-disabled")) {
			layer.msg("正在提交中，请勿重复提交！",{icon: 5});
			return false;
		}
		//禁用按钮
		saveBtn.addClass("layui-btn-disabled");
		$.ajax({
	      url:'page_saveLimitSet',
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

	//添加子级
	function addChild() {
		$("#limitDiv").append($("#tmpLimitDiv").children().clone(false));
	}

	//删除
	function delTr (obj) {
		layer.confirm('确定要删除吗？', function(index){
			layer.close(index);
			$(obj).closest("#limitSet").remove();
			var limitId = $(obj).closest('#limitSet').find("#limitId").val();
			if (limitId>0) {
				var removeIds = $("#removeIds").val();
				removeIds = removeIds!=null?removeIds:"";
				removeIds += (removeIds.length>0?",":"")+limitId;
				$("#removeIds").val(removeIds);
			}
		});
	}

	//移动div
	function moveDiv (obj,order) {
		var nowDiv = $(obj).closest("#limitSet");//当前div
		var parentDiv = nowDiv.parent();//父级元素
		if (parentDiv.children().length==1) {//只剩一个则直接返回
            return false;
		}
		var prevDiv = nowDiv.prev();//前一个元素
		var afterDiv = nowDiv.next();//后一个元素
        if (order==0){//上移
            //如果当前是最高一级，则移到最后一级
	        if (prevDiv==null || prevDiv.length==0) {
	        	parentDiv.children().last().after(nowDiv.clone(false));
		    } else {
		    	prevDiv.before(nowDiv.clone(false));
			}
	        nowDiv.remove();
        } else {
            //如果当前是最高一级，则移到最后一级
   	        if (afterDiv==null || afterDiv.length==0) {
   	        	parentDiv.children().first().before(nowDiv.clone(false));
   		    } else {
   		    	afterDiv.after(nowDiv.clone(false));
   			}
   		    nowDiv.remove();
        }
	}
</script>
</body>
</html>
