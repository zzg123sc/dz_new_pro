/**
 * 初始化选择字段
 * @param fieldList
 * @return
 */
function initFieldSet ($from,fieldList,flag) {
    var tdIndex = 0;
    var trIndex = 0;
    var fieldTable = $from.find("#showFieldTable").find('tbody');//显示字段
    var relationTableId=0;
    if (flag==2) {
    	fieldTable = $from.find("#hideFieldTable").find('tbody');//隐藏字段
    } else if (flag==3){
    	fieldTable = $from.find("#chooseTable").find('tbody');//选择字段
    	if ($from.attr("name")=='pageSetForm') {//判断是否是子页面的设置
    		relationTableId = $from.find("#relationTableId").val();
    	}
    }
    for (var i=0;i<fieldList.length;i++) {
        var field = fieldList[i];
        var dqTr = fieldTable.children("tr").eq(trIndex);
        if (dqTr.length==0) {
        	dqTr = $("<tr><td></td><td></td><td></td></tr>");
        	dqTr.find('td').addClass(flag==3?'drage':'drage canDrop');
    		fieldTable.append(dqTr);
        }
        var dqTd = dqTr.children("td").eq(tdIndex);
        var fieldDiv = fieldSetHtml(field,flag,relationTableId);
        dqTd.append(fieldDiv);
        if (flag==3) {
        	dqTd.data("fieldSet",field);
        	dqTd.attr("fieldId",field.fieldId);
        }
       	tdIndex++;
    	if (i>0 && i%3==2) {
    		var trHtml = $("<tr><td></td><td></td><td></td></tr>");
    		trHtml.find('td').addClass(flag==3?'drage':'drage canDrop');
    		fieldTable.append(trHtml);
    		tdIndex = 0;
    		trIndex ++;
        }
    }
    if (flag!=3) {
    	fieldTable.find('td').dblclick(function () {
    		addFieldClick(this);
    	});
    }
}

/**
 * 字段设置内容
 * @param field
 * @return
 */
