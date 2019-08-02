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
<title>字段</title>
<head>
	<link rel="stylesheet" type="text/css" href="layuiadmin/layui/css/layui.css">
</head>
<body >
      <script type="text/html" id="toolbarDemo">
  			<div class="layui-btn-container">
                <button class="layui-btn layuiadmin-btn-forum-list" lay-submit="" lay-filter="LAY-app-forumlist-search" onclick='initFieldTable()'>
              <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
            </button>
  			</div>
		</script>
	  <div ondblclick="$(this).wordExport('字段');">
      	<table id="fieldListTable" lay-filter="LAY-app-forum-list" ></table>
      </div>
      
<script type="text/javascript" src="layuiadmin/layui/layui.all.js"></script>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<script>
	//初始化表格
	initFieldTable ();
	function initFieldTable () {
		layui.table.render({
		    elem: '#fieldListTable'
	    	,height: 'full'
   	        ,cellMinWidth: 80
   	        ,limit:5
   	        ,toolbar: '#toolbarDemo'
		    ,url: 'object_getFields?tableId='+${tableId}//数据接口
		    ,page: true //开启分页
		    ,cols: [[ //表头
		      {type: 'numbers', title: '序号', fixed: 'left'} 
		      ,{field: 'fieldId', title: 'ID', width:80, sort: true, fixed: 'left'}
		      ,{field: 'fieldCnName', title: '字段中文名称', width:120}
		      ,{field: 'fieldName', title: '字段英文名称', width:120}
		      ,{field: 'fieldType', title: '字段类型', width:120,templet: function(rowData){
				  var typeName = '';
		          for (var i=0;i<${allFieldTypes}.length;i++) {
	                  if (rowData.fieldType==${allFieldTypes}[i].id) {
	                	  typeName = ${allFieldTypes}[i].name;
	                	  break;
	                  }
			      }
		          return typeName;
		        }
		      }
		      ,{field: 'ifOnly', title: '是否唯一', width:120 ,templet: function(rowData){
		          return rowData.ifOnly==1?"是":"否";
		        }
		      }
		    ]]
		  });
	}

</script>
</body>
</html>
