package com.mbfw.service.system.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mbfw.servlet.InitDataServlet;
import com.mbfw.util.CreateTableUtils;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service("objectService")
public class ObjectService {
	
	/**
	 * 添加对象
	 */
	public void addObject (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		//添加对象
		DBCollection tableDbc = db.getCollection("table");
		//添加字段
		DBCollection fieldDbc = db.getCollection("field");
		DBObject tableDbo = new BasicDBObject();
		tableDbo.put("tableId", MongoDbFileUtil.getIncr("table"));
		tableDbo.put("tableName", ((String)args.get("tableName")).toUpperCase());
		tableDbo.put("tableCnName", args.get("tableCnName"));
		tableDbo.put("tableType", args.get("tableType"));
		tableDbo.put("ifParent", args.get("ifParent"));
		tableDbo.put("remark", args.get("remark"));
		tableDbc.save(tableDbo);
		
		//添加对象关联字段
		addTableOtherFields(true, tableDbc, fieldDbc, tableDbo);
		
		String[] fieldNames = (String[])args.get("fieldName");
		String[] fieldCnNames = (String[])args.get("fieldCnName");
		Integer[] fieldTypes = (Integer[])args.get("fieldType");
		Integer[] relationTableIds = (Integer[])args.get("relationTableId");
		Integer[] dicParents = (Integer[])args.get("dicParent");
		Integer[] ifOnlys = (Integer[])args.get("ifOnly");
		Integer[] numTypes = (Integer[])args.get("numType");
		Integer[] lengths = (Integer[])args.get("length");
		List<DBObject> fieldList = new ArrayList<DBObject>();
		int dicParent = 0;//数据字典
		DBObject dzDicdbo = null;//多值选择项字段
		for (int i=0;i<fieldNames.length;i++) {
			DBObject field = new BasicDBObject();
			field.put("fieldId", MongoDbFileUtil.getIncr("field"));
			field.put("fieldName", fieldNames[i].toUpperCase());
			field.put("fieldCnName", fieldCnNames[i]);
			field.put("fieldType", fieldTypes[i]);
			if (fieldTypes[i]==11) {//单值选择项
				dicParent = dicParents[i];
				field.put("dicParent", dicParents[i]);
			} else if (fieldTypes[i]==15) {//判断是否是内部对象
				field.put("relationTableId", relationTableIds[i]);
				//添加内部对象多值字段
				dzDicdbo = addRelationField(true, fieldDbc,tableDbc, field, tableDbo, relationTableIds[i]);
			} else if (fieldTypes[i]==9) {//数据
				field.put("numType", numTypes[i]);
			}  else if (fieldTypes[i]!=10 && fieldTypes[i]!=13 && fieldTypes[i]!=17 && fieldTypes[i]!=20) {
				field.put("length", lengths[i]);
			}
			field.put("ifOnly", ifOnlys[i]);
			field.put("tableId", tableDbo.get("tableId"));
			field.put("tableCnName", tableDbo.get("tableCnName"));
			field.put("tableName", tableDbo.get("tableName"));
			fieldList.add(field);
		}
		int tableType = MyNumberUtils.toInt(args.get("tableType"));//对象类型
		if (tableType==5) {
			dzDicdbo.put("dicParent", dicParent);
			fieldDbc.save(dzDicdbo);
		}
		fieldDbc.insert(fieldList);
		CreateTableUtils.createTable(tableDbo, fieldDbc.find(new BasicDBObject("tableId", tableDbo.get("tableId"))).toArray());
		//人员表
		if (MyNumberUtils.toInt(args.get("tableType"))==2) {
			InitDataServlet.refreshLoginSql();
		}
	}
	