function fieldSetHtml (field,flag,relationTableId) {
	var fieldDiv ="";
	relationTableId = (relationTableId==null || relationTableId==undefined)?0:relationTableId;
	if (flag!=4) {//是否是空白字段设置
		//内部对象或多值字段红色标识
		var color = "";
		if (field.fieldType>=11 && field.fieldType<=14 ) {//文档和数据字典
			color = "color='#01AAED'";
		} else if (field.fieldType==15  || field.fieldType==16 || field.fieldType==21) {
			color = "color='#FF5722'";
		} else if (relationTableId>0 && field.tableId!=relationTableId) {//判断是否是外键表字段
			color = "color='#009688'";
		}
		fieldDiv ="<span id='fName' name='fName'><font "+color+">"+field.fieldCnName+"</font></span>";
	}
	if (flag!=3) {//不是字段选择
		if (flag!=4) {//是否是空白字段设置
			fieldDiv+= "<a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i>  </a>";
			fieldDiv+= "	<div id='fieldSetDiv' class='layui-hide'>";
		}
	    fieldDiv+="<div class=\"layui-form-item\">";
	    fieldDiv+="<div class=\"layui-inline\">";
	    fieldDiv+="<label class=\"layui-form-label\">字段名称</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="	 <input type='hidden' id='fieldId' name='fieldId' value='"+field.fieldId+"'>";
	    fieldDiv+="	 <input type='hidden' id='tableId' name='tableId' value='"+field.tableId+"'>";
	    fieldDiv+="	 <input type='hidden' id='fieldName' name='fieldName' value='"+(field.fieldName==null?"":field.fieldName)+"'>";
	    fieldDiv+="	 <input type='hidden' id='fieldType' name='fieldType' value='"+field.fieldType+"'>";
	    fieldDiv+="  <input type=\"text\" id=\"fieldCnName\" name=\"fieldCnName\" value='"+(field.fieldCnName==null?"":field.fieldCnName)+"' placeholder=\"字段中文名称\" lay-verify=\"required\" class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    fieldDiv+="<div class=\"layui-inline\">";
	    fieldDiv+="<label class=\"layui-form-label\">字段别名</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <input type=\"text\" id=\"zdyName\" name=\"zdyName\" value='"+(field.zdyName==null?"":field.zdyName)+"' "+(field.fieldType==0?"lay-verify='required'":"")+"placeholder=\"字段别名\" class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">是否必填</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifMust' name='ifMust'>";
	    fieldDiv+="		<option value='0' "+(field.ifMust==0?"selected='true'":"")+">否</option>";
	    fieldDiv+="		<option value='1' "+(field.ifMust==1?"selected='true'":"")+">是</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">是否可修改</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifUpdate' name='ifUpdate'>";
	    fieldDiv+="		<option value='0' "+(field.ifUpdate==0?"selected='true'":"")+">是</option>";
	    fieldDiv+="		<option value='1' "+(field.ifUpdate==1?"selected='true'":"")+">否</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">是否显示为默认值</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifShowDefVal' name='ifShowDefVal'>";
	    fieldDiv+="		<option value='0' "+(field.ifShowDefVal==0?"selected='true'":"")+">是</option>";
	    fieldDiv+="		<option value='1' "+(field.ifShowDefVal==1?"selected='true'":"")+">否</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">为空显示为默认值</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifNullShowDefVal' name='ifNullShowDefVal'>";
	    fieldDiv+="		<option value='0' "+(field.ifNullShowDefVal==0?"selected='true'":"")+">是</option>";
	    fieldDiv+="		<option value='1' "+(field.ifNullShowDefVal==1?"selected='true'":"")+">否</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";

	    if (field.fieldType==11 || field.fieldType==12) {//数据字典
	    	fieldDiv+="<div class=\"layui-inline dicSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline dicSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">默认显示值</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select class='xm-select' id='defaultShow' name='defaultShow' xm-select='select_"+field.fieldId+"_defaultShow' xm-select-searchAttr='page_getDicChildren?dicParent="+field.dicParent+"' xm-select-show-count='2'>";
	    var defaultShowStr = field.defaultShow;
	    if (defaultShowStr!=null && defaultShowStr.length>0) {
	    	var defaultShowArr =defaultShowStr.split(',');
	    	for (var i=0;i<defaultShowArr.length;i++) {
	    		fieldDiv+="		<option value='"+defaultShowArr[i]+"' selected='true'>默认显示值</option>";
	    	}
	    } else {
	    	fieldDiv+="		<option value=''>--无数据--</option>";
	    }
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==11 || field.fieldType==12) {//数据字典
	    	fieldDiv+="<div class=\"layui-inline dicSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline dicSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">默认选中值</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select class='xm-select' id='defaultSelect' name='defaultSelect' xm-select='select_"+field.fieldId+"'_defaultSelect "+(field.fieldType==11?'xm-select-radio':'')+" xm-select-searchAttr='page_getDicChildren?dicParent="+field.dicParent+"' xm-select-show-count='2'>";
	    var defaultSelectStr = field.defaultSelect;
	    if (defaultSelectStr!=null && defaultSelectStr.length>0) {
	    	var defaultSelectArr =defaultSelectStr.split(',');
	    	for (var i=0;i<defaultSelectArr.length;i++) {
	    		fieldDiv+="		<option value='"+defaultSelectArr[i]+"' selected='true'>默认选中值</option>";
	    	}
	    } else {
	    	fieldDiv+="		<option value=''>--无数据--</option>";
	    }
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    
	    if (field.fieldType==11 || field.fieldType==12) {
	    	fieldDiv+="<div class=\"layui-inline inputSet layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline inputSet\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">默认值</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <input type=\"text\" id=\"defaultVal\" name=\"defaultVal\" value='"+(field.defaultVal==null?"":field.defaultVal)+"' placeholder=\"默认值\" class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==-1 || field.fieldType==1) {//日期/数据字典
	    	fieldDiv+="<div class=\"layui-inline inputSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline inputSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">展现形式</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='inputStyle' name='inputStyle'>";
	    fieldDiv+="		<option value='0' "+(field.inputStyle==0?"selected='true'":"")+">文本</option>";
	    fieldDiv+="		<option value='1' "+(field.inputStyle==1?"selected='true'":"")+">文本域</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==10) {//日期
	    	fieldDiv+="<div class=\"layui-inline dateSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline dateSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">日期格式</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='dateStyle' name='dateStyle'>";
	    fieldDiv+="		<option value='yyyy-MM-dd' "+(field.dateStyle=='yyyy-MM-dd'?"selected='true'":"")+">yyyy-MM-dd</option>";
	    fieldDiv+="		<option value='yyyy-MM-dd HH:mm:ss' "+(field.dateStyle=='yyyy-MM-dd HH:mm:ss'?"selected='true'":"")+">yyyy-MM-dd HH:mm:ss</option>";
        fieldDiv+="		<option value='yyyy-MM' "+(field.dateStyle=='yyyy-MM'?"selected='true'":"")+">yyyy-MM</option>";
        fieldDiv+="		<option value='yyyy' "+(field.dateStyle=='yyyy'?"selected='true'":"")+">yyyy</option>";
        fieldDiv+="		<option value='HH:mm' "+(field.dateStyle=='HH:mm'?"selected='true'":"")+">HH:mm</option>";
        fieldDiv+="		<option value='HH:mm:ss' "+(field.dateStyle=='HH:mm:ss'?"selected='true'":"")+">HH:mm:ss</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    
	    //内部对象/多值/父级
	    if (field.fieldType==15 || field.fieldType==16 || field.fieldType==21) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">关联对象</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <input type=\"hidden\" id=\"childPageId\" name=\"childPageId\" value='"+(field.childPageId==null?"":field.childPageId)+"' class=\"layui-input\">";
	    fieldDiv+="  <input type=\"hidden\" id=\"relationTableId\" name=\"relationTableId\" value='"+field.relationTableId+"' class=\"layui-input\">";
	    fieldDiv+="  <input type=\"text\" readonly='readonly' id=\"relationTableCnName\" name=\"relationTableCnName\" value='"+field.relationTableCnName+"' class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==15 || field.fieldType==16 || field.fieldType==21) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">添加形式</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='addStyle' name='addStyle' onchange='changeAddStyle(this)'>";
	    fieldDiv+="		<option value='0' "+(field.addStyle==0?"selected='true'":"")+">选择添加</option>";
	    if (field.fieldType==16) {
	    	fieldDiv+="		<option value='1' "+(field.addStyle==1?"selected='true'":"")+">点击+添加</option>";
	    	fieldDiv+="		<option value='2' "+(field.addStyle==2?"selected='true'":"")+">默认全部添加</option>";
	    }
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==16) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">选择个数限制</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <input type=\"text\" id=\"dataMaxNum\" name=\"dataMaxNum\" value='"+(field.dataMaxNum==null?'':field.dataMaxNum)+"' class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==15 || field.fieldType==16 || field.fieldType==21) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">是否可新增</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifCanAdd' name='ifCanAdd'>";
	    fieldDiv+="		<option value='0' "+(field.ifCanAdd==0?"selected='true'":"")+">否</option>";
	    fieldDiv+="		<option value='1' "+(field.ifCanAdd==1?"selected='true'":"")+">是</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==16) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">是否只新增</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='ifOnlyAdd' name='ifOnlyAdd'>";
	    fieldDiv+="		<option value='0' "+(field.ifOnlyAdd==0?"selected='true'":"")+">否</option>";
	    fieldDiv+="		<option value='1' "+(field.ifOnlyAdd==1?"selected='true'":"")+">是</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==16) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\" >外键字段</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='dialogField' name='dialogField' onchange='changeWjField(this)'>";
	    fieldDiv+="		<option value=''>--无--</option>";
		if (field.dialogField!=null) {
			fieldDiv+="		<option value='"+field.dialogField+"' selected='true'>外键字段</option>";
		}
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==15 || field.fieldType==16 || field.fieldType==21) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">选择形式</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select id='chooseType' name='chooseType' onchange='changeChooseType(this)'>";
	    fieldDiv+="		<option value='0' "+(field.chooseType==0?"selected='true'":"")+">弹层</option>";
	    fieldDiv+="		<option value='1' "+(field.chooseType==1?"selected='true'":"")+">下拉框</option>";
	    fieldDiv+="		<option value='2' "+(field.chooseType==2?"selected='true'":"")+">下拉树</option>";
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType==15 || field.fieldType==16 || field.fieldType==21) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">弹出层页面</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <select  id='dialogPage' name='dialogPage' onchange='changeDialogPage(this)' "+((field.fieldType==15 || field.fieldType==16 || field.fieldType==21)?"lay-verify='required'":"")+"  lay-search>";
	    fieldDiv+="		<option value=''>--新增--</option>";
		if (field.dialogPage!=null) {
			fieldDiv+="		<option value='"+field.dialogPage+"' selected='true'>弹层</option>";
		}
	    fieldDiv+="</select>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    
	    if ((field.fieldType==15 || field.fieldType==16 || field.fieldType==21) && field.dialogPage==null) {
	    	fieldDiv+="<div class=\"layui-inline relationSet\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline relationSet layui-hide\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">页面名称</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <input type=\"text\" id=\"dialogPageName\" name=\"dialogPageName\" value='"+field.relationTableCnName+"选择弹层' class=\"layui-input\">";
	    fieldDiv+="</div>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="  <button class='layui-btn' type='button' onclick='createDialogPage(this)'>生成弹层</button>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">自定义验证属性</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="<textarea id='zdyVerify' name='zdyVerify' placeholder=\"自定义字段验证规则名称，多个以'|'分开\" class='layui-textarea'>"+(field.zdyVerify==null?"":field.zdyVerify)+"</textarea>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    if (field.fieldType<0) {//值插件
	    	fieldDiv+="<div class=\"layui-inline layui-hide\">";
	    } else {
	    	fieldDiv+="<div class=\"layui-inline\">";
	    }
	    fieldDiv+="<label class=\"layui-form-label\">字段描述</label>";
	    fieldDiv+="<div class=\"layui-input-inline\">";
	    fieldDiv+="<textarea id='fieldDescript' name='fieldDescript' placeholder=\"对此字段作一个描述\" class='layui-textarea'>"+(field.fieldDescript==null?"":field.fieldDescript)+"</textarea>";
	    fieldDiv+="</div>";
	    fieldDiv+="</div>";
	    
	    fieldDiv+="</div>";
	    if (field.fieldType!=15 && field.fieldType!=16 && field.fieldType!=21) {
	    	fieldDiv+="<div class='layui-hide'>";
	    }
	    	fieldDiv+="<div class='layui-collapse'>";
	    	fieldDiv+="	<div class='layui-colla-item'>";
    		fieldDiv+="<h2 class='layui-colla-title'>"+field.fieldCnName+"_子查询sql(当前)</h2>";
			fieldDiv+="<div class='layui-colla-content'>";
			fieldDiv+="<textarea id='childSelectSql' readonly='readonly' name='childSelectSql' placeholder=\"字段保存会自动生成\" class='layui-textarea'></textarea>";
		    fieldDiv+="</div>";
	    	fieldDiv+="</div>";
    		fieldDiv+="</div>";
	    	fieldDiv+="<div class='layui-collapse'>";
	    	fieldDiv+="	<div class='layui-colla-item'>";
    		fieldDiv+="<h2 class='layui-colla-title'>"+field.fieldCnName+"_子查询sql(自定义)</h2>";
			fieldDiv+="<div class='layui-colla-content'>";
			fieldDiv+="<textarea id='childZdySelectSql' name='childZdySelectSql' placeholder=\"自定义子查询sql\" class='layui-textarea'></textarea>";
		    fieldDiv+="</div>";
	    	fieldDiv+="</div>";
    		fieldDiv+="</div>";
    		fieldDiv+="<form class='layui-form' id='innerFormSet'>";
		    fieldDiv+="<div class='layui-collapse'>";
	    	fieldDiv+="	<div class='layui-colla-item'>";
    		fieldDiv+="<h2 class='layui-colla-title'>"+field.fieldCnName+"_子表格</h2>";
			fieldDiv+="<div class='layui-colla-content fieldSetColl'>";
			fieldDiv+="<div class='layui-fluid childSet'>";
			fieldDiv+="</div>";
		    fieldDiv+="</div>";
	    	fieldDiv+="</div>";
    		fieldDiv+="</div>";
    		fieldDiv+="</form>";
		if (field.fieldType!=15 && field.fieldType!=16 && field.fieldType!=21) {
	    	fieldDiv+="</div>";
	    }
	    if (flag!=4) {//是否是空白字段设置
	    	fieldDiv+="</div>";
	    }
	}
    return fieldDiv;
}

