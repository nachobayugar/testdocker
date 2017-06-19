package testdocker

import grails.converters.JSON
import redis.clients.jedis.Jedis;
import org.codehaus.groovy.grails.web.json.JSONObject

class RedisService {
	
	private static String host;
	private static int port;
	private static Jedis jedis;
	
	public static void initRedis(){
		String redisHost = grails.util.Holders.config.redisHost
		int redisPort = grails.util.Holders.config.redisPort
		this.host = redisHost
		this.port = redisPort
		jedis = new Jedis(redisHost, redisPort)
	}

	public static boolean set(String key,String value){
		initRedis()
		jedis.set(key, value)
		jedis.disconnect()
		return true
	}
	
	public static String get(String key){
		initRedis()
		String value = jedis.get(key)
		jedis.disconnect()
		return value
	}
	
	public static boolean del(String key){
		initRedis()
		jedis.del(key)
		jedis.disconnect()
		return true
	}

	
}
