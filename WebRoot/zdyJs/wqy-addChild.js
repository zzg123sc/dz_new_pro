//块的移动方法
(function($) {
    $.omWidget('om.wqyAddChild', {
        options:{
    		 "ifUpdate":0,
    		 "dataMaxNum":0,
			 "callBack" : function(data,ele){
  			 
    		 },
    		 "jsCallBack" : function(data,ele){
      			 
    		 },
    		 "zdyCallBack" : function(data,ele){
      			 
    		 }
        },
        //private methods
        _create:function(){
            
            
        },
        _init:function(){
        	var $el=this.element,
        		ops = this.options;
			//绑定添加新增一块事件
            this._bindClick();  		
        },
		_bindClick:function(){
			var $el=this.element,
				ops = this.options,
				self = this;
			if (ops.ifUpdate==1) {//不可修改
				return;
			}
			$el.unbind("click").bind("click",function(){
				  var thBody = ops.thBody;
				  var trBody = ops.trBody;
				  var jsBody = ops.jsBody;//js内容
				  var childTableName = $el.attr("name")+"_table";
				  var dqIsChild = $el.closest('tr').length==0;//判断是否是子表格的内容
				  var eleParent = $el.closest(dqIsChild?'.layui-form-item':'tr');//当前元素所在行的父级
				  var childTable = eleParent.next().find("#"+childTableName);
				  if (childTable==null || childTable.length==0) {
					  childTable = $("<table class='layui-table' id='"+childTableName+"' name='"+childTableName+"'></table>");
					  if (!dqIsChild) {
						  var childTd = eleParent.next().find('childTd:first');
						  if (childTd.length==0) {
							  var childTr = $("<tr class='childTr'></tr>");
							  childTd = $("<td class='childTd' colspan='"+(eleParent.children('td').length)+"'></td>");
							  childTr.append(childTd);
							  eleParent.after(childTr);
						  }
						  childTd.append(childTable);
					  } else {
						  eleParent.after(childTable);
						  childTable.wrap('<div class="layui-form-item"></div>');
					  }
					  if (thBody!=null && thBody!=undefined) {//判断是否需要添加子表格
						  thBody = thBody.replace("</tr>","<th>操作</th></tr>");
						  childTable.append("<thead>"+thBody+"</thead>");
				      }
					  var tbody = $("<tbody></tbody>");
					  childTable.append(tbody);
				  } else {
					  tbody = childTable.children("tbody");
					  //个数限制
					  if (ops.dataMaxNum>0 && ops.dataMaxNum==tbody.children('.ptr').length) {
						  alertMessage("最多只能添加"+ops.dataMaxNum+"条！",5);
						  return;
					  }
				  }
				  var needAppendHtm = "<button type='button' class='layui-btn layui-bg-red layui-btn-sm delete'><i class='layui-icon'>&#xe640;</i></button>";
				  needAppendHtm+="<button type='button' class='layui-btn layui-btn-sm moveUp'><i class='layui-icon layui-icon-up'></i></button>";
				  needAppendHtm+="<button type='button' class='layui-btn layui-btn-sm moveDown'><i class='layui-icon layui-icon-down'></i></button>";
				  trBody = trBody.replace("</tr>","<td>"+needAppendHtm+"</td></tr>");
				  tbody.append(trBody);
				  //绑定删除事件
				  tbody.find('.delete:last').bind('click',function () {
					  //先删除关联的子设置信息
					  $(this).closest('tr').next('.childTr').remove();
					  //再删除当前行
					  $(this).closest('tr').remove();
				  });
				  //绑定上移事件
				  tbody.find('.moveUp:last').bind('click',function () {
					  var dqTr = $(this).closest('tr');//克隆当前行
					  var dqChild = dqTr.next('.childTr');//克隆当前行的子表格
					  var dqPreve = dqTr.prev();//前一行
					  var dqAfter = dqChild.length==0?dqTr.next():dqChild.next();//后一行
					  //只有当前一组数据，则不处理
					  if ((dqPreve==null || dqPreve.length==0) && (dqAfter==null || dqAfter.length==0)) {
						  return false;
					  }
					  if (dqPreve==null || dqPreve.length==0) {//当前就是第一行，则移动到最后一行
						  var ptbody = dqTr.closest('tbody');
						  ptbody.append(dqTr.clone(true));
						  dqTr.remove();
						  if (dqChild!=null) {
							  ptbody.append(dqChild.clone(true));
							  dqChild.remove();
						  }
					  } else {
						  if (dqPreve.hasClass('childTr')) {//前一行是子表格情况
							  dqPreve = dqPreve.prev();
						  }
						  dqPreve.before(dqTr.clone(true));
						  dqTr.remove();
						  if (dqChild!=null) {
							  dqPreve.before(dqChild.clone(true));
							  dqChild.remove();
						  }
					  }
					  //初始化样式
					  layui.element.render();
		        	  layui.formSelects.render();
		        	  layui.form.render('select');
				  });
				  //绑下移事件
				  tbody.find('.moveDown:last').bind('click',function () {
					  var dqTr = $(this).closest('tr');//克隆当前行
					  var dqChild = dqTr.next('.childTr');//克隆当前行的子表格
					  var dqPreve = dqTr.prev();//前一行
					  var dqAfter = dqChild.length==0?dqTr.next():dqChild.next();//后一行
					  //只有当前一组数据，则不处理
					  if ((dqPreve==null || dqPreve.length==0) && (dqAfter==null || dqAfter.length==0)) {
						  return false;
					  }
					  if (dqAfter==null || dqAfter.length==0) {//当前就是最后一行，则移动到第一行
						  var ptbody = dqTr.closest('tbody');
						  if (dqChild!=null) {
							  ptbody.children('tr:first').before(dqChild.clone(true));
							  dqChild.remove();
						  }
						  ptbody.children('tr:first').before(dqTr.clone(true));
						  dqTr.remove();
					  } else {
						  if (dqAfter.next().hasClass('childTr')) {
							  dqAfter = dqAfter.next();
						  }
						  dqAfter.after(dqTr.clone(true));
						  dqTr.remove();
						  if (dqChild!=null) {
							  dqAfter.next().after(dqChild.clone(true));
							  dqChild.remove();
						  }
					  }
					  //初始化样式
					  layui.element.render();
		        	  layui.formSelects.render();
		        	  layui.form.render('select');
				  });
				  self._trigger("jsCallBack",null, null, $el);
				  self._trigger("callBack",null, null, $el);
				  self._trigger("zdyCallBack",null, null, $el);
				  //初始化样式
				  layui.element.render();
	        	  layui.formSelects.render();
	        	  layui.form.render('select');
			});
		}
    });
})(jQuery);