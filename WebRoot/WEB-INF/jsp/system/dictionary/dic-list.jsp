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
<title>栏目管理</title>
<head>
	<link rel="stylesheet" href="static/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css"/>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body layadmin-themealias="default">
<div class="layui-fluid">  
  <div class="layui-row">
    <div class="layui-col-md2">
		<div class="layui-btn-group">
		  <button class="layui-btn" onclick='changeZtree()' style='width:40%;'><i class="layui-icon layui-icon-shrink-right"></i></button>
		  <button class="layui-btn" onclick='expandZtree(true)' style='width:30%;'><i class="layui-icon layui-icon-screen-full"></i></button>
		  <button class="layui-btn" onclick='expandZtree(false)' style='width:30%;'><i class="layui-icon layui-icon-screen-restore"></i></button>
		</div>
		<form id='treeForm'>
			<div class="layui-input-inline" style='width:130px'>
				<input type="text" id="treeName" name="treeName" placeholder="请输入" autocomplete="off" class="layui-input">
			</div>
			<a class="layui-btn layui-btn-xs" onclick='refreshTree()'><i class="layui-icon layui-icon-search"></i></a>
		</form>
		<div id="dicTree" class="ztree"></div>
    </div>
    <div class="layui-col-md10">
	  <div class="layui-fluid">
	    <div class="layui-card">
	      	<form class="layui-form" id='listForm'>
	      	   <div class="layui-collapse">
				  <div class="layui-colla-item">
				    <h2 class="layui-colla-title">搜索条件</h2>
				    <div class="layui-colla-content">
				        <div class="layui-form-item">
				          <div class="layui-inline">
				            <label class="layui-form-label">字典名称</label>
				            <div class="layui-input-inline">
				              <input type="text" name="dicName" placeholder="请输入" autocomplete="off" class="layui-input">
				            </div>
				          </div>
				          <div class="layui-inline">
				            <label class="layui-form-label">父级字典</label>
				            <div class="layui-input-inline">
				              <input type="text" name="parentDicName" placeholder="请输入" autocomplete="off" class="layui-input">
				            </div>
				          </div>
				        </div>
				     </div>
				   </div>
				</div>
	        </form>
	      
	      <div class="layui-card-body">
	        <script type="text/html" id="batchToolbar" lay-filter="batchToolbar">
  			<div class="layui-btn-container">
                <button class="layui-btn layuiadmin-btn-forum-list" lay-submit="" lay-filter="LAY-app-forumlist-search" lay-event="initTable">
              <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
            </button>
    			<button class="layui-btn layuiadmin-btn-forum-list" data-type="batchdel" lay-event="batchdel">删除</button>
          		<button class="layui-btn layuiadmin-btn-forum-list" data-type="add" lay-event="add">添加</button>
  			</div>
		</script>
	        <table id="listTable" lay-filter="listTable" lay-filter="listTable"></table>
	        <script type="text/html" id="listBar">
 			<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
  			<a class="layui-btn layui-btn-xs" lay-event="update">修改</a>
		</script>
	      </div>
	    </div>
	  </div>
	</div>
