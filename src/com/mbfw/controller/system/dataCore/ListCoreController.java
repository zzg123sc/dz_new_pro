package com.mbfw.controller.system.dataCore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.ShardedJedis;
import com.alibaba.fastjson.JSON;
import com.mbfw.controller.base.BaseController;
import com.mbfw.util.DbDatautils;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.DBObject;

@Controller
public class ListCoreController extends BaseController{
	
	@Resource(name = "dbDataUtils")
	public DbDatautils dbDatautils;
	
	/**
	 * 去列表页面
	 * @return
	 */
	@RequestMapping(value = "/listCore_toList", produces = "text/html;charset=UTF-8")
	public @ResponseBody String toList () {
		String pageId = getParam("pageId");
		String tableId = getParam("tableId");
		String domId = tableId +"_"+pageId;
		ShardedJedis jds = RedisUtil.getJedis();
		String pageHtml = jds.get(domId+"_page");
		RedisUtil.returnJedis(jds);
		return replacePageContent(pageHtml);
	}
	
	@RequestMapping(value = "/listCore_ajaxList")
	public @ResponseBody Object ajaxList()  {
		Map<String, Object> tabMap = new HashMap<String, Object>();
		tabMap.put("code", 0);
		tabMap.put("msg", "");
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		ShardedJedis jds = RedisUtil.getJedis();
		String pageId = getParam("pageId");
		String tableId = getParam("tableId");
		String domId = tableId +"_"+pageId;
		String sql = jds.get(domId+"_sql");
		int ifOnlyCnt = MyNumberUtils.toInt(getParam("ifOnlyCnt"));//是否只获取记录数
		if (ifOnlyCnt==0) {
			int page = MyNumberUtils.toInt(getParam("page"));//页数
			int limit = MyNumberUtils.toInt(getParam("limit"));//每页条数
			if (page>0 && limit>0) {
				sql+=" LIMIT "+(page-1)*limit+","+limit;
			}
		}
		RedisUtil.returnJedis(jds);
		int count = 0;
		if (MyStringUtils.notBlank(sql)) {
			try {
				sql = getAllSearch(sql);
				dataList = dbDatautils.select(sql);
				//获取记录数sql
				String cntSql= sql;
				cntSql = cntSql.replaceAll("\r\n", " ").trim();
				//只获取记录数
				cntSql = "SELECT COUNT(1) CNT "+(cntSql.substring(cntSql.indexOf(" FROM "),cntSql.lastIndexOf(" ORDER BY ")));
				System.out.println("==cntSql=="+cntSql);
				List<Map<String, Object>> cntList = dbDatautils.select(cntSql);
				count = MyNumberUtils.toInt(cntList.get(0).get("CNT"));
			} catch (Exception e) {
				tabMap.put("code", -1);
				tabMap.put("msg", "服务器出错啦：");
				e.printStackTrace();
			}
		}
		if (ifOnlyCnt==0) {
			tabMap.put("count", count);
			tabMap.put("data", replaceListData(dataList));
			return JSON.toJSON(tabMap);
		} else {
			return dataList.get(0).get("CNT");
		}
	}
	
