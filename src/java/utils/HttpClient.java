package utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

import org.codehaus.groovy.grails.web.json.*;

public class HttpClient {


	public static Map executeRequest(Map requestProperties) {
		return executeRequest(requestProperties, 1);
	}
	
	public static Map executeRequest(Map requestProperties, int retryNumbers) {
		int attempts = 0;
		Map response = new LinkedHashMap();
		while (attempts < retryNumbers){
			try{
				response = JsonUtils.jsonToMap(execute(requestProperties));
				if((Integer) response.get("status") < 500){
					return response;
				}
			}
			catch(Exception e){
				System.out.println("Exception reading response: " + e);
			}
			
			attempts +=1;
		}
		return response;
	}

	public static JSONObject execute(Map requestProperties) {
		String method = (String) requestProperties.get("method");
		String uriWithQueryString = (String) requestProperties.get("uriWithQueryString");
		JSONObject headersJson = null;
		Object body = requestProperties.get("body");
		JSONObject response = new JSONObject();
		try {
			headersJson = (requestProperties.get("headers") != null && requestProperties.get("headers") instanceof Map) ? JsonUtils.mapToJSON((Map) requestProperties.get("headers")) : new JSONObject();
		} catch (JSONException je) {

		}
		try {
			if (body instanceof Map) {
				if (((Map) body).size() > 0) {
					body = JsonUtils.mapToJSON((Map) body);
				} else {
					body = null;
				}

			} else if (body instanceof java.util.List) {
				if (((java.util.List) body).size() > 0) {
					body = JsonUtils.listToJSONArray((java.util.List) body);
				} else {
					body = null;
				}

			}

		} catch (JSONException je) {

		}

		try {
			//System.out.println(requestProperties.get("baseUrl") + uriWithQueryString);

			HttpURLConnection conection = null;
			if(requestProperties.get("proxyHost")!=null){
				String proxyHost = requestProperties.get("proxyHost").toString();
				Integer proxyPort = (requestProperties.get("proxyPort")!=null) ? (Integer) requestProperties.get("proxyPort") : 80;
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
				if(requestProperties.get("baseUrl") != null){
                	conection = (HttpURLConnection) new URL(requestProperties.get("baseUrl") + uriWithQueryString).openConnection(proxy);
                }else{
                	conection = (HttpURLConnection) new URL(uriWithQueryString).openConnection(proxy);
                }
			}
			else{
				if(requestProperties.get("baseUrl") != null){
                    conection = (HttpURLConnection) new URL(requestProperties.get("baseUrl") + uriWithQueryString).openConnection();
                }else{
                    conection = (HttpURLConnection) new URL(uriWithQueryString).openConnection();
                }
			}

			int socketTimeout = (requestProperties.get("socketTimeout") != null) ? (Integer) requestProperties.get("socketTimeout") : 15000;
			int connectionTimeout = (requestProperties.get("connectionTimeout") != null) ? (Integer) requestProperties.get("connectionTimeout") : 3000;

			conection.setRequestMethod(method);
			conection.setConnectTimeout(connectionTimeout);
			conection.setReadTimeout(socketTimeout);

			Iterator<?> keys = headersJson.keys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				try {
					String value = (String) headersJson.get(key);
					conection.setRequestProperty(key, value);
				} catch (JSONException je) {
					System.out.println("Error writing request header: " + key);
				}
			}

			//conection.setRequestProperty("Accept","*/*");
			if (conection.getRequestProperty("Accept") == null || conection.getRequestProperty("Accept").indexOf("text/html,application/xhtml+xml,application/xml")>=0) {
				conection.setRequestProperty("Accept", "application/json");
			}
			OutputStream os = null;
			if (body != null && body.toString().length() > 0) {
				if (body instanceof JSONObject || body instanceof JSONArray) {
					conection.setRequestProperty("Accept", "application/json");
					conection.setRequestProperty("Content-Type", "application/json");
				}

				conection.setRequestProperty("Content-Length", "" + Integer.toString(body.toString().getBytes().length));
				conection.setDoOutput(true);
				os = conection.getOutputStream();
				byte[] outputInBytes = null;
				outputInBytes = body.toString().getBytes("UTF-8");
				os.write(outputInBytes);
			} else {
				//conection.getOutputStream().write("-k --insecure".getBytes());
				conection.connect();
				//os.write("-k --insecure".getBytes());
			}
			int status = conection.getResponseCode();
			StringBuilder responseJSON = new StringBuilder();

			if (status < 400) {
				BufferedReader in = null;
				boolean encoded = false;
				try {
					if (headersJson.has("Accept-Encoding") && headersJson.get("Accept-Encoding").toString().contains("gzip") && headersJson.has("Content-Encoding") && headersJson.get("Content-Encoding").toString().contains("gzip")) {
						encoded = true;
					}
				} catch (JSONException je) {

				}
				if (encoded) {
					GZIPInputStream gzis = new GZIPInputStream(conection.getInputStream());
					InputStreamReader reader = new InputStreamReader(gzis);
					in = new BufferedReader(reader);
				} else {
					in = new BufferedReader(new InputStreamReader(conection.getInputStream()));
				}


				String line = null;
				int l = 0;
				while ((line = in.readLine()) != null) {
					//System.out.println(line);
					if(l>0){
						responseJSON.append("\n" + line);
					}
					else{
						responseJSON.append(line);
					}

				}

				in.close();

			} else {
				BufferedReader errorIn = null;

				errorIn = new BufferedReader(new InputStreamReader(conection.getErrorStream()));
				String errorLine = null;
				while ((errorLine = errorIn.readLine()) != null) {
					//System.out.println(errorLine);
					responseJSON.append(errorLine);
				}
			}


			if (os != null) {
				os.close();
			}

			JSONObject headers = new JSONObject();
			Map headersMap = conection.getHeaderFields();
			Iterator headersMapIterator = headersMap.entrySet().iterator();
			while (headersMapIterator.hasNext()) {
				Map.Entry pair = (Map.Entry) headersMapIterator.next();
				String headerName = (String) pair.getKey();
				Collection headerValue = (Collection) pair.getValue();
				try {
					if (headerName != null && !"Transfer-Encoding".equals(headerName)) {
						headers.put(headerName, headerValue.iterator().next());
					}
				} catch (JSONException je) {

				}

			}

			try {
				if (responseJSON.toString().replaceAll(" ", "").startsWith("[")) {
					JSONArray respBody = new JSONArray(responseJSON.toString());
					response.put("body", respBody);
				} else {
					JSONObject respBody = new JSONObject(responseJSON.toString());
					//if (responseJSON.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "").length() > respBody.toString().length()) {
					//	response.put("body", responseJSON.toString());
					//} else {
					response.put("body", respBody);
					//}

				}

				response.put("status", status);
				response.put("headers", headers);
			} catch (JSONException je) {
				System.out.println("Exception converting responseJSON " + je);
				try{
					response.put("status", status);
					response.put("headers", headers);
					response.put("body", responseJSON.toString());
				}catch(Exception ignore){}

			}

		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (IOException e) {
			try{
				System.out.println(e);
				response.put("status", 503);
				response.put("headers", new JSONObject());
				JSONObject responseJSON = new JSONObject();
				responseJSON.put("exception", e);
				response.put("body", responseJSON.toString());
			}
			catch(JSONException je){}
			
		}
		//{"sourcemap":null,"_version":"1.0","error":null,"ast":{"content":[{"content":[{"content":"Questions related resources of the **Questions API**\n\n+ Attributes (object)\n    + title (string)\n    + question (string)\n    \n","element":"copy"},{"content":[],"element":"resource","model":{},"uriTemplate":"/questions","description":"","name":"Questions Collection","parameters":[],"actions":[{"content":[],"description":"","name":"List all Questions","method":"GET","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[{"content":"[{\n  \"id\": 1, \"title\": \"This is a title\", \"question\":\"This is a question\"\n}, {\n  \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\"\n}]\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"[{\n  \"id\": 1, \"title\": \"This is a title\", \"question\":\"This is a question\"\n}, {\n  \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\"\n}]\n","description":"","name":"200"}]}]},{"content":[{"element":"dataStructure","sections":[{"content":[{"content":{"sections":[],"description":"","name":{"literal":"title"},"valueDefinition":{"values":[],"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"string"},"attributes":["required"]}}},"class":"property"},{"content":{"sections":[],"description":"","name":{"literal":"question"},"valueDefinition":{"values":[],"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"string"},"attributes":["required"]}}},"class":"property"}],"class":"memberType"}],"name":null,"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"object"},"attributes":[]}}],"description":"","name":"Create a Question","method":"POST","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[{"content":[{"content":"{\"question\":\"{question}\"}\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"{\"question\":\"{question}\"}\n","description":"","name":""}],"description":"","name":"","responses":[{"content":[{"content":"{ \"id\": 3, \"title\": \"Second Question\", \"question\":\"To be or not to be\"}\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"{ \"id\": 3, \"title\": \"Second Question\", \"question\":\"To be or not to be\"}\n","description":"","name":"201"}]}]}]},{"content":[],"element":"resource","model":{},"uriTemplate":"/questions/{id}","description":"A single Question object with all its details\n\n","name":"Question","parameters":[{"values":[],"default":"","description":"Numeric `id` of the Question to perform action with. Has example value.","name":"id","example":"1","required":true,"type":"number"}],"actions":[{"content":[],"description":"","name":"Retrieve a Question","method":"GET","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[{"content":"{ \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\" }\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"},{"name":"X-My-Header","value":"The Value"}],"body":"{ \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\" }\n","description":"","name":"200"}]}]},{"content":[],"description":"","name":"Remove a Question","method":"DELETE","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[],"schema":"","headers":[],"body":"","description":"","name":"204"}]}]}]}],"element":"category","attributes":{"name":"Questions"}}],"element":"category","_version":"3.0","description":"Questions API is a *short texts saving* service similar to its physical paper presence on your table.\n\n","name":"test","resourceGroups":[{"resources":[{"content":[],"element":"resource","model":{},"uriTemplate":"/questions","description":"","name":"Questions Collection","parameters":[],"actions":[{"content":[],"description":"","name":"List all Questions","method":"GET","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[{"content":"[{\n  \"id\": 1, \"title\": \"This is a title\", \"question\":\"This is a question\"\n}, {\n  \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\"\n}]\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"[{\n  \"id\": 1, \"title\": \"This is a title\", \"question\":\"This is a question\"\n}, {\n  \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\"\n}]\n","description":"","name":"200"}]}]},{"content":[{"element":"dataStructure","sections":[{"content":[{"content":{"sections":[],"description":"","name":{"literal":"title"},"valueDefinition":{"values":[],"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"string"},"attributes":["required"]}}},"class":"property"},{"content":{"sections":[],"description":"","name":{"literal":"question"},"valueDefinition":{"values":[],"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"string"},"attributes":["required"]}}},"class":"property"}],"class":"memberType"}],"name":null,"typeDefinition":{"typeSpecification":{"nestedTypes":[],"name":"object"},"attributes":[]}}],"description":"","name":"Create a Question","method":"POST","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[{"content":[{"content":"{\"question\":\"{question}\"}\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"{\"question\":\"{question}\"}\n","description":"","name":""}],"description":"","name":"","responses":[{"content":[{"content":"{ \"id\": 3, \"title\": \"Second Question\", \"question\":\"To be or not to be\"}\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"}],"body":"{ \"id\": 3, \"title\": \"Second Question\", \"question\":\"To be or not to be\"}\n","description":"","name":"201"}]}]}]},{"content":[],"element":"resource","model":{},"uriTemplate":"/questions/{id}","description":"A single Question object with all its details\n\n","name":"Question","parameters":[{"values":[],"default":"","description":"Numeric `id` of the Question to perform action with. Has example value.","name":"id","example":"1","required":true,"type":"number"}],"actions":[{"content":[],"description":"","name":"Retrieve a Question","method":"GET","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[{"content":"{ \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\" }\n","element":"asset","attributes":{"role":"bodyExample"}}],"schema":"","headers":[{"name":"Content-Type","value":"application/json"},{"name":"X-My-Header","value":"The Value"}],"body":"{ \"id\": 2, \"title\": \"Second Question\", \"question\":\"To be or not to be\" }\n","description":"","name":"200"}]}]},{"content":[],"description":"","name":"Remove a Question","method":"DELETE","attributes":{"uriTemplate":"","relation":""},"parameters":[],"examples":[{"requests":[],"description":"","name":"","responses":[{"content":[],"schema":"","headers":[],"body":"","description":"","name":"204"}]}]}]}],"description":"Questions related resources of the **Questions API**\n\n+ Attributes (object)\n    + title (string)\n    + question (string)\n    \n","name":"Questions"}],"metadata":[{"name":"FORMAT","value":"1A"}]},"warnings":[]}
		return response;
	}

}