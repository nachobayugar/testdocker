package utils;

import static utils.NumberUtils.eval;
import static utils.NumberUtils.valueOf;

import java.awt.AlphaComposite;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random; 

public class NumberUtils {

	public static Number valueOf(Object value){
		String className = "Integer";
		try{
			value = Integer.valueOf(value.toString());
			return (Number) value;
		}
		catch(Exception e){
			try{
				value = Long.valueOf(value.toString());
				className = "Long";
				return (Number) value;
			}
			catch(Exception e1){
				try{
					Float floatValue = Float.valueOf(value.toString());
					Double doubleValue = Double.valueOf(value.toString());
					if(doubleValue>floatValue){
						throw new Exception("It's at least a Double");
					}
					return (Number) value;
				}
				catch(Exception e2){
					try{
						value = Double.valueOf(value.toString());
						className = "Double";
						return (Number) value;
					}
					catch(Exception e3){
						return (Number) value;
					}
					
				}
			}
		}
	}
	
	public static Number valueOf(String type, Object value){
		if(value==null){
			return null;
		}
		value = value.toString();
		Map<String, String> classesNames = new LinkedHashMap();
		
		classesNames.put("LONG", "Long");
		classesNames.put("FLOAT", "Float");
		classesNames.put("INTEGER", "Integer");
		classesNames.put("DOUBLE", "Double");
		
		String className =  "java.lang." + classesNames.get(type.toUpperCase());
		try{
			Class<?> clazz = Class.forName(className);
			String methodName = "valueOf";
			Method m = clazz.getMethod(methodName, new Class[] {String.class});
			Object result = m.invoke(null, value);
			return (Number) result;
		}
		catch(Exception exc){
			System.out.println("Exception instantiating class " + className + "; " + exc);
		}
		return null;
	}
	
	public static Number getMin(String type){
		if("LONG".equalsIgnoreCase(type)){
			return Long.MIN_VALUE;
		}
		else if("DOUBLE".equalsIgnoreCase(type)){
			return Double.MIN_VALUE;
		}
		else if("FLOAT".equalsIgnoreCase(type)){
			return Float.MIN_VALUE;
		}
		else{
			return Integer.MIN_VALUE;
		}
	}
	
	public static Number getMax(String type){
		if("LONG".equalsIgnoreCase(type)){
			return Long.MAX_VALUE;
		}
		else if("DOUBLE".equalsIgnoreCase(type)){
			return Double.MAX_VALUE;
		}
		else if("FLOAT".equalsIgnoreCase(type)){
			return Float.MAX_VALUE;
		}
		else{
			return Integer.MAX_VALUE;
		}
	}
	
	public static Object getRandomNumber(String type){
		Random random = new Random();
		
		if(type.equalsIgnoreCase("Float")){
			return random.nextFloat();
		}
		if(type.equalsIgnoreCase("Long")){
			return random.nextLong();
		}
		if(type.equalsIgnoreCase("Integer")){
			return random.nextInt();
		}
		if(type.equalsIgnoreCase("Double")){
			return random.nextDouble();
		}
		return 0;
	}
	
	public static Number divide(Number first, Number valueToAdd, String type){
		return applyOperation(first, valueToAdd, type, "divide");
	}
	
	public static Number add(Number first, Number valueToAdd, String type){
		return applyOperation(first, valueToAdd, type, "add");
	}
	
	public static Number multiplyBy(Number first, Number valueToAdd, String type){
		return applyOperation(first, valueToAdd, type, "multiply");
	}
	
