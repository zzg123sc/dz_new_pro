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
	<link rel="stylesheet" type="text/css" href="static/formSelects/formSelects-v4.css">
</head>
<body layadmin-themealias="default">
  <div class="layui-fluid">
    <div class="layui-card">
     <form class="layui-form" id='listForm'>
     	<div class="layui-collapse">
		  <div class="layui-colla-item">
		    <h2 class="layui-colla-title">搜索条件</h2>
		    <div class="layui-colla-content">
		        <div class="layui-form-item">
		          <div class="layui-inline">
		            <label class="layui-form-label">名称_中文</label>
		            <div class="layui-input-inline">
		              <input type="text" name="tableCnName" placeholder="请输入" autocomplete="off" class="layui-input">
		            </div>
		          </div>
		          <div class="layui-inline">
		            <label class="layui-form-label">名称_英文</label>
		            <div class="layui-input-inline">
		              <input type="text" name="tableName" placeholder="请输入" autocomplete="off" class="layui-input">
		            </div>
		          </div>
		          <div class="layui-inline">
		            <label class="layui-form-label">对象类型</label>
		            <div class="layui-input-inline">
		              <select name="tableType" xm-select='select1' xm-select-search='page_getAllPage'>
		              <option value="">--请选择--</option>
		              <c:forEach items="${allObjTypes}" var="types">
				        <option value="${ types.id }">${ types.name }</option>
				      </c:forEach>
				      </select>
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
      </div>
    </div>
  </div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/formSelects/formSelects-v4.js"></script>
<script type="text/javascript" src="zdyJs/common.js"></script>
<script>
	var table = layui.table ;//表格
	var layer = layui.layer;
	var $ = layui.jquery;
    layer.config({
    	skin:'layui-layer-molv'
    });
	//初始化表格
	initTable ();

	$("#listForm").keydown(function(event){
		if(event.keyCode == 13){ //绑定回车 
			initTable ();
		}
    });
    
	function initTable () {
		table.render({
		    elem: '#listTable'
	    	,height: 'full-100'
   	        ,cellMinWidth: 80
   	        ,toolbar: '#batchToolbar'
		    ,url: 'object_ajaxList?'+$("#listForm").serialize() //数据接口
		    ,page: true //开启分页
		    ,cols: [[ //表头
		      {type: 'checkbox', fixed: 'left'}  
		      ,{type: 'numbers', title: '序号', fixed: 'left'}      
		      ,{field: 'tableId', title: 'ID', width:80, sort: true, fixed: 'left'}
		      ,{field: 'tableCnName', title: '对象中文名称', width:120,templet: function(rowData){
					return "<a href='#' title='查看对象关联字段' onclick='lookFields("+rowData.tableId+",\""+rowData.tableCnName+"\")'><i class='layui-icon layui-icon-link' style='font-size: 15px; color: #1E9FFF;'></i> "+rowData.tableCnName+"</a>";
		      		}
		      }
		      ,{field: 'tableName', title: '对象英文名称', width:120}
		      ,{field: 'tableType', title: '对象类型', width:120,templet: function(rowData){
				  var typeName = '';
		          for (var i=0;i<${allObjTypes}.length;i++) {
	                  if (rowData.tableType==${allObjTypes}[i].id) {
	                	  typeName = ${allObjTypes}[i].name;
	                	  break;
	                  }
			      }
		          return typeName;
		        }
		      }
		      ,{field: 'ifParent', title: '是否支持父子级', width:150
		    	  ,templet: function(rowData){
		          return rowData.ifParent==1?"是":"否";
		        }
		       }
		      ,{field: 'remark', title: '对象备注', width:200}
		      ,{fixed: 'right', title: '按钮', width: 150, align:'center',templet: function(rowData){
					return '<a class="layui-btn layui-btn-danger layui-btn-xs operaBtn" onclick="deleteObject('+rowData.tableId+',\''+rowData.tableCnName+'\')">删除</a>'+
		  			'<a class="layui-btn layui-btn-xs operaBtn" lay-event="update" onclick="toUpdateObject ('+rowData.tableId+',\''+rowData.tableCnName+'\')">修改</a>'+
					'<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="createPage('+rowData.tableId+',\''+rowData.tableCnName+'\')">生成页面</a>';
		      	}
		      }
		    ]],done:function () {
		    	//给操作按钮赋值下标
		    	$(".operaBtn").each(function (i,one) {
					$(one).attr("index",$(one).closest('tr').attr('data-index'));
				});	
			}
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
	    	toAddObject ();
	      break;
	      case 'batchdel':
		      if (data.length>0) {
			      var rmIds = "",rmNames="";
			      for (var i=0;i<data.length;i++) {
			    	  rmIds+=(i>0?",":"")+data[i].tableId;
			    	  rmNames+=(i>0?",":"")+data[i].tableCnName;
				  }
	    	  	  deleteObject(rmIds,rmNames);
		      } else {
		    	  layer.msg("请至少选择一条数据！",{icon: 5,offset: 'rt'});
			  }
	      break;
	    };
	});

	//查看对象字段
	function lookFields (tableId,tableCnName) {
		layer.open({
			  title:tableCnName,
			  type:2,
			  anim: 5
			  ,area:['600px','400px']
			  ,content: 'object_toLookField?tableId='+tableId
			  ,offset: 'auto',
			  shadeClose:true
		});
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
		 parent.layer.iframeAuto(index);
	}

	//去添加对象
	function toAddObject () {
		openPage ("对象添加","object_toAdd",1000,500,"saveObj");
	}

	//去修改对象
	function toUpdateObject (tableId,tableCnName) {
	    openPage ('对象修改-'+tableCnName,'object_toUpdate?tableId='+tableId,1000,500,"saveObj");
	}

	//删除对象
	function deleteObject (tableIds,tableNames) {
		layer.confirm('确定要删除<font color="red">'+tableNames+'</font>吗？', function(index){
			layer.close(index);
			$.ajax({
		      url:'object_delete',
		      type:'post',
		      data:{tableId:tableIds},
		      success:function(result){
	       		layer.msg(result.errorInfo,{icon: result.icon,offset: 't'});
	        	initTable ();
		      },error:function (result) {
		    	  layer.msg("删除失败！",{icon: 5,offset: 'rt'});
			  }
		    });
		});
	}

	//生成页面
	function createPage (tableId,tableCnName) {
		openPage ('生成页面-'+tableCnName,'object_toCreatePage?tableId='+tableId+'&tableCnName='+tableCnName,680,300,"saveObj",false,['生成页面', '取消']);
	}

	
</script>
</body>
</html>
