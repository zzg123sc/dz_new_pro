//块的移动方法
(function($) {
    $.omWidget('wqyMoveOrder', {
        options:{
            //srcName 要移动的块的元素名称
			//upDown 上移 下移
			//callBack  回调函数
			 "callBack" : function(moveEle, parEle){
  			 }
        },
        //private methods
        _create:function(){
            var options=this.options,el=this.element;
            
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
			$el.unbind("click").bind("click",function(){
				var scrName = ops.srcName;
				var upDownValue = ops.upDown;
				var moveEle = $el.closest("[name='"+scrName+"']");
				var parEle = moveEle.parent();
				var EleLen = parEle.find("[name='"+scrName+"']").length;
				if(EleLen > 1){ //移动元素个数大于1时，移动才有意义
					// 下移
					if(upDownValue==0) { 	
						var nextObj = moveEle.next("[name='"+scrName+"']");
						if(nextObj.length > 0) {
							nextObj.after(moveEle);					
						}else{
							var firstObj = parEle.find('[name="'+scrName+'"]').first();
							firstObj.before(moveEle);					
						}
					} else {
						var preObj = moveEle.prev("[name='"+scrName+"']");
						if(preObj.length > 0) {
							preObj.before(moveEle);
						} else {
							var lastObj = parEle.find('[name="'+scrName+'"]').last();
							lastObj.after(moveEle);
						}
					}
				}
				parEle.find('[name="'+scrName+'"]').each(function(i,key){
					
					$(this).attr("order",i+1);				
				});			
				self._trigger("callBack",null, moveEle,parEle);
			});
		}
    });
})(jQuery);

//删除块的方法,改变排序
(function($) {
    $.omWidget('om.wqyDeleteDom', {
        options:/** @lends omGrid#*/{
            //srcName 要删除的块的元素名称
			//callBack  回调函数
			 "callBack" : function(moveEle, parEle){
  			 }
        },
        //private methods
        _create:function(){
            var options=this.options,el=this.element;
            
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
			$el.bind("click",function(){
				var scrName = ops.srcName;
				var delEle = $el.closest("[name='"+scrName+"']");
				var parEle = delEle.parent();
				delEle.remove();				
				parEle.find('[name="'+scrName+'"]').each(function(i,key){					
					$(this).attr("order",i+1);				
				});			
				self._trigger("callBack",null, delEle,parEle);
			});
		}
    });
})(jQuery);



	/** 
     * @name wqyBlockClone
     * @class 块元素复制的功能。<br/>
     * &lt;script type="text/javascript" &gt;
     * 实例：languageSett-list.jsp
	 *  $('#div_set_page').wqyBlockClone({
	 *     	srcEle : $("#div_set_page table tr:last"),//$("#div_set_page table:first"),
	 *      parEle :  $("#div_set_page table"),//$("#div_set_page"),
	 *      ifCloneEvent : true,
	 *      ifClean : false,
	 *      srcHtml : $("#div_set_page table tr:last").clone()
	 *  });
     */
(function($) {
    $.omWidget('om.wqyBlockClone', {
        options: {
            //srcEle  要复制的块的  jquery 对象  srcEle 存在 就不使用srcHtml   俩者只存在一种
        	srcEle : "",
        	
			//parEle  要复制到得位置 的父级的 jquery 对象
        	parEle : "",
        	
			//ifCloneEvent 是否克隆 事件
        	ifCloneEvent : false,
        	
			//ifClean  是否清除元素内容
        	ifClean : false,
        	
			//srcHtml   要复制新增元素的 源html数据  如果此元素不为空 复制 srcEle的元素 否则复制parEle
        	srcHtml : "",
        	
			//callBack  回调函数
			 callBack : []
        },
       
        _create:function(){
            var options=this.options,
            el=this.element;
            
        },
        _init:function(){
        	var $el=this.element,
        		ops = this.options;
            this._bindClick();
        },
        
		_bindClick:function(){
			var $el=this.element,
				ops = this.options,
				self = this;
			var ifCloneEvent = ops.ifCloneEvent;
			var srcHtml = ops.srcHtml;
			var parEle = ops.parEle;
			var srcEle = ops.srcEle;

			$el.bind("click",function(){
				var oneClone = null;
				//srcHtml  要复制新增元素的 源html数据  如果此元素不为空 复制 srcEle的元素 否则复制parEle
				if(srcHtml == "") {
					oneClone = srcEle.clone(ifCloneEvent);
				} else {
					oneClone = $(srcHtml);
				}
				if(ops.ifClean) {
                    oneClone.find("input[type='text']").val("");
					oneClone.find("input[type='hidden']").val("");
					oneClone.find("option").attr("selected", false);
				}
				parEle.append(oneClone);
				self._trigger("callBack",null, oneClone);
			});
		}
    });
})(jQuery);


	/** 
     * @name wqyTrTdMerge
     * @class 表格的合并功能。<br/>
     * &lt;script type="text/javascript" &gt;
     * 实例：languageSett-list.jsp
	 *  $('#div_set_page').wqyBlockClone({
	 *     	srcEle : $("#div_set_page table tr:last"),//$("#div_set_page table:first"),
	 *      parEle :  $("#div_set_page table"),//$("#div_set_page"),
	 *      ifCloneEvent : true,
	 *      ifClean : false,
	 *      srcHtml : $("#div_set_page table tr:last").clone()
	 *  });
     */
