//块的移动方法
(function($) {
    $.omWidget('om.wqyDialog', {
        options:{
             "width":'650px',
             "height":'400px',
             "ifChildTable":false,
             "ifUpdate":0,
             "dataMaxNum":0,
             "isMulti":true,
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
			var listTableId = "listTable_"+ops.tableId+"_"+ops.pageId;
			$el.unbind("click").bind("click",function(){
				$el = $(this);
	            var parentForm = $(this).closest("form");//获取当前form对象
	            var $dialogDiv  = $("<div class='dialogDiv'></div>");//弹层div
          		parentForm.after($dialogDiv);
	          	var nowCheckData = [];
	          	$.ajax({
	      	      url:'listCore_toList',
	      	      type:'post',
	      	      data:{"tableId":ops.tableId,"pageId":ops.pageId},
	      	      success:function(result){
	      	    	if (!ops.isMulti) {//单选
	      	    		ops.dataMaxNum==1;
	      	    	}
	      	    	$dialogDiv.append(result);
		          	layer.open({
		        		  title:'选择层',
		          		  type:1,
		          		  anim: 5
		          		  ,area:[ops.width,ops.height]
		          		  ,content: $dialogDiv
		          		  ,btn: ['确定', '取消']
		          		  ,btnAlign: 'c'
		          		  ,offset: 'auto'
		          		  //,shadeClose:true
		          		  ,yes: function(index, layero){
		          			  //获取选中的数据
			          		  var checkStatus = table.checkStatus(listTableId)
			        	      ,data = checkStatus.data;
			          		  self._callBack(data,$el);
			          		  //回调
			          		  self._trigger("jsCallBack",null, data, $el);
							  self._trigger("callBack",null, data, $el);
							  self._trigger("zdyCallBack",null, data, $el);
			          		  layer.close(index);
			          		  $dialogDiv.children().remove(); 
		          			  $dialogDiv.closest('.layui-layer').next('.dialogDiv').remove();
		          		  },btn2: function(index, layero){
		          			  $dialogDiv.children().remove(); 
		          			  $dialogDiv.closest('.layui-layer').next('.dialogDiv').remove();
		          		  },cancel: function(){ 
		          			  $dialogDiv.children().remove();
		          			  $dialogDiv.closest('.layui-layer').next('.dialogDiv').remove();
		          		  },end: function(){ 
		          			  $dialogDiv.children().remove();
		          			  $dialogDiv.closest('.layui-layer').next('.dialogDiv').remove();
		          		  }
		          		});
		          		layui.element.render();
		          		layui.formSelects.render();
		          	    layui.form.render('select');
		          	    //行单击事件
		          	    layui.table.on('row('+listTableId+')', function(obj){
		          	    	
		          	    	
			            });
	      	      },error:function (result) {
	      	    	    layer.msg("弹层打开失败！",{icon: 5,offset: 'rt'});
	      	      }
	          	});		
			});
		},
		_callBack:function (data,$el) {
			var ops = this.options,
			self = this;
			var thBody = ops.thBody;
			var trBody = ops.trBody;
			var jsBody = ops.jsBody;//js内容
			  var checkId = '';
			  var checkName = '';
			  var tbody = null;
			  var childTableName = $el.attr("name");
			  var commonName = childTableName.replace("_choose","");
			  commonName = commonName.substring(0,commonName.lastIndexOf('_'));
			  childTableName = (childTableName.replace("_choose",""))+"_table";
			  var dqIsChild = $el.closest('tr').length==0;//判断是否是子表格的内容
			  var eleParent = $el.closest(dqIsChild?'.layui-form-item':'tr');//当前元素所在行的父级
			  var childTable = eleParent.next().find("#"+childTableName);
			  if (data==null || data.length==0) {
				  childTable.parent().remove();
			  } else if (ops.ifChildTable) {//判断是否需要添加子表格
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
						  childTable.append("<thead>"+thBody+"</thead>");
				      }
					  tbody = $("<tbody></tbody>");
					  childTable.append(tbody);
				  } else {
					  tbody = childTable.find("tbody").eq(0);
				  }
				  tbody.children().remove();
			  }
		      var rmIds = "",rmNames="";
		      for (var i=0;i<data.length;i++) {
		    	checkId+=(i>0?",":"")+data[i].ID;
		    	eval("var tmpName = data[i]."+ops.firstAlias);
		    	if (tmpName!=null && tmpName!=undefined) {
		    		checkName+=(i>0?",":"")+tmpName;
		    	}
		    	if (ops.ifChildTable) {//判断是否需要添加子表格
		    		 tbody.append(trBody);
		    		 tbody.find('tr').eq(i).find("[name*='"+commonName+"_']").each(function (a,one) {
		    			 var dataName = $(one).attr('dataName');
		    			 //判断是否有数据别名
		    			 if (dataName!=null && dataName!=undefined) {
		    				 eval("var tVal = data[i]."+dataName);
		    				 if (tVal!=null && tVal!=undefined) {
			    				 var tagName = $(one)[0].tagName.toLowerCase();//获取元素类型
			    				 if (tagName=='textarea') {//文本域
			    					 $(one).text(tVal);
			    				 } else {
			    					 $(one).val(tVal);
			    				 }
		    				 }
		    			 }
		    		 });
		    	 }
			  }
			  $el.attr("checkId", checkId);
			  $el.prev().val(checkId);
			  $el.val(checkName);
			  $el.data("json",data);
			  //初始化样式
			  if (ops.ifChildTable) {//判断是否需要添加子表格
				  layui.element.render();
	        	  layui.formSelects.render();
	        	  layui.form.render('select');
			  }
		}
    });
})(jQuery);