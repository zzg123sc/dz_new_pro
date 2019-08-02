/**
 * 页面内容设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageContentSet (tableId,pageId) {
	openPage ("列表显示设置",'page_contentSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 页面搜索条件设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageSearchSet (tableId,pageId) {
	openPage ("列表搜索条件设置",'page_searchSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 页面sql设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageSqlSet (tableId,pageId) {
	openPage ("页面sql设置",'page_sqlSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 页面html编辑
 * @param tableId
 * @param pageId
 * @return
 */
function pageHtmlSet (tableId,pageId) {
	openPage ("页面html设置",'page_htmlSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 页面权限设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageLimitSet (tableId,pageId) {
	openPage ("页面权限设置",'page_limitSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 页面按钮条设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageButtonSet (tableId,pageId) {
	openPage ("页面按钮设置",'page_buttonSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 数据按钮设置
 * @param tableId
 * @param pageId
 * @return
 */
function pageListButton (tableId,pageId) {
	openPage ("数据按钮设置",'page_listButtonSet?tableId='+tableId+'&pageId='+pageId,1000,500,"savePage");
}

/**
 * 去添加/关联添加页面设置
 * @param tableId
 * @param pageId
 * @param flag
 * @return
 */
function toAddPageSet (tableId,pageId,buttonType) {
	openPage ("添加页面设置","page_addPageSet?tableId="+tableId+"&pageId="+pageId+"&buttonType="+buttonType,1000,500,"savePage");
}

/**
 * 去修改/查看页面设置
 * @param tableId
 * @param pageId
 * @param flag
 * @return
 */
function toUpdatePageSet (tableId,pageId,flag) {
	openPage ("修改页面设置","page_updatePageSet?tableId="+tableId+"&pageId="+pageId+"&buttonType="+buttonType,1000,500,"savePage");
}

/**
 * 去添加页面设置
 * @param tableId
 * @param pageId
 * @param flag
 * @return
 */
function toBatchUpdatePageSet (tableId,pageId) {
	openPage ("批量修改页面设置","page_batchUpdatePageSet?tableId="+tableId+"&pageId="+pageId,1000,500,"savePage");
}

/*************************************************页面关联信息设置***********************************************************/
//按钮点击事件
function buttonClick (obj) {
	var dqTd = $(obj);
	if (dqTd.children().length==0) {//判断是否是空白
		return;
	}
    var titleName = dqTd.find("#btnName").text();
    var contentHtml = dqTd.find("#btnSetDiv").children().clone(false);
    $("#pageSetDiv").append("<form class=\"layui-form\" id='pageSetForm'></form>");
    $("#pageSetForm").append(contentHtml);
    var buttonType = $("#pageSetForm").find("#buttonType").val();
    var buttonPageIdSelect = $("#pageSetForm").find("#buttonPageId");
    if (buttonType==1 || buttonType==2 || buttonType==8 || buttonType==9) {
    	//动态加载页面数据
    	var searchAttr = buttonPageIdSelect.attr("xm-select-searchAttr");
    	if (searchAttr!=null && searchAttr.length>0) {
    		buttonPageIdSelect.attr("xm-select-search",buttonPageIdSelect.attr("xm-select-searchAttr"));
    	}
    }
    var $from = null;
	layer.open({
		  title:'按钮-'+titleName,
		  type:1,
		  anim: 5
		  ,area:['690px','400px']
		  ,content: $("#pageSetDiv")
		  ,btn: ['保存', '取消']
		  ,btnAlign: 'c'
		  ,offset: 'auto'
		  ,yes: function(index, layero){
			//调用保存
            var submit = layero.find('#saveFieldSet');
            $from = layero.find("#pageSetForm");
			submit.trigger('click');
		  },btn2: function(index, layero){
			 $("#pageSetDiv").children().remove();  
		  },cancel: function(){ 
			$("#pageSetDiv").children().remove();
		  }
		});
		//改变按钮设置隐藏显示
		changeButtonHide($("#pageSetForm"),$("#pageSetForm").find("#buttonType").val());
	    element.render();
	    formSelects.render();
	    layui.form.render('select');
    	//监听下拉框选中事件
    	layui.form.on("select",function (data) {
	    	//当前select元素
    		var dqSelect = $(this).parent().parent().parent().find("select");
    		//触发change事件
    		dqSelect.change();
    	});
	    layui.form.on("submit(saveFieldSet)",function (data) {
	    	var selectOptions = $from.find("option:selected");//选中的下拉框
	    	$from.find("option").removeAttr("selected");
	    	selectOptions.attr("selected",true);
	    	buttonPageIdSelect.removeAttr("xm-select-search");//避免多次查询加载
			dqTd.find("#btnName").text($from.find("#buttonName").val());
			dqTd.find("#btnSetDiv").children().remove();
			dqTd.find("#btnSetDiv").append($from.children().clone(false));
			layer.closeAll();
			$("#pageSetDiv").children().remove();
		});
}

//按钮设置初始化
function btnSetInit (layer,element) {
	$("#buttonSetTable").find('.drage').dblclick(function () {
		buttonClick (this);
	});
	
	//双击新增按钮
	$(".layui-btn-container").find('button').dblclick(function () {
		 var maxTdIndex = $("#buttonSetTable").find('.drage').length-1;//td的行数
	     var lastField = $("#buttonSetTable").find("span[name='btnName']:last");
	     var lastFieldTrIndex = lastField.length==0?0:lastField.closest('tr').index();
	     var lastFieldTdIndex = lastField.length==0?0:lastField.closest('td').index();
	     lastFieldTdIndex = lastFieldTrIndex*4+lastFieldTdIndex;
	     if (lastFieldTdIndex==maxTdIndex) {
	       var $lastTr = $("<tr><td></td><td></td><td></td><td></td></tr>");
	       $lastTr.children('td').addClass("drage canDrop");
	  	   $("#buttonSetTable").append($lastTr);
	  	   $lastTr.children('.drage').dblclick(function () {
	  		   buttonClick (this);
		   });
	  	   //按钮拖拽事件处理
	  	   initBtnDropOrDrage();
	     }
	     lastFieldTdIndex = lastField.length==0?lastFieldTdIndex:lastFieldTdIndex+1;
	     var cloneBtn = $(this).clone(false);//克隆当前字段
	     var buttonType = cloneBtn.attr("buttonType");//按钮类型
	     $("#buttonSetTable").find('.drage').eq(lastFieldTdIndex).append(btnSetHtml({"buttonId":0,"buttonType":buttonType,"buttonName":cloneBtn.text()}));
	});
}

//初始化按钮表格
function inintButtonTable (btnTable,btnList) {
    var tdIndex = 0;
    var trIndex = 0;
    btnTable = btnTable.find('tbody');
    for (var i=0;i<btnList.length;i++) {
        var button = btnList[i];
        var dqTd = btnTable.children("tr").eq(trIndex).children("td").eq(tdIndex);
        var btnDiv = btnSetHtml (button);
        dqTd.append(btnDiv);
        console.log(trIndex+"---"+tdIndex+"--"+dqTd.length);
        if (button.buttonType==3 || button.buttonType==7) {
        	delBtnInit(dqTd,button);//初始化删除
        }
       	tdIndex++;
    	if (i>0 && i%4==3) {
    		var trHtml = $("<tr><td></td><td></td><td></td><td></td></tr>");
    		trHtml.find('td').addClass('drage canDrop');
    		btnTable.append(trHtml);
    		tdIndex = 0;
    		trIndex ++;
    		console.log("---添加行");
        }
    }
}

//删除按钮初始化
function delBtnInit (td,button) {
	var delReList = button.delReList;
	if (delReList!=null && delReList.length>0) {
		for (var i=0;i<delReList.length;i++) {
			var tableId = delReList[i].tableId;
			var deleteType = delReList[i].deleteType;
			var delSel = td.find("select[tmpId='delSet_"+tableId+"']");//删除设置
			if (delSel.length>0) {
				delSel.find("option[value='"+deleteType+"']").attr("selected",true);
			}
		}
	}
}

//改变按钮设置隐藏显示
function changeButtonHide(cloneBtn,buttonType) {
	buttonType = buttonType*1;
	switch (buttonType) {
 	case 1://添加
 		cloneBtn.find("#buttonPageId").closest('.layui-inline').removeClass('layui-hide');
 		break;
 	case 2://批量修改
 		cloneBtn.find("#buttonPageId").closest('.layui-inline').removeClass('layui-hide');
 		break;
 	case 3://批量删除
 		cloneBtn.find("#deleteSet").removeClass('layui-hide');
 		break;
	case 4://导入
 		break;
	case 5://导出
		break;
	case 6://批量自定义
		cloneBtn.find("#pageUrl").closest('.layui-inline').removeClass('layui-hide');
		cloneBtn.find("#urlParam").closest('.layui-form-item').removeClass('layui-hide');
		cloneBtn.find("#pageUrl").attr("lay-verify","required");
		break;
	case 7://删除
 		cloneBtn.find("#deleteSet").removeClass('layui-hide');
		break;
	case 8://关联添加
		cloneBtn.find("#buttonPageId").closest('.layui-inline').removeClass('layui-hide');
		break;
	case 9://修改
		cloneBtn.find("#buttonPageId").closest('.layui-inline').removeClass('layui-hide');
		break;
	case 10://查看
		cloneBtn.find("#buttonPageId").closest('.layui-inline').removeClass('layui-hide');
		break;
	case 11://打印
		break;
	case 12://直接操作
		break;
	case 13://自定义
		cloneBtn.find("#pageUrl").closest('.layui-inline').removeClass('layui-hide');
		cloneBtn.find("#urlParam").closest('.layui-form-item').removeClass('layui-hide');
		cloneBtn.find("#pageUrl").closest('.layui-inline').attr("lay-verify","required");
		break;
   }
}

//字段设置的html
function btnSetHtml (buttonSet) {
	var tableId = $("#tableId").val();
	var btnDiv ="<button type='button' class='layui-btn'><span id='btnName' name='btnName'>"+buttonSet.buttonName+"</span></button>";
	btnDiv+= "<a href='#' onclick='delBtn(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i>  </a>";
	btnDiv+= "	<div id='btnSetDiv' class='layui-hide'>";
    btnDiv+="<div class=\"layui-form-item\">";
    btnDiv+="<div class=\"layui-inline\">";
    btnDiv+="<label class=\"layui-form-label\">按钮名称</label>";
    btnDiv+="<div class=\"layui-input-inline\">";
    btnDiv+="	 <input type='hidden' id='buttonId' name='buttonId' value='"+buttonSet.buttonId+"'>";
    btnDiv+="	 <input type='hidden' id='buttonType' name='buttonType' value='"+buttonSet.buttonType+"'>";
    btnDiv+="  <input type=\"text\" id=\"buttonName\" name=\"buttonName\" value='"+buttonSet.buttonName+"' placeholder=\"按钮名称\" lay-verify=\"required\" class=\"layui-input\">";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-inline layui-hide\">";
    btnDiv+="<label class=\"layui-form-label\">页面ID</label>";
    btnDiv+="<div class=\"layui-input-inline\">";
    btnDiv+="  <select id='buttonPageId' name='buttonPageId' xm-select='select_"+buttonSet.buttonType+"' xm-select-radio xm-select-searchAttr='page_getAllPage?tableId="+tableId+"&buttonType="+buttonSet.buttonType+"'>";
    if (buttonSet.buttonPageId!=null && buttonSet.buttonPageId>0) {
    	btnDiv+="<option value='"+buttonSet.buttonPageId+"' selected='true'>"+buttonSet.buttonPageName+"</option>";
    } else {
    	btnDiv+="<option value=''>--请选择--</option>";
    }
    btnDiv+="</select>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-inline\">";
    btnDiv+="<label class=\"layui-form-label\">是否区分角色权限</label>";
    btnDiv+="<div class=\"layui-input-inline\">";
    btnDiv+="  <select id='ifDisRole' name='ifDisRole'>";
    btnDiv+="		<option value='0' "+(buttonSet.ifDisRole==0?"selected='true'":"")+">是</option>";
    btnDiv+="		<option value='1' "+(buttonSet.ifDisRole==1?"selected='true'":"")+">否</option>";
    btnDiv+="</select>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-inline\">";
    btnDiv+="<label class=\"layui-form-label\">页面打开方式</label>";
    btnDiv+="<div class=\"layui-input-inline\">";
    btnDiv+="  <select id='openStyle' name='openStyle'>";
    btnDiv+="		<option value='0' "+(buttonSet.openStyle==0?"selected='true'":"")+">弹层</option>";
    btnDiv+="		<option value='1' "+(buttonSet.openStyle==1?"selected='true'":"")+">iframe选项卡</option>";
    btnDiv+="		<option value='2' "+(buttonSet.openStyle==2?"selected='true'":"")+">无</option>";
    btnDiv+="</select>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-inline\">";
    btnDiv+="<label class=\"layui-form-label\">页面宽/高</label>";
    btnDiv+="<div class=\"layui-input-inline\" style='width: 90px;'>";
    btnDiv+="  <input type='text' id='pageWidth'  name='pageWidth' value='"+(buttonSet.pageWidth==null?0:buttonSet.pageWidth)+"' lay-verify=\"number\" placeholder='请输入宽度' autocomplete='off' class='layui-input'>";
    btnDiv+="</div><div class=\"layui-input-inline\" style='width: 90px;'>";
    btnDiv+="  <input type='text' id='pageHeight'  name='pageHeight' value='"+(buttonSet.pageHeight==null?0:buttonSet.pageHeight)+"' lay-verify=\"number\" placeholder='请输入高度' autocomplete='off' class='layui-input'>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-inline layui-hide\">";
    btnDiv+="<label class=\"layui-form-label\">url地址</label>";
    btnDiv+="<div class=\"layui-input-inline\">";
    btnDiv+="  <input type='text' id='pageUrl'  name='pageUrl' value='"+(buttonSet.pageUrl==null?"":buttonSet.pageUrl)+"' placeholder='请输入页面url地址' autocomplete='off' class='layui-input'>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div class=\"layui-form-item layui-hide\">";
    btnDiv+="<label class=\"layui-form-label\">参数</label>";
    btnDiv+="<div class=\"layui-input-block\">";
    btnDiv+="  <textarea id='urlParam' name='urlParam' placeholder='参数类似param1={AFM_1}&param2={AFM_2}...' class='layui-textarea' style='width:100%'>"+(buttonSet.urlParam==null?"":buttonSet.urlParam)+"</textarea>";
    btnDiv+="</div>";
    btnDiv+="</div>";
    btnDiv+="<div id='deleteSet' class='layui-collapse layui-hide'>";
    btnDiv+="  <div class='layui-colla-item'>";
    btnDiv+="    <h2 class='layui-colla-title'>关联删除设置</h2>";
    btnDiv+="    <div class='layui-colla-content' id='delTableInfo'>"+$("#commonDeleteSet").html();
	btnDiv+="</div></div>";
    btnDiv+="</div>";
    btnDiv+="<div class='layui-collapse'>";
    btnDiv+="  <div class='layui-colla-item'>";
    btnDiv+="    <h2 class='layui-colla-title'>按钮操作前JS</h2>";
    btnDiv+="    <div class='layui-colla-content'>";
    btnDiv+="  <textarea id='btnBeforeJs' name='btnBeforeJs' placeholder='按钮操作前js...' class='layui-textarea' style='width:100%'>"+(buttonSet.btnBeforeJs==null?"":buttonSet.btnBeforeJs)+"</textarea>";
    btnDiv+="</div></div>";
    btnDiv+="</div>";
    btnDiv+="<div class='layui-collapse'>";
    btnDiv+="  <div class='layui-colla-item'>";
    btnDiv+="    <h2 class='layui-colla-title'>按钮操作后JS</h2>";
    btnDiv+="    <div class='layui-colla-content'>";
    btnDiv+="  <textarea id='btnAfterJs' name='btnAfterJs' placeholder='按钮操作后js...' class='layui-textarea' style='width:100%'>"+(buttonSet.btnAfterJs==null?"":buttonSet.btnAfterJs)+"</textarea>";
    btnDiv+="</div></div>";
    btnDiv+="</div>";
    btnDiv+="	<button  type='button' id='saveFieldSet' class=\"layui-btn layui-hide\" lay-submit lay-filter=\"saveFieldSet\">立即提交</button>";
   	btnDiv+="</div>";
    return btnDiv;
}

//删除按钮
function delBtn (obj) {
    if (obj==null || obj==undefined) {//清空按钮
    	layer.confirm('确定清空<font color="red">所有按钮</font>吗？', function(index){
			layer.close(index);
        	$("#buttonSetTable").find("td").children().remove();
    	});
    } else  {//删除某个字段
        var parentTd = $(obj).closest('td');
    	layer.confirm('确定要删除<font color="red">'+parentTd.find("#btnName").text()+'</font>按钮吗？', function(index){
			layer.close(index);
			parentTd.children().remove();
    	});
    }
}

//删除字段
function delField (obj) {
    if (obj==null || obj==undefined) {//清空字段
    	layer.confirm('确定清空<font color="red">所有字段</font>吗？', function(index){
			layer.close(index);
        	$("#showFieldTable").find("td").children().remove();
    	});
    } else  {//删除某个字段
        var parentTd = $(obj).closest('td');
    	layer.confirm('确定要删除<font color="red">'+parentTd.find("#fname").text()+'</font>吗？', function(index){
			layer.close(index);
			parentTd.children().remove();
    	});
    }
}

//拖拽事件处理
function initFieldDropOrDrage() {
    $(".drage").omDraggable({
	   helper:"clone"
	});;
    $(".canDrop").omDroppable({
           accept: ".drage",
		onDrop: function(source,event){
    	var dqTd = $(this);
		var sourceClone = $(source).clone(false);
		if ($(source).hasClass('canDrop')) {//已设置的字段调整
			$(source).children().remove();
			$(source).append($(this).children());
		} else {//选择层字段拖放
			var dqFieldId = sourceClone.find("#fieldId").val();//当前fieldID
	        //判断是否已经存在此字段
	        if ($("#showFieldTable").find("input[name='fieldId'][value='"+dqFieldId+"']").length>0) {
	        	layer.msg("已经存在此字段了！",{icon: 5,offset: 't'});
				return;
	        }
			sourceClone.find("#fname").after("<a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i></a>"); 
		}
		if ($(source).hasClass('canDrop') || dqTd.children().length==0) {
			dqTd.children().remove();
			dqTd.append(sourceClone.children());
		} else {
			layer.confirm("当前区域有数据，确定要覆盖吗？", function(index){
				layer.close(index);
				dqTd.children().remove();
				dqTd.append(sourceClone.children());
			});
		}
    }
    });
}

//按钮拖拽事件处理
function initBtnDropOrDrage() {
    $(".drage").omDraggable({
	   helper:"clone"
	});;
    $(".canDrop").omDroppable({
           accept: ".drage",
		onDrop: function(source,event){
		var sourceClone = $(source).clone(false);
		var dqTd = $(this);
		if ($(source).hasClass('canDrop')) {//已设置的字段调整
			$(source).children().remove();
			$(source).append(dqTd.children());
		} else {//选择层字段拖放
		    var buttonType = sourceClone.attr("buttonType");//按钮类型
		    sourceClone.append(btnSetHtml({"buttonId":0,"buttonType":buttonType,"buttonName":sourceClone.text()}));
		}
		if ($(source).hasClass('canDrop') || dqTd.children().length==0) {
			dqTd.children().remove();
			dqTd.append(sourceClone.children());
		} else {
			layer.confirm("当前区域有数据，确定要覆盖吗？", function(index){
				layer.close(index);
				dqTd.children().remove();
				dqTd.append(sourceClone.children());
			});
		}
    }
    });
}

/***
 * 初始化鼠标右键事件
 * tabObj 表格对象
 */
function intiRightMouseEvent(tabObj,flag) {
	//绑定显示表格右键事件
	tabObj.wqyTrTdEvent({
		onAddRow :function(tr){
			var allTds = tr.find("td");
			allTds.dblclick(function () {
				if (flag==1) {//表格内容和搜索条件
					fieldDbClick(this);
				} else if (flag==2) {//按钮
					buttonClick (this);
				}
			});
			allTds.attr("class","drage canDrop");
			//初始化拖拽事件
			if (flag==1) {//表格内容和搜索条件
				initFieldDropOrDrage();
			} else if (flag==2) {//按钮
				initBtnDropOrDrage();
			}
		},
		onAddCol :function(td){
			//清空元素
			td.attr("class","drage canDrop");
			td.dblclick(function () {
				if (flag==1) {//表格内容和搜索条件
					fieldDbClick(this);
				} else if (flag==2) {//按钮
					buttonClick (this);
				}
			});
			//初始化拖拽事件
			if (flag==1) {//表格内容和搜索条件
				initFieldDropOrDrage();
			} else if (flag==2) {//按钮
				initBtnDropOrDrage();
			}
		}
	});
}

//批量改变删除设置
function batchChangeDelSet(obj) {
	$(obj).closest('table').find("option[value='"+$(obj).val()+"']").attr("selected",true);
	layui.form.render('select');
}