(function($) {
    $.omWidget('om.wqyTrTdEvent', {
        options: {
            //eventName 事件名称  001 :合并  002：添加列  003:添加行  004：删除列  005：删除行  006：拆分单元格
            // 如没有传参数 则默认是有所有的事件 已逗号分隔 反之 则表示某些事件没有
            // 如 ：eventName : "001,002"
        	eventName : "",
        	
        	//不可删的行的个数
        	nodelRowSize:0,
        	
			//callBack  回调函数
			 callBack : []
        },
       
        _create:function(){
            var options=this.options,
            el=this.element;
            this.hidDiv = $('<div></div>');
            el.after(this.hidDiv);
        },
        
        _init:function(){
        	var $el=this.element,
        		ops = this.options;
        	this.TimeFn = null; //定义setTimeout执行方法
            this._bindClick();
        },
        
        /* 给表格绑定右键事件
         */
		_bindClick:function(){
			var $el=this.element,
				ops = this.options,
				self = this;
			var name = ops.eventName;
			var TimeFn = null; //定义setTimeout执行方法
			//给td加单击事件  die先死亡再附加一个事件  live：给某元素绑定事件后以后新增的同样元素则不用再绑定事件
			$el.find("td").die("click.edusColor").unbind("click.edusColor").live("click.edusColor",function(){
				self.clickTdColor($(this));	//单元格切换背景样式 变色标示 以便后续操作
			});
			
			self.hidDiv.omMenu({
				minWidth : 150,
			    maxWidth : 220,
			    contextMenu : true,
			    dataSource : [ 
		        	//{id:'001',label:'合 并'},
			        {id:'002',label:'添加列',children:[
		                {id:'002001',label:'左侧添加列'},
		                {id:'002002',label:'右侧添加列'}
		             ]},
			        {id:'003',label:'添加行',children:[
		                {id:'003001',label:'上添加行'},
		                {id:'003002',label:'下添加行'}
		             ]},
			        {id:'004',label:'删除列'},
			        {id:'005',label:'删除行'},
			        //{id:'006',label:'拆 分'},
			        //{id:'007',label:'内容后移'},
			        //{id:'008',label:'自动美化表格'}
		        ],
		        onSelect : function(item,options,event){
		            //options.nowClickId  菜单的id
		             var id = item.id;
		        	 var tdId = options.nowClickId;
		             if(id =='001'){
		             //合并
		             	self._merge(item,options);
		             }else if(id == '002001'){
		             //添加左列
		             	self._addCol(item,options,0);
		             }else if(id == '002002'){
		             //添加右列
		             	self._addCol(item,options,1);
		             }else if(id == '003001'){
		             	//添加上行
		             	self._addRow(item,options,0);
		             }else if(id == '003002'){
		             	//添加下行
		             	self._addRow(item,options,1);
		             }else if(id == '004'){
		             	//删除列
		             	self._removeCol(item,options);
		             }else if(id == '005'){
		             	//删除列
		             	self._removeRow(item,options);
		             }else if(id == '006'){
		             	//拆分
		             	self._splitCells(item,options);
		             }else if(id == '007'){
		             	//内容后移
		             	self._contentAfterShift(item,options);
		             }else if(id == '008'){
		             	//自动美化表格
		             	self._tableBeautify(item,options);
		             }
				}
			});
            $el.bind('contextmenu',function(e){
		         self.hidDiv.omMenu('show',e);
		 	});
		 	//显示所有的菜单
		 	if(name != ""){
		 		//禁用多个菜单
			 	if(name.indexOf(",") > 0){
	            	var nameArr = name.split(",");
	            	for(var i =0 ;i < nameArr.length; i++){
	            		if(!isNaN(nameArr[i])){
		            		 self.hidDiv.omMenu('disableItem',nameArr[i]);
	            		}
	            	} 
	            }else {
	            //只禁用一个菜单
		            if (!isNaN(name)){
		            	 self.hidDiv.omMenu('disableItem',name);
		            }
	            }
		 	}
			self._trigger("callBack",null);
		},clickTdColor:function (obj) {
			var $el=this.element;
		    var dqClass = $(obj).attr('class');
		    if (dqClass.indexOf("layui-bg-orange")>0) {
		    	$(obj).removeClass("layui-bg-orange");
		    } else {
		    	$el.find('td').removeClass("layui-bg-orange");
		    	$(obj).addClass("layui-bg-orange");
		    }
		},/*	添加列 方法
		 *	item：选中的菜单选项
		 *  options:设置的属性
		 *	number：数值 0:左  1:右
		 */
		_addCol:function(item,options,number){
			var $el = this.element,
				self = this,
				ops = self.options;
			if ($el.find('.layui-bg-orange').length==0) {
				layer.msg("请选择你要操作的列！",{icon: 5,offset: 'rt'});
				return ;
			}
			var checkdTd = $el.find('.layui-bg-orange');//当前选中的td
			var checkdTdIndex = checkdTd.index();//当前选中的td下标
			$el.find("tr").each(function (i,one) {
				var $td = $("<td></td>");
				if (number==0) {//左添加列
					$(one).find('td').eq(checkdTdIndex).before($td);
				} else {
					$(one).find('td').eq(checkdTdIndex).after($td);
				}
				self._trigger("onAddCol",null,$td);
			});
			
		},
		/*	添加列 方法
		 *	item：选中的菜单选项
		 *  options:设置的属性
		 *	number：数值 0:左  1:右
		 */
		_addRow:function(item,options,number){
			var $el = this.element,
				self = this,
				ops = self.options;
			if ($el.find('.layui-bg-orange').length==0) {
				layer.msg("请选择你要操作的列！",{icon: 5,offset: 'rt'});
				return ;
			}
			var checkdTr = $el.find('.layui-bg-orange').closest('tr');//当前选中的td
			var checkdTrIndex = checkdTr.index();//当前选中的td下标
			//克隆行并清除列数据和样式
			var cloneTr = checkdTr.clone(false);
			cloneTr.find("td").removeAttr("class");
			cloneTr.find("td").children().remove();
			if (number==0) {//左添加列
				$el.find("tr").eq(checkdTrIndex).before(cloneTr);
			} else {
				$el.find("tr").eq(checkdTrIndex).after(cloneTr);
			}
			self._trigger("onAddRow",null,cloneTr);
			
		},_removeCol:function(item,options,number){//删除列
			var $el = this.element,
			self = this,
			ops = self.options;
			if ($el.find('.layui-bg-orange').length==0) {
				layer.msg("请选择你要操作的列！",{icon: 5,offset: 'rt'});
				return ;
			}
			if ($el.find("tr").eq(0).find('td').length==1) {
				layer.msg("表格仅剩一列,不可删除！",{icon: 5,offset: 'rt'});
				return ;
			}
			var checkdTd = $el.find('.layui-bg-orange');//当前选中的td
			var checkdTdIndex = checkdTd.index();//当前选中的td下标
			var tdDataNum = 0 ;//判断当前列下是否有数据
			$el.find("tr").each(function (i,one) {
				if ($(one).find('td').eq(checkdTdIndex).children().length>0) {
					tdDataNum++;
				}
			});
			if (tdDataNum>0) {//有数据则提示是否可删除
				layer.confirm("当前列下有<font color='red'>"+tdDataNum+"</font>条数据，确定要删除整列吗？", function(index){
					layer.close(index);
					$el.find("tr").each(function (i,one) {
						$(one).find('td').eq(checkdTdIndex).remove();
					});
		    	});
			} else {
				$el.find("tr").each(function (i,one) {
					$(one).find('td').eq(checkdTdIndex).remove();
				});
			}
		},_removeRow:function(item,options,number){//删除行
			var $el = this.element,
			self = this,
			ops = self.options;
			if ($el.find('.layui-bg-orange').length==0) {
				layer.msg("请选择你要操作的列！",{icon: 5,offset: 'rt'});
				return ;
			}
			if ($el.find("tr").length==1) {
				layer.msg("表格仅剩一行,不可删除！",{icon: 5,offset: 'rt'});
				return ;
			}
			var checkdTr = $el.find('.layui-bg-orange').closest('tr');//当前选中的td
			var checkdTrIndex = checkdTr.index();//当前选中的td下标
			var tdDataNum = 0 ;//判断当前列下是否有数据
			checkdTr.find("td").each(function (i,one) {
				if ($(one).children().length>0) {
					tdDataNum++;
				}
			});
			if (tdDataNum>0) {//有数据则提示是否可删除
				layer.confirm("当前行下有<font color='red'>"+tdDataNum+"</font>条数据，确定要删除整行吗？", function(index){
					layer.close(index);
					checkdTr.remove();
		    	});
			} else {
				checkdTr.remove();
			}
		}
    });
})(jQuery);

