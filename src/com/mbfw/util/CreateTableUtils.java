package com.mbfw.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mongodb.DBObject;


/**
 * CreateTableUtils 生成数据库表的工具类
 * 
 * @Description 生成数据库表的工具类
 * @author wangqiyu
 * @version 版本号,Feb 17, 2014
 * @see
 * @since
 */
public class CreateTableUtils {
	
    /**
     * createTable 生成数据库表的方法
     * 
     * @Description 生成数据库表的方法
     * @param map void
     * @see
     * @author wqy
     * @createDate Feb 17, 2014
     */
    public static boolean createTable(DBObject tabInfo,List<DBObject> fieldList) {
    	Map<String, Object> tabMap=new HashMap<String, Object>();
    	String tableName = (String)tabInfo.get("tableName");
        StringBuffer createSql = new StringBuffer();
        //创建表(语句：createTableSql)
        createSql.append("create table "+tableName+"(");
        DBObject dbo=null;
        int type;
        String fieldName = "";
        for(int i=0;i<fieldList.size();i++) {
        	dbo=fieldList.get(i);
        	type=(Integer)dbo.get("fieldType");
        	fieldName = (String)dbo.get("fieldName");
        	if(type==12 || type==14 || type==16) {//多值选择项、内部对象多值、多文档字段不向数据库创建
        		continue;
        	}
        	if(i>0) {
        		createSql.append(",");
        	}
        	switch ((Integer)dbo.get("fieldType")) {
        	case 1://字符
    		case 3://身份证
    		case 4://email
    		case 5://手机
    		case 6://电话
    		case 7://规则生成
    		case 8://名称
    		case 18://用户名
            case 19://密码
				createSql.append(fieldName+" VARCHAR("+(MyNumberUtils.toInt(dbo.get("length"))==0?200:dbo.get("length"))+")   null");
    			break;
    		case 2://富文本
    		case 9://数据
    		case 11://单值选择项
    		case 13://单文档
    		case 15://内部对象
    		case 17://星期
    		case 20://排序
    		case 21://父级引用
    		case 22://创建人
    		case 24://修改人
    			int numType = MyNumberUtils.toInt(dbo.get("numType"));
    			String numTypeStr = "INT";
    			if (numType==1) {
    				numTypeStr = "FLOAT";
    			} else if (numType==2) {
    				numTypeStr = "DOUBLE";
    			} else if (numType==3) {
    				numTypeStr = "DECIMAL";
    			}
    			createSql.append(fieldName+" "+numTypeStr+"  null");
    			break;
    		case 10://日期
    		case 23://创建日期
    		case 25://修改日期
    			createSql.append(fieldName+" datetime null");
    			break;
    		case 0://主键
    			createSql.append(fieldName+" int primary key AUTO_INCREMENT");
    			break;
    		default:
    			break;
    		}
        	createSql.append(" comment '"+dbo.get("fieldCnName")+"'");
        }
        createSql.append(") comment='"+tabInfo.get("tableCnName")+"'");
        tabMap.put("createTab", createSql);
        try {
        	JdbcBean.createTable(tabMap);
        	return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    /**
     * 
     * alterTable  {修改数据库表}
     * 
     * @Description {修改数据库表}
     * @param map
     * @return void
     * @see
     */
    public static boolean alterTable(DBObject tabInfo,DBObject dbo) {
    	int type=(Integer)dbo.get("fieldType");
    	String tableName = (String)tabInfo.get("tableName");
    	String fieldName = (String)dbo.get("fieldName");
    	int length = MyNumberUtils.toInt(dbo.get("length"));
    	if(type==12 || type==14 || type==16) {//多值选择项、内部对象多值、多文档字段不向数据库创建
    		return true;
    	}
        StringBuffer createSql = new StringBuffer();
    	createSql.append("alter table "+ tableName);
    	if(dbo.get("_id")!=null) {//判断是否不是新增 
        	createSql.append(" modify ");
    	} else {
    		createSql.append(" add ");
    	}
    	switch (type) {
    	case 1://字符
		case 3://身份证
		case 4://email
		case 5://手机
		case 6://电话
		case 7://规则生成
		case 8://名称
		case 18://用户名
        case 19://密码
			createSql.append(fieldName+" VARCHAR"+"("+(length==0?200:length)+")");
			break;
        case 2://富文本
		case 9://数据
		case 11://单值选择项
		case 13://单文档
		case 15://内部对象
		case 17://星期
		case 20://排序
		case 21://父级引用
		case 22://创建人
		case 24://修改人
            int numType = MyNumberUtils.toInt(dbo.get("numType"));
			String numTypeStr = "INT";
			if (numType==1) {
				numTypeStr = "FLOAT(50,2)";
			} else if (numType==2) {
				numTypeStr = "FLOAT(50,3)";
			} else if (numType==3) {
				numTypeStr = "FLOAT(50,3)";
			}
			createSql.append(fieldName+" "+numTypeStr);
		    break;
		case 10://日期
		case 23://创建日期
		case 25://修改日期
			createSql.append(fieldName+" DATETIME");
			break;
		default:
			break;
    	}
    	createSql.append(" comment '"+dbo.get("fieldCnName")+"'");
    	boolean flag = true;
    	flag = JdbcBean.alterTable(createSql.toString());
        return flag;
    }
    
    /**
     * 
     * colHaveData  {判断某一行是否存在数据}
     * 
     * @Description {判断某一行是否存在数据}
     * @param map
     * @return Boolean
     * @see
     */
    public static Boolean colHaveData(String tName,String fName) {
    	if(MyStringUtils.isBlank(tName)) {
    		return false;
    	}
    	String sql="";
    	if(MyStringUtils.isBlank(fName)){
    		sql="select count(*) from "+tName;
    	} else {
    		sql="select count("+fName+") from "+tName;
    	}
    	System.out.println(sql);
    	return JdbcBean.colHaveData(sql);
    }
    
    /**
     * 
     * deleteTableField  {删除表字段}
     * 
     * @Description {删除表字段} void
     * @see
     */
    public static void deleteTableField(String tName,String fName) {
    	String sql="";
    	if(MyStringUtils.notBlank(fName)) {
    		sql="alter table "+tName+" drop column "+fName;
    		JdbcBean.alterTable(sql);
    	} else {
    		sql="drop table "+tName;
    		JdbcBean.alterTable(sql);
    	}
    }
    
    /**
     * 
     * clearTableField  {清空表字段数据}
     * 
     * @Description {清空表字段数据} void
     * @see
     */
    public static void clearTableField(String tName,String fName) {
    	String sql="";
    	if(MyStringUtils.notBlank(fName)) {
    		sql="update "+tName+" set "+fName+"=null";
    	} else {
    		sql="delete from "+tName;
    	}
    	JdbcBean.alterTable(sql);
    }
    

    
    /**
     * alertColName  修改列名
     *ALTER TABLE SCOTT.TEST RENAME COLUMN NAME TO NAME1 
     */
    public static void alertColName(String oldFieldName, DBObject tabInfo, DBObject dbo) {
        StringBuffer alterNameSb = new StringBuffer();
        String tableName = (String)tabInfo.get("tableName");
        String newFieldName = (String)dbo.get("fieldName");
        
        int type=MyNumberUtils.toInt(dbo.get("fieldType"));
        alterNameSb.append("ALTER TABLE "+ tableName + " CHANGE " + oldFieldName +" " + newFieldName);
        switch (type) {
        case 1://字符
		case 3://身份证
		case 4://email
		case 5://手机
		case 6://电话
		case 7://规则生成
		case 8://名称
		case 18://用户名
        case 19://密码
            alterNameSb.append(" VARCHAR(" + dbo.get("length") + ") ");
            break;
        case 2://富文本
		case 9://数据
		case 11://单值选择项
		case 13://单文档
		case 15://内部对象
		case 17://星期
		case 20://排序
		case 21://父级引用
		case 22://创建人
		case 24://修改人
			int numType = MyNumberUtils.toInt(dbo.get("numType"));
			String numTypeStr = "INT";
			if (numType==1) {
				numTypeStr = "FLOAT(50,2)";
			} else if (numType==2) {
				numTypeStr = "FLOAT(50,3)";
			} else if (numType==3) {
				numTypeStr = "FLOAT(50,3)";
			}
			alterNameSb.append(" "+numTypeStr);
            break;
		case 10://日期
		case 23://创建日期
		case 25://修改日期
            alterNameSb.append(" DATETIME ");
            break;
        default:
            break;
        }
        JdbcBean.alterTable(alterNameSb.toString());
    }
    /**alertTableName修改表名
     * 
     * ALTER TABLE SCOTT.TEST RENAME COLUMN NAME TO NAME1 
     */
    public static void alertTableName(Object oldTableName, Object newTableName, Object newTableCnName) {
        StringBuffer alterNameSb = new StringBuffer();
        alterNameSb.append("ALTER TABLE "+ oldTableName + " RENAME " + newTableName+", comment '"+newTableCnName+"'");
        JdbcBean.alterTable(alterNameSb.toString());
    }
}