	public static Number applyOperation(Number first, Number valueToAdd, String type, String methodName){
		
		String firstType = getType(first);
		
		Number result = first;
		BigInteger maxInt = new BigInteger(String.valueOf(Integer.MAX_VALUE));
		BigInteger minInt = new BigInteger(String.valueOf(Integer.MIN_VALUE));
		BigInteger maxLong = new BigInteger(String.valueOf(Long.MAX_VALUE));
		BigInteger minLong = new BigInteger(String.valueOf(Long.MIN_VALUE));
		
		BigDecimal maxFloat = new BigDecimal(String.valueOf(Float.MAX_VALUE));
		BigDecimal minFloat = new BigDecimal(String.valueOf(Float.MIN_VALUE));
		BigDecimal maxDouble = new BigDecimal(String.valueOf(Double.MAX_VALUE));
		BigDecimal minDouble = new BigDecimal(String.valueOf(Double.MIN_VALUE));
		
		Locale l = new Locale("en", "US");
		BigDecimal f = new BigDecimal(first.toString());
		BigDecimal v = new BigDecimal(valueToAdd.toString());
		Class[] cArray = {BigDecimal.class};
		BigDecimal finalRes;
		try{
			Method m = f.getClass().getMethod(methodName, cArray);
			finalRes = (BigDecimal) m.invoke(f, v);
		}
		catch(Exception e){
			System.out.println("Exception invoking method: " + methodName);
			return null;
		}
		
		if("LONG".equalsIgnoreCase(type)){
			BigInteger rounded = new BigInteger(finalRes.setScale(0, BigDecimal.ROUND_UP).toString());
			if(rounded.compareTo(maxLong) == 1 || rounded.compareTo(minLong)==-1 ){
				return rounded;
			}
			else{
				return rounded.longValue();
			}
		}
		else if("INTEGER".equalsIgnoreCase(type)){
			BigInteger rounded = new BigInteger(finalRes.setScale(0, BigDecimal.ROUND_UP).toString());
			if(rounded.compareTo(maxLong) == 1 || rounded.compareTo(minLong)==-1 ){
				return rounded;
			}
			else if(rounded.compareTo(maxInt) == 1 || rounded.compareTo(minInt)==-1 ){
				return rounded.longValue();
			}
			else{
				return rounded.intValue();
			}	
		}
		else if("FLOAT".equalsIgnoreCase(type)){
			if(finalRes.compareTo(maxDouble) == 1 || finalRes.compareTo(minDouble)==-1 ){
				return finalRes;
			}
			else if(finalRes.compareTo(maxFloat) == 1 || finalRes.compareTo(minFloat)==-1 ){
				return Double.valueOf(String.format(l, "%.50f", finalRes));
			}
			else{
				return finalRes.floatValue();
			}
			
		}
		else if("DOUBLE".equalsIgnoreCase(type)){
			if(finalRes.compareTo(maxDouble) == 1 || finalRes.compareTo(minDouble)==-1 ){
				return finalRes;
			}
			else {
				return Double.valueOf(String.format(l, "%.50f", finalRes));
			}
		}
		else{
			return finalRes;
		}
}

	public static Number eval(String expression, String type){
		
		javax.script.ScriptEngineManager mgr = new javax.script.ScriptEngineManager();
		javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
		Object result = null;
		try{
			result = engine.eval(expression);
		}
		catch(Exception e){
			System.out.println("eval Exception " + e);
		}
		if(result!=null){
			if("LONG".equalsIgnoreCase(type)){
				Double rounded = (double) Math.round(((Double) result)); 
				return rounded.longValue();
			}
			else if("INTEGER".equalsIgnoreCase(type)){
				Double rounded = (double) Math.round(((Double) result)); 
				int intValue = rounded.intValue();
				if(rounded < Double.valueOf(intValue) || rounded > Double.valueOf(intValue)){
					return rounded.longValue();
				}
				else{
					return rounded.intValue();
				}
			}
			else if("FLOAT".equalsIgnoreCase(type)){
				Double rounded = (double) Math.round(((Double) result)); 
				Float intValue = rounded.floatValue();
				if(rounded < Double.valueOf(intValue) || rounded > Double.valueOf(intValue)){
					return rounded;
				}
				else{
					return intValue;
				}
			}
			else{
				return (Number) result;
			}
		}
		else{
			return null;
		}
	}
	
	public static boolean equalsThan(Number firstNumber, Number secondNumber){
		try{
			return new BigDecimal(firstNumber.toString()).compareTo( new BigDecimal(secondNumber.toString()) ) == 0; 
		}
		catch(Exception e){
			System.out.println("equalsThan Exception " + e);
		}
		return false;
	}
	
