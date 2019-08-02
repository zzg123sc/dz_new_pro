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
    <div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>字段设置</h2>
    		<div class='layui-colla-content fieldSetColl layui-show'>
			    <div class="layui-fluid" id='fieldBasicSet'>  
				  	<div class="layui-row">
					    <div class="layui-col-md7" style='max-height: 300px; overflow: auto;'>
					    	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
							  <legend>显示字段<a href='#' onclick="delField(this)"><i class="layui-icon layui-icon-close-fill" style="font-size: 20px; color: #FF0000;"></i></a></legend>
							</fieldset>
							<table class="layui-table" id='showFieldTable' lay-size='lg'>
							    <tr>
						        <td class='drage canDrop'></td>
						        <td class='drage canDrop'></td>
						        <td class='drage canDrop'></td>
						      </tr>
						    </table>
						    
						    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
							  <legend>隐藏字段<a href='#' onclick="delField(this)"><i class="layui-icon layui-icon-close-fill" style="font-size: 20px; color: #FF0000;"></i></a></legend>
							</fieldset>
							<table class="layui-table" id='hideFieldTable' lay-size='lg'>
							    <tr>
						        <td class='drage canDrop'></td>
						        <td class='drage canDrop'></td>
						        <td class='drage canDrop'></td>
						      </tr>
						    </table>
					    </div>
					    <div class="layui-col-md5" style='max-height: 300px; overflow: auto;'>
					    	<div class="layui-fluid">
						    	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
								  <legend>选择字段</legend>
								</fieldset>
								<table class="layui-table" id='chooseTable' lay-size='lg'>
								    <tr>
							        <td	class='drage'></td>
							        <td class='drage'></td>
							        <td	class='drage'></td>
							      </tr>
							    </table>
							 </div>
					    </div>
				    </div>
				</div>
			</div>
    	</div>
    </div>
	<div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>自定义JS</h2>
    		<div class='layui-colla-content'>
    			<textarea id='customerJs' name='customerJs' placeholder='自定义js...' class='layui-textarea' style='width:100%;height:300px;'></textarea>
    		</div>
    	</div>
    </div>
    <div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>开发描述</h2>
    		<div class='layui-colla-content'>
    			<textarea id='developScript' name='developScript' placeholder='开发描述...' class='layui-textarea' style='width:100%;height:300px;'></textarea>
    		</div>
    	</div>
    </div>
    <div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>显示块设置</h2>
    		<div class='layui-colla-content'>
    		</div>
    	</div>
    </div>
    <div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>后续操作</h2>
    		<div class='layui-colla-content'>
    		</div>
    	</div>
    </div>
    <div class='layui-collapse'>
    	<div class='layui-colla-item'>
    	  <h2 class='layui-colla-title'>页面保存限制</h2>
    		<div class='layui-colla-content'>
    			<div id='limitSet'>
			<div class="layui-form-item">
			  <div class="layui-inline">
			    <label class="layui-form-label">提示信息</label>
			    <div class="layui-input-inline">
			      <input type="text" name="limitName" placeholder="信息内容"  autocomplete="off" class="layui-input">
			    </div>
			    <div class="layui-input-inline">
				    <button type="button" class="layui-btn layui-btn-sm" onclick='delTr(this,0)'><i class="layui-icon"></i></button>
			    </div>
			  </div>
			</div>
			<div class="layui-form-item">
			    <label class="layui-form-label">限制sql</label>
			    <div class="layui-input-block">
			      <textarea id='saveLimitSql' name='saveLimitSql' placeholder="保存限制sql..." class="layui-textarea" style='width:100%'></textarea>
			    </div>
			</div>
		</div>
    		</div>
    	</div>
    </div>
	<div class="layui-form-item layui-hide">
		  <input type="hidden" id="mainTableId" name="mainTableId" value='${tableId}'/>
      	  <input type="hidden" id="pageId" name="pageId" value='${pageId}'/>
      	  <input type="hidden" id="buttonType" name="buttonType" value='${buttonType}'/>
	      <button  type='button' id='savePage' class="layui-btn" lay-submit lay-filter="savePage">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/formSelects/formSelects-v4.js"></script>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="static/operamask-ui.js"></script>
<script type="text/javascript" src="zdyJs/common.js"></script>
<script type="text/javascript" src="zdyJs/addOrUpdatePage-commonset.js"></script>
<script type="text/javascript" src="zdyJs/wqy-method.js"></script>
<script>
	var layuiForm = layui.form;
	var layer = layui.layer;
	var parent$ = window.parent.layui.jquery;//父页面jquery对象
	var element = layui.element;
	var formSelects = layui.formSelects;
	var coloneForm = $("#fieldBasicSet").clone(false);//克隆自动设置的form
	$("#contentForm").data('basicSet',coloneForm);//用于其他页面复制使用
	layuiForm.on("submit(savePage)",function (data) {
		//显示列的个数
    	var showNum = $("#contentForm").find("#showFieldTable").find("input[name='fieldId']").length;
  		if (showNum==0) {
  			layer.msg('请至少选择一个显示字段！',{icon: 5,offset: 't'});
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
	      url:'page_saveAddPageSet',
	      type:'post',
	      data:'showNum='+showNum+'&'+$("#contentForm").serialize(),
	      success:function(result){
	       if(result>0){
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

	//初始化显示字段
	initFieldSet($("#contentForm"),${showFieldList},1);
	//初始化隐藏字段
	initFieldSet($("#contentForm"),${hideFieldList},2);
	//初始化选择字段
	initFieldSet($("#contentForm"),${fieldList},3);

	//初始化表格右键事件
	intiRightMouseEvent($("#showFieldTable"));
	intiRightMouseEvent($("#hideFieldTable"));
	//初始化拖拽事件
	initFieldDropOrDrage();
</script>
</body>
<style>
	.layui-table td{
		min-width:60px;
		max-height:30px;
	}
</style>
</html>