	/**
	 * 修改对象
	 */
	public int updateObject (Map<String, Object> args) {
		DB db = MongoDbFileUtil.getDb();
		//添加对象
		DBCollection tableDbc = db.getCollection("table");
		//添加字段
		DBCollection fieldDbc = db.getCollection("field");
		String errInfo = "";
		DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId", args.get("tableId")));
		Object oldTableName = tableDbo.get("tableName");
		tableDbo.put("tableName", ((String)args.get("tableName")).toUpperCase());
		tableDbo.put("tableCnName", args.get("tableCnName"));
		tableDbo.put("tableType", args.get("tableType"));
		tableDbo.put("ifParent", args.get("ifParent"));
		tableDbo.put("remark", args.get("remark"));
		tableDbc.save(tableDbo);
		//修改数据库表名称
		CreateTableUtils.alertTableName(oldTableName, tableDbo.get("tableName"), tableDbo.get("tableCnName"));
		
		//删除字段
		String removeField = (String)args.get("removeField");
		if (MyStringUtils.notBlank(removeField)) {
			String[] rfIds = removeField.split(",");
			for (String rid : rfIds) {
				DBObject fieldDbo = fieldDbc.findOne(new BasicDBObject("fieldId",MyNumberUtils.toInt(rid)));
				//删除对应的字段
				CreateTableUtils.deleteTableField((String)tableDbo.get("tableName"), (String)fieldDbo.get("fieldName"));
				fieldDbc.remove(fieldDbo);
				
				//删除字段关联的内部对象多值字段
				fieldDbc.remove(new BasicDBObject("relationFieldId", MyNumberUtils.toInt(rid)));
			}
			
		}
		
		//添加对象关联字段
		addTableOtherFields(false, tableDbc, fieldDbc, tableDbo);
		
		Integer[] fieldIds = (Integer[])args.get("fieldId");
		String[] fieldNames = (String[])args.get("fieldName");
		String[] fieldCnNames = (String[])args.get("fieldCnName");
		Integer[] fieldTypes = (Integer[])args.get("fieldType");
		Integer[] relationTableIds = (Integer[])args.get("relationTableId");
		Integer[] dicParents = (Integer[])args.get("dicParent");
		Integer[] ifOnlys = (Integer[])args.get("ifOnly");
		Integer[] numTypes = (Integer[])args.get("numType");
		Integer[] lengths = (Integer[])args.get("length");
		int dicParent = 0;//数据字典
		DBObject dzDicdbo = null;//多值选择项字段
		for (int i=0;i<fieldNames.length;i++) {
			DBObject field = null;
			int fieldId = fieldIds[i];
			if (fieldId>0) {
				field = fieldDbc.findOne(new BasicDBObject("fieldId", fieldId));
			} else {
				field = new BasicDBObject();
				field.put("fieldId", MongoDbFileUtil.getIncr("field"));
			}
			field.put("fieldName", fieldNames[i].toUpperCase());
			field.put("fieldCnName", fieldCnNames[i]);
			field.put("fieldType", fieldTypes[i]);
			if (fieldTypes[i]==11) {//单值选择项
				dicParent = dicParents[i];
				field.put("dicParent", dicParents[i]);
			} else if (fieldTypes[i]==15) {//判断是否是内部对象
				field.put("relationTableId", relationTableIds[i]);
				
				//添加内部对象多值字段
				dzDicdbo = addRelationField(fieldId<=0, fieldDbc, tableDbc,field, tableDbo, relationTableIds[i]);
			} else if (fieldTypes[i]==9) {//数据
				field.put("numType", numTypes[i]);
			}  else if (fieldTypes[i]!=10 && fieldTypes[i]!=13 && fieldTypes[i]!=17 && fieldTypes[i]!=20) {
				field.put("length", lengths[i]);
			}
			field.put("ifOnly", ifOnlys[i]);
			field.put("tableId", tableDbo.get("tableId"));
			field.put("tableCnName", tableDbo.get("tableCnName"));
			field.put("tableName", tableDbo.get("tableName"));
			//修改或新增字段
			CreateTableUtils.alterTable(tableDbo, field);
			fieldDbc.save(field);
		}
		int tableType = MyNumberUtils.toInt(args.get("tableType"));//对象类型
		if (tableType==5) {
			dzDicdbo.put("dicParent", dicParent);
			fieldDbc.save(dzDicdbo);
		}
		//人员表
		if (MyNumberUtils.toInt(args.get("tableType"))==2) {
			InitDataServlet.refreshLoginSql();
		}
		return 1;
	}
	
