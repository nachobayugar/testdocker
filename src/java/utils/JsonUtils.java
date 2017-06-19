package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
//import org.json.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONException;
import org.codehaus.groovy.grails.web.json.*;

public class JsonUtils {
	public static List<Map<String, Object>> jsonArrayToMapList (JSONArray collection) throws JSONException {
		List <Map<String, Object>> result = new ArrayList();

        for (int i=0; i< collection.length(); i++) {
        	Object object = collection.get(i);
        	if (object instanceof JSONObject ) {
        		result.add( jsonToMap( (JSONObject)object ) );
        	} 
        	else if(object instanceof String) {
        		try{
        			JSONObject json = new JSONObject((String)object);
        			result.add(jsonToMap(json));
        		}
        		catch (Exception e){
        			System.out.println(e);
        		}
        		
        	}
        }
        
        return result;
    }
	
	public static List<Object> jsonArrayToList (JSONArray collection) throws JSONException {
		List <Object> result = new ArrayList();

        for (int i=0; i< collection.length(); i++) {
        	Object object = collection.get(i);
        	if (object instanceof JSONObject ) {
        		result.add( jsonToMap( (JSONObject)object ) );
        	} 
        	else if(object instanceof String) {
        		try{
        			JSONObject json = new JSONObject((String)object);
        			result.add(jsonToMap(json));
        		}
        		catch (Exception e){
        			//System.out.println(e);
        			result.add(object);
        		}
        		
        	}
        	else if(object instanceof Number) {
        		result.add(object);
        	}
        }
        
        return result;
    }
	
	public static Map<String, String> jsonToStringsMap(JSONObject o) throws JSONException	{
		Iterator ji = o.keys();
		Map<String, String> b = new LinkedHashMap<String, String>();
		while (ji.hasNext())
		{
			String key = (String) ji.next();
			String val = (String) o.get(key);
			b.put(key, val);
		}
		return b;
	}
	
	
	public static Map<String, Object> jsonToMap(JSONObject o) throws JSONException	{
		Iterator ji = o.keys();
		Map<String, Object> b = new LinkedHashMap<String, Object>();
		while (ji.hasNext())
		{
			String key = (String) ji.next();
			Object val = o.get(key);
			if (val.getClass() == JSONObject.class)
				b.put(key, jsonToMap((JSONObject) val));
		    else if (val.getClass() == JSONArray.class)
		    {
		    	List<Object> l = new ArrayList<Object>();
		    	JSONArray arr = (JSONArray) val;
		    	for (int a = 0; a < arr.length(); a++)
		    	{
		    		Object element = arr.get(a);
		    		if (element instanceof JSONObject)
		    			l.add(jsonToMap((JSONObject) element));
		    		else
		    			if (JSONObject.NULL != element)
		    				l.add(element);
		    			else
		    				l.add(null);
		    	}
		    	b.put(key, l);
		    }
		    else
		    	b.put(key, val);
		}
		return b;
	}
	
	public static JSONObject mapToJSON (Map<?, ?> mapToConvert) throws JSONException {
		JSONObject result = new JSONObject();

        for (Entry<?, ?> entry : mapToConvert.entrySet()) {
        	if (entry.getValue () instanceof Collection) {
        		result.put((String) entry.getKey(), mapListToJSONArray((Collection<?>)entry.getValue()));
        	} else if (entry.getValue() instanceof Map) {
        		result.put((String) entry.getKey(), mapToJSON((Map<?, ?>)entry.getValue()));
        	} else {
        		result.put((String) entry.getKey(), entry.getValue());
        	}
        }

        return result;
	}

	public static JSONArray mapListToJSONArray (Collection<?> collection) throws JSONException {
		JSONArray result = new JSONArray();

        for (Object object : collection) {
        	if (object instanceof Map) {
        		result.put(mapToJSON((Map<?, ?>)object));
        	} else {
        		result.put(object);
        	}
        }
        
        return result;
    }
	
	public static JSONArray listToJSONArray (Collection<?> collection) throws JSONException {
		JSONArray result = new JSONArray();

		 for (Object object : collection) {
	        	if (object instanceof Map) {
	        		result.put(mapToJSON((Map<?, ?>)object));
	        	} else {
	        		result.put(object);
	        	}
	        }
        
        return result;
    }

}
