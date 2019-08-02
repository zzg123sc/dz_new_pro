package com.mbfw.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;


/**
 * JdbcBean 该类是使用JDBC连接数据库
 * 
 * @Description createTable()方法获得数据库的链接
 * @author fzy
 * @version 版本号,Feb 19, 2014
 * @see
 * @since
 */
public class JdbcBean {
    /**
     * createTable 创建表、序列、触发器
     * 
     * @Description 
     * @param sql void
     * @see
     * @author fzy
     * @createDate Feb 25, 2014
     */
    @SuppressWarnings("unchecked")
	public static boolean createTable(Map<String, Object> args) {
        Connection conn = null;
        try {
        	conn=JdbcUtil.connectDateBase();
            Statement stmt=conn.createStatement();
            // 创建序列
            if (args.get("createTab") != null) {
            	System.out.println(args.get("createTab").toString());
                stmt.execute(args.get("createTab").toString());
            }
            if (args.get("tabRemark") != null) {
            	System.out.println(args.get("tabRemark").toString());
                stmt.execute(args.get("tabRemark").toString());
            }
            return true;
        } catch (Exception e) {
            System.out.println("=======创建数据表失败======="+e.getMessage());
        	return false;
        } finally {
        	closeConnection(conn);
        }
    }
    
    /**
     * 
     * colHaveData  {判断数据库是否有数据}
     * 
     * @Description {判断数据库是否有数据}
     * @param sql
     * @return Boolean
     * @see
     */
    public static Boolean colHaveData(String sql) {
    	Connection conn = null;
        try {
        	conn=JdbcUtil.connectDateBase();
            PreparedStatement pres=conn.prepareStatement(sql);
            ResultSet rs=pres.executeQuery();
            while(rs.next()) {
                if (rs.getInt(1) == 0) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
        	System.out.println("错误信息："+e.getMessage());
        	return true;
        } finally {
        	closeConnection(conn);
        }
    }
    
    /**
     * 
     * alterTable 修改表结构
     * 
     * @Description 包括修改表的字段类型，长度，删除表的字段等
     * @param sql void
     * @see
     */
    public static boolean alterTable(String sql) {
        Connection conn = null;
        try {
        	conn=JdbcUtil.connectDateBase();
            Statement stmt=conn.createStatement();
            System.out.println("========="+sql);
            stmt.execute(sql);
            return true;
        } catch (Exception e) {
        	System.out.println("错误信息："+e.getMessage());
        	return false;
        } finally {
        	closeConnection(conn);
        }
    }
    
    /**
     * 
     * closeConnection  {关闭数据库连接}
     * 
     * @Description {关闭数据库连接} void
     * @see
     */
    public static void closeConnection(Connection con) {
    	if(con!=null) {
    		try {
    			System.out.println("=======关闭数据库========");
				con.close();
			} catch (SQLException e) {
				System.out.println("错误信息："+e.getMessage());
			} 
    	}
    }
}
