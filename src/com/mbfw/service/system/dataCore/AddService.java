package com.mbfw.service.system.dataCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.mbfw.util.DbDatautils;
import com.mbfw.util.MD5;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;

import redis.clients.jedis.ShardedJedis;

@Service("addService")
public class AddService {
	
	@Resource(name = "dbDataUtils")
	public DbDatautils dbDatautils;
	public int save (String domId,int pDataId,int index,int valueIndex,Map<String, Integer> childDomMap,Map<String, List<String>> idMap,ShardedJedis jds,HttpServletRequest request) throws Exception{
		String addSql = jds.get(domId+"_insertSql");
		if (pDataId>0) {
			addSql = addSql.replace("#{mainId}", pDataId+"");
		}
		System.out.println(valueIndex+"==sql=="+domId);
		addSql = replaceAddSql(addSql,valueIndex, request);
		int rsId = dbDatautils.insert(addSql);
		List<String> itMap = idMap.get(domId);
		itMap = (MyCollectionUtils.isEmpty(itMap))?new ArrayList<String>():itMap;
		itMap.add(rsId+"_1");
		idMap.put(domId, itMap);
		
		//获取当前层级的子dom
		String childDom = jds.get(domId+"_childDom");
		if (MyStringUtils.notBlank(childDom)) {
			System.out.println("===childDom=="+childDom);
			String[] childDomArr = childDom.split(",");
			for (String cdom : childDomArr) {
				//获取当前dom的个数
				String domFieldName = jds.get(cdom+"_fieldName");
				String[] childSize = request.getParameterValues(domFieldName+"_size");//获取所有的子项（统计当前dom的所有个数）
				String[] dqField = request.getParameterValues(domFieldName);//当前字段
				System.out.println(cdom+"==cdom=="+domFieldName);
				int csize = (childSize!=null && childSize.length>0)?MyNumberUtils.toInt(childSize[valueIndex]):0;
				if (csize>0) {//证明有子项
					System.out.println(cdom+"==cdomcsize="+csize+"="+childDomMap);
					if (csize>0) {
						for (int j=0;j<csize;j++) {
							int childNum = MyNumberUtils.toInt(childDomMap.get(cdom+"_child"));
							//递归保存
							save(cdom,rsId, 1, childNum,childDomMap, idMap, jds, request);
							childDomMap.put(cdom+"_child",childNum+1);
						}
					}
				} else if (dqField!=null) {//弹层选择无子表格
					String dqFieldValue = dqField[valueIndex];
					if (MyStringUtils.isBlank(dqFieldValue)) {
						continue;
					}
					String[] valueArr = dqFieldValue.split(",");
					for (String value : valueArr) {
						System.out.println("==cdom=="+cdom);
						addSql = jds.get(cdom+"_insertSql");
						addSql = addSql.replace("#{mainId}", rsId+"");
						addSql = addSql.replace("#{userId}", "null");
						addSql = addSql.replaceAll("#\\{[a-zA-Z0-9:-_]+\\}", value);
						int tmpRsId = dbDatautils.insert(addSql);
						List<String> citMap = idMap.get(domId);
						citMap = (MyCollectionUtils.isEmpty(citMap))?new ArrayList<String>():citMap;
						citMap.add(tmpRsId+"_1");
						idMap.put(cdom, citMap);
					}
				}
			}
		}
		return rsId;
	}
	
	/**
	 * 替换添加sql
	 * @return
	 */
	public String replaceAddSql (String sql,int childIndex,HttpServletRequest request) {
		System.out.println("==sql=="+sql);
		//正则 替换sql中的参数类型
		Pattern pattern = Pattern.compile("#\\{[a-zA-Z0-9:-_]+\\}");
		Matcher match =  pattern.matcher(sql);
		while(match.find()) {
			String group = match.group();
			String pname = group.substring(2, group.length()-1);
			String[] valueArr = request.getParameterValues(pname);//获取值
			String value = valueArr!=null && valueArr.length>0?valueArr[childIndex]:null;
			if (pname.startsWith("WQYPASSWORD_")) {
				pname = pname.replace("WQYPASSWORD_", "");
				valueArr = request.getParameterValues(pname);
				value = valueArr!=null && valueArr.length>0?valueArr[childIndex]:null;
				value = MyStringUtils.notBlank(value)?value:"000000";
				value = MD5.md5(value);
			}
			if (MyStringUtils.isBlank(value)) {
				value = "null";
			} else {
				value = MyNumberUtils.can2Int(value)?value:("'"+value+"'");
			}
			sql = sql.replace(group, value);
		}
		return sql;
	}
}
