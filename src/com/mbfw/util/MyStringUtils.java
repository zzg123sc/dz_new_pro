package com.mbfw.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import com.alibaba.fastjson.JSON;


/**
 * @author Administrator
 *
 */
public class MyStringUtils{
    
    /**
     * arrayToString 数组转字符
     * 
     * @Description 数组转字符
     * @param arr[] 需转换的数组对象
     * @param joinStr 以某字符拼接
     * @param ifNullJoin 是否将空值拼接
     * @return String 
     * @see
     */
    public static String arrayToString(Object[] arr,String joinStr,boolean ifNullJoin){
        String returnStr = "";
        if(isBlank(joinStr)){
            joinStr = ",";
        }
        if(arr != null){
            for (Object object : arr) {
                if(!ifNullJoin && object=="" && object == null){
                    continue;
                }
                returnStr += (returnStr!=""?joinStr+object:object);
            }
        }
        return returnStr;
    }
    
	/**
	 * 判断参数string是否不为空(null||length==0||全部空格)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:非空<br>
	 * 		false:空
	 */
	public static boolean notBlank(String str){
		return !isBlank(str);
	}
	/**
	 * 判断参数string是否为空(null||length==0||全部空格)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:空<br>
	 * 		false:非空
	 */
	public static boolean isBlank(String str){
		if(isEmpty(str)){
			return true;
		}
		int strLen=str.length();
		for(int i=0;i<strLen;i++){
			if(!Character.isWhitespace(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	/**
	 * 判断参数string是否不为空(null||length==0)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:非空<br>
	 * 		false:空
	 */
	public static boolean notEmpty(String str){
		return !isEmpty(str);
	}
	/**
	 * 判断参数string是否为空(null||length==0)
	 * @param str
	 * 		字符串参数
	 * @return
	 * 		true:空<br>
	 * 		false:非空
	 */
	public static boolean isEmpty(String str){
		return (str==null)||(str.length()==0);
	}
	/**
	 * 将参数str中的换行符替换为指定的内容
	 * @param str
	 * @param replace
	 * @return
	 */
	public static String replaceBr(String str,String replace){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"\r\n|\n",replace);
	}
	/**
	 * 去掉字符串中的空格(包括中文空格)
	 * @param str
	 * @return
	 */
	public static String delKongge(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str.replace((char)12288,' '),"\\s+","");
	}
	/**
	 * 去掉空行(去掉连续的空行)
	 * @param str
	 * @return
	 */
	public static String deleteEmptyLine(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"(\r?\n(\\s*\r?\n)+)","\r\n");
	}
	/**
	 * 变为一行
	 * @param str
	 * @return
	 */
	public static String toSingleLine(String str){
		if(isEmpty(str)){
			return str;
		}
		return replaceAll(str,"[\n\t\r]+","");
	}
	/**
	 * 将字符串escape,保证html脚本中不会出现非法字符(htmlEscape)
	 * @param str
	 * @return
	 */
	public static String escape2Html(Object str){
		if(str!=null){
			return HtmlUtils.htmlEscapeHex(str.toString());
		}
		return null;
	}
	/**
	 * 将字符串escape,保证javascript脚本中不会出现非法字符(javaScriptEscape)
	 * @param str
	 * @return
	 */
	public static String escape2Js(Object str){
		if(str!=null){
			return JavaScriptUtils.javaScriptEscape(str.toString());
		}
		return null;
	}
	/**
	 * 将字符串escape,保证html或javascript脚本中不会出现非法字符(htmlEscape&&javaScriptEscape)
	 * @param str
	 * @return
	 */
	public static String escape2HtmlJs(Object str){
		if(str!=null){
			return escape2Html(escape2Js(str));
		}
		return null;
	}
	/**
	 * 替换字符串中匹配regex的内容为replace
	 * @param str
	 * 		要替换的字符串
	 * @param regex
	 * 		匹配
	 * @param replace
	 * 		如果为null则在此方法变为空字符串:""
	 * @return
	 */
	public static String replaceAll(String str,String regex,String replace){
		if(str==null||regex==null){
			return str;
		}
		if(replace==null){
			replace="";
		}
		return str.replaceAll(regex,replace);
	}
	public static String replace(String str,String regex,String replace){
		if(str==null||regex==null){
			return str;
		}
		if(replace==null){
			replace="";
		}
		if(str.indexOf(regex) >= 0) {
			str = str.replace(regex,replace);
			return replace(str, regex, replace);
		} else {
			return str;
		}
	}
	
	
	public static String dbColumnCase(String src){
		//oracle数据库查询返回的列名都是大写
		if (src!=null) {
			return src.toUpperCase();
		}
		return null;
	}
	/**
	 * 判断括号"()"是否匹配,并不会判断排列顺序是否正确
	 * 
	 * @param text
	 *            要判断的文本
	 * @return 如果匹配返回true,否则返回false
	 */
	public static boolean isBracketCanPartnership(String text) {
		if (text == null
				|| (getAppearCount(text, '(') != getAppearCount(text, ')'))) {
			return false;
		}
		return true;
	}
	/**
	 * 得到一个字符在另一个字符串中出现的次数
	 * @param text	文本
	 * @param ch    字符
	 */
	public static int getAppearCount(String text, char ch) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
		}
		return count;
	}
	
	/**
	 * 判断在字符串中 两个字符的出现次数是否相等
	 * @param text  字符串
	 * @param ch	字符1
	 * @param ch1   字符2
	 * @return
	 */
	public static boolean compareAppearCount(String text, char ch, char ch1) {
		int count = 0;
		int count1 = 0;
		for (int i = 0; i < text.length(); i++) {
			count = (text.charAt(i) == ch) ? count + 1 : count;
			count1 = (text.charAt(i) == ch1) ? count1 + 1 : count1;
		}
		if(count == count1) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * (字符串前后没有逗号的情况下)将字符串前后加上逗号
	 * @param str
	 * @return
	 * 		"1,2,3"=>",1,2,3,"
	 */
	public static String commaBeginEnd(String str){
		if (str!=null) {
			StringBuffer buff=new StringBuffer();
			if (!str.startsWith(",")) {
				buff.append(",");
			}
			buff.append(str);
			if (!str.endsWith(",")) {
				buff.append(",");
			}
			return buff.toString();
		}
		return null;
	}
	/**
	 * 将sql变为1行<br>
	 * 1.sql过滤规则:<br>
	 * 	 1).换行替换为1个空格<br>
	 * 	 2).连续的1个以上空格替换为1个空格<br>
	 * 	 3).去掉:{左右小括号,逗号,等号}后紧跟的空格<br>
	 * @param sql
	 * @return
	 */
	public static String getLineSql(String sql) {
		if (sql==null) {
			return "";
		}
		return sql.
			//将所有空格(包括中文空格),tab之类替换为英文空格
			replaceAll("\\s+", " ").
			//换行替换为1个空格,连续的1个以上空格替换为1个空格
			replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ")
			//左括号前后的空格
			.replaceAll("\\(\\s+", "(")
			.replaceAll("\\s+\\(", "(")
			//右括号前后的空格
			.replaceAll("\\s+\\)", ")")
			.replaceAll("\\)\\s+", ")")
			//逗号前后的空格
			.replaceAll(",\\s+", ",")
			.replaceAll("\\s+,", ",")
			//等号前后的空格
			.replaceAll("\\s+=", "=")
			.replaceAll("=\\s+", "=")
			//转为小写
			/*.toLowerCase()*/;
	}
	/**
	 * 去掉url前面的"/",保证最后url不以"/"开头
	 * @param url
	 * @return
	 */
	public static String urlNoStartSlash(String url) {
		if (url!=null) {
			while(url.startsWith("/")){
				url=url.substring(1);
			}
		}
		return url;
	}

	/**
	 * 根据传入字符串 获得  转为驼峰后的 bean名
	 * @param s
	 * @return
	 */
	public static String getClassName( String s) {
		String[] ss = s.split("_");
		String result = "";
		for (int i = 0; i < ss.length; i++) {
			result += ss[i].substring(0, 1).toUpperCase() + ss[i].substring(1);
		}
		return result;
	}
	/**
	 * 根据传入字符串 去除下划线后 转为get set方法
	 * @param s		user_id
	 * @param s2	"get" "set"
	 * @return
	 */
	public static String  getMethodName(String s , String s2){
		return s2+getClassName(s);
	}
	
	/**
	 * 
	 * @param charInterval
	 * @param input
	 * @return
	 */
	public static String intervalByChar(String charInterval, String input) {
		if(input != null) {
			char[] tempChar = input.toCharArray();
			StringBuffer result = new StringBuffer("");
			for (int i = 0; i < tempChar.length; i++) {
				char temp = tempChar[i];
				if(i != 0) {
					if(Character.isLowerCase(temp)) {
						result.append(temp);
					} else {
						result.append("_" + temp);
					}
				} else {
					result.append(temp);
				}
			}
			return result.toString();
		} else {
			return null;
		}
	}

	/**
	 * 验证传入字符串  是否  是数字
	 * @param str
	 * @return	如果是 数字 返回 true
	 * 			为空或不是数字 返回 false
	 */
	public static boolean isNumeric(String str){ 
		if(str != null) {
			Pattern pattern = Pattern.compile("^1[3458]\\d{9}$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	} 
	
	/**
	 * 验证传入字符串  是否 是 数字组合  
	 * @param str	1,2,3,4,5,6		中间不可以有数字为空
	 * @return	如果符合  返回 true
	 * 			为空 或者不符合 返回 false
	 */
	public static boolean isComposeNumeric(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("^([0-9]+,)*[0-9]+$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}
	
	/**
	 * 验证传入字符串  是否 是 数字组合  
	 * @param str	1,2,,4,5,6		中间可以有数字为空
	 * @return	如果符合  返回 true
	 * 			为空 或者不符合 返回 false
	 */
	public static boolean isComposeOrNullNumeric(String str) {
		if(str != null) {
			Pattern pattern = Pattern.compile("^([0-9]*,)*[0-9]+$"); 
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}
	
	public static String splitMailType(String mailName) {
		if(notBlank(mailName) && mailName.indexOf("@") >= 0 && mailName.indexOf(".") > mailName.indexOf("@")) {
			String[] tmpArr = mailName.split("@");
			if(tmpArr.length>1) {
				String[] tmp = tmpArr[1].split("\\.");
				if(tmp.length>0) {
					return tmp[0];
				}
			}
		}
		return null;
	}
	
	/**
	 * 将表名截取  截取 每个下划线前面2个字符
	 * @param fieldName
	 * @return
	 */
	public static StringBuffer splitTableName(String fieldName) {
		Pattern p = Pattern.compile("_", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(fieldName);
		StringBuffer last = new StringBuffer("");
		int end = 0;
		int count = 0;
		while(matcher.find()) {
			end = matcher.start();
			last.append(fieldName.substring(end-2,end) + "_");
			count ++;
		}
		if(count==0) {
			end = fieldName.length()-1;
			last.append(fieldName.substring(end-2,end) + "_");
		}
		
		
		if("".equals(last.toString())) {
			last.append(fieldName);
		} else {
			end = fieldName.length();
			last.append(fieldName.substring(end-2,end));
		}
		return last;
	}
    
    
    /**
     * 将对象转换为json字符串(用于在后台构造发往前台的json字符串),顺带替换对象里面的数据字典值
     * @param obj
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
     * @return String or null;
     */
    public static String toJSON(List<Map<String, Object>> obj, List<String> replaceK){
        return JSON.toJSONString(obj);
    }
    
    /**
     * 将List对象转换为json字符串(用于在后台构造发往前台的json字符串),顺带替换对象里面的数据字典值，顺带格式化日期
     * @param obj
     *      Map对象,
     *      replaceK 替换的key
     *      dataList 数据字典值 
     *      dateFormat 格式化日期
     * @return String or null;
     */
    public static String toJSON(List<Map<String, Object>> obj, List<String> replaceK, String dateFormat){
        return JSON.toJSONStringWithDateFormat(obj, dateFormat);
    }
    
	/**
	 * 从insert语句中获取出表名
	 * @param tempSql
	 * @return
	 */
	public static String getTableNameFromInsertSql(String tempSql) {
		Pattern p = Pattern.compile("INTO [a-zA-Z_]+[\\( ]{1}", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 6) {
			tableName = tmpName.substring(5, tmpName.length()-1);
		}
		return tableName;
	}
	
	/**
	 * 从delete语句中获取出表名
	 * @param tempSql
	 * @return
	 */
	public static String getTableNameFromDeleteSql(String tempSql) {
		Pattern p = Pattern.compile("FROM [a-zA-Z_]+[\\( ]{1}", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 6) {
			tableName = tmpName.substring(5, tmpName.length()-1);
		}
		return tableName;
	}
	
	/**
	 * 获取insert Sql中的 所有字段信息
	 * @param tempSql
	 * @return
	 */
	public static String[] getFieldNameFromInsertSql(String tempSql) {
		Pattern p = Pattern.compile("INTO [a-zA-Z_]+\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String fieldSql = "";
		if(matcher.find()) {
			fieldSql = matcher.group();
		}
		String filedStr = "";
		if(fieldSql.length() > 6) {
			Pattern p1 = Pattern.compile("\\([^\\)]+\\)", Pattern.CASE_INSENSITIVE); 
			Matcher matcher1 = p1.matcher(fieldSql);
			
			if(matcher1.find()) {
				filedStr = matcher1.group();
			}
			filedStr = filedStr.substring(1, filedStr.length()-1);
		}
		if(MyStringUtils.notBlank(filedStr)) {
			return filedStr.split(",");
		} else {
			return null;
		}
	}
    
	

	/**
	 * 从update语句中获取出表名
	 * @param tempSql
	 * @return
	 */
	public static String getTableNameFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile("UPDATE [a-zA-Z_]+ ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 6) {
			tableName = tmpName.substring(7, tmpName.length()-1);
		}
		return tableName;
	}
	/**
	 * 从update语句中获取出别名
	 * @param tempSql
	 * @return
	 */
	public static String getTableAliasNameFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile(" [a-zA-Z_]+ set", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String tmpName = "";
		if(matcher.find()) {
			tmpName = matcher.group();
		}
		String tableName = "";
		if(tmpName.length() > 4) {
			tableName = tmpName.substring(1, tmpName.length()-4);
		}
		return tableName;
	}
	
	/**
	 * 从update语句中获取出 修改的 属性名
	 * @param tempSql
	 * @return
	 */
	public static String[] getFieldNameFromUpdateSql(String tempSql) {
		List<String> resultList = new ArrayList<String>();
		Pattern p = Pattern.compile("SET [\\S]+ WHERE", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		String fieldSql = "";
		if(matcher.find()) {
			fieldSql = matcher.group();
		}
		if(fieldSql.length() > 8) {
			fieldSql = fieldSql.substring(4, fieldSql.length()-6);
		}
		if(MyStringUtils.notBlank(fieldSql)) {
			String[] fieldSqlArr = fieldSql.split(",");
			for (String string : fieldSqlArr) {
				if(MyStringUtils.notBlank(string)) {
					String[] arr = string.split("=");
					if(arr.length >= 2) {
						if(MyStringUtils.notBlank(arr[0])) {
							resultList.add(arr[0]);
						}
					}
				}
			}
			if(MyCollectionUtils.notEmpty(resultList)) {
				return MyCollectionUtils.joinCollection(resultList, ",").split(",");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	/**
	 * 获取 update sql中where后面的
	 * @param tempSql
	 * @return
	 */
	public static String getWhereSqlFromUpdateSql(String tempSql) {
		Pattern p = Pattern.compile("WHERE ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(tempSql);
		int startIndex = 0;
		while(matcher.find()) {
			startIndex = matcher.start();
			String tmp = tempSql.substring(startIndex, tempSql.length());
			boolean ifEqual = MyStringUtils.compareAppearCount(tmp, "(".toCharArray()[0], ")".toCharArray()[0]);
			if(ifEqual) {
			    break;
			}
		}
		if(startIndex > 0) {
			return tempSql.substring(startIndex, tempSql.length());
		}
		return null;
	}
	
	/**
	 * 获取tableName  根据 查询sql
	 * @param sql
	 * @return
	 */
	public static Map<String, String> getTableNameFromSelectSql(String sql) {
		Pattern p = Pattern.compile("from [a-zA-Z_]+ [\\S]+ ", Pattern.CASE_INSENSITIVE); 
		Matcher matcher = p.matcher(sql);
		String tmpName = "";
		Map<String, String> map = new HashMap<String, String>();
		if(matcher.find()) {
			tmpName = matcher.group();
			String nameAndAlias = tmpName.substring(5, tmpName.length()-1);
			String tableName = nameAndAlias;
			String alias = nameAndAlias;
			if(tableName.indexOf(" ") != -1) {
				String[] split = nameAndAlias.split(" ");
				if(split.length > 1) {
					tableName = split[0]; 
					alias = split[1];
					if(!"inner".equals(alias) && !"left".equals(alias) && !"where".equals(alias) && !"group".equals(alias) && !"order".equals(alias)) {
						map.put("alias", alias);
					}
				}
			} else if(tableName.indexOf(",") != -1) {
				tableName = nameAndAlias.split(",")[0]; 
			}
			map.put("tableName", tableName);
		} else {
			int tmpIndex = sql.indexOf("from");
			map.put("tableName", sql.substring(tmpIndex + 5));
		}
		return map;
	}
	
	/**
	 * 根据传入字符串 拼接 查询正则
	 * @param schools    1123,1143
	 * @return  (,1123,)?(,1143,)?
	 */
	public static String getRegIds(String schools) {
		if(MyStringUtils.notBlank(schools)) {
	        String[] schArr = schools.split(",");
	        StringBuffer regSchool = new StringBuffer("");
	        for (int i = 0; i < schArr.length; i++) {
	        	String schId = schArr[i];
	        	if(MyStringUtils.notBlank(schId)) {
	        		regSchool.append("(" + MyStringUtils.commaBeginEnd(schId) + "){1}");
	        		if(i!= schArr.length-1) {
	        			regSchool.append("|");
	        		}
	        	}
			}
	        return regSchool.toString();
        }
		return "";
	}
	
	/**
	 * 判断两个时间 是否冲突
	 * @param interval  12:00-15:00
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean judgeInterval(String interval, Date startTime, Date endTime) {
		boolean flag = false;
		String prevStart = interval.substring(0, 5);
		String prevEnd = interval.substring(6);
		String nextStart = MyDateUtils.date2String(startTime, "HH:mm");
		String nextEnd = MyDateUtils.date2String(endTime, "HH:mm");
		if((MyDateUtils.string2Date(nextEnd, "HH:mm").compareTo(MyDateUtils.string2Date(prevStart, "HH:mm")) != 1 
				|| MyDateUtils.string2Date(nextStart, "HH:mm").compareTo(MyDateUtils.string2Date(prevEnd, "HH:mm")) != -1)) {
			//flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
	
	public static boolean judgeInterval(Date sourceStartTime, Date sourceEndTime, Date startTime, Date endTime) {
		boolean flag = false;
		String prevStart = MyDateUtils.date2String(sourceStartTime, "HH:mm");
		String prevEnd = MyDateUtils.date2String(sourceEndTime, "HH:mm");
		String nextStart = MyDateUtils.date2String(startTime, "HH:mm");
		String nextEnd = MyDateUtils.date2String(endTime, "HH:mm");
		if((MyDateUtils.string2Date(nextEnd, "HH:mm").compareTo(MyDateUtils.string2Date(prevStart, "HH:mm")) != 1 
				|| MyDateUtils.string2Date(nextStart, "HH:mm").compareTo(MyDateUtils.string2Date(prevEnd, "HH:mm")) != -1)) {
			//flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * @param paramValue : "wm_concat(id||'!'||name)"所获得的值
	 * @return 一一对应的"id"之间“,”间隔分开,"name"用逗号间隔分开，中间加上“!”\
	 * 例如：输入："1!a,2!b";返回"1,2!a,b"
	 */
	public static String getIdAndNames(String paramValue){
		if(!MyStringUtils.notBlank(paramValue)){
			return "";
		}else{
			StringBuffer idAndName = new StringBuffer("");
			StringBuffer ids = new StringBuffer("");
			StringBuffer names = new StringBuffer("");
			String[] params = new String[]{};
			int paramsLength =0;
			if(paramValue.contains(",")){
				params = paramValue.split(",");
			}
			paramsLength = params.length;
			if(paramsLength ==1){
				return paramValue;
			}else{
				for(int i=0;i<paramsLength-1;i++){
					String temp = params[i];
					if(temp.contains("!")){
						ids.append(temp.split("!")[0]+",");
						names.append(temp.split("!")[1]+",");
					}
				}
				if(paramsLength>0){
					String temp = params[paramsLength-1];
					if(temp.contains("!")){
						ids.append(temp.split("!")[0]);
						names.append(temp.split("!")[1]);
					}
				}
			}
		    return idAndName.append(ids.append("!")+names.toString()).toString();
		}
	}
	
	/**
	 * 验证字符串是否符合邮箱格式
	 * args:要验证邮箱的字符窜
	 * true：符合邮箱的格式
	 * false：不符合邮箱的格式
	 */
	 public static boolean ifEmailTest(String args) {

		 Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
	     Matcher m = p.matcher(args);
	
	     boolean b = m.matches();
	      //true
	     return b;
     }
	 
	 public static boolean isMobileNO(String mobiles) { 
	    boolean flag = false; 
	    try { 
	        //13********* ,15********,18*********  
	        Pattern p = Pattern 
	                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"); 
	        Matcher m = p.matcher(mobiles); 
	        flag = m.matches(); 
	    } catch (Exception e) { 
	        flag = false; 
	    } 
	    return flag; 
	} 
	 
	 /**
	 * 
	 * getSqlfield 得到select 与 from 之间的字段，并按@符进行分隔
	 * @Description 功能详细描述
	 * @param sql
	 * @return String
	 * @author xqy
	 * @date Mar 9, 2015
	 * @see
	 */
	public static String getSqlfield(String sql){
        int tmpIndex = 0;
        int flag = 0;
        while(tmpIndex < sql.length()) {
            tmpIndex = sql.indexOf("," , tmpIndex+1);
            if(tmpIndex >= 0 && tmpIndex < sql.length()) {
                if(MyStringUtils.compareAppearCount(sql.substring(0, tmpIndex), '(', ')')) {
                    sql = sql.substring(0, tmpIndex) + "@" + sql.substring(tmpIndex+1);
                }
                if(tmpIndex == -1) {
                    break;
                }
            } else {
                break;
            }
            flag ++;
            if(flag > 10000) {
                break;
            }
        }
        
        return sql;
	}
	
	/**
	 * getSqlFieldByAliasName 根据别名获取到 可以作为where条件的字段名
	 * 
	 * @Description 根据别名获取到 可以作为where条件的字段名
	 * @param sql
	 * @param aliasName
	 * @return String
	 * @see
	 */
	public static String getSqlFieldByAliasName(String sql, String aliasName,Map<String,String> searchNameMap) {
		if (aliasName.startsWith("DYNAMIC_")) {
			String[] dynamics = aliasName.split(aliasName.startsWith("DYNAMIC_DIC_")?"<DYNAMIC_DIC_[a-zA-Z0-9]+>":"<DYNAMIC_[a-zA-Z0-9]+>");
			if (dynamics!=null && dynamics.length>0) {
                for (int i=0;i<dynamics.length;i++) {
                    sql = sql.substring(sql.indexOf("<"+aliasName+">")+aliasName.length()+2, sql.indexOf("</"+aliasName+">"));
                }
            } else {
                dynamics = aliasName.split("<DYNAMIC_[a-zA-Z0-9]+_[a-zA-Z0-9]+>");
            }
			return sql;
		} else if(searchNameMap == null) {
			int tmpIndex = 0;
			int flag = 0;
			while(tmpIndex < sql.length()) {
				tmpIndex = sql.indexOf("," , tmpIndex+1);
				if(tmpIndex >= 0 && tmpIndex < sql.length()) {
					if(MyStringUtils.compareAppearCount(sql.substring(0, tmpIndex), '(', ')')) {
						if(sql.substring(0, tmpIndex).trim().endsWith(" " + aliasName.toUpperCase())) {
							String newSql = sql.substring(0,tmpIndex).trim();
							return newSql.substring(newSql.lastIndexOf("@")+1, newSql.length()-aliasName.length()).trim();
						}
						sql = sql.substring(0, tmpIndex) + "@" + sql.substring(tmpIndex+1);
					}
					if(tmpIndex == -1) {
						break;
					}
				} else {
					if(sql.endsWith(" " + aliasName.toUpperCase())) {
						return sql.substring(sql.lastIndexOf("@")+1, sql.length()-aliasName.length()).trim();
					}
					break;
				}
				flag ++;
				if(flag > 10000) {
					break;
				}
			}
		} else {
			return searchNameMap.get(aliasName.toUpperCase());
		}
		return "";
	}
	
	/**
	 * 
	 * delEndTag 截取字符串最后一个字符
	 * @Description 截取字符串最后一个字符
	 * @param str
	 * @param tag
	 * @return String
	 * @author xqy
	 * @date Jan 6, 2015
	 * @see
	 */
	public static String delEndTag(String str,String tag){
	    if(str.endsWith(tag)){
	        return  str.substring(0, str.length()-1);
	    }else{
	        return str;
	    }
	}
	
	/**
     * 
     * delEndTag 截取字符串最后一个字符
     * @Description 截取字符串最后一个字符
     * @param str
     * @param tag
     * @return String
     * @author xqy
     * @date Jan 6, 2015
     * @see
     */
    public static String delBeginTag(String str,String tag){
        if(str.endsWith(tag)){
            return  str.substring(tag.length());
        }else{
            return str;
        }
    }
    
    public static String getNextDc(String str, String prevStr) {
    	int indexOfTField = str.toUpperCase().indexOf(prevStr.toUpperCase());
		if(indexOfTField > 0) {
			int i = indexOfTField + prevStr.length();
			if(" ".equals(str.substring(i, i+1))) {
				//如果为空格则 获取下一个单词 如果单词不为from 则为别名 
				String substring = str.substring(i + 1);
				int indexOf = substring.indexOf(" ");
				int dhIndexOf = substring.indexOf(",");
				if(indexOf > 0 || dhIndexOf > 0) {
					String nextDc = substring.substring(0, (indexOf>dhIndexOf?dhIndexOf:indexOf));
					if(MyStringUtils.notBlank(nextDc)) {
						if(!"FROM".equals(nextDc.toUpperCase())) {
							return nextDc;
						}
					}
				}
			}
		}
    	return "";
    }
    
    /**
     * 将list<String> 拼接为以逗号拼接的字符串
     * @param list
     * @return
     */
    public static String getList2String(List<Object> list, String opera) {
        if (!list.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            
            for (Object obj : list) {
                sb.append(obj + opera);
            }
            return sb.toString().substring(0,sb.toString().lastIndexOf(","));
        }
        return "";
    }
    
    public static void main(String[] args) {
		String ss = "<STARTDATES_2> ATM_11_1.CREATE_DATE <STARTDATES_2>";
		System.out.println(ss.substring(ss.indexOf("<STARTDATES_2>")+"<STARTDATES_2>".length(), ss.lastIndexOf("<STARTDATES_2>")));
	}
}
