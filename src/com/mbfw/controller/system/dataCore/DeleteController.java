package com.mbfw.controller.system.dataCore;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mbfw.controller.base.BaseController;
import com.mbfw.service.system.dataCore.DeleteService;
import com.mbfw.util.MyStringUtils;

@Controller
public class DeleteController  extends BaseController{
	
	@Resource(name = "deleteService")
	public DeleteService deleteService;
	
	@RequestMapping(value = "/delete_delete", produces = "text/html;charset=UTF-8")
	public @ResponseBody Object delete () {
		try {
			String buttonKey = getParam("buttonKey");
			String dataId = getParam("dataId");//单个删除的id
			String batchId = getParam("batchIds");//批量删除的id
			boolean ifBatchDelete = false;//是否是批量删除
			if (MyStringUtils.notBlank(batchId)) {
				ifBatchDelete = true;
				dataId = batchId;
			}
			int deleteLength = deleteService.delete(ifBatchDelete, dataId, buttonKey);
			String info = "删除成功！";
			//判断是否都删除了
			int oldLength = dataId.split(",").length;
			if (deleteLength<oldLength) {
				info = ifBatchDelete?info = "成功删除了<font color='red'>"+deleteLength+"条数据</font>,还有<font color='red'>"+(oldLength-deleteLength)+"条有数据</font>不可删除！":"<font color='red'>有数据不可删除</font>";
			}
			return info;
		} catch (Exception e) {
			return "服务器错误,删除失败！";
		}
	}
}