(function($){
    function isIE7() {
        return $.browser.msie && parseInt($.browser.version) == 7;
    }
    
		//联动显示隐藏
		$.omWidget('om.wqyHideOrShow', {
	        options:/** @lends omGrid#*/{
	            srcName:[], //= 源的name值    前置条件的 name集合
				srcValue:[], //= 源的value值    前置条件 name集合对应的 value值 多个value值  ,号隔开
				hidEleName:[], //= 需要隐藏的元素的name值  
				showEleName:[], //= 需求显示的元素的name值
				parEle:'', //这些元素的父级 的 jquery对象
				ifDelegate:false, //新增元素元素是否添加 事件
				callBackGetValue :[]
	        },
	        //private methods
	        _create:function(){
	            var options=this.options,el=this.element;
	        },
	        _init:function(){
	        	var $el=this.element,
	        		ops = this.options;
	        		
				//绑定切换隐藏显示事件
	            this._bindOnchange();
	        },
			_bindOnchange:function(){
				var self = this;
				var $el = this.element,
					ops = this.options;
					if(ops.parEle == null) {
						ops.parEle = $el.parent();
					} 
					//
					$.each(ops.srcName, function(i, oneName){
						if(ops.ifDelegate) {
							//delegate 
							ops.parEle.delegate('[name="' + oneName + '"]',"change",function(){
								self._onChangeContent();
							});
						} else {
							ops.parEle.find('[name="' + oneName + '"]').each(function(j){
								$(this).bind("change",function(){
									self._onChangeContent(j);
								});
							})
						}
					});
					
					self._trigger("callBackGetValue",null, ops.srcName);
			},
			_onChangeContent:function(nameIndex){
				var self = this;
				var $el=this.element,
					ops = this.options;
				var srcName = ops.srcName;
				var srcValue = ops.srcValue;
				$(srcValue).each(function(j, nowSrcValue){
					var flag = true;
					$.each(srcName, function(i, oneName){
						//不是判断=   判断 是否符合  例如  1,3  判断value 为1 或者 3 
						var tmpVal = ops.parEle.find('[name="' + oneName + '"]').eq(nameIndex).val() + "";
						if(nowSrcValue[i] == "" || (tmpVal != "" && nowSrcValue[i].indexOf(tmpVal) >= 0)) {
							
						} else {
							flag = false;	
						};
					});
					if(flag) {
						//执行隐藏  显示
						self._changeHidShow(nameIndex, j, 0);
					} else {
						//执行显示  隐藏
						self._changeHidShow(nameIndex, j, 1);
					}
				});
			},
			//执行显示 隐藏
			_changeHidShow:function(nameIndex, hidShowIndex, type){
				var self = this;
				var $el=this.element,
					ops = this.options;
				var hidEleName = ops.hidEleName;
				var showEleName = ops.showEleName;
				$(hidEleName[hidShowIndex]).each(function(k, oneName){
					var oneArr = oneName.split(",");
					$(oneArr).each(function(p, tmpName){
						var tmp = ops.parEle.find('[name="' + tmpName + '"]').eq(nameIndex);
						if(type == 0) {
							//隐藏
							tmp.hide();
						} else {
							//显示
							tmp.show();
						}	
					});
				});
				$(showEleName[hidShowIndex]).each(function(k, oneName){
					var oneArr = oneName.split(",");
					$(oneArr).each(function(p, tmpName){
						var tmp = ops.parEle.find('[name="' + tmpName + '"]').eq(nameIndex);
						if(type == 0) {
							//显示
							tmp.show();
						} else {
							//隐藏
							tmp.hide();
						}	
					});
				});
			}
			
	    });
})(jQuery);
//判断开始字符串
String.prototype.startWith=function(str){    
  var reg=new RegExp("^"+str);    
  return reg.test(this);       
} 
//判断结束字符串
String.prototype.endWith=function(str){    
  var reg=new RegExp(str+"$");    
  return reg.test(this);       
}