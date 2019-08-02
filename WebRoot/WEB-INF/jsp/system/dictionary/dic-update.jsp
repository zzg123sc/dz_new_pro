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
<title>字典修改</title>
<head>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body layadmin-themealias="default">
<form class="layui-form" id='dicForm'>
	<c:if test="${dicDbo.DIC_PARENT_ID==0 }">
	    <div class="layui-form-item">
		   <div class="layui-inline">
			    <label class="layui-form-label" id='parentLable'>父级名称</label>
			    <div class="layui-input-inline" id="parentEdit">
			      <input type="hidden" name="parentId" value="${dicDbo.DIC_ID }"/>
			      <input type="text" name="parentDicName" id="parentDicName" value="${dicDbo.DIC_NAME }" lay-verify="required"  autocomplete="off" class="layui-input">
			    </div>
		   </div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<button class="layui-btn" type="button" onclick='addChild()'><i class="layui-icon">&#xe608;</i>添加子级</button>
			</div>
		</div>
	    <input type="hidden" id="removeIds" name="removeIds"/>
		<div id='childDiv'>
			<c:forEach items="${dicChildList}" var="dic">
				<div class="layui-form-item">
				  <div class="layui-inline">
				    <label class="layui-form-label">子级_ID</label>
				    <div class="layui-input-inline">
				      <input type="hidden" id="dicId"  name="dicId" value="${dic.DIC_ID }"/>
				      <input type="text" name="rgDicId" lay-verify="required|number" value="${dic.RG_DIC_ID }"  autocomplete="off" class="layui-input">
				    </div>
				  </div>
				  <div class="layui-inline">
				    <label class="layui-form-label">子级_名称</label>
				    <div class="layui-input-inline">
				      <input type="text" name="dicName" lay-verify="required"  value="${dic.DIC_NAME }" autocomplete="off" class="layui-input">
				    </div>
				    <button type="button" class="layui-btn layui-btn-sm" onclick='delTr(this,0)'><i class="layui-icon"></i></button>
				  	<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,0)'><i class="layui-icon layui-icon-up"></i></button>
		    		<button type="button" class="layui-btn layui-btn-sm" onclick='moveDiv(this,1)'><i class="layui-icon layui-icon-down"></i></button>
				  </div>
				</div>
			</c:forEach>
		</div>
	</c:if>
	 <c:if test="${dicDbo.DIC_PARENT_ID>0 }">
	    <div class="layui-form-item">
		   <div class="layui-inline">
			    <label class="layui-form-label" id='parentLable'>父级字典</label>
			    <div class="layui-input-inline" id="parentChoose">
				    <input type="hidden" id="cparentDicName" name="cparentDicName" value="${dicDbo.DIC_PARENT_NAME}"/>
			        <select name="parentDicId" lay-search onchange="getParentName(this)">
			          <c:forEach items="${dicList}" var="dic">
				        <option value="${ dic.DIC_ID }" <c:if test="${dicDbo.DIC_PARENT_ID==dic.DIC_ID }">selected</c:if>>${ dic.DIC_NAME }</option>
				      </c:forEach>
				    </select>
			    </div>
		   </div>
		</div>
		<div class="layui-form-item">
		  <div class="layui-inline">
		    <label class="layui-form-label">子级_ID</label>
		    <div class="layui-input-inline">
		      <input type="hidden" id="dicId" name="dicId" value="${dicDbo.DIC_ID }"/>
		      <input type="text" name="rgDicId" lay-verify="required|number" value="${dicDbo.RG_DIC_ID }"  autocomplete="off" class="layui-input">
		    </div>
		  </div>
		  <div class="layui-inline">
		    <label class="layui-form-label">子级_名称</label>
		    <div class="layui-input-inline">
		      <input type="text" name="dicName" lay-verify="required"  value="${dicDbo.DIC_NAME }" autocomplete="off" class="layui-input">
		    </div>
		  </div>
		</div>
	</c:if>
	<div class="layui-form-item layui-hide">
	      <button  type='button' id='saveDic' class="layui-btn" lay-submit lay-filter="saveDic">立即提交</button>
	</div>
</form>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script>
    var layuiForm = layui.form;
    var layer = layui.layer;
    var $ = layui.jquery; 
    var parent$ = window.parent.layui.jquery;//父页面jquery对象
    var childDiv = '${dicDbo.DIC_PARENT_ID}'=='0'?$("#childDiv").children().eq(0).prop("outerHTML"):"";
	//监听下拉框选中事件
	layuiForm.on("select",function (data) {
		//触发change事件
		$(this).parent().parent().parent().find("select").change();
	});
	//监听下拉框选中事件
	layuiForm.on("radio",function (data) {
		//触发change事件
		$(this).change();
	});
	layuiForm.on("submit(saveDic)",function (data) {
		if ('${dicDbo.DIC_PARENT_ID}'=='0' && $("#childDiv").children().length==0) {
			layer.msg("请至少添加一条子级字典！",{icon: 5});
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
	      url:'dic_update',
	      type:'post',
	      data:$("#dicForm").serialize(),
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

	//获取字典父级
	function getParentName(obj) {
		$("#cparentDicName").val($(obj).find("option[value='"+$(obj).val()+"']").text());
	}

	//添加子级
	function addChild() {
		$("#childDiv").append(childDiv);
		$("#childDiv").children().last().find("input").val("");
		$("#childDiv").children().last().find("#dicId").val(0);
	}

	//删除
	function delTr (obj) {
		layer.confirm('确定要删除吗？', function(index){
			$(obj).closest(".layui-form-item").remove();
			layer.msg("已删除");
			var dicId = $(obj).closest('.layui-form-item').find("#dicId").val();
			if (dicId>0) {
				var removeIds = $("#removeIds").val();
				removeIds = removeIds!=null?removeIds:"";
				removeIds += (removeIds.length>0?",":"")+dicId;
				$("#removeIds").val(removeIds);
			}
		});
	}

	//移动div
	function moveDiv (obj,order) {
		var nowDiv = $(obj).closest(".layui-form-item");//当前div
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
