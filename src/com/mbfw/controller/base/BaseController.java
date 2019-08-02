package com.mbfw.controller.base;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import com.mbfw.entity.Page;
import com.mbfw.util.Const;
import com.mbfw.util.Logger;
import com.mbfw.util.MongoDbFileUtil;
import com.mbfw.util.MyCollectionUtils;
import com.mbfw.util.MyDateUtils;
import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;
import com.mbfw.util.PageData;
import com.mbfw.util.Tools;
import com.mbfw.util.UuidUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;

public class BaseController {

	@Autowired
	public ServletContext servletContext;
	protected Logger logger = Logger.getLogger(this.getClass());

	private static final long serialVersionUID = 6357869213649815390L;

	/**
	 * 得到PageData
	 */
	public PageData getPageData() {
		return new PageData(this.getRequest());
	}

	/**
	 * 得到ModelAndView
	 */
	public ModelAndView getModelAndView() {
		return new ModelAndView();
	}

	/**
	 * 得到request对象
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
	
	/**
	 * 得到request对象
	 */
	public HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();
		response.setCharacterEncoding("UTF-8");
		return response;
	}

	/**
	 * 得到32位的uuid
	 * 
	 * @return
	 */
	public String get32UUID() {

		return UuidUtil.get32UUID();
	}

	/**
	 * 得到分页列表的信息
	 */
	public Page getPage() {

		return new Page();
	}

	public static void logBefore(Logger logger, String interfaceName) {
		logger.info("");
		logger.info("start");
		logger.info(interfaceName);
	}

	public static void logAfter(Logger logger) {
		logger.info("end");
		logger.info("");
	}
	
	/**
	 * response输出
	 * @param content
	 * 		内容
	 * @param contentType
	 * 		内容类型(eg:text/plain)
	 */
	private void render(String content,String contentType) {
		if (content==null) {
			return;
		}
		if (contentType!=null) {
			try {
				contentType+=";charset=utf-8";
				HttpServletResponse response = getResponse();
				if (response!=null&&!response.isCommitted()) {
					response.resetBuffer();
					noCache();
					response.setContentType(contentType);
					PrintWriter out = response.getWriter();
					if (out!=null) {
						out.print(content);
						out.flush();
					}
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * response不缓存
	 */
	protected void noCache(){
		HttpServletResponse response = getResponse();
		if (response!=null) {
			response.setDateHeader("Expires", 0L);
		    response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "no-cache");
		}
	}
	
	/**
	 * response输出纯文本
	 * @param text
	 * 		文本内容
	 */
	protected void renderText(Object text) {
		render(text==null?"":text.toString(), "text/plain");
	}
	/**
	 * response输出html
	 * @param html
	 * 		html内容
	 */
	protected void renderHtml(Object html) {
		render(html==null?"":html.toString(), "text/html");
	}
	/**
	 * response输出xml
	 * @param xml
	 * 		xml内容
	 */
	protected void renderXml(Object xml) {
		render(xml==null?"":xml.toString(),"text/xml");
	}
	/**
	 * response输出json(<code>String</code>)
	 * @param jsonStr
	 * 		json内容
	 */
	protected void renderJson(Object jsonStr) {
		/*
		 * text/json其实是根本不存在的
		 * text/javascript在有些时候客户端处理起来会有歧义
		 */
		render(jsonStr==null?"":jsonStr.toString(),"application/json");
	}
	
	/**
	 * 获得parameter为name的值
	 * @param name String
	 * @return
	 */
	protected String getParam(String name) {
		if(name==null){
			return null;
		}
		HttpServletRequest request = getRequest();
		if (request!=null) {
			String parameter = request.getParameter(name);
			if (parameter!=null) {
				return parameter.indexOf("♆")>=0?parameter.split("♆")[0]:parameter;
			}
		}
		return null;
	}
	/**
	 * 获得parameter为name的值数组
	 * @param name String
	 * @return
	 * 		如果没有值返回一个空数组,这样在调用返回时就不用判断是否为null
	 */
	protected String[] getParamValues(String name) {
		String[] empty=new String[0];
		if(name==null){
			return empty;
		}
		HttpServletRequest request = getRequest();
		if (request!=null) {
			String[] parameterValues = request.getParameterValues(name);
			if (parameterValues!=null) {
				if (parameterValues.length>0 && parameterValues[0].indexOf("♆")>=0) {
					parameterValues = parameterValues[0].split("♆");
				}
				return parameterValues;
			}
		}
		return empty;
	}
	
	/**
	 * 获得parameter为name的值数组
	 * @param name String
	 * @return
	 * 		如果没有值返回一个空数组,这样在调用返回时就不用判断是否为null
	 */
	public static String[] getParamValueArr(HttpServletRequest request,String name) {
		String[] empty=new String[0];
		if(name==null){
			return empty;
		}
		if (request!=null) {
			String[] parameterValues = request.getParameterValues(name);
			if (parameterValues!=null) {
				if (parameterValues.length>0 && parameterValues[0].indexOf("♆")>=0) {
					parameterValues = parameterValues[0].split("♆");
				}
				return parameterValues;
			}
		}
		return empty;
	}
	
	 /**
     * 获得parameter为name的值数组
     * @param name String
     * @return
     *      如果没有值返回一个空数组,这样在调用返回时就不用判断是否为null
     */
    protected Integer[] getParamInteger(String name) {
        Integer[] empty=new Integer[0];
        if(name==null){
            return empty;
        }
        HttpServletRequest request = getRequest();
        if (request!=null) {
            String[] parameterValues = request.getParameterValues(name);
            if (parameterValues!=null) {
                Integer[] intArr = new Integer[parameterValues.length];
                for (int i = 0; i < parameterValues.length; i++) {
                    intArr[i] = MyNumberUtils.toInt(parameterValues[i], 10, 0);
                }
                return intArr;
            }
        }
        return empty;
    }
    
    
    /**
     * 获得parameter为name的值数组
     * @param name String
     * @return
     *      如果没有值返回一个空数组,这样在调用返回时就不用判断是否为null
     */
    protected Long[] getParamLong(String name) {
        Long[] empty=new Long[0];
        if(name==null){
            return empty;
        }
        HttpServletRequest request = getRequest();
        if (request!=null) {
            String[] parameterValues = request.getParameterValues(name);
            if (parameterValues!=null) {
                Long[] intArr = new Long[parameterValues.length];
                for (int i = 0; i < parameterValues.length; i++) {
                    if (!MyNumberUtils.can2Number(parameterValues[i])) {
                        intArr[i]=-1l; 
                    }
                    intArr[i] = Long.parseLong(parameterValues[i]==""?"-1":parameterValues[i]);
                }
                return intArr;
            }
        }
        return empty;
    }
    
	
	/**
     * 获取参数中的所有值的方法                 lmf
     * @param args          存放值的map
     * @param paramName     页面的name,param中的name
     * @param argsName      map中的key名
     * @param classType     需要转换的数据类型
     * @param nullPut       boolean 空值是否放入map中
     */
    @SuppressWarnings("unchecked")
    public void getAllParam(Map<String, Object> args, String[] paramName, String[] argsName, Class[] classType, boolean nullPut) {
        if(paramName.length == argsName.length && argsName.length == classType.length) {
            for (int i = 0; i < paramName.length; i++) {
                String paramN = paramName[i];
                String argsN = argsName[i];
                Class classT = classType[i];
                if(String[].class == classT) {
                    String[] tmpVs = getParamValues(paramN);
                    if(nullPut || tmpVs != null) {
                        args.put(argsN, tmpVs);
                    }
                }else if(Integer[].class == classT) {
                    Integer[] tmpVs = getParamInteger(paramN);
                    if(nullPut || tmpVs != null) {
                        args.put(argsN, tmpVs);
                    }
                }else if(Long[].class == classT) {
                    Long[] tmpVs = getParamLong(paramN);
                    if(nullPut || tmpVs != null) {
                        args.put(argsN, tmpVs);
                    }
                } else if(Integer.class == classT) {
                    //如果 不能转为 interger 则为 null
                    Integer tmp = MyNumberUtils.toInt(getParam(paramN), 10);
                    if(nullPut || tmp != null) {
                        args.put(argsN, tmp);
                    }
                } else if(Long.class == classT) {
                    //如果 不能转为 long 则为 -1
                    
                    String t = MyStringUtils.isBlank(getParam(paramN))?"-1":getParam(paramN);
                    Long tmp = MyNumberUtils.can2Number(t)?Long.parseLong(t):-1;
                    if(nullPut || tmp != null) {
                        args.put(argsN, tmp);
                    }
                } else if(Date.class == classT) {
                    String trueArgsN = "";
                    String format = "";
                    int index = argsN.indexOf(":");
                    if(index >= 0) {
                        trueArgsN = argsN.substring(0, index);
                        format = argsN.substring(index+1);
                    }
                    if(MyStringUtils.isBlank(format)) {
                        trueArgsN = argsN;
                        format = "yyyy-MM-dd";
                    }
                    Date tmp = MyDateUtils.string2Date(getParam(paramN), format);
                    if(nullPut || tmp != null) {
                        if(trueArgsN.startsWith("start")) {
                            args.put(trueArgsN, MyDateUtils.date2Daybreak(tmp));
                        } else if(trueArgsN.startsWith("end")) {
                            args.put(trueArgsN, MyDateUtils.date2MidNight(tmp));
                        } else {
                            args.put(trueArgsN, tmp);
                        }
                    }
                } else if(String.class == classT) {
                    
                    String tmp =getParam(paramN);
                    if(nullPut || MyStringUtils.notBlank(tmp)) {
                        args.put(argsN, MyStringUtils.notBlank(tmp)?tmp.trim():null);
                    }
                } else if(Float.class == classT) {
                    String tmp =getParam(paramN);
                    if(nullPut || MyNumberUtils.can2Number(tmp)) {
                        args.put(argsN, MyNumberUtils.toFloat(tmp, 0.0f));
                    }
                }
                
            }
        }
    }
    
    /**
	 * 获取所有的对象类型
	 * @return
	 */
	public static List<Map<String, Object>> getAllObjectType () {
		List<Map<String, Object>> types = new ArrayList<Map<String,Object>>();
		Map<String, Object> type = new HashMap<String, Object>();
		type.put("name","普通");
		type.put("id",0);
		types.add(type);
		
		Map<String, Object> type1 = new HashMap<String, Object>();
		type1.put("name","管理维度");
		type1.put("id",1);
		types.add(type1);
		
		Map<String, Object> type2 = new HashMap<String, Object>();
		type2.put("name","帐户表");
		type2.put("id",2);
		types.add(type2);
		
		Map<String, Object> type3 = new HashMap<String, Object>();
		type3.put("name","角色表");
		type3.put("id",3);
		types.add(type3);
		
		Map<String, Object> type4 = new HashMap<String, Object>();
		type4.put("name","账户管理维度");
		type4.put("id",4);
		types.add(type4);
		
		Map<String, Object> type5 = new HashMap<String, Object>();
		type5.put("name","多值选择项");
		type5.put("id",5);
		types.add(type5);
		
		Map<String, Object> type6 = new HashMap<String, Object>();
		type6.put("name","多文档");
		type6.put("id",6);
		types.add(type6);
		return types;
	}
	
	/**
	 * 获取所有的字段类型
	 * @return
	 */
	public static List<Map<String, Object>> getAllFieldType (String noShowTypes) {
		List<Map<String, Object>> types = new ArrayList<Map<String,Object>>();
		noShowTypes = ","+noShowTypes+",";
		if (noShowTypes.indexOf(",0,")<0) {
			Map<String, Object> type = new HashMap<String, Object>();
			type.put("name","主键");
			type.put("id",0);
			types.add(type);
		}
		
		if (noShowTypes.indexOf(",1,")<0) {
			Map<String, Object> type0 = new HashMap<String, Object>();
			type0.put("name","字符");
			type0.put("id",1);
			types.add(type0);
		}
		
		if (noShowTypes.indexOf(",2,")<0) {
			Map<String, Object> type1 = new HashMap<String, Object>();
			type1.put("name","富文本");
			type1.put("id",2);
			types.add(type1);
		}
		
		if (noShowTypes.indexOf(",3,")<0) {
			Map<String, Object> type2 = new HashMap<String, Object>();
			type2.put("name","身份证");
			type2.put("id",3);
			types.add(type2);
		}
		
		if (noShowTypes.indexOf(",4,")<0) {
			Map<String, Object> type3 = new HashMap<String, Object>();
			type3.put("name","Email");
			type3.put("id",4);
			types.add(type3);
		}
		
		if (noShowTypes.indexOf(",5,")<0) {
			Map<String, Object> type4 = new HashMap<String, Object>();
			type4.put("name","手机");
			type4.put("id",5);
			types.add(type4);
		}
		
		if (noShowTypes.indexOf(",6,")<0) {
			Map<String, Object> type5 = new HashMap<String, Object>();
			type5.put("name","电话");
			type5.put("id",6);
			types.add(type5);
		}
		
		if (noShowTypes.indexOf(",7,")<0) {
			Map<String, Object> type6 = new HashMap<String, Object>();
			type6.put("name","规则生成");
			type6.put("id",7);
			types.add(type6);
		}
		
		if (noShowTypes.indexOf(",8,")<0) {
			Map<String, Object> type7 = new HashMap<String, Object>();
			type7.put("name","名称");
			type7.put("id",8);
			types.add(type7);
		}
		
		if (noShowTypes.indexOf(",9,")<0) {
			Map<String, Object> type8 = new HashMap<String, Object>();
			type8.put("name","数据");
			type8.put("id",9);
			types.add(type8);
		}
		
		if (noShowTypes.indexOf(",10,")<0) {
			Map<String, Object> type9 = new HashMap<String, Object>();
			type9.put("name","日期");
			type9.put("id",10);
			types.add(type9);
		}
		
		if (noShowTypes.indexOf(",11,")<0) {
			Map<String, Object> type10 = new HashMap<String, Object>();
			type10.put("name","单值选择项");
			type10.put("id",11);
			types.add(type10);
		}
		
		if (noShowTypes.indexOf(",12,")<0) {
			Map<String, Object> type11 = new HashMap<String, Object>();
			type11.put("name","多值选择项");
			type11.put("id",12);
			types.add(type11);
		}
		
		if (noShowTypes.indexOf(",13,")<0) {
			Map<String, Object> type12 = new HashMap<String, Object>();
			type12.put("name","单文档");
			type12.put("id",13);
			types.add(type12);
		}
		
		if (noShowTypes.indexOf(",14,")<0) {
			Map<String, Object> type13 = new HashMap<String, Object>();
			type13.put("name","多文档");
			type13.put("id",14);
			types.add(type13);
		}
		
		if (noShowTypes.indexOf(",15,")<0) {
			Map<String, Object> type14 = new HashMap<String, Object>();
			type14.put("name","内部对象");
			type14.put("id",15);
			types.add(type14);
		}
		
		if (noShowTypes.indexOf(",16,")<0) {
			Map<String, Object> type15 = new HashMap<String, Object>();
			type15.put("name","内部对象多值");
			type15.put("id",16);
			types.add(type15);
		}
		
		if (noShowTypes.indexOf(",17,")<0) {
			Map<String, Object> type16 = new HashMap<String, Object>();
			type16.put("name","星期");
			type16.put("id",17);
			types.add(type16);
		}
		
		if (noShowTypes.indexOf(",18,")<0) {
			Map<String, Object> type17 = new HashMap<String, Object>();
			type17.put("name","用户名");
			type17.put("id",18);
			types.add(type17);
		}
		
		if (noShowTypes.indexOf(",19,")<0) {
			Map<String, Object> type18 = new HashMap<String, Object>();
			type18.put("name","密码");
			type18.put("id",19);
			types.add(type18);
		}
		
		if (noShowTypes.indexOf(",20,")<0) {
			Map<String, Object> type19 = new HashMap<String, Object>();
			type19.put("name","排序");
			type19.put("id",20);
			types.add(type19);
		}
		
		if (noShowTypes.indexOf(",21,")<0) {
			Map<String, Object> type20 = new HashMap<String, Object>();
			type20.put("name","父级引用");
			type20.put("id",21);
			types.add(type20);
		}
		
		if (noShowTypes.indexOf(",22,")<0) {
			Map<String, Object> type21 = new HashMap<String, Object>();
			type21.put("name","创建人");
			type21.put("id",22);
			types.add(type21);
		}
		
		if (noShowTypes.indexOf(",23,")<0) {
			Map<String, Object> type22 = new HashMap<String, Object>();
			type22.put("name","创建日期");
			type22.put("id",23);
			types.add(type22);
		}
		
		if (noShowTypes.indexOf(",24,")<0) {
			Map<String, Object> type23 = new HashMap<String, Object>();
			type23.put("name","修改人");
			type23.put("id",24);
			types.add(type23);
		}
		if (noShowTypes.indexOf(",25,")<0) {
			Map<String, Object> type24 = new HashMap<String, Object>();
			type24.put("name","修改日期");
			type24.put("id",25);
			types.add(type24);
		}
		return types;
	}
	
	/**
	 * 获得原始的session<br>
	 * <strong>注意:有些操作需要判断此session是否还有效(即:没有invalidated)</strong>
	 * @return
	 */
	protected Session getSession() {
		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser!=null) {
			Session session = currentUser.getSession();
			return session;
		}
		return null;
	}
	
	/**
	 * 获取所有子集字典
	 * @param parentId
	 * @return
	 */
	public List<DBObject> getAllDicChild (Object parentId) {
		if (servletContext==null) {
			return new ArrayList<DBObject>();
		}
		Map<Object,List<DBObject>> childMap= (Map<Object,List<DBObject>>)servletContext.getAttribute("childDicTree");
		System.out.println("====childMap==="+childMap);
		return childMap==null?new ArrayList<DBObject>():childMap.get(parentId);
	}
	
	/**
	 * 根据子集id获取名称
	 * @param parentId
	 * @return
	 */
	public String getDicNameById (Object dicId) {
		if (servletContext==null) {
			return "";
		}
		Map<Object,String> dicMap=(Map<Object,String>)servletContext.getAttribute("chidDic");
		return dicMap==null?"":dicMap.get(dicId);
	}
	
	
	/**
	 * 获取父级数据字典
	 * @return
	 */
	public List<DBObject> getDicParentList () {
		/*DB db = MongoDbFileUtil.getDb();
		DBCollection dicDbc = db.getCollection("dictionary");
		DBCursor dicCur = dicDbc.find(new BasicDBObject("DIC_PARENT_ID",0));
		return dicCur==null?new ArrayList<DBObject>():dicCur.toArray();*/
		if (servletContext==null) {
			return new ArrayList<DBObject>();
		}
		List<DBObject> parentList= (List<DBObject>)servletContext.getAttribute("dicParent");
		return parentList==null?new ArrayList<DBObject>():parentList;
	}
	
	
	/**
	 * 替换sql中的数据字典值
	 * @param listData
	 * @return
	 */
	public List<Map<String, Object>> replaceListData(List<Map<String, Object>> listData) {
		if (MyCollectionUtils.notEmpty(listData)) {
			for (Map<String, Object> data : listData) {
				Iterator<String> it = data.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();//键
					Object value = data.get(key);//值
					if (key.startsWith("DIC_") && value!=null) {
						value = getDicNameById(key.split("_")[1]+"_"+value);
						data.put(key, value);//重新赋值
					}
				}
			}
		}
		return listData;
	}
	
	/**
	 * 替换页面内容
	 * @param pageHtml
	 * @return
	 */
	public String replacePageContent (String pageHtml) {
		Pattern fieldPattern = Pattern.compile("val_\\{[a-zA-Z0-9_-]+\\}");
        Matcher fieldMatch =  fieldPattern.matcher(pageHtml);
        while(fieldMatch.find()) {
            String group = fieldMatch.group();
            pageHtml = pageHtml.replace(group, "");
        }
        //替换数据字典值
        fieldPattern = Pattern.compile("DIC_\\{[a-zA-Z0-9_-]+\\}");
        fieldMatch =  fieldPattern.matcher(pageHtml);
        while(fieldMatch.find()) {
            String group = fieldMatch.group();
            String tmpGroup = group.replace("DIC_{", "").replace("}","");
            List<DBObject> dicList = getAllDicChild(tmpGroup);
            StringBuffer dicSb = new StringBuffer();
            if (MyCollectionUtils.notEmpty(dicList)){
	            for (DBObject dic : dicList) {
	            	dicSb.append("<option value='"+dic.get("value")+"'>"+dic.get("name")+"</option>");
	            }
            }
            pageHtml = pageHtml.replace(group, dicSb.toString());
        }
		return pageHtml;
	}
}
