package com.mbfw.service.system.dataCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;

import com.alibaba.fastjson.JSON;
import com.mbfw.util.DbDatautils;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;

@Service("deleteService")
public class DeleteService {
	
	@Resource(name = "dbDataUtils")
	public DbDatautils dbDatautils;
	
	public int delete (boolean ifBatchDelete,String dataIds,String buttonKey) throws Exception{
		ShardedJedis jds = RedisUtil.getJedis();
		//获取删除之前的查询sql
		String deleteSelectSql = jds.get("deleteSelectSql_"+buttonKey);
		if (MyStringUtils.notBlank(deleteSelectSql)) {
			deleteSelectSql = deleteSelectSql.replace("#{DELETEIDS}", dataIds);
			//查询判断是否可以删除
			List<Map<String, Object>> selList = dbDatautils.select(deleteSelectSql);
			//获取不能删除的id
			if (MyCollectionUtils.notEmpty(selList)) {
				String[] idArr = dataIds.split(",");
				List<String> idList = new ArrayList<String>();
				for (String id : idArr) {
					idList.add(id);
				}
				for (Map<String, Object> map : selList) {
					if (idList.contains(map.get("ID")+"")) {
						idList.remove(map.get("ID")+"");
					}
				}
				dataIds = MyCollectionUtils.notEmpty(idList)?(idList.toString().replace("[", "").replace("]", "").replace(" ", "")):"";
			}
		}
		//执行删除操作
		if (MyStringUtils.notBlank(dataIds)) {
			String deleteJson = jds.get("deleteSql_"+buttonKey);
			if (MyStringUtils.notBlank(deleteJson)) {
				List<String> deleteList = (List<String>)JSON.parse(deleteJson);
				for (String deleteSql : deleteList) {
					deleteSql = deleteSql.replace("#{DELETEIDS}", dataIds);
					dbDatautils.delete(deleteSql);
				}
				return dataIds.split(",").length;
			}
		}
		RedisUtil.returnJedis(jds);
		return 0;
	}
}
