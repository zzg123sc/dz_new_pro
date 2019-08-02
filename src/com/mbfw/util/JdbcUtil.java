package com.mbfw.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import sun.security.jca.GetInstance.Instance;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mysql.jdbc.Statement;

public class JdbcUtil {
	static long time = 50000;
    static String tmocatUrl = null;
    static String driverClassName = null;
    static String url = null;
    static String user = null;
    static String password = null;
    static{
        try {
            InputStream in =JdbcUtil.class.getResourceAsStream("/dbconfig.properties");
            Properties p = new Properties();
            //读取配加密配置文件
            p.load(in);
            driverClassName = p.getProperty("driverClassName");
            url = p.getProperty("url");
            user = p.getProperty("username");
            password = p.getProperty("password");  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection connectDateBase() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        try {
          
            Class.forName(driverClassName); 
            conn = DriverManager.getConnection(url, user, password); 
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            
        }
    }
    
    /**
     * 执行查询语句
     * @param ps
     * @param rs
     * @param conn
     * @param sql
     * @param parameterIn
     * @param parameterOut
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> exeSelect(Connection conn, String sql, Object[] parameterIn) {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
			ps = conn.prepareStatement(sql);
			
	        if(parameterIn != null) {
	            for(int i = 1; i <= parameterIn.length; i++) {
	                ps.setObject(i, parameterIn[i-1]);
	            }
	        }
	        rs = ps.executeQuery();
	        HashMap<String, Object> map = null;   
	        while (rs.next()) {
	        	ResultSetMetaData rsmd = rs.getMetaData();
	        	//rs.get
	        	int cols = rsmd.getColumnCount();
	            map = new HashMap<String, Object>();
	            // 将每个字段的key/value对保存到HashMap中
	            for (int i = 1; i <= cols; i++) {
	            	String field =  rsmd.getColumnLabel(i);
	                Object value = rs.getObject(i);
	                if(value instanceof BigDecimal) {
	                    value = MyNumberUtils.toDouble(value);
                        int s = MyNumberUtils.toInt(value);                    
                        double valueFloat = MyNumberUtils.toDouble(value);
                        double sfloat = (double)s;
                        if(sfloat == valueFloat){
                            value = s;
                        }
	                }
	                map.put(field, value);
	                //判断 field是否已 dic开头 或者 判断是否是editor字段
	                if((field.startsWith("DIC_") || field.startsWith("dic_")) && value != null) {
	                	DB db = MongoDbFileUtil.getDb();
	            		DBCollection dbColl = db.getCollection("dictionary");
	                	DBObject findOne = dbColl.findOne(new BasicDBObject("DIC_ID",value));
	                	if(findOne != null) {
							map.put(field, findOne.get("DIC_NAME"));
	                	}
	                }
	            }
	            // 将当前记录添加到List中
	            list.add(map);
	        }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
        	closeRsAndPs(rs, ps);
        }
        return list;
    }
    
    
    /**
     * 执行修改语句
     * @param ps
     * @param rs
     * @param conn
     * @param sql
     * @param parameterIn
     * @param parameterOut
     * @return
     * @throws SQLException
     */
    public static Integer exeUpdate(Connection conn, String sql) {
        Integer s = 0;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);            
            s = ps.executeUpdate();        
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	closeRsAndPs(rs, ps);
        }
        return s;
    }
    
    /**
     * 执行修改语句：返回执行修改的数据ID
     * @param ps
     * @param rs
     * @param conn
     * @param sql
     * @param parameterIn
     * @param parameterOut
     * @return
     * @throws SQLException
     */
    public static Integer exeUpdateReturnId(Connection conn, String sql) {
        Integer s = 0;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
        	ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
            
            ResultSet results = ps.getGeneratedKeys();
            if(results.next())
            {
                s = results.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeRsAndPs(rs, ps);
        return s;
    }
    
    /**
     * 关闭数据库 ps和rs
     * @param rs
     * @param ps
     */
    public static void closeRsAndPs(ResultSet rs, PreparedStatement ps) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