/***
 * 初始化鼠标右键事件
 * tabObj 表格对象
 */
function intiRightMouseEvent(tabObj) {
	//绑定显示表格右键事件
	tabObj.wqyTrTdEvent({
		onAddRow :function(tr){
			var allTds = tr.find("td");
			allTds.dblclick(function () {
				addFieldClick(this);
			});
			allTds.attr("class","drage canDrop");
			//初始化拖拽事件
			initFieldDropOrDrage();
		},
		onAddCol :function(td){
			//清空元素
			td.attr("class","drage canDrop");
			td.dblclick(function () {
	    		addFieldClick(this);
			});
			//初始化拖拽事件
			initFieldDropOrDrage();
		}
	});
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
    	var form = $(source).closest('form');
		var sourceClone = $(source).clone(false);
		if ($(source).hasClass('canDrop')) {//已设置的字段调整
			$(source).children().remove();
			$(source).append($(this).children());
		} else {//选择层字段拖放
			var dqFieldId = sourceClone.attr("fieldId");//当前fieldID
	        //判断是否已经存在此字段
	        if (form.find("input[name='fieldId'][value='"+dqFieldId+"']").length>0) {
	        	layer.msg("已经存在此字段了！",{icon: 5,offset: 't'});
				return;
	        }
	        var fieldData = $(source).data("fieldSet");//当前字段数据
	        var fieldDiv = fieldSetHtml(fieldData,0);//生成html
	        sourceClone.html(fieldDiv);
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
  //删除字段
  function delField (obj) {
	    if ($(obj).closest('fieldset').length>0) {//清空字段
	    	layer.confirm('确定清空<font color="red">所有字段</font>吗？', function(index){
				layer.close(index);
	        	$(obj).closest('fieldset').next('table').find("td").children().remove();
	        	alertMessage('已删除所有！');
	    	});
	    } else  {//删除某个字段
	    	var parentTd = $(obj).closest('td');
	    	layer.confirm('确定要删除<font color="red">'+parentTd.find("#fName").text()+'</font>吗？', function(index){
				layer.close(index);
				parentTd.children().remove();
	    	});
	    }

}
  
  //按钮点击事件
  function addFieldClick (obj) {
  	var dqTd = $(obj);
  	var contentHtml = "";
  	var titleName = "";
  	var fieldId = $(obj).find("#fieldId").val();
  	if (dqTd.children().length==0) {//判断是否是空白
  		titleName = "自定义字段";
  		contentHtml = fieldSetHtml({fieldId:0,fieldType:-1},4);
  		fieldId = 0;
  	} else {
	    titleName = dqTd.find("#fName").text();
	    contentHtml = dqTd.find("#fieldSetDiv").children().clone(false);
  	}
  	var parentForm = $(obj).closest('form');
  	var $dialogDiv = $("#contentForm").next('.dialogDiv');
  	if ($dialogDiv==null || $dialogDiv.length==0 || $dialogDiv.children().length>0) {
  		$dialogDiv = $("<div class='dialogDiv'></div>");//弹层div
  		$("#contentForm").after($dialogDiv);
  	}
  	parentForm.find(".fieldSetColl").removeClass('layui-show');//隐藏父级弹层的字段设置，防止拖拽列重合问题
  	var $form = $("<form class=\"layui-form\" id='pageSetForm' name='pageSetForm'></form>");
  	$form.append(contentHtml);
  	$form.append("	<button  type='button' id='saveFieldSet' class=\"layui-btn layui-hide\" lay-submit lay-filter=\"saveFieldSet_"+fieldId+"\">立即提交</button>");
  	$dialogDiv.append($form);
  	//设置子页面表格的最大高度
  	$form.find('.layui-col-md7').attr('style','max-height: 200px; overflow: auto;');
  	$form.find('.layui-col-md5').attr('style','max-height: 200px; overflow: auto;');
    var fieldType = $form.find("#fieldType").val();
    //处理复选下拉框
    $form.find(".xm-select").each(function (i,one) {
    	$(one).attr("xm-select-search",$(one).attr("xm-select-searchAttr"));
    });
    //初始化子表格相关(加载过不再重复加载)
    if (fieldType==15 || fieldType==16 || fieldType==21) {
    	var basicSet = $("#contentForm").data('basicSet');
		$form.find(".childSet").html(basicSet.html());
    	//切换隐藏显示并异步加载子页面表格设置
		$form.find("#addStyle").trigger('change');
    }
    var layerIndex = 0;
  	layer.open({
  		  title:'字段-'+titleName,
  		  type:1,
  		  anim: 5
  		  ,area:['690px','400px']
  		  ,content: $dialogDiv
  		  ,btn: ['保存', '取消']
  		  ,btnAlign: 'c'
  		  ,offset: 'auto'
  		  ,yes: function(index, layero){
  			  //调用保存
  			  layerIndex = index;
              var submit = layero.find('#saveFieldSet');
              console.log(submit.length);
  			  submit.trigger('click');
  		  },btn2: function(index, layero){
  			  $dialogDiv.children().remove(); 
  			  $dialogDiv.closest('.layui-layer').prev('.dialogDiv').remove();
  			  parentForm.find(".fieldSetColl").addClass('layui-show'); 
  		  },cancel: function(){ 
  			  $dialogDiv.children().remove();
  			  $dialogDiv.closest('.layui-layer').prev('.dialogDiv').remove();
  			  parentForm.find(".fieldSetColl").addClass('layui-show'); 
  		  }
  		});
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
  	    layui.form.on("submit(saveFieldSet_"+fieldId+")",function (data) {
  	    	if (fieldType==15 || fieldType==16 || fieldType==21) {
  	    		//显示列的个数
	  	    	var showNum = $form.find("#showFieldTable").find("input[name='fieldId']").length;
  	    		if (showNum==0) {
  	    			layer.msg('请至少选择一个显示字段！',{icon: 5,offset: 't'});
  	    			return false;
  	    		}
  	    		var tableId = $form.find("#relationTableId").val();//关联对象
  	    		//同步保存子页面设置
  	    		var innerForm = $form.find("#innerFormSet");
	  	    	var childPageId = $form.find("#childPageId").val();
	  	    	var pageName = $form.find("#relationTableCnName").val()+"回显子页面";
	  	    	$.ajax({
	  	    	      url:'page_saveAddPageSet',
	  	    	      type:'post',
	  	    	      data:'isChild=1&showNum='+showNum+'&mainTableId='+tableId+'&pageId='+childPageId+'&pageName='+pageName+'&buttonType='+$("#buttonType").val()+'&'+innerForm.serialize(),
	  	    	      success:function(result){
	  	    		  	childPageId=result;
	  	    		  	if (childPageId>0) {
		  	    		  	//同步保存字段
	  	    		  		$form.find("#childPageId").val(childPageId);
		  	  	    		var basicSet = $("#contentForm").data('basicSet');
		  	  	    		$form.find(".childSet").children().remove();
		  	  	    		saveChildAfter ($form,dqTd,$dialogDiv,parentForm,layerIndex)
		  	    		    layer.msg("保存成功！",{icon: 6,offset: 't'});
	  	    		  	} else {
	  	    		  		layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	  	    		  	}
	  	    		  	return false;
	  	    	      },error:function (result) {
	  	    	    	  layer.msg("保存失败！",{icon: 5,offset: 'rt'});
	  	    		  }
	  	    	 });
  	    	} else {
  	    		saveChildAfter ($form,dqTd,$dialogDiv,parentForm,layerIndex);
  	    	}
  	    	return false;
  		});
  }
  
  /**
   * 保存之后回调
   * @param $form
   * @param dqTd
   * @param $dialogDiv
   * @param parentForm
   * @return
   */
  function saveChildAfter ($form,dqTd,$dialogDiv,parentForm,layerIndex) {
	  var selectOptions = $form.find("option:selected");//选中的下拉框
    	$form.find("option").removeAttr("selected");
    	selectOptions.attr("selected",true);
    	$form.find(".xm-select").removeAttr("xm-select-search");//避免多次查询加载
    	if (dqTd.children().length>0) {
    		dqTd.find("#fName").text($form.find("#fieldCnName").val());
    		dqTd.find("#fieldSetDiv").children().remove();
    	} else {
    		dqTd.append("<span id='fName' name='fName'>"+$form.find("#fieldCnName").val()+"</span>");
    		dqTd.append("<a href='#' onclick='delField(this)'><i class=\"layui-icon layui-icon-close-fill\" style=\"font-size: 20px; color: #FF0000;\"></i>  </a>");
    		dqTd.append("	<div id='fieldSetDiv' class='layui-hide'></div>");
    	}
    	$form.find("#saveFieldSet").remove();
    	dqTd.find("#fieldSetDiv").append($form.children().clone(false));
		layer.close(layerIndex);
		$dialogDiv.children().remove();
		$dialogDiv.closest('.layui-layer').prev('.dialogDiv').remove();
		parentForm.find(".fieldSetColl").addClass('layui-show'); 
  }

  /**
   * 异步加载子表格设置
   * @param childForm
   * @return
   */
  function ajaxChildTableSet (childForm) {
	  var fieldType = childForm.find("#fieldType").val();//当前字段类型
	  var tableId = childForm.find("#relationTableId").val();//当前表id
	  var childPageId = childForm.find("#childPageId").val();//子页面id
	  var mainTableId = childForm.find("#tableId").val();//主表id
	  var wjTableId = childForm.find("#dialogField").val();//外键对象id
	  wjTableId = (wjTableId===null || wjTableId==undefined)?0:wjTableId.split('_')[1];
	  var addStyle = childForm.find("#addStyle").val();//当前表id
	  var ifInit = childForm.data("ifInit");//是否初始化过
	  ifInit=(ifInit==null || ifInit==undefined)?0:ifInit;
	  if (fieldType!=16) {
		  wjTableId =  0;
	  }
	  $.ajax({
	      url:'page_getChildPageSet',
	      type:'post',
	      data:{"tableId":tableId,"mainTableId":mainTableId,
		    "wjTableId":wjTableId,"childPageId":childPageId
		    ,"ifInit":ifInit,"addStyle":addStyle},
	      success:function(result){
		    if (ifInit==0) {
		    	//初始化外键字段
		    	if (fieldType==16) {
		    		initWjField (childForm,result.wjFieldList);
		    	}
		    	//初始化显示字段
		      	initFieldSet(childForm,result.showFieldList,1);
		      	//初始化隐藏字段
		      	initFieldSet(childForm,result.hideFieldList,2);
		    }
	      	//初始化选择字段
	      	initFieldSet(childForm,result.fieldList,3);

	      	//初始化表格右键事件
	      	intiRightMouseEvent(childForm.find("#showFieldTable"));
	      	intiRightMouseEvent(childForm.find("#hideFieldTable"));
	      	//初始化拖拽事件
	      	initFieldDropOrDrage();
	      	childForm.data("ifInit",1);
	      },error:function (result) {
	    	  layer.msg("加载失败！",{icon: 5,offset: 'rt'});
		  }
	    });
  }
  
  /**
   * 初始化外键字段
   * @param childForm
   * @param wjFieldList
   * @return
   */
  function initWjField (childForm,wjFieldList) {
	  var wjSelect = childForm.find("#dialogField");//外键字段
	  if (wjFieldList!=null && wjFieldList.length>0) {
		  var wjOptions = "";
		  var dialogField = wjSelect.val();
		  for (var i=0;i<wjFieldList.length;i++) {
			  var wjField = wjFieldList[i];
			  var optionVal = wjField.fieldId+"_"+wjField.relationTableId;
			  var selectOp = "";//判断选中
			  if (optionVal==dialogField) {
				  selectOp = " selected='true' ";
			  }
			  wjOptions +="<option value='"+optionVal+"' "+selectOp+">"+wjField.fieldCnName+"("+wjField.relationTableCnName+")"+"</option>";
		  }
		  //重新赋值选择项
		  wjSelect.html(wjOptions);
		  layui.form.render('select');
		  //初始化弹层
		  initDialogPage(wjSelect);
	  }
  }
  
  /**
   * 改变添加形式
   * @param obj
   * @return
   */
  function changeAddStyle (obj) {
	  var addStyle = $(obj).val();
	  var fieldType = $(obj).closest('form').find('#fieldType').val();
	  var form = $(obj).closest('form');
	  var ifDfHide = form.find("#dialogField").eq(0).closest('.layui-inline').hasClass('layui-hide');//判断是否需要改变
	  var ifNeedLoad = false;
	  var dialogPage = form.find("#dialogPage").val();
	  if (addStyle==1) {//添加+添加
		  if (!ifDfHide){
			  ifNeedLoad = true;
		  }
		  form.find("#dialogField").removeAttr('lay-verify');
		  form.find("#dialogPage").removeAttr('lay-verify');
		  form.find("#dialogField").closest('.layui-inline').addClass('layui-hide');
		  form.find("#chooseType").closest('.layui-inline').addClass('layui-hide');
		  form.find("#dialogPage").closest('.layui-inline').addClass('layui-hide');
		  form.find("#dialogPageName").closest('.layui-inline').addClass('layui-hide');
	  } else if (ifDfHide && fieldType==16){
		  ifNeedLoad = true;
		  form.find("#dialogField").attr('lay-verify','required');
		  form.find("#dialogPage").attr('lay-verify','required');
		  form.find("#dialogField").closest('.layui-inline').removeClass('layui-hide');
		  form.find("#chooseType").closest('.layui-inline').removeClass('layui-hide');
		  form.find("#dialogPage").closest('.layui-inline').removeClass('layui-hide');
		  if (dialogPage==null || dialogPage=="") {
			  form.find("#dialogPageName").closest('.layui-inline').removeClass('layui-hide');
		  }
	  }
	  //加载字段设置
	  var ifInit = form.data("ifInit");//是否初始化过
	  ifInit=(ifInit==null || ifInit==undefined)?0:ifInit;
	  if (ifNeedLoad || ifInit==0) {
		  form.find('.drage').children().remove();
		  var coloneTr = form.find('#chooseTable').find('tr:first').clone();
		  form.find('#chooseTable').find('tr').remove();
		  form.find('#chooseTable').find('tbody').append(coloneTr);
		  ajaxChildTableSet(form);
	  }
  }
  
  /**
   * 改变外键字段
   * @param obj
   * @return
   */
  function changeWjField (obj) {
	  var form = $(obj).closest('form');
	  ajaxChildTableSet(form);
	  //加载关联页面
	  initDialogPage(obj);
  }
  
  /**
   * 改变页面类型
   * @param obj
   * @return
   */
  function changeChooseType (obj) {
	  //加载关联页面
	  initDialogPage(obj);
  }
  
  /**
   * 加载弹层页面
   * @param obj
   * @return
   */
  function initDialogPage (obj) {
	  var form = $(obj).closest('form');
	  var fieldType = form.find("#fieldType").val();//字段类型
	  var tableId = form.find("#relationTableId").val();//关联对象
	  var dialogPageSel = form.find("#dialogPage");//弹层页面对象
	  if (fieldType==16) {//内部对象多值
		  tableId = form.find("#dialogField").val();
		  tableId = (tableId == null || tableId==undefined)?0:tableId.split('_')[1];
	  }
	  if (tableId>0) {
		  $.ajax({
		      url:'page_getAllPage',
		      type:'post',
		      data:{"tableId":tableId,"pageType":5},
		      success:function(result){
		    	var data = result.data;
		    	var dialogPageId= dialogPageSel.val();
			    if (data!=null && data.length>0) {
			    	dialogPageSel.find('option:first').nextAll().remove();
			    	for (var i=0;i<data.length;i++) {
			    		var nowData = data[i];
			    		var selectOp = "";//判断选中
			    		var optionVal = nowData.value;
			    		if (optionVal==dialogPageId) {
						  selectOp = " selected='true' ";
			    		}
			    		dialogPageSel.append("<option value='"+optionVal+"' "+selectOp+">"+nowData.name+"</option>");
			    	}
			    	layui.form.render('select');
			    }
			  },error:function (result) {
				  layer.msg("加载失败！",{icon: 5,offset: 'rt'});
			  }
		  });
	  } else {
		  dialogPageSel.children().remove();
		  
	  }
  }
  
  /**
   * 生成弹层页面
   * @param obj
   * @return
   */
  function createDialogPage (obj) {
	  var form = $(obj).closest('form');
	  var fieldType = form.find("#fieldType").val();//字段类型
	  var tableId = form.find("#relationTableId").val();//关联对象
	  var pageName = form.find("#dialogPageName").val();//弹层名称
	  var dialogPageSel = form.find("#dialogPage");//弹层页面对象
	  if (fieldType==16) {//内部对象多值
		  tableId = form.find("#dialogField").val();
		  tableId = (tableId == null || tableId==undefined)?0:tableId.split('_')[1];
	  }
	  if (tableId>0) {
		  $.ajax({
		      url:'object_createPage',
		      type:'post',
		      data:{"tableId":tableId,
			  "pageName":pageName
			  ,"pageType":5},
		      success:function(result){
		    	  if (result>0) {//生成成功并且默认选中
		    		  dialogPageSel.children().removeAttr("selected");
		    		  dialogPageSel.append("<option value='"+result+"' selected='true'>"+pageName+"</option>");
		    		  form.find("#dialogPageName").closest('.layui-inline').addClass('layui-hide');
		    		  layui.form.render('select');
		    		  layer.msg("生成成功！",{icon: 6,offset: 'rt'});
		    	  }
		      },error:function (result) {
				  layer.msg("加载失败！",{icon: 5,offset: 'rt'});
			  }
		  });
	  }
  }
  
  /**
   * 改变弹层选择
   * @param obj
   * @return
   */
  function changeDialogPage (obj) {
	  var dialogPageId = $(obj).val();
	  var form = $(obj).closest('form');
	  if (dialogPageId>0) {
		  form.find("#dialogPageName").closest('.layui-inline').addClass('layui-hide');
	  } else {
		  form.find("#dialogPageName").closest('.layui-inline').removeClass('layui-hide');
	  }
  } 
