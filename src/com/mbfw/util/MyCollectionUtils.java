package com.mbfw.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * collection工具类
 * @author lulichao
 * @date   Apr 22, 2012 6:13:06 PM
 *
 */
@SuppressWarnings("unchecked")
public class MyCollectionUtils {
	private static final String childKey="children";
	/**
	 * 得到map中指定key的value并转换为特定类型<br>
	 * (如果您不确定返回的类型,则 clazz 设置为 {@link Object} 即可)
	 * @param <T>
	 * @param map
	 * @param key
	 * @param clazz
	 * 		value对应的类型
	 * @return
	 */
	public static <T> T getFromMap(Map map,Object key,Class<T> clazz){
		if (map!=null) {
			Object o = map.get(key);
			if (o==null) {
				return null;
			}
			if (clazz.isAssignableFrom(o.getClass())) {
				return (T)o;
			}
			return null;
		}
		return null;
	}
	public static <T> T getFromMap(Map map,Object key,Object defaultVal,Class<T> clazz){
		T obj = getFromMap(map, key, clazz);
		if (obj==null) {
			if (defaultVal!=null) {
				if (clazz.isAssignableFrom(defaultVal.getClass())) {
					return (T)defaultVal;
				}
			}
			return null;
		}
		return obj;
	}
	/**
	 * 判断集合是否为空(=null或size<=0)
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection){
		return collection==null||collection.isEmpty();
	}
	/**
	 * 判断集合是否不为空(!=null且size<=0)
	 * @param collection
	 * @return
	 */
	public static boolean notEmpty(Collection<?> collection){
		return !isEmpty(collection);
	}
	/**
	 * list去重(顺序不变)
	 * @param list
	 */
	public static void removeDuplicateWithOrder(List list) {
		if (!isEmpty(list)) {
			Set set = new HashSet();
			List newList = new ArrayList();
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (set.add(element)){
					newList.add(element);
				}
			}
			set.clear();
			set=null;
			list.clear();
			list.addAll(newList);
		}
	}
	public static Object getFromCollection(Collection<?> col,int index){
		if (col!=null&&col.size()>index&&index>-1) {
			return col.toArray()[index];
		}
		return null;
	}
	public static <T> T getFromCollection(Collection<?> col,int index, Class<T> clazz){
		Object obj = getFromCollection(col, index);
		if (obj!=null) {
			if (clazz.isAssignableFrom(obj.getClass())) {
				return (T)obj;
			}
		}
		return null;
	}
	/**
	 * 类似于js数组的join方法
	 * @param col
	 * @param sepa
	 * @return
	 */
	public static String joinCollection(Collection<?> col,String sepa){
		if (!isEmpty(col)) {
			Iterator<?> ite = col.iterator();
			if (ite!=null) {
				StringBuffer result=new StringBuffer();
				int i=0;
				while(ite.hasNext()){
					Object next = ite.next();
					result.append((i==0?"":sepa)+next);
					i=1;
				}
				return result.toString();
			}
		}else if (col!=null&&col.size()==0) {
			return "";
		}
		return null;
	}
	
	/**
     * 类似于js数组的join方法  Collection<?> col 是String[2] 的数组  两元素：分割
     * @param col
     * @param sepa
     * @return
     */
    public static String joinCollection2(Collection<?> col,String sepa){
        if (!isEmpty(col)) {
            Iterator<?> ite = col.iterator();
            if (ite!=null) {
                StringBuffer result=new StringBuffer();
                int i=0;
                while(ite.hasNext()){
                    String[] next = (String[])ite.next();
                    String str = "";
                    for (int j = 0; j < next.length; j++) {
                         str = str + next[j]+":";
                    }   
                    if(str.lastIndexOf(":")>0){
                        str = str.substring(0, str.length()-1);
                    }
                    result.append((i==0?"":sepa)+str);                        
                    i=1;
                }
                return result.toString();
            }
        }else if (col!=null&&col.size()==0) {
            return "";
        }
        return null;
    }
}
