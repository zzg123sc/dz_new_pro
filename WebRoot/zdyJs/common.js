//行单击事件
/*layui.table.on('row', function(obj){
	    var dqTrChoose = obj.tr.find('input[name="layTableCheckbox"]');
        if (dqTrChoose.length>0) {
          dqTrChoose.prop("checked",!dqTrChoose.prop("checked"));
      	  layui.form.render('checkbox');
        } else {
      	  dqTrChoose = obj.tr.find('input[lay-type="layTableRadio"]');
      	  dqTrChoose = dqTrChoose.eq(0);
  		  dqTrChoose.attr("checked",!dqTrChoose.prop("checked"));
      	  layui.form.render('radio');
        }
        if (dqTrChoose.prop("checked")) {
  		  obj.tr.addClass('layui-bg-orange');//选中行样式
  	    } else {
  		  obj.tr.removeClass('layui-bg-orange');
  	    }
});*/

//获取月中周
function getMonthWeek(yearMonth,week) {
	  var yearMonths = yearMonth.split('-');
	  year = yearMonths[0];
	  month = yearMonths[1];
	  var d = new Date();
      // 该月第一天
      d.setFullYear(year, month-1, 1);
      var w1 = d.getDay();
      if (w1 == 0) w1 = 7;
      // 该月天数
      d.setFullYear(year, month, 0);
      var dd = d.getDate();
      // 第一个周一
      var d1=0;
      if (w1 != 1) {d1 = 7 - w1 + 2;week=week-1;}
      else {d1 = 1;}
      var monday = d1+(week-1)*7;
      var sunday = monday + 6;
      monday=monday<=0?1:monday;
      sunday = sunday>dd?dd:sunday;
      monday = monday>dd?dd:monday;
      monday = monday<10?'0'+monday:monday;
      var from = year+"-"+month+"-"+monday;
      var to;
      if (sunday <= dd) {
    	  sunday = sunday<10?'0'+sunday:sunday;
          to = year+"-"+month+"-"+sunday;
      } else {
          d.setFullYear(year, month-1, sunday);
          sunday=d.getDate();
          month = d.getMonth();
          sunday = sunday<10?'0'+sunday:sunday;
          to = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+sunday;
      }
      console.log(week+1+" 从" + from + " 到 " + to + "");
      return week+1+" 从" + from + " 到 " + to + "";
  }

  //提示错误信息
  function alertMessage (content,iconVal) {
	  var iconVal=iconVal!=null?iconVal:6;
	  if (content.indexOf("失败")>=0) {
		  iconVal = 5;
	  }
	  layer.msg(content,{icon: iconVal,offset: 't'});
  }
  
  /**
   * 打开弹层
   * @param titleName
   * @param url
   * @param width
   * @param height
   * @param submitId
   * @param ifNeedConfirm
   * @param btnaArr
   * @param ifIframe
   * @return
   */
  function openPage (titleName,url,width,height,submitId,ifNeedConfirm,btnaArr) {
	  	ifNeedConfirm = ifNeedConfirm!=null && ifNeedConfirm!=undefined?ifNeedConfirm:true;
		btnaArr = btnaArr!=null && btnaArr!=undefined?btnaArr:['保存', '取消'];
	  	layer.open({
	  		  title:titleName,
	  		  type:2,
	  		  anim: 5
	  		  ,area:[width+'px',height+'px']
	  		  ,content: url
	  		  ,btn: btnaArr
	  		  ,btnAlign: 'c'
	  		  ,offset: 'auto'
	  		  ,yes: function(index, layero){
	  			//调用保存
	  		    var submit = layero.find('iframe').contents().find('#'+ submitId);
	  			submit.trigger('click');
	  		  },btn2: function(index, layero){
	  			if (ifNeedConfirm) {
		  			layer.confirm("取消后将<font color='red'>不可恢复</font>,确定要取消吗？", {
		  	    		   btn: ['确定', '取消']
		  	  	    	    },function(index, layero){
		  	  	    		   layer.closeAll();
		  	  	    	   	}, function(index){
		  	  	    	   	   layer.close(index);
		  	  	    	   	}
		  	  	     );
		  			return false;
	  			}
	  			layer.closeAll();
	  		  },cancel:function () {
	  			  if (ifNeedConfirm) {
		  			  layer.confirm("取消后将<font color='red'>不可恢复</font>,确定要取消吗？", {
		  	    		   btn: ['确定', '取消']
		  	  	    	    },function(index, layero){
		  	  	    		   layer.closeAll();
		  	  	    	   	}, function(index){
		  	  	    	   	   layer.close(index);
		  	  	    	   	}
		  	  	      );
		  			  return false;
	  			  }
	  			 layer.closeAll();
	  		  }
	  		});
	  		//var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	  		//parent.layer.iframeAuto(index);
  }
  
  /**
   * 操作等待
   * @return
   */
  function loadOpera (timeSecend) {
	  timeSecend = timeSecend==null || timeSecend==undefined?15*1000:timeSecend;
	  var index = layer.load(2,{shade: [0.3, '#393D49'],time:timeSecend});
	  return index;
  }
  

  /**
   * form对象
   * @param $form
   * @return
   */
  function wqySerSerialize ($form) {
	  //处理内部对象多值数据
	  $form.find('[fieldType="16"]').each(function (i,one) {
		  var fname = $(one).attr('name');
		  var dqIsChild = $(one).closest('tr').length>0;//判断是否是子表格的内容
		  var eleParent = null;//当前元素所在行的父级
		  var childTable = null;
		  if (dqIsChild) {//子表格内查找
			  eleParent = $(one).closest('tr');
			  childTable = eleParent.next().find("#"+fname+"_table");//当前内部对象子集
		  } else {//最外层查找
			  eleParent = $(one).closest('form');
			  childTable = eleParent.find("#"+fname+"_table");//当前内部对象子集
		  }
		  //判断下面子表格个数
		  var innerSizeinput = $(one).parent().find("input[name='"+fname+"_size']");
		  if (innerSizeinput.length==0) {
			  innerSizeinput = $("<input type='hidden' name='"+fname+"_size'/>");
			  $(one).parent().append(innerSizeinput);
		  }
		  innerSizeinput.val(childTable.children('tbody').children('.ptr').length);
	  });
	  var data = $form.serialize();
	  data= decodeURIComponent(data,true);//防止中文乱码
	  console.log("---data--"+data);
	  data=data.replace(/&/g,"\",\""); 
	  data=data.replace(/=/g,"\":\""); 
	  data="{\""+data+"\"}"; 
	  console.log("------json---"+data);
	  return $form.serialize();//先序列化所有参数
  }