	/**
	 * 添加内部对象多值关联字段
	 */
	public DBObject addRelationField (boolean ifNewField,DBCollection fieldDbc,DBCollection tableDbc,DBObject field,DBObject tableDbo
			,int relationTableId) {
		boolean ifAddRelation = true;//是否需要添加关联内部对象多值字段
		int tableType = MyNumberUtils.toInt(tableDbo.get("tableType"));//对象类型
		int fieldRole = 16;//内部对象多值
		if (tableType==5) {//多值选择项
			fieldRole = 12;
		} else if (tableType==6) {//多文档
			fieldRole = 14;
		}
		if (!ifNewField) {//判断是修改字段
			//判断是否已经添加过内部对象多值字段
			DBObject innerObj = fieldDbc.findOne(new BasicDBObject("relationFieldId",field.get("fieldId")));
			if (innerObj!=null) {
				//判断是否修改过内部对象
				if (MyNumberUtils.toInt(innerObj.get("relationTableId"))!=relationTableId) {
					//删掉原有的内部对象多值关联
					fieldDbc.remove(innerObj);
				} else {
					ifAddRelation = false;//没有修改过则不需要重新添加
					innerObj.put("fieldType", fieldRole);
					fieldDbc.save(innerObj);
					return innerObj;
				}
			}
		}
		DBObject innobjM = new BasicDBObject();
		if (ifAddRelation) {
			//创建内部对象多值字段
			innobjM.put("fieldId", MongoDbFileUtil.getIncr("field"));
			innobjM.put("fieldName", "FIELD_"+innobjM.get("fieldId"));
			innobjM.put("fieldCnName", tableDbo.get("tableCnName")+"_"+field.get("fieldCnName"));
			innobjM.put("fieldType", fieldRole);
			innobjM.put("relationFieldId", field.get("fieldId"));
			innobjM.put("relationTableId", tableDbo.get("tableId"));
			innobjM.put("relationTableName", tableDbo.get("tableName"));
			innobjM.put("relationTableCnName", tableDbo.get("tableCnName"));
			innobjM.put("tableId", relationTableId);
			//获取relationTable
			DBObject relationTable = tableDbc.findOne(new BasicDBObject("tableId",relationTableId));
			innobjM.put("tableCnName", relationTable.get("tableCnName"));
			innobjM.put("tableName", relationTable.get("tableName"));
			fieldDbc.save(innobjM);
		}
		return innobjM;
	}
	
	/**
	 * 添加对象关联的其他字段
	 */
	public void addTableOtherFields (boolean ifAdd, DBCollection tableDbc, DBCollection fieldDbc,DBObject tableDbo) {
		if (ifAdd) {//判断是否是添加对象
			DBObject mainField = new BasicDBObject();
			mainField.put("fieldId", MongoDbFileUtil.getIncr("field"));
			
			String tableName = (String)tableDbo.get("tableName");//主键字段名称
			String mainName = "";
			if (tableName.indexOf("_")>0) {//名称包含_则获取首字母
				String[] tms = tableName.split("_");
				for (String nm : tms) {
					mainName+=(mainName.length()>0?"_":"")+(nm.substring(0,1));
				}
			} else {
				mainName = tableName.substring(0,1);
			}
			mainField.put("fieldName", mainName+"_ID");
			mainField.put("fieldCnName", "主键");
			mainField.put("fieldType", 0);
			mainField.put("tableId", tableDbo.get("tableId"));
			mainField.put("tableCnName", tableDbo.get("tableCnName"));
			mainField.put("tableName", tableDbo.get("tableName"));
			fieldDbc.save(mainField);
			
			//对象回填主键
			tableDbo.put("mainFieldId", mainField.get("mainFieldId"));
			tableDbo.put("mainFieldName", mainName);
			tableDbc.save(tableDbo);
			
			//创建人
			DBObject innobjM = new BasicDBObject();
			innobjM.put("fieldId", MongoDbFileUtil.getIncr("field"));
			innobjM.put("fieldName", "CREATER");
			innobjM.put("fieldCnName", "创建人");
			innobjM.put("fieldType", 22);
			innobjM.put("tableId", tableDbo.get("tableId"));
			innobjM.put("tableCnName", tableDbo.get("tableCnName"));
			innobjM.put("tableName", tableDbo.get("tableName"));
			fieldDbc.save(innobjM);
			
			//创建日期
			innobjM = new BasicDBObject();
			innobjM.put("fieldId", MongoDbFileUtil.getIncr("field"));
			innobjM.put("fieldName", "CREATE_DATE");
			innobjM.put("fieldCnName", "创建时间");
			innobjM.put("fieldType", 23);
			innobjM.put("tableId", tableDbo.get("tableId"));
			innobjM.put("tableCnName", tableDbo.get("tableCnName"));
			innobjM.put("tableName", tableDbo.get("tableName"));
			fieldDbc.save(innobjM);
			
			//修改人
			innobjM = new BasicDBObject();
			innobjM.put("fieldId", MongoDbFileUtil.getIncr("field"));
			innobjM.put("fieldName", "UPDATER");
			innobjM.put("fieldCnName", "修改人");
			innobjM.put("fieldType", 24);
			innobjM.put("tableId", tableDbo.get("tableId"));
			innobjM.put("tableCnName", tableDbo.get("tableCnName"));
			innobjM.put("tableName", tableDbo.get("tableName"));
			fieldDbc.save(innobjM);
			
			//修改日期
			innobjM = new BasicDBObject();
			innobjM.put("fieldId", MongoDbFileUtil.getIncr("field"));
			innobjM.put("fieldName", "UPDATE_DATE");
			innobjM.put("fieldCnName", "修改日期");
			innobjM.put("fieldType", 25);
			innobjM.put("tableId", tableDbo.get("tableId"));
			innobjM.put("tableCnName", tableDbo.get("tableCnName"));
			innobjM.put("tableName", tableDbo.get("tableName"));
			fieldDbc.save(innobjM);
		} 
		
		int ifParent = MyNumberUtils.toInt(tableDbo.get("ifParent"));//是否支持父子级
		DBObject args = new BasicDBObject();
		args.put("tableId", tableDbo.get("tableId"));
		args.put("fieldType", 21);
		DBObject innerObj = fieldDbc.findOne(args);
		if (ifParent==1) {//判断是否已经添加过父级字段
			if (innerObj==null) {//父级字段不存在则创建
				//创建父级字段
				DBObject innobjP = new BasicDBObject();
				innobjP.put("fieldId", MongoDbFileUtil.getIncr("field"));
				innobjP.put("fieldName", "FIELD_"+MongoDbFileUtil.getIncr("field"));
				innobjP.put("fieldCnName", tableDbo.get("tableCnName")+"_父级");
				innobjP.put("fieldType", 21);
				innobjP.put("relationFieldId", tableDbo.get("mainFieldId"));
				innobjP.put("relationTableId", tableDbo.get("tableId"));
				innobjP.put("tableId", tableDbo.get("tableId"));
				innobjP.put("tableCnName", tableDbo.get("tableCnName"));
				innobjP.put("tableName", tableDbo.get("tableName"));
				//修改或新增字段
				CreateTableUtils.alterTable(tableDbo, innobjP);
				fieldDbc.save(innobjP);
			}
		} else {
			if (innerObj!=null) {
				CreateTableUtils.deleteTableField((String)tableDbo.get("tableName"), (String)innerObj.get("fieldName"));
			}
			fieldDbc.remove(args);
		}
		
	}
	
