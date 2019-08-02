package com.mbfw.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.mbfw.dao.DaoSupport;

@Repository("dbDataUtils")
public class DbDatautils {
	
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 查询
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> select (String sql) {
		Map<String, Object> sqlArgs = new HashMap<String, Object>();
		sqlArgs.put("sql", sql);
		try {
			return (List<Map<String, Object>>)dao.findForList("UtilsMapper.select", sqlArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 添加
	 * @param sql
	 * @return
	 */
	public Integer insert (String sql) {
		try {
			Map<String, Object> sqlArgs = new HashMap<String, Object>();
			sqlArgs.put("sql", sql);
			dao.save("UtilsMapper.insert", sqlArgs);
			return MyNumberUtils.toInt(sqlArgs.get("ID"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 修改
	 * @param sql
	 * @return
	 */
	public Map<String, Object> update (String sql) {
		try {
			Map<String, Object> sqlArgs = new HashMap<String, Object>();
			sqlArgs.put("sql", sql);
			return (Map<String, Object>)dao.update("UtilsMapper.insert", sqlArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 删除
	 * @param sql
	 * @return
	 */
	public Map<String, Object> delete (String sql) {
		try {
			Map<String, Object> sqlArgs = new HashMap<String, Object>();
			sqlArgs.put("sql", sql);
			return (Map<String, Object>)dao.delete("UtilsMapper.delete", sqlArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 执行存储过程
	 * @param sql
	 * @return
	 */
	public Map<String, Object> exeProcedure (String sql) {
		try {
			Map<String, Object> sqlArgs = new HashMap<String, Object>();
			sqlArgs.put("sql", sql);
			return (Map<String, Object>)dao.update("UtilsMapper.exeProcedure", sqlArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
