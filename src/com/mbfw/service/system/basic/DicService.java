package com.mbfw.service.system.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mbfw.servlet.InitDataServlet;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


@Service("dicService")
public class DicService {
	
	/**
	 * 添加数据字典
	 * @param args
	 * @return
	 */
	public int addDic (Map<String, Object> args) {
		int dicType = MyNumberUtils.toInt(args.get("dicType"));//类型
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		long parentId = MyNumberUtils.toLong(args.get("parentDicId"),0);
		String parentDicName = (String)args.get("cparentDicName");
		List<DBObject> dicList = new ArrayList<DBObject>();
		if (dicType == 0) {//添加的是父级
			parentDicName = (String)args.get("parentDicName");
			DBObject parentDbo = new BasicDBObject();
			parentId = MongoDbFileUtil.getIncr("dictionary");
			parentDbo.put("DIC_ID", parentId);//自增id
			parentDbo.put("DIC_NAME", parentDicName);
			parentDbo.put("DIC_PARENT_ID", 0);
			dicList.add(parentDbo);
		}
		Integer[] rgDicIds = (Integer[])args.get("rgDicId");
		String[] dicNames = (String[])args.get("dicName");
		if (rgDicIds!=null && rgDicIds.length>0) {
			for (int i=0;i<rgDicIds.length;i++) {
				DBObject dicDbo = new BasicDBObject();
				dicDbo.put("DIC_ID", MongoDbFileUtil.getIncr("dictionary"));
				dicDbo.put("RG_DIC_ID", rgDicIds[i]);
				dicDbo.put("DIC_NAME", dicNames[i]);
				dicDbo.put("DIC_PARENT_ID", parentId);
				dicDbo.put("DIC_PARENT_NAME", parentDicName);
				dicDbo.put("DIC_ORDER", i);
				dicList.add(dicDbo);
			}
		}
		dicDbc.insert(dicList);
		return 1;
	}
	
	/**
	 * 修改数据字典
	 * @param args
	 * @return
	 */
	public int updateDic (Map<String, Object> args) {
		int parentId = MyNumberUtils.toInt(args.get("parentId"));//父级字典修改的id
		DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		int parentDicId = MyNumberUtils.toInt(args.get("parentDicId"));
		String parentDicName = (String)args.get("cparentDicName");
		if (parentId > 0) {//父级修改
			parentDicName = (String)args.get("parentDicName");
			parentDicId = parentId;
			DBObject parentDbo = dicDbc.findOne(new BasicDBObject("DIC_ID", parentId));
			parentDbo.put("DIC_NAME", parentDicName);
			dicDbc.save(parentDbo);
		}
		
		//添加子级
		Integer[] dicIds = (Integer[])args.get("dicId");
		Integer[] rgDicIds = (Integer[])args.get("rgDicId");
		String[] dicNames = (String[])args.get("dicName");
		Integer[] orders = (Integer[])args.get("order");
		if (rgDicIds!=null && rgDicIds.length>0) {
			for (int i=0;i<rgDicIds.length;i++) {
				DBObject dicDbo = dicDbc.findOne(new BasicDBObject("DIC_ID", dicIds[i]));
				if (dicDbo==null) {
					dicDbo = new BasicDBObject();
					dicDbo.put("DIC_ID", MongoDbFileUtil.getIncr("dictionary"));
				}
				dicDbo.put("RG_DIC_ID", rgDicIds[i]);
				dicDbo.put("DIC_NAME", dicNames[i]);
				dicDbo.put("DIC_PARENT_ID", parentDicId);
				dicDbo.put("DIC_PARENT_NAME", parentDicName);
				dicDbo.put("DIC_ORDER", i);
				dicDbc.save(dicDbo);
			}
		}
		//删除数据字典
		String removeIds = (String)args.get("removeIds");
		if (MyStringUtils.notBlank(removeIds)) {
			String[] rmIds = removeIds.split(",");
			for (String rmId : rmIds) {
				dicDbc.remove(new BasicDBObject("DIC_ID", MyNumberUtils.toInt(rmId)));
			}
		}
		return 1;
	}

}