	/**
	 * 获取所有的搜索条件
	 * @param sql
	 */
	public String getAllSearch (String sql) {
		String fromSql = ","+(sql.substring(6,sql.indexOf(" from ")>0?sql.indexOf(" from "):sql.indexOf(" FROM ")));
		String[] searNames = getParamValues("searchName");
		StringBuffer searchBuffer = new StringBuffer();
		fromSql = fromSql.replaceAll("\r\n", "").trim();
		for (String searchName : searNames) {
			int searchType = MyNumberUtils.toInt(getParam(searchName+"_searchType"));//搜索条件类型
			int searchType1 = MyNumberUtils.toInt(getParam(searchName+"_oneType"));//搜索类型
			int searchType2 = MyNumberUtils.toInt(getParam(searchName+"_twoType"));//搜索类型
			//获取别名对应的查询sql
			if (fromSql.indexOf(" "+searchName)<0) {
				continue;
			}
			//截取要查询的字段
			String searchFsql = MyStringUtils.getSqlFieldByAliasName(fromSql, searchName, null);
			StringBuffer searchJoin = new StringBuffer();
			if (searchType==3) {//判断是否是日期
				String startDate = getParam(searchName+"_start");//获取开始日期
				String endDate = getParam(searchName+"_end");//获取结束日期
				if (MyStringUtils.notBlank(startDate)) {
					searchJoin.append(" AND "+ searchFsql+">='"+startDate+"'");
				} 
				
				if (MyStringUtils.notBlank(endDate)) {
					searchJoin.append(" AND "+ searchFsql+"<='"+endDate+"'");
				}
			} else if (searchType==2) {//判断是否是数字类型
				String num1 = getParam(searchName+"_one");//获取第一个数字
				String num2 = getParam(searchName+"_two");//获取第二个数字
				if (MyStringUtils.notBlank(num1)) {
					switch (searchType1) {
					case 1://大于
						searchJoin.append(" AND "+searchFsql+">"+num1);
						break;
					case 2://大于等于
						searchJoin.append(" AND "+searchFsql+">="+num1);
						break;
					case 3://等于
						searchJoin.append(" AND "+searchFsql+"="+num1);
						break;
					case 4://为空
						searchJoin.append(" AND "+searchFsql+" is null");
						break;
					case 5://不为空
						searchJoin.append(" AND "+searchFsql+" is not null");
						break;
					default:
						break;
					}
				} 
				
				if (MyStringUtils.notBlank(num2)) {
					switch (searchType2) {
					case 1://小于
						searchJoin.append(" AND "+searchFsql+"<"+num1);
						break;
					case 2://小于等于
						searchJoin.append(" AND "+searchFsql+"<="+num1);
						break;
					case 3://等于
						searchJoin.append(" AND "+searchFsql+"="+num1);
						break;
					case 4://为空
						searchJoin.append(" AND "+searchFsql+" is null");
						break;
					case 5://不为空
						searchJoin.append(" AND "+searchFsql+" is not null");
						break;
					default:
						break;
					}
				}
			} else if (searchType==4){//下拉框
				String dicVal = getParam(searchName);//获取搜索名称
				if (MyStringUtils.notBlank(dicVal)) {
					switch (searchType1) {
					case 1://包含
						searchJoin.append(" AND "+searchFsql+" in ("+dicVal+")");
						break;
					case 2://小于等于
						searchJoin.append(" AND "+searchFsql+" not in ("+dicVal+")");
						break;
					default:
						break;
					}
				}
			} else {//字符类型
				String str = getParam(searchName);//获取第一个数字
				if (MyStringUtils.notBlank(str)) {
					switch (searchType1) {
					case 1://包含
						searchJoin.append(" AND "+searchFsql+" like '%"+str+"%'");
						break;
					case 2://不包含
						searchJoin.append(" AND "+searchFsql+" not like '%"+str+"%'");
						break;
					case 3://等于
						searchJoin.append(" AND "+searchFsql+"='"+str+"'");
						break;
					case 4://起始
						searchJoin.append(" AND "+searchFsql+" like '"+str+"%'");
						break;
					case 5://终止
						searchJoin.append(" AND "+searchFsql+" like '%"+str+"'");
						break;
					case 6://为空
						searchJoin.append(" AND "+searchFsql+" is null");
						break;
					case 7://不为空
						searchJoin.append(" AND "+searchFsql+" is not null");
						break;
					default:
						break;
					}
				}
			}
			if (MyStringUtils.notBlank(searchJoin.toString())) {
				searchBuffer.append(searchJoin);
			}
		} 
		sql = sql.replace("${WHERE}", searchBuffer.toString());
		return sql;
	}
}
