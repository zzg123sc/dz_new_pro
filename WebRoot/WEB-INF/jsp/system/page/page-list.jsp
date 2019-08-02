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
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
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
		            <label class="layui-form-label">页面ID</label>
		            <div class="layui-input-inline">
		              <input type="text" name="pageId" placeholder="请输入" autocomplete="off" class="layui-input">
		            </div>
		          </div>
		          <div class="layui-inline">
		            <label class="layui-form-label">页面名称</label>
		            <div class="layui-input-inline">
		              <input type="text" name="pageName" placeholder="请输入" autocomplete="off" class="layui-input">
		            </div>
		          </div>
		          <div class="layui-inline">
		            <label class="layui-form-label">对象ID</label>
		            <div class="layui-input-inline">
		              <input type="text" name="tableId" placeholder="请输入" autocomplete="off" class="layui-input">
		            </div>
		          </div>
		          <div class="layui-inline">
		            <label class="layui-form-label">对象名称</label>
		            <div class="layui-input-inline">
		              <input type="text" name="tableName" placeholder="请输入" autocomplete="off" class="layui-input">
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
    			<button class="layui-btn layuiadmin-btn-forum-list" data-type="batchdel" lay-event="batchdel">批量删除</button>
    			<button class="layui-btn layuiadmin-btn-forum-list " data-type="batchRefResh" lay-event="batchRefResh">批量刷新页面</button>
  			</div>
		</script>
        <table id="listTable" lay-filter="listTable" lay-filter="listTable"></table>
      </div>
    </div>
  </div>
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="zdyJs/common.js"></script>
<script type="text/javascript" src="zdyJs/page-commonset.js"></script>
<script>
	var table = layui.table //表格
	var layer = layui.layer;
	var $ = layui.jquery;
    layer.config({
    	skin:'layui-layer-molv'
    });

    $("#listForm").keydown(function(event){
		if(event.keyCode == 13){ //绑定回车 
			initTable ();
		}
    });
    
	//初始化表格
	initTable ();
	
	function initTable () {
		var ifCnt = false;
		table.render({
		    elem: '#listTable'
	    	,height: 'full-100'
   	        ,cellMinWidth: 80
   	        ,toolbar: '#batchToolbar'
		    ,url: 'page_ajaxList?'+$("#listForm").serialize() //数据接口
		    ,page: true //开启分页
		    ,cols: [[ //表头
		      {type: 'checkbox', fixed: 'left'}    
		      ,{type: 'numbers', title: '序号', fixed: 'left'} 
		      ,{field: 'pageId', title: '页面ID', width:150, sort: true, fixed: 'left'}
		      ,{field: 'pageName', title: '页面名称', width:150}
		      ,{field: 'pageType', title: '页面类型', width:120,templet: function(rowData){
			        var value = '';
					if (rowData.pageType==1) {
						value = '列表';
					} else if (rowData.pageType==2) {
						value = '添加';
					} else if (rowData.pageType==3) {
						value = '修改';
					} else if (rowData.pageType==4) {
						value = '批量修改';
					} else if (rowData.pageType==5) {
						value = '弹层';
					} else if (rowData.pageType==6) {
						value = '下拉树';
					} else if (rowData.pageType==7) {
						value = '查看';
					}
					if (rowData.isChild!=null && rowData.isChild==1) {
						value="子页面_"+value;
					}
					return value;
		      }}
		      ,{field: 'tableId', title: '对象ID', width:150}
		      ,{field: 'tableName', title: '对象名称', width:150}
		      ,{fixed: 'right', title: '操作', width: 150, align:'left',templet: function(rowData){
					var opera =  '<a class="layui-btn layui-btn-danger layui-btn-xs operaBtn" onclick="deletePage('+rowData.pageId+',\''+rowData.pageName+'\')">删除</a>';
					opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="update" onclick="refReshPage ('+rowData.pageId+')">刷新</a>';
					if (rowData.pageType==1) {//列表类型
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="update" onclick="pageContentSet ('+rowData.tableId+',\''+rowData.pageId+'\')">内容设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageSearchSet('+rowData.tableId+',\''+rowData.pageId+'\')">搜索条件设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageSqlSet('+rowData.tableId+',\''+rowData.pageId+'\')">页面sql设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageHtmlSet('+rowData.tableId+',\''+rowData.pageId+'\')">页面html设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageLimitSet('+rowData.tableId+',\''+rowData.pageId+'\')">页面权限设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageButtonSet('+rowData.tableId+',\''+rowData.pageId+'\')">按钮条设置</a>';
						opera +='<a class="layui-btn layui-btn-xs operaBtn" lay-event="createPage" onclick="pageListButton('+rowData.tableId+',\''+rowData.pageId+'\')">数据按钮设置</a>';
					}
					return opera;
				 }
		      }
		    ]],done:function (res,curr,count) {
		        if (!ifCnt) {
					table.reload('listTable', {
				        page: {
				          count: 25 //重新从第 1 页开始
				        }
				      },'data');
					ifCnt = true;
		        }
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
	      case 'batchdel':
		      if (data.length>0) {
			      var rmIds = "",rmNames="";
			      for (var i=0;i<data.length;i++) {
			    	  rmIds+=(i>0?",":"")+data[i].pageId;
			    	  rmNames+=(i>0?",":"")+data[i].pageName;
				  }
	    	  	  deletePage(rmIds,rmNames);
		      } else {
		    	  layer.msg("请至少选择一条数据！",{icon: 5,offset: 'rt'});
			  }
	      	  break;
	      case 'batchRefResh':
	    	  if (data.length>0) {
			      var rmIds = "";
			      for (var i=0;i<data.length;i++) {
			    	  rmIds+=(i>0?",":"")+data[i].pageId;
				  }
			      refReshPage(rmIds);
		      } else {
		    	  layer.msg("请至少选择一条数据！",{icon: 5,offset: 'rt'});
			  }
		      break;
	    };
	});

	//删除页面
	function deletePage (pageIds,pageNames) {
		layer.confirm('确定要删除<font color="red">'+pageNames+'</font>吗？', function(index){
			layer.close(index);
			$.ajax({
		      url:'page_delete',
		      type:'post',
		      data:{pageId:pageIds},
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

	//刷新页面
	function refReshPage (pageIds) {
		var index = loadOpera();
		$.ajax({
	      url:'page_refReshPage',
	      type:'post',
	      data:{pageId:pageIds},
	      success:function(result){
	       if(result==1){
	         	parent.layer.msg("刷新成功！",{icon: 6,offset: 't'});
	       }
	       layer.close(index);
	      },error:function (result) {
	    	  layer.msg("刷新失败！",{icon: 5,offset: 'rt'});
	    	  layer.close(index);
		  }
	    });
	}
</script>
</body>
</html>