</div></div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="static/ztree/js/jquery.ztree.exedit.js"></script>
<script type="text/javascript" src="zdyJs/common.js"></script>
<script>
	var table = layui.table //表格
	var layer = layui.layer;
    layer.config({
    	skin:'layui-layer-molv'
    });
    var setting = {
    		edit: {
    			enable: false,
    			showRemoveBtn :false,
    			showRenameBtn: false
    		},
    		data: {
    			simpleData: {
    				enable: true
    			}
    		},
    		callback: {
    			onClick:clickTree
    		}
    };

    $("#listForm").keydown(function(event){
		if(event.keyCode == 13){ //绑定回车 
			initTable ();
		}
    });

    $("#treeForm").keydown(function(event){
		if(event.keyCode == 13){ //绑定回车 
			refreshTree ();
		}
    });

    refreshTree();

	/*刷新树
	*/
	function refreshTree(){
		$.ajax({
			type : "post",
			url : "dic_ajaxTreeDic",
			data : $("#treeForm").serialize(),
			success : function (data) {
				$.fn.zTree.init($('#dicTree'), setting, data);
			}
		});	
	}

	/*
	 *树的点击事件
	 */
	function clickTree(event, treeId, treeNode, clickFlag) {
		initTable(treeNode.id);
		//修改字典
		toUpdateDic (treeNode.id,treeNode.name);
	}
    
    
	//初始化表格
	initTable ();
	
	function initTable (parentId) {
		table.render({
		    elem: '#listTable'
	    	,height: 'full-100'
   	        ,cellMinWidth: 80
   	        ,toolbar: '#batchToolbar'
		    ,url: 'dic_ajaxList?'+$("#listForm").serialize()+'&parentId='+parentId //数据接口
		    ,page: true //开启分页
		    ,cols: [[ //表头
		      {type: 'checkbox', fixed: 'left'}    
		      ,{type: 'numbers', title: '序号', fixed: 'left'} 
		      ,{field: 'DIC_ID', title: 'ID', width:80, sort: true, fixed: 'left'}
		      ,{field: 'RG_DIC_ID', title: '固定ID', width:150}
		      ,{field: 'DIC_NAME', title: '字典名称', width:150}
		      ,{field: 'DIC_PARENT_ID', title: '父级字典ID', width:150}
		      ,{field: 'DIC_PARENT_NAME', title: '父级字典名称', width:150}
		      ,{fixed: 'right', width: 165, align:'center', toolbar: '#listBar'}
		    ]]
		  });
	}

	//监听头工具栏事件
	table.on('toolbar(listTable)', function(obj){
	    var checkStatus = table.checkStatus(obj.config.id)
	    ,data = checkStatus.data; //获取选中的数据
	    switch(obj.event){
	      case 'initTable':
	    	  initTable();
	    	  break;
	      case 'add':
		    	toAddDic ();
		      break;
	      case 'batchdel':
		      if (data.length>0) {
			      var rmIds = "",rmNames="";
			      for (var i=0;i<data.length;i++) {
			    	  rmIds+=(i>0?",":"")+data[i].DIC_ID;
			    	  rmNames+=(i>0?",":"")+data[i].DIC_NAME;
				  }
	    	  	  deleteMenu(rmIds,rmNames);
		      } else {
		    	  layer.msg("请至少选择一条数据！",{icon: 5,offset: 'rt'});
			  }
	      break;
	    };
	});

	//监听行工具事件
	table.on('tool(listTable)', function(obj){
		var data = obj.data //获得当前行数据
	    ,layEvent = obj.event; //获得 lay-event 对应的值
	    switch(layEvent){
	      case 'del':
	    	  deleteMenu(data.DIC_ID,data.DIC_NAME);
	      break;
	      case 'update':
	    	  toUpdateDic (data.DIC_ID,data.DIC_NAME);
	      break;
	    };
	});

	//去添加栏目
	function toAddDic () {
		 openPage ("数据字典添加","dic_toAdd",820,320,"saveDic");
	}

	//去修改栏目
	function toUpdateDic (dicId,dicName) {
		openPage ('数据字典修改-'+dicName,'dic_toUpdate?dicId='+dicId,820,320,"saveDic");
	}

	//删除栏目
	function deleteMenu (dicIds,dicNames) {
		layer.confirm('确定要删除<font color="red">'+dicNames+'</font>吗？', function(index){
			layer.close(index);
			$.ajax({
		      url:'dic_delete',
		      type:'post',
		      data:{"dicId":dicIds},
		      success:function(result){
		       if(result==1){
		         	parent.layer.msg("删除成功！",{icon: 6,offset: 't'});
		        	initTable ();
		       }
		      },error:function (result) {
		    	  layer.msg("删除失败！",{icon: 5,offset: 'rt'});
			  }
		    });
		});
	}

	//树的展开收起
	function changeZtree() {
		if ($(".layui-col-md2").length>0) {
			$(".layui-col-md2").removeClass('layui-col-md2').addClass('layui-col-md1');
			$(".layui-col-md10").removeClass('layui-col-md10').addClass('layui-col-md11');
			$(".layui-icon-shrink-right").removeClass('layui-icon-shrink-right').addClass('layui-icon-spread-left');
		} else {
			$(".layui-col-md1").removeClass('layui-col-md1').addClass('layui-col-md2');
			$(".layui-col-md11").removeClass('layui-col-md11').addClass('layui-col-md10');
			$(".layui-icon-spread-left").removeClass('layui-icon-spread-left').addClass('layui-icon-shrink-right');
		}
	}

	//展开收起树
	function expandZtree (flag) {
		var treeObj = $.fn.zTree.getZTreeObj("dicTree");
		treeObj.expandAll(flag);
	}
</script>
</body>
</html>
