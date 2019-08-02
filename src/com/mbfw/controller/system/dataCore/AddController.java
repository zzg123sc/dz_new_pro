package com.mbfw.controller.system.dataCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.ShardedJedis;

import com.mbfw.controller.base.BaseController;
import com.mbfw.service.system.dataCore.AddService;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.DBObject;

@Controller
public class AddController extends BaseController{
	
	@Resource(name = "addService")
	public AddService addService;
	/**
	 * 去列表页面
	 * @return
	 */
	@RequestMapping(value = "/addPage_toAdd", produces = "text/html;charset=UTF-8")
	public @ResponseBody String toAdd () {
		String pageId = getParam("pageId");
		String tableId = getParam("tableId");
		String domId = tableId +"_"+pageId;
		ShardedJedis jds = RedisUtil.getJedis();
		String pageHtml = jds.get(domId+"_page");
		RedisUtil.returnJedis(jds);
		return replacePageContent(pageHtml);
	}
	
	@RequestMapping(value = "/addPage_save")
	public @ResponseBody int save () {
		ShardedJedis jds = RedisUtil.getJedis();
		try {
			//存放添加数据后的id集合（tableId:ID_type）
			Map<String, List<String>> idMap = new HashMap<String, List<String>>();
			//用于子表格层递归计数使用
			Map<String, Integer>childDomMap = new HashMap<String, Integer>();
			addService.save(getParam("tableId")+"_"+getParam("pageId"),0,0,0,childDomMap,idMap, jds, getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			RedisUtil.returnJedis(jds);
		}
		return 1;
	}
}