	public static boolean lowerOrEqualsThan(Number firstNumber, Number secondNumber){
		try{
			return new BigDecimal(firstNumber.toString()).compareTo( new BigDecimal(secondNumber.toString()) ) <= 0; 
		}
		catch(Exception e){
			System.out.println("lowerOrEqualsThan Exception " + e);
		}
		return false;
	}
	
	public static boolean lowerThan(Number firstNumber, Number secondNumber){
		try{
			return new BigDecimal(firstNumber.toString()).compareTo( new BigDecimal(secondNumber.toString()) ) == -1; 
		}
		catch(Exception e){
			System.out.println("lowerThan Exception " + e);
		}
		return false;
	}
	
	public static boolean higherOrEqualsThan(Number firstNumber, Number secondNumber){
		try{
			return new BigDecimal(firstNumber.toString()).compareTo( new BigDecimal(secondNumber.toString()) ) >= 0; 
		}
		catch(Exception e){
			System.out.println("higherOrEqualsThan Exception " + e);
		}
		return false;
	}
	
	public static boolean higherThan(Number firstNumber, Number secondNumber){
		try{
			return new BigDecimal(firstNumber.toString()).compareTo( new BigDecimal(secondNumber.toString()) ) == 1; 
		}
		catch(Exception e){
			System.out.println("higherThan Exception " + e);
		}
		return false;
	}
	
	public static Number getNextNumber(Object number){
		return getNextNumber((Number) number); 
	}
	
	public static Number getNextNumber(Number number){
		String className = "Integer";
		try{
			Integer result = Integer.valueOf(number.toString());
		}
		catch(Exception e){
			try{
				Long result = Long.valueOf(number.toString());
				className = "Long";
			}
			catch(Exception e1){
				try{
					Float result = Float.valueOf(number.toString());
					Float finalResult = java.lang.Math.nextUp(result);
					if(finalResult.equals(Float.POSITIVE_INFINITY)){
						throw new Exception("it's at least a Double");
					}
					return finalResult;
				}
				catch(Exception e2){
					try{
						Double result = Double.valueOf(number.toString());
						Double finalResult = java.lang.Math.nextUp(result);
						if(finalResult.equals(Double.POSITIVE_INFINITY)){
							throw new Exception("it's at least a BigDecimal");
						}
						return finalResult;
					}
					catch(Exception e3){
						BigDecimal result = new BigDecimal(number.toString());
						BigDecimal finalResult = result.add(new BigDecimal("1"));
						return finalResult;
					}
					
				}
			}
		}
		//Object returnValue = eval(number + "+1", className);
		Object returnValue = add(number, 1, className);
		return (Number) returnValue;
	}
	
	public static Number getPreviousNumber(Object number){
		return getPreviousNumber((Number) number); 
	}
	
	public static Number getPreviousNumber(Number number){
		String className = getType(number);
		
		//Object returnValue = eval(number + "-1", className);
		Object returnValue = add(number, -1, className);
		return (Number) returnValue;
	}
	
	public static boolean isNegative(Object value){
		javax.script.ScriptEngineManager mgr = new javax.script.ScriptEngineManager();
		javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
		
		try{
			return Boolean.parseBoolean(engine.eval(value.toString() + "<0").toString());
		}
		catch(Exception e){
			return false;
		}
		
	}
	
	private static String getType(Number number){
		String className = "Integer";
		try{
			Integer result = Integer.valueOf(number.toString());
		}
		catch(Exception e){
			try{
				Long result = Long.valueOf(number.toString());
				className = "Long";
			}
			catch(Exception e1){
				try{
					Float result = Float.valueOf(number.toString());
					className = "Float";
					if(!result.equals(Double.valueOf(number.toString()))){
						className = "Double";
					}
				}
				catch(Exception e2){
					Double result = Double.valueOf(number.toString());
					if(!result.equals(Double.POSITIVE_INFINITY) && !result.equals(Double.NEGATIVE_INFINITY)){
						className = "Double";
					}
					else{
						className = "BigDecimal";
					}
					
				}
			}
		}
		return className;
	}
}
