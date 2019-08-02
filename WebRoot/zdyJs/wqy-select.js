//块的移动方法
(function($) {
    $.omWidget('om.wqySelect', {
        options:{
             "ifChildTable":false,
             "dicParent":"",
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
        		ops = this.options,
        		dicParent= ops.dicParent
        	//判断师傅是数据字典
            if (dicParent!="" && dicParent.length>0) {
            	if ($el.children().length<=1) {
	            	$.ajax({
	  	      	      url:'page_getDicChildren',
	  	      	      type:'post',
	  	      	      data:{"dicParent":dicParent},
	  	      	      success:function(result){
	  	      	    	  var data = result.data;
	  	      	    	  if (data!=null && data.length>0) {
	  	      	    		  if (ops.isMulti) {
	  	      	    			  $el.children().remove();
	  	      	    		  }
	  	      	    		  for (var i=0;i<data.length;i++) {
	   	      	    			$el.append("<option value='"+data[i].value+"'>"+data[i].name+"</option>");
	   	      	    		  }
	  	      	    	  }
	  	      	    	  if (ops.isMulti) {
	  	      	    		$el.attr("xm-select","");
	  	      	    	  } else {
	  	      	    		layui.form.render('select');
	  	      	    	  }
	  	      	      },error:function () {
	  	      	    	layer.msg("加载失败！",{icon: 5,offset: 'rt'});
	  	      	      }
	            	});
            	} else if (ops.isMulti) {
            		var name = $el.attr("name");
      	    		$el.attr("xm-select",name+"_select");
            	}
            } 		
        },
		_callBack:function (data,$el) {
			var ops = this.options,
			self = this;
        }
    });
})(jQuery);