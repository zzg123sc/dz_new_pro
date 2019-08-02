package com.mbfw.util;


import java.util.regex.Pattern;

public class MyNumberUtils{
	/**
	 * 正则:int数字(-2147483648~2147483647)
	 */
	private static final String intRegex="^[-]?((0\\d{0,10})|([1-9]\\d{0,9})|([0-9a-fA-F]{1,7}))$";
	/**
	 * 正则:long数字(-9223372036854775808~9223372036854775807)
	 */
	private static final String longRegex="^[-]?((0\\d{0,19})|([1-9]\\d{0,18})|([0-9a-fA-F]{1,15}))$";
	/**
	 * 正则:数字(包括整数&小数)
	 */
	private static final String numberRegex="^[-]?(0|([1-9]\\d*)|((0\\.)\\d+)|(([1-9](\\d*)\\.)\\d+))$";
	/**
	 * 判断参数obj是否可以转换为int数字
	 * @param obj Object
	 * @return
	 */
	public static boolean can2Int(Object obj){
		if(obj==null){
			return false;
		}
		String str_1=obj.toString();
		if(MyStringUtils.notBlank(str_1)){
			return Pattern.matches(numberRegex,str_1);
		}
		return false;
	}
	/**
	 * 判断参数obj是否可以转换为整数数字
	 * @param obj Object
	 * @return
	 */
	public static boolean can2Long(Object obj){
		if(obj==null){
			return false;
		}
		String str_1=obj.toString();
		if(MyStringUtils.notBlank(str_1)){
			return Pattern.matches(longRegex,str_1);
		}
		return false;
	}
	/**
	 * 判断参数obj是否可以转换为数字(整数,小数)
	 * @param obj Object
	 * @return
	 */
	public static boolean can2Number(Object obj){
		if(obj==null){
			return false;
		}
		String str_1=obj.toString();
		if(MyStringUtils.notBlank(str_1)){
			return Pattern.matches(numberRegex,str_1);
		}
		return false;
	}
	/**
	 * 
	 * 对象转为integer对象
	 * @param obj
	 * @param radix
	 * 		进制(2,8,10,16等等)
	 * @return
	 * 		如果失败返回null
	 */
	public static Integer toInt(Object obj,int radix){
		if (can2Int(obj)) {
			if(radix<=0){
				radix=10;
			}
			if (obj instanceof Number) {
				return ((Number)obj).intValue();
			}
			try{
				return Integer.parseInt(obj.toString(),radix);
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}
	public static Integer toInt(Object obj,int radix, int defaultValue){
	    if (can2Int(obj)) {
			if(radix<=0){
				radix=10;
			}
			if (obj instanceof Number) {
				return ((Number)obj).intValue();
			}
			try{
				return Integer.parseInt(obj.toString(),radix);
			}catch(Exception e){
				return defaultValue;
			}
		}
		return defaultValue;
	}
	public static Integer toInt(Object obj){
		return toInt(obj, 10, 0);
	}
	/**
	 * 对象转为long对象
	 * @param obj
	 * @param radix
	 * 		进制(2,8,10,16等等)
	 * @return
	 * 		如果失败返回null
	 */
	public static Long toLong(Object obj,int radix){
		if (can2Long(obj)) {
			if(radix<=0){
				radix=10;
			}
			if (obj instanceof Number) {
				return ((Number)obj).longValue();
			}
			try{
				return Long.parseLong(obj.toString(),radix);
			}catch(Exception e){
				return 0l;
			}
		}
		return 0l;
	}
	/**
	 * 对象转为float对象
	 * @param obj
	 * @return
	 * 		如果失败返回null
	 */
	public static Float toFloat(Object obj){
		if(obj!=null){
			if (obj instanceof Number) {
				return ((Number)obj).floatValue();
			}
			try{
				return Float.parseFloat(obj.toString());
			}catch(Exception e){
				return 0f;
			}
		}
		return 0f;
	}
	public static Float toFloat(Object obj, Float defaultValue){
		if(obj!=null){
			if (obj instanceof Number) {
				return ((Number)obj).floatValue();
			}
			try{
				return Float.parseFloat(obj.toString());
			}catch(Exception e){
				return defaultValue;
			}
		}
		return defaultValue;
	}
	/**
	 * 对象转为double对象
	 * @param obj
	 * @return
	 * 		如果失败返回null
	 */
	public static Double toDouble(Object obj){
		if(obj!=null){
			if (obj instanceof Number) {
				return ((Number)obj).doubleValue();
			}
			try{
				return Double.parseDouble(obj.toString());
			}catch(Exception e){
				return 0d;
			}
		}
		return 0d;
	}
	/**
	 * obj转换为Number
	 * @param obj
	 * @return
	 */
	public static Number toNumber(Object obj){
		if (obj==null) {
			return null;
		}
		if (obj instanceof Number) {
			return (Number)obj;
		}
		try {
			return (Number)obj;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 四舍五入
	 * @param n1
	 * 		数字1
	 * @param n2
	 * 		数字2
	 * @param count
	 * 		小数点后精确到几位
	 * @param flag
	 * 		(n1在前,n2在后)1:加法,2:减法,3:乘法,4:除法,5:求余
	 * @return
	 */
	public static double sswr(Number n1,Number n2,
			int count,int flag){
		if(n1!=null&&n2!=null){
			if(count<0){
				count=0;
			}
			double d1=n1.doubleValue();
			double d2=n2.doubleValue();
			Double d=null;
			switch(flag){
			case 1:
				d=d1+d2;
				break;
			case 2:
				d=d1-d2;
				break;
			case 3:
				d=d1*d2;
				break;
			case 4:
				if(d2!=0){
					d=d1/d2;
				}
				break;
			case 5:
				if(d2!=0){
					d=d1%d2;
				}
				break;
			default:
				break;
			}
			if(d!=null){
				double tmp=Math.pow(10.0,count);
				double result=Math.round(d.doubleValue()*tmp)/tmp;
				return result;
			}
		}
		return 0.0;
	}
	
	/**
	 * 数字金额大写转换，思想先写个完整的然后将如零拾替换成零
	 * 要用到正则表达式
	 */
	public static String digitUppercase(double n){
		String fraction[] = {"角", "分"};
	    String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	    String unit[][] = {{"元", "万", "亿"},
	                 {"", "拾", "佰", "仟"}};

	    String head = n < 0? "负": "";
	    n = Math.abs(n);
	    
	    String s = "";
	    for (int i = 0; i < fraction.length; i++) {
	        s += (digit[(int)(Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
	    }
	    if(s.length()<1){
		    s = "整";	
	    }
	    int integerPart = (int)Math.floor(n);

	    for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
	        String p ="";
	        for (int j = 0; j < unit[1].length && n > 0; j++) {
	            p = digit[integerPart%10]+unit[1][j] + p;
	            integerPart = integerPart/10;
	        }
	        s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
	    }
	    return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}
	
	
	/**
    * 去除数值类型小数点后的0和小数点
    * 例如  5000.00  转换后返回  5000
    * @param s
    * @return
    */
   public static String subZeroAndDot(String s){   
	   if(s.indexOf(".") > 0){   
		   s = s.replaceAll("0+?$", "");//去掉多余的0   
		   s = s.replaceAll("[.]$", "");//如最后一位是.则去掉   
	   }   
	   return s;   
   }
   
   //判断是否是数字字符串
   public static boolean ifNumStr (String s) {
	   boolean ifNumStr = true;
	   if (MyStringUtils.notBlank(s)) {
		   String[] arr = s.split(",");
		   for (String a : arr) {
			   if (!MyNumberUtils.can2Int(a)) {
				   ifNumStr = false;
			   }
		   }
	   } else {
		   ifNumStr = false;
	   }
	   return ifNumStr;
   }
}

