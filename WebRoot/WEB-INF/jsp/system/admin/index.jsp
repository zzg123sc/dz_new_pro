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
<html>
<head>
  <meta charset="utf-8">
  <title>东志教务管理系统</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
  <link rel="stylesheet" href="layuiadmin/layui/css/layui.css" media="all">
  <link rel="stylesheet" href="layuiadmin/style/admin.css" media="all">
</head>
<body class="layui-layout-body">
  
  <div id="LAY_app">
    <div class="layui-layout layui-layout-admin">
      <div class="layui-header">
        <!-- 头部区域 -->
        <ul class="layui-nav layui-layout-left">
          <li class="layui-nav-item layadmin-flexible" lay-unselect>
            <a href="javascript:;" layadmin-event="flexible" title="侧边伸缩">
              <i class="layui-icon layui-icon-shrink-right" id="LAY_app_flexible"></i>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="http://www.layui.com/admin/" target="_blank" title="前台">
              <i class="layui-icon layui-icon-website"></i>
            </a>
          </li>
          <li class="layui-nav-item" lay-unselect>
            <a href="javascript:;" layadmin-event="refresh" title="刷新">
              <i class="layui-icon layui-icon-refresh-3"></i>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <input type="text" placeholder="搜索..." autocomplete="off" class="layui-input layui-input-search" layadmin-event="serach" lay-action="template/search.html?keywords="> 
          </li>
        </ul>
        <ul class="layui-nav layui-layout-right" lay-filter="layadmin-layout-right">
          
          <li class="layui-nav-item" lay-unselect>
            <a lay-href="app/message/index.html" layadmin-event="message" lay-text="消息中心">
              <i class="layui-icon layui-icon-notice"></i>  
              
              <!-- 如果有新消息，则显示小圆点 -->
              <span class="layui-badge-dot"></span>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="theme">
              <i class="layui-icon layui-icon-theme"></i>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="note">
              <i class="layui-icon layui-icon-note"></i>
            </a>
          </li>
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="fullscreen">
              <i class="layui-icon layui-icon-screen-full"></i>
            </a>
          </li>
          <li class="layui-nav-item" lay-unselect>
            <a href="javascript:;">
              <cite>${loginUserName}</cite>
            </a>
            <dl class="layui-nav-child">
              <dd><a lay-href="set/user/info.html">基本资料</a></dd>
              <dd><a lay-href="set/user/password.html">修改密码</a></dd>
              <hr>
              <dd  style="text-align: center;"><a href='logout'>退出</a></dd>
            </dl>
          </li>
          
          <li class="layui-nav-item layui-hide-xs" lay-unselect>
            <a href="javascript:;" layadmin-event="about"><i class="layui-icon layui-icon-more-vertical"></i></a>
          </li>
          <li class="layui-nav-item layui-show-xs-inline-block layui-hide-sm" lay-unselect>
            <a href="javascript:;" layadmin-event="more"><i class="layui-icon layui-icon-more-vertical"></i></a>
          </li>
        </ul>
      </div>
      
      <!-- 侧边菜单 -->
      <div class="layui-side layui-side-menu">
        <div class="layui-side-scroll">
          <div class="layui-logo">
            <span>东志教务管理系统</span>
          </div>
          <ul class="layui-nav layui-nav-tree" lay-shrink="all" id="LAY-system-side-menu" lay-filter="layadmin-system-side-menu">
            <li class="layui-nav-item systermMenu">
              <a href="javascript:;" lay-tips="对象管理" lay-href="object_toList" lay-direction="2">
                <i class="layui-icon layui-icon-component"></i>
                <cite>对象管理</cite>
              </a>
            </li>
            <li  class="layui-nav-item systermMenu">
              <a href="javascript:;" lay-tips="栏目管理" lay-href="menu_toList" lay-direction="2">
                <i class="layui-icon layui-icon-app"></i>
                <cite>栏目管理</cite>
              </a>
            </li>
            <li  class="layui-nav-item systermMenu">
              <a href="javascript:;" lay-tips="数据字典管理" lay-href="dic_toList" lay-direction="2">
                <i class="layui-icon layui-icon-senior"></i>
                <cite>数据字典管理</cite>
              </a>
            </li>
            <li  class="layui-nav-item systermMenu">
              <a href="javascript:;" lay-tips="页面管理" lay-href="page_toList" lay-direction="2">
                <i class="layui-icon layui-icon-template"></i>
                <cite>页面管理</cite>
              </a>
            </li>
            <c:forEach items="${menuList}" var="menu">
            	<li  class="layui-nav-item" id='pm_${menu.menuId }'>
		  	    	<a href="javascript:;" lay-tips="${menu.menuName}" lay-direction="2">
		                <i class="layui-icon layui-icon-senior"></i>
		                <cite>${menu.menuName}</cite>
		            </a>
	            </li>
		  	</c:forEach>
          </ul>
        </div>
      </div>

      <!-- 页面标签 -->
      <div class="layadmin-pagetabs" id="LAY_app_tabs">
        <div class="layui-icon layadmin-tabs-control layui-icon-prev" layadmin-event="leftPage"></div>
        <div class="layui-icon layadmin-tabs-control layui-icon-next" layadmin-event="rightPage"></div>
        <div class="layui-icon layadmin-tabs-control layui-icon-down">
          <ul class="layui-nav layadmin-tabs-select" lay-filter="layadmin-pagetabs-nav">
            <li class="layui-nav-item" lay-unselect>
              <a href="javascript:;"></a>
              <dl class="layui-nav-child layui-anim-fadein">
                <dd layadmin-event="closeThisTabs"><a href="javascript:;">关闭当前标签页</a></dd>
                <dd layadmin-event="closeOtherTabs"><a href="javascript:;">关闭其它标签页</a></dd>
                <dd layadmin-event="closeAllTabs"><a href="javascript:;">关闭全部标签页</a></dd>
              </dl>
            </li>
          </ul>
        </div>
        <div class="layui-tab" lay-unauto lay-allowClose="true" lay-filter="layadmin-layout-tabs">
          <ul class="layui-tab-title" id="LAY_app_tabsheader">
            <li class="layui-this"><i class="layui-icon layui-icon-home"></i></li>
          </ul>
        </div>
      </div>
      
      
      <!-- 主体内容 -->
      <div class="layui-body" id="LAY_app_body">
        <div class="layadmin-tabsbody-item layui-show">
          <iframe src="login_home" frameborder="0" class="layadmin-iframe"></iframe>
        </div>
      </div>
      <!-- 辅助元素，一般用于移动设备下遮罩 -->
      <div class="layadmin-body-shade" layadmin-event="shade"></div>
    </div>
  </div>

  <script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
  <script src="layuiadmin/layui/layui.js"></script>
  <script>
  layui.config({
    base: 'layuiadmin/' //静态资源所在路径
  }).extend({
    index: 'lib/index' //主入口模块
  }).use('index');

   //非管理员看不到这些系统设置栏目
   if ('${loginUserId}'!='0') {
		$(".systermMenu").remove();
   }
  
  //初始化栏目加载
  var menuList = ${menuList};
  if (menuList.length>0) {
	  for (var i=0;i<menuList.length;i++) {
	  	 var parentMenuId = menuList[i].parentMenuId;//父级id
	  	 var menuId =menuList[i].menuId;//栏目id
	  	 var menuName =menuList[i].menuName;//栏目名称
	  	 var pageId = menuList[i].pageId;//栏目pageId
	  	 var tableId = menuList[i].tableId;//栏目tableId
	  	 var pageUrl = menuList[i].pageUrl;//栏目pageUrl
	  	 pageId = (pageId==null || pageId==undefined)?0:pageId;
	  	 pageUrl = (pageUrl==null || pageUrl==undefined)?"":pageUrl;
  	 	 pageUrl = pageId>0?'listCore_toList?pageId='+pageId+'&tableId='+tableId:pageUrl;
	  	 if ($("#pm_"+parentMenuId).length>0) {
	  	 	var childDiv = $("#pm_"+parentMenuId).find(".layui-nav-child");//父级的子级div
	  	 	if (childDiv.length==0) {
	  	 		childDiv = $('<dl class="layui-nav-child"></dl>');
	  	 		$("#pm_"+parentMenuId).append(childDiv);
	  	 	}
	  	 	var dqChildDiv = $("#pm_"+menuId).find(".layui-nav-child");//当前的子级div
	  	 	var dqChildDivHtml = dqChildDiv.length==0?"":dqChildDiv.prop("outerHTML");
	  	 	$("#pm_"+menuId).remove();
	  	 	pageUrl = pageUrl.length>0?'lay-href="'+pageUrl+'"':'href="javascript:;"';
	  	 	childDiv.append('<dd id="pm_'+menuId+'"><a '+pageUrl+'>'+menuName+'</a>'+dqChildDivHtml+'</dd>');
	  	 } else if (pageUrl.length>0){
	  	 	$("#pm_"+menuId).find('a').eq(0).removeAttr("href").attr("lay-href",pageUrl);
	  	 }
	  }
  }
  </script>
</body>
</html>


