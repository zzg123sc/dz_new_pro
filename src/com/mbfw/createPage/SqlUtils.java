package com.mbfw.createPage;

import java.util.ArrayList;
import java.util.List;


import redis.clients.jedis.ShardedJedis;

import com.alibaba.fastjson.JSON;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class SqlUtils {
	
	/**
	 * 拼接列表sql
	 * @param showFieldList
	 * @param tableDbc
	 * @param fieldDbc
	 * @param tableId
	 * @return
	 */
	public static String appendListSql (List<DBObject> showFieldList,DBCollection tableDbc,DBCollection fieldDbc,int tableId) {
		StringBuffer selectSqlBuffer = new StringBuffer();
		StringBuffer fromSqlBuffer = new StringBuffer();
		//获取当前主对象
		DBObject mainTable = tableDbc.findOne(new BasicDBObject("tableId",tableId));
		
		//获取主键字段
		DBObject args = new BasicDBObject();
		args.put("fieldType", 0);
		args.put("tableId", tableId);
		DBObject mainFieldDbo = fieldDbc.findOne(args);
		selectSqlBuffer.append("SELECT T_0."+mainFieldDbo.get("fieldName")+" ID");
		fromSqlBuffer.append(" FROM "+mainTable.get("tableName")+" T_0 ");
		for (int i=0;i<showFieldList.size();i++) {
			DBObject field = showFieldList.get(i);
			int fieldId = MyNumberUtils.toInt(field.get("fieldId"));
			if (fieldId<=0) {//自定义字段
				continue;
			}
			
			int fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
			String fieldName = (String)field.get("fieldName");
			String zdyName = (String)field.get("zdyName");
			zdyName = MyStringUtils.notBlank(zdyName)?zdyName:"AFM_"+fieldId;
			field.put("zdyName", zdyName);
			if (fieldType==15 || fieldType==21 || fieldType==22 || fieldType==24) {//内部对象
				Integer relationTableId = MyNumberUtils.toInt(field.get("relationTableId"));
				DBObject userTable = null;
				if (fieldType==22 || fieldType==24) {//创建人/修改人
					userTable = tableDbc.findOne(new BasicDBObject("tableType",2));
					if (userTable==null) {
						continue;
					}
					relationTableId =MyNumberUtils.toInt(userTable.get("tableId"));
				}
				//获取内部对象对应的主键字段
				args = new BasicDBObject();
				args.put("fieldType", 0);
				args.put("tableId", relationTableId);
				DBObject mfield = fieldDbc.findOne(args);
				if (mfield==null) {
					continue;
				}
				
				//获取关联表的名称字段
				args = new BasicDBObject();
				args.put("fieldType", 8);
				args.put("tableId", relationTableId);
				DBObject namefield = fieldDbc.findOne(args);
				if (namefield==null) {
					continue;
				}
				DBObject reTable = null;
				if (fieldType==22 || fieldType==24) {//创建人/修改人
					reTable = userTable;
				} else {
					//获取关联表
					args = new BasicDBObject();
					args.put("tableId", relationTableId);
					reTable = tableDbc.findOne(args);
					if (reTable==null) {
						continue;
					}
				}
				
				selectSqlBuffer.append(",TMP_"+i+"."+namefield.get("fieldName")+" "+zdyName+",TMP_"+i+"."+mfield.get("fieldName")+" "+zdyName+"_ID ");
				fromSqlBuffer.append(" LEFT JOIN "+reTable.get("tableName")+" TMP_"+i+" ON ");
				fromSqlBuffer.append("TMP_"+i+"."+mfield.get("fieldName")+"=T_0."+field.get("fieldName"));
			} else if (fieldType==16) {//内对象多值
				//获取关联的内部对象字段
				args = new BasicDBObject();
				args.put("fieldId", field.get("relationFieldId"));
				DBObject rfield = fieldDbc.findOne(args);
				if (rfield==null) {
					continue;
				}
				
				//获取内部对象多值表
				args = new BasicDBObject();
				args.put("tableId", field.get("relationTableId"));
				DBObject dzTable = tableDbc.findOne(args);
				if (dzTable==null) {
					continue;
				}
				
				//获取另一个内部对象字段
				args = new BasicDBObject();
				args.put("fieldType", 15);
				args.put("fieldId", new BasicDBObject("$ne",field.get("relationFieldId")));
				args.put("tableId", field.get("relationTableId"));
				DBObject otherDbo = fieldDbc.findOne(args);
				if (otherDbo==null) {
					continue;
				}
				
				//获取另一个内部对象的名称字段
				args = new BasicDBObject();
				args.put("fieldType", 8);
				args.put("tableId", otherDbo.get("relationTableId"));
				DBObject namefield = fieldDbc.findOne(args);
				if (namefield==null) {
					continue;
				}
				
				//获取另一个内部对象的表信息
				args = new BasicDBObject();
				args.put("tableId", otherDbo.get("relationTableId"));
				DBObject reTable = tableDbc.findOne(args);
				if (reTable==null) {
					continue;
				}
				
				//获取另一个内部对象的主键字段
				args = new BasicDBObject();
				args.put("fieldType", 0);
				args.put("tableId", otherDbo.get("relationTableId"));
				DBObject omfield = fieldDbc.findOne(args);
				if (omfield==null) {
					continue;
				}
				selectSqlBuffer.append(",TMP_"+i+"."+zdyName+" "+zdyName+" ,TMP_"+i+"."+zdyName+"_ID "+zdyName+"_ID ");
				fromSqlBuffer.append(" LEFT JOIN (SELECT TP_0."+rfield.get("fieldName")+",GROUP_CONCAT(TP_1."+namefield.get("fieldName")+") "+zdyName);
				fromSqlBuffer.append(",GROUP_CONCAT(TP_1."+omfield.get("fieldName")+") "+zdyName+"_ID ");
				fromSqlBuffer.append(" FROM "+dzTable.get("tableName")+" TP_0 LEFT JOIN "+reTable.get("tableName")+" TP_1 ");
				fromSqlBuffer.append(" ON TP_1."+omfield.get("fieldName")+"=TP_0."+otherDbo.get("fieldName")+" GROUP BY TP_0."+rfield.get("fieldName")+") TMP_"+i);
				fromSqlBuffer.append(" ON TMP_"+i+"."+rfield.get("fieldName")+"="+"T_0."+mainFieldDbo.get("fieldName"));
			} else if (fieldType==11) {//单值选择项
				selectSqlBuffer.append(",T_0."+fieldName+" "+zdyName+",T_0."+fieldName+" DIC_"+field.get("dicParent")+"_"+zdyName);
			} else if (fieldType==12){//多值选择项
				//获取关联的内部对象字段
				args = new BasicDBObject();
				args.put("fieldId", field.get("relationFieldId"));
				DBObject rfield = fieldDbc.findOne(args);
				if (rfield==null) {
					continue;
				}
				
				//获取内部对象多值表
				args = new BasicDBObject();
				args.put("tableId", field.get("relationTableId"));
				DBObject dzTable = tableDbc.findOne(args);
				if (dzTable==null) {
					continue;
				}
				
				//获取单值选择项字段
				args = new BasicDBObject();
				args.put("fieldType", 11);
				args.put("tableId", field.get("relationTableId"));
				DBObject ozDbo = fieldDbc.findOne(args);
				if (ozDbo==null) {
					continue;
				}
				selectSqlBuffer.append(",TMP_"+i+"."+zdyName+" "+zdyName+",TMP_"+i+"."+zdyName+" DIC_"+field.get("dicParent")+"_"+zdyName);
				fromSqlBuffer.append(" LEFT JOIN (SELECT TP_0."+rfield.get("fieldName")+",GROUP_CONCAT(TP_0."+ozDbo.get("fieldName")+") "+zdyName);
				fromSqlBuffer.append(" FROM "+dzTable.get("tableName")+" TP_0 GROUP BY TP_0."+rfield.get("fieldName")+") TMP_"+i);
				fromSqlBuffer.append(" ON TMP_"+i+"."+rfield.get("fieldName")+"="+"T_0."+mainFieldDbo.get("fieldName"));
			} else if (fieldType==17) {//星期
				selectSqlBuffer.append(",T_0."+fieldName+" "+zdyName+",T_0."+fieldName+" XQ_"+zdyName);
			} else if (fieldType==10 || fieldType==23 || fieldType==25) {
				selectSqlBuffer.append(",DATE_FORMAT(T_0."+fieldName+",'%Y-%m-%d') "+zdyName);
			}else {
				selectSqlBuffer.append(",T_0."+fieldName+" "+zdyName);
			}
		}
		return selectSqlBuffer.append(fromSqlBuffer).toString()+" WHERE 1=1 ${WHERE} ORDER BY ID DESC";
	}
	
	/**
	 * 添加sql
	 * @param fieldList
	 * @return
	 */
	public static String appendInsertSql (DBCollection tableDbc,DBCollection fieldDbc,List<DBObject> fieldList,int tableId,long pageId) {
		StringBuffer insertSql = new StringBuffer();
		if (MyCollectionUtils.notEmpty(fieldList)) {
			StringBuffer valueSql = new StringBuffer();
			DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId", tableId));
			insertSql.append("INSERT INTO "+tableDbo.get("tableName")+" (");
			valueSql.append(" VALUES (");
			for (int i=0;i<fieldList.size();i++) {
				DBObject field = fieldList.get(i);
				int fieldId = MyNumberUtils.toInt(field.get("fieldId"));
				int tmpTableId = MyNumberUtils.toInt(field.get("tableId"));
				int fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
				String preveDataId = (String)field.get("preveDataId");//上一层的数据id
				if (fieldId<=0 || ",0,12,14,16,".indexOf(","+fieldType+",")>=0 || tmpTableId!=tableId) {//判断是否是自定义字段或者多值字段
					continue;
				}
				String valueName = (String)field.get("zdyName");//字段别名
				if (MyStringUtils.isBlank(valueName)) {
					valueName = "field_"+tableId+"_"+pageId+"_"+fieldId;
				}
				if (MyStringUtils.notBlank(preveDataId)) {
					valueName = preveDataId;
				}
				valueName = fieldType==2?("WQYEDITOR_"+valueName):valueName;//富文本字段特殊处理
				valueName = fieldType==19?("WQYPASSWORD_"+valueName):valueName;//密码字段特殊处理
				insertSql.append((valueSql.indexOf("#")>0?",":"")+field.get("fieldName"));
				valueName = "#{"+valueName+"}";
				valueSql.append((valueSql.indexOf("#")>0?",":"")+valueName);
			}
			insertSql.append(",creater,create_date)");
			valueSql.append(",#{userId},now())");
			insertSql.append(valueSql);
		}
		return insertSql.toString();
	}
	
	/**
	 * 修改sql
	 * @param fieldList
	 * @return
	 */
	public static String appendUpdateSql (DBCollection tableDbc,DBCollection fieldDbc,List<DBObject> fieldList,int tableId,long pageId) {
		StringBuffer updateSql = new StringBuffer();
		if (MyCollectionUtils.notEmpty(fieldList)) {
			DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId", tableId));
			updateSql.append("UPDATE "+tableDbo.get("tableName")+" SET ");
			for (int i=0;i<fieldList.size();i++) {
				DBObject field = fieldList.get(i);
				int fieldId = MyNumberUtils.toInt(field.get("fieldId"));
				int tmpTableId = MyNumberUtils.toInt(field.get("tmpTableId"));
				int fieldType = MyNumberUtils.toInt(field.get("fieldType"));//字段类型
				if (fieldId<=0 || ",0,12,14,16,".indexOf(","+fieldType+",")>=0 || tmpTableId!=tableId) {//判断是否是自定义字段或者多值字段
					continue;
				}
				String valueName = (String)field.get("zdyName");//字段别名
				if (MyStringUtils.isBlank(valueName)) {
					valueName = "field_"+tableId+"_"+pageId+"_"+fieldId;
				}
				valueName = fieldType==2?("WQYEDITOR_"+valueName):valueName;//富文本字段特殊处理
				valueName = fieldType==19?("WQYPASSWORD_"+valueName):valueName;//密码字段特殊处理
				updateSql.append((updateSql.indexOf("=")>0?",":"")+field.get("fieldName")+"="+valueName);
			}
			//获取主键
			DBObject args = new BasicDBObject();
			args.put("fieldType", 0);
			args.put("tableId", tableId);
			DBObject mainField = fieldDbc.findOne(args);
			updateSql.append(" ,updater=#{userId},update_date=now() ");
			updateSql.append(" WHERE "+mainField.get("fieldName")+"=#{mainId}");
		}
		return updateSql.toString();
	}
	
	/**
	 * 拼接删除sql
	 * @param tableDbc
	 * @param fieldDbc
	 * @param tableList
	 * @param tableId
	 * @param pageId
	 * @param ifBatch
	 * @return
	 */
	public static void appendDeleteSql (DBCollection tableDbc,DBCollection fieldDbc,String domId,DBObject buttonInfo) {
		List<DBObject> delReList = (List<DBObject>)buttonInfo.get("delReList");//删除关联对象信息
		StringBuffer needSelectBuffer = new StringBuffer();//删除前需查询判断sql
		StringBuffer whereBuffer = new StringBuffer();
		int tableId = MyNumberUtils.toInt(domId.split("_")[0]);
		//获取主删除对象
		DBObject mainTable = tableDbc.findOne(new BasicDBObject("tableId",tableId));
		
		//获取主删除对象的主键
		DBObject args = new BasicDBObject();
		args.put("fieldType", 0);
		args.put("tableId", tableId);
		DBObject mainField = fieldDbc.findOne(args);
		ShardedJedis jds = RedisUtil.getJedis();
		List<String> deleteSqlList = new ArrayList<String>();
		//获取有数据不可删除的表
		needSelectBuffer.append("SELECT T0."+mainField.get("fieldName")+" ID FROM "+mainTable.get("tableName")+" T0 ");
		deleteSqlList.add("DELETE FROM "+mainTable.get("tableName")+" WHERE "+mainField.get("fieldName")+" IN (#{DELETEIDS})");
		boolean ifNeedSelect = false;
		for (DBObject table : delReList) {
			int relationField = MyNumberUtils.toInt(table.get("relationFieldId"));
			int tmpTableId = MyNumberUtils.toInt(table.get("tableId"));
			int deleteType = MyNumberUtils.toInt(table.get("deleteType"));//删除类型:0有数据不可删除1联动删除2无操作
			if (deleteType==2) {//无操作
				continue;
			}
			//获取当前table对象
			DBObject args1 = new BasicDBObject();
			args1.put("tableId", tmpTableId);
			DBObject tmpTable = tableDbc.findOne(args1);
			//获取当前对象的外键字段
			args1.put("fieldId", relationField);
			DBObject tmpWjField = fieldDbc.findOne(args1);
			System.out.println(args1+"==tmpWjField="+tmpWjField);
			if (deleteType==0) {//有数据不可删除
				if (ifNeedSelect) {
					whereBuffer.append(" OR ");
				}
				ifNeedSelect = true;
				needSelectBuffer.append(" LEFT JOIN "+tmpTable.get("tableName")+" T_"+tmpTableId+" ON" +
						"  T_"+tmpTableId+"."+tmpWjField.get("fieldName")+"=T0."+mainField.get("fieldName"));
				whereBuffer.append(" T_"+tmpTableId+"."+tmpWjField.get("fieldName")+" IS NOT NULL ");
			} 
			deleteSqlList.add("DELETE FROM "+tmpTable.get("tableName")+" WHERE "+tmpWjField.get("fieldName")+" IN (#{DELETEIDS})");
		}
		needSelectBuffer.append(" WHERE T0."+mainField.get("fieldName")+" IN (#{DELETEIDS})");
		if (ifNeedSelect) {
			needSelectBuffer.append(" AND ("+whereBuffer+")");
			jds.set("deleteSelectSql_"+domId+"_"+buttonInfo.get("buttonId"), needSelectBuffer.toString());
		} else {
			jds.del("deleteSelectSql_"+domId+"_"+buttonInfo.get("buttonId"));
		}
		jds.set("deleteSql_"+domId+"_"+buttonInfo.get("buttonId"), JSON.toJSONString(deleteSqlList));
		buttonInfo.put("deleteSelectSql", needSelectBuffer.toString());
		buttonInfo.put("deleteSql", JSON.toJSONString(deleteSqlList));
		System.out.println("deleteSelectSql---"+needSelectBuffer.toString());
		System.out.println("deleteSql---"+JSON.toJSONString(deleteSqlList));
		RedisUtil.returnJedis(jds);
	}
}