	/**
	 * 删除对象
	 * @return
	 */
	public Object deleteObject(String tableIds) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("table");
		DBCollection fieldDbc = db.getCollection("field");
		String[] tableIdArr = tableIds.split(",");
		String errorInfo = "";
		for (String tableId : tableIdArr) {
			DBObject dbo = tableDbc.findOne(new BasicDBObject("tableId", MyNumberUtils.toInt(tableId)));
			//判断是否可以删除
			if (CreateTableUtils.colHaveData((String)dbo.get("tableName"), null)) {
				errorInfo+=(errorInfo.length()>0?",":"")+dbo.get("tableCnName");
				continue;
			}
			tableDbc.remove(new BasicDBObject("tableId", MyNumberUtils.toInt(tableId)));
			fieldDbc.remove(new BasicDBObject("tableId", MyNumberUtils.toInt(tableId)));
			fieldDbc.remove(new BasicDBObject("relationTableId", MyNumberUtils.toInt(tableId)));
			//删除数据表
			CreateTableUtils.deleteTableField((String)dbo.get("tableName"), null);
		}
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("icon", errorInfo.length()==0?6:5);
		rs.put("errorInfo", errorInfo.length()==0?"删除成功！":errorInfo+"有数据，不可删除！");
		return JSON.toJSON(rs);
	}

	public static void main(String[] args) {
		DB db = MongoDbFileUtil.getDb();
		DBCollection tableDbc = db.getCollection("table");
		DBCollection fieldDbc = db.getCollection("field");
		List<Integer> typeList = new ArrayList<Integer>();
		typeList.add(12);
		typeList.add(14);
		typeList.add(15);
		typeList.add(16);
		typeList.add(21);
		DBCursor fieldCur = fieldDbc.find(new BasicDBObject("fieldType",new BasicDBObject("$in", typeList)));
		for (DBObject field : fieldCur) {
			DBObject tableDbo = tableDbc.findOne(new BasicDBObject("tableId",field.get("relationTableId"))); 
			field.put("relationTableName", tableDbo.get("tableName"));
			field.put("relationTableCnName", tableDbo.get("tableCnName"));
			fieldDbc.save(field);
		}
	}
}
