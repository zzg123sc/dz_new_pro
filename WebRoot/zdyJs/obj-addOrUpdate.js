	//删除字段
	function delTr(obj,fieldId) {
		layer.confirm('确定要删除吗？', function(index){
			$(obj).closest('tr').remove();
			layer.msg("已删除");
			fieldId = (fieldId==null || fieldId==undefined)?$(obj).closest('tr').closest("#fieldId").val():fieldId;
			fieldId = (fieldId==null || fieldId==undefined)?0:fieldId;
			if (fieldId>0) {
				var removeIds = $("#removeField").val();
				removeIds = removeIds!=null?removeIds:"";
				removeIds += (removeIds.length>0?",":"")+fieldId;
				$("#removeField").val(removeIds);
			}
		});
	}

	//字段类型切换
	function changeFieldType(obj) {
		if ($(obj).val()==11) {//判断是否是单值选择项
			$(obj).parent().find("#dicSelectDiv").show();
			$(obj).parent().find("[name='dicParent']").attr("lay-verify","required");
			 
			$(obj).parent().find("#innerObjDiv").hide();
			$(obj).parent().find("[name='relationTableId']").removeAttr("lay-verify");
			$(obj).closest("td").find("#length").hide();
			$(obj).closest("td").find("#length").removeAttr("lay-verify");
			$(obj).parent().find("#numSelectDiv").hide();
		} else if ($(obj).val()==15) {//判断是否是内部对象
			$(obj).parent().find("#innerObjDiv").show();
			$(obj).parent().find("[name='relationTableId']").attr("lay-verify","required");

			$(obj).parent().find("#dicSelectDiv").hide();
			$(obj).parent().find("[name='dicParent']").removeAttr("lay-verify");
			$(obj).closest("td").find("#length").hide();
			$(obj).closest("td").find("#length").removeAttr("lay-verify");
			$(obj).parent().find("#numSelectDiv").hide();
		} else if ($(obj).val()==9){//数据类型
			$(obj).parent().find("#dicSelectDiv").hide();
			$(obj).parent().find("[name='dicParent']").removeAttr("lay-verify");
			
			$(obj).parent().find("#innerObjDiv").hide();
			$(obj).parent().find("[name='relationTableId']").removeAttr("lay-verify");
			$(obj).closest("td").find("#length").hide();
			$(obj).closest("td").find("#length").removeAttr("lay-verify");
			
			$(obj).parent().find("#numSelectDiv").show();
		} else {
			$(obj).parent().find("#dicSelectDiv").hide();
			$(obj).parent().find("[name='dicParent']").removeAttr("lay-verify");
			
			$(obj).parent().find("#innerObjDiv").hide();
			$(obj).parent().find("[name='relationTableId']").removeAttr("lay-verify");
			$(obj).parent().find("#numSelectDiv").hide();
			if ($(obj).val()==13 || $(obj).val()==10 || $(obj).val()==17 || $(obj).val()==20 ) {//单文档
				$(obj).closest("td").find("#length").hide();
				$(obj).closest("td").find("#length").removeAttr("lay-verify");
			} else {
				$(obj).closest("td").find("#length").show();
				$(obj).closest("td").find("#length").attr("lay-verify","required|number");
			}
		}
	}
	
	//保存前验证
	function canSave () {
		if ($("input[name='fieldName']").length<1) {
			layer.msg("请至少添加一个字段！",{icon: 5});
			return false;
		}
		var tableType = $("#tableType").val();//对象类型
		if (tableType==1 || tableType==2 || tableType==3 ) {//管理维度/账户/角色
			var nameLenght = 0;
			$("select[name='fieldType']").each(function (i,one) {
				if ($(one).val()==8) {
					nameLenght++;
				}
			});
			if (nameLenght!=1) {
				layer.msg("请添加一个名称类型字段！",{icon: 5});
				return false;
			}
		} else if (tableType==5) {//多值选择项
			var dicLenght = 0;
			var innerLength = 0;
			$("select[name='fieldType']").each(function (i,one) {
				if ($(one).val()==11) {
					dicLenght++;
				} else if ($(one).val()==15) {
					innerLength++;
				}
			});
			if (dicLenght!=1) {
				layer.msg("请添加一个单值选择项类型字段！",{icon: 5});
				return false;
			}
			if (innerLength!=1) {
				layer.msg("请添加一个内部对项类型字段！",{icon: 5});
				return false;
			}
		} else if (tableType==6) {//多文档
			var dwdLenght = 0;
			var innerLength = 0;
			$("select[name='fieldType']").each(function (i,one) {
				if ($(one).val()==13) {
					dwdLenght++;
				} else if ($(one).val()==15) {
					innerLength++;
				}
			});
			if (dwdLenght!=1) {
				layer.msg("请添加一个单文档类型字段！",{icon: 5});
				return false;
			}
			if (innerLength!=1) {
				layer.msg("请添加一个内部对项类型字段！",{icon: 5});
				return false;
			}
		}
		return true;
	}