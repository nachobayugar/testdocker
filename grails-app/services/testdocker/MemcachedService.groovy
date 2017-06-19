package testdocker

import grails.converters.JSON
import java.io.IOException;
import net.spy.memcached.*;
import org.codehaus.groovy.grails.web.json.JSONObject

class MemcachedService {

     static final Object NULL = "NULL"
    def MemcachedClient memcachedClient
 
    def void afterPropertiesSet() {
		String memcachedServer = grails.util.Holders.config.memcachedServer
		String memcachedPort = grails.util.Holders.config.memcachedPort
        memcachedClient = new MemcachedClient(AddrUtil.getAddresses(memcachedServer + ":" + memcachedPort))
    }
 
    def get(String key) {
		if(memcachedClient == null){
			afterPropertiesSet()
		}
        return memcachedClient.get(key)
    }
 
	def set(String key, Map value) {
		if(memcachedClient == null){
			afterPropertiesSet()
		}
		memcachedClient.set(key, 600, (value as JSON).toString())
	}
	
    def set(String key, JSONObject value) {
		if(memcachedClient == null){
			afterPropertiesSet()
		}
        memcachedClient.set(key, 600, value.toString())
    }
 
    def delete(String key) {
        memcachedClient.delete(key)
    }
 
    def clear() {
        memcachedClient.flush()
    }
 
    def update(key, function) {
        def value = function()
        if (value == null) value = NULL
        set(key, value)
        return value
    }
 
    def get(key) {
        def value = get(key)
        /*if (value == null) {
            value = update(key, function)
        }*/
        return (value == NULL) ? null : value;
    }
}
