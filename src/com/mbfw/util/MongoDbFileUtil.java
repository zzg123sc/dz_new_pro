package com.mbfw.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

public class MongoDbFileUtil {

    private static String USER = "ysBaoBiao";
    private static String PASSWORD = "ysBaoBiao";
    private static String DB_NAME = "acg_file";
    private static String SORCE_DB_NAME = "acg_file";      //源db
    
    private static Mongo mongo;
    private static boolean loginSuccess = true;
    static {
        try {
            InputStream in =MongoDbFileUtil.class.getResourceAsStream("/mongoConfig.properties");
            Properties p = new Properties();
            p.load(in);
            String serverUrl = p.getProperty("url");
            int port = MyNumberUtils.toInt(p.getProperty("port"));
            if(port <= 0) {
            	port = 27017;
            }
            DB_NAME = p.getProperty("name").trim();
            SORCE_DB_NAME = p.getProperty("sourceName");
            USER = p.getProperty("USER");
            PASSWORD = p.getProperty("PASSWORD");
            if (MyStringUtils.notBlank(USER) && MyStringUtils.notBlank(PASSWORD)) {
            	ServerAddress sa = new ServerAddress(serverUrl, port);  
            	List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>(); 
            	mongoCredentialList.add(MongoCredential.createCredential(USER, DB_NAME, PASSWORD.toCharArray()));
            	mongo = new MongoClient(sa, mongoCredentialList);
            	loginSuccess = true;
            } else {
            	mongo = new Mongo(serverUrl, port);
            }
            MongoOptions opt = mongo.getMongoOptions(); 
            opt.connectionsPerHost = 500; 
            opt.threadsAllowedToBlockForConnectionMultiplier = 500; 
//            mongo.getMongoClientOptions().builder()
//            .socketKeepAlive(true) 
//            .connectionsPerHost(500) 
//            .threadsAllowedToBlockForConnectionMultiplier(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static GridFS getMongoDb() throws Exception {
    	DB myDB = mongo.getDB(DB_NAME); // 数据库名称
        if(!loginSuccess) {
        	System.out.println("mongo用户密码验证没有通过");
        	return null;
        }
        return new GridFS(myDB);
    }
    
    public static DB getDb() {
    	DB myDB = mongo.getDB(DB_NAME);// 数据库名称
    	if(!loginSuccess) {
			System.out.println("mongo用户密码验证没有通过");
        	return null;
    	}
    	return myDB;
    }

	public static DB getSourceDb() {
    	DB myDB = mongo.getDB(DB_NAME); // 数据库名称
        if(!loginSuccess) {
            //loginSuccess = myDB.authenticate(USER, PASSWORD.toCharArray());
        }
        if(!loginSuccess) {
			System.out.println("mongo用户密码验证没有通过");
        	return null;
    	}
        return myDB;
    }
	
	/**
	 * 获取dbc对象
	 * @param dbcName
	 * @return
	 */
	public static DBCollection getDbcCollection (String dbcName) {
		DB db = getDb();
		return db.getCollection(dbcName);
	}

    
    /**
     * changeName 去除重名
     * 
     * @Description 去除重名
     * @param filename
     * @param zipNameList
     * @return 
     * String
     * @see
     */
    public static String changeName(String srcFilename, List<String> zipNameList) {
        if(zipNameList.contains(srcFilename)) {
            String filename;
            String type;
            int tmpIndex = srcFilename.lastIndexOf(".");
            if(tmpIndex >= 0) {
                filename = srcFilename.substring(0, tmpIndex);
                type = srcFilename.substring(tmpIndex);
            } else {
                filename = srcFilename;
                type = "";
            }
            Pattern cpPattern = Pattern.compile(".*\\(\\d+\\)$");
            Matcher cmMch = cpPattern.matcher(filename);
            if(cmMch.find()) {
                int lastIndexOf = filename.lastIndexOf("(");
                int now = MyNumberUtils.toInt(filename.substring(lastIndexOf+1, filename.length()-1), 10);
                filename = filename.substring(0, lastIndexOf) + "(" + (now+1) + ")";
            } else {
                filename = filename + "(1)";
            }
            srcFilename = filename + type;
        } else {
            zipNameList.add(srcFilename);
            return srcFilename;
        }
        return changeName(srcFilename, zipNameList);
    }
    
    /**
     * 写入文件
     * 
     * @param fileName
     */
    public static Map<String, String> saveFile(Object file, Map<String, String> map) {
        try {
            if(file != null) {
                GridFS gridFS = getMongoDb();
                GridFSFile mongofile;
                if(File.class.isAssignableFrom(file.getClass())) {
                    mongofile = gridFS.createFile((File)file);
                } else {
                    mongofile = gridFS.createFile((InputStream)file);
                }
                if(map != null) {
                    Set<String> set = map.keySet();
                    for (String string : set) {
                        mongofile.put(string, map.get(string));
                    }
                }
                //文件是否真正有用
                mongofile.put("ifUseful", 0);
                mongofile.save();
                Map<String, String> resultMap = fsFileToMap(mongofile);
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> fsFileToMap(GridFSFile mongofile) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("_id", mongofile.getId()!=null?mongofile.getId().toString():null);
        resultMap.put("fileName", mongofile.getFilename()!=null?mongofile.getFilename().toString():null);
        resultMap.put("contentType", mongofile.get("contentType") + "");
        resultMap.put("md5", mongofile.getMD5()!=null?mongofile.getMD5().toString():null);
        resultMap.put("chunkSize", mongofile.getChunkSize() + "");
        resultMap.put("hwType", mongofile.get("hwType") + "");
        resultMap.put("stuId", mongofile.get("stuId") + "");
        resultMap.put("length", mongofile.getLength() + "");
        resultMap.put("uploadDate", MyDateUtils.date2String(mongofile.getUploadDate(), "yyyy-MM-dd HH:mm:ss"));
        return resultMap;
    }
    /**
     * saveFileRId 写入文件  返回 _id
     * 
     * @Description 
     * @param file
     * @param map
     * @return 
     * String
     * @see
     */
    public static String saveFileRId(Object file, Map<String, String> map) {
        try {
            Map<String, String> mongofile = saveFile(file, map);
            if(mongofile != null) {
                map.put("length", mongofile.get("length")); 
                return mongofile.get("_id").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * saveFile 批量上传返回文件
     * 
     * @Description 
     * @param fileList
     * @param map
     * @return 
     * List<GridFSFile>
     * @see
     */
    public static List<Map<String, String>> saveFile(List<Object> fileList, Map<String, String> map) {
        try {
            List<Map<String, String>> resultFileList = new ArrayList<Map<String, String>>();
            for (Object file : fileList) {
                Map<String, String> mongofile = saveFile(file, map);
                resultFileList.add(mongofile);
            }
            return resultFileList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * saveFileRId 批量上传返回文件 _id  
     * 
     * @Description 批量上传返回文件 _id  
     * @param fileList
     * @param map
     * @return 
     * List<String>
     * @see
     */
    public static List<String> saveFileRId(List<Object> fileList, Map<String, String> map) {
        try {
            List<String> resultFileList = new ArrayList<String>();
            for (Object file : fileList) {
                String str = saveFileRId(file, map);
                resultFileList.add(str);
            }
            return resultFileList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件
     * 
     * @param fileName
     * @throws Exception 
     */
    public static List<GridFSDBFile> findFiles(Map<String, Object> map) {
        // List<GridFSDBFile> list =gridFS.find(fileName);
        GridFS gridFS;
        List<GridFSDBFile> list = new ArrayList<GridFSDBFile>();
        try {
            gridFS = getMongoDb();
            DBObject query = new BasicDBObject();
            boolean tmpFlag = true;
            if(map != null) {
                Set<String> set =  map.keySet();
                for (String string : set) {
                    Object value;
                    System.err.println(string);
                    if("_id".equals(string)) {
                        GridFSDBFile gf = gridFS.find(new ObjectId(map.get(string)+""));
                        list.add(gf);
                        tmpFlag = false;
                        break;
                    } else if("chunkSize".equals(string)) {
                        value = MyNumberUtils.toLong(map.get(string), 10);
                    } else if("length".equals(string)) {
                        value = MyNumberUtils.toLong(map.get(string), 10);
                    } else if("uploadDate".equals(string)) {
                        value = MyDateUtils.string2Date(map.get(string) + "", "yyyy-MM-dd HH-mm-ss");
                    } else {
                        value = map.get(string);
                    }
                    query.put(string, value);
                }
            }
            if(tmpFlag) {
                list = gridFS.find(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         
        return list;
    }
    
    /**
     * fileMapListFiles 读取文件信息 返回list<Map>
     * 
     * @Description 读取文件信息 返回list<Map>
     * @param map
     * @return
     *  _id：主键
        fileName：文件名
        contentType：类型
        md5：MD5
        chunkSize：chunkSize
        length：长度
        uploadDate：上传时间
        hwType：作业类型 5142：源作业    5143：完成作业  5144：批改后作业
     * List<Map<String,String>>
     * @see
     */
    public static List<Map<String, String>> fileMapListFiles(Map<String, Object> map) {
        List<GridFSDBFile> list = findFiles(map);
        return fsListToMapList(list);
    }
    
    private static List<Map<String, String>> fsListToMapList(List<GridFSDBFile> list) {
        List<Map<String, String>> mapList = new ArrayList<Map<String,String>>();
        if(list != null) {
            for (GridFSDBFile mongofile : list) {
                Map<String, String> resultMap = fsFileToMap(mongofile);
                mapList.add(resultMap);
            }
        }
        return mapList;
    }
    
    /**
     * updateUseType 修改文件 是否为正式文件的状态    ifUseful
     * 
     * @Description 修改文件 是否为正式文件的状态
     * @param map 
     * void
     * @see
     */
    public static void updateUseType(Map<String, Object> map, int flag, int hwId) {
        List<GridFSDBFile> list = findFiles(map);
        for (GridFSDBFile gridFSDBFile : list) {
            gridFSDBFile.put("ifUseful", flag);
            if(hwId != 0) {
                gridFSDBFile.put("hwId", hwId);
            }
            gridFSDBFile.save();
        }
    }
    
    //mongdb  分页查询方法
    public  static  List<DBObject> find(DBObject query, DBObject sort, int start, int limit,DBCollection dt) {
        DBCursor cur;
        if (query != null) {
            cur = dt.find(query);
        } else {
            cur = dt.find();
        }
        if (sort != null) {
            cur.sort(sort);
        }
        if (start == 0) {
            cur.batchSize(limit);
        } else {
            cur.skip(start).limit(limit);
        }
        List<DBObject> list = new ArrayList<DBObject>();
        if (cur != null) {
            list = cur.toArray();
        }
        return list;
    }
    
    /**
     * findOneByCollName 根据collection  name及查询条件 返回一个查询出的对象
     * 
     * @Description 根据collection  name及查询条件 返回一个查询出的对象
     * @param query
     * @param collName
     * @return DBObject
     * @see
     */
    public static DBObject findOneByCollName(DBObject query, String collName) {
    	DB db = getDb();
    	DBCollection dbColl = db.getCollection(collName);
    	return dbColl.findOne(query);
    }
    
    /**
     * getIncr 
     * 
     * @Description 
     * @param key
     * @return long
     * @see
     */
    public static long getIncr(String key) {
        DB db = getDb();
        DBCollection ids = db.getCollection("sequence_name");
        DBObject query = new BasicDBObject("name", key);
        if(ids.findOne(query) == null) {
        	DBObject tmpAdd = new BasicDBObject("name", key);
        	tmpAdd.put("id", 1);
        	ids.save(tmpAdd);
        }
        DBObject update = new BasicDBObject("$inc", new BasicDBObject(new BasicDBObject("id", 1)));
        DBObject newsIdObj = ids.findAndModify(query, update);
        return MyNumberUtils.toLong(newsIdObj.get("id"), 10);
    }
    
    public static void initIncr(String key, long id) {
    	DB db = getDb();
        DBCollection ids = db.getCollection("sequence_name");
        DBObject query = new BasicDBObject("name", key);
        DBObject obj = ids.findOne(query);
        if(obj == null) {
        	obj = new BasicDBObject("name", key);
        } 
        obj.put("id", id);
        ids.save(obj);
    }
    
    /**
     * findList 从mongodb中查询List信息
     * 从mongodb中查询List信息TODO{功能详细描述}
     * @param queryDbo
     * @param coll
     * @return List<DBObject>
     * @see
     */
    public static List<DBObject> findList(DBObject queryDbo, DBCollection coll) {
    	DBCursor cur = coll.find(queryDbo);
    	if(cur != null) {
    		return cur.toArray();
    	} else {
    		return new ArrayList<DBObject>();
    	}
    }
}

