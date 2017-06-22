package testdocker

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.spock.IntegrationSpec
import org.codehaus.groovy.grails.web.json.JSONObject
import static utils.HttpClient.execute;

class DockerUserFucntionalSpec extends Specification{

	def setup() {
		println "SETUP"
	}

	def cleanup() {
		println "CLEANUP"
	}
	
	def "Register docker user" (){
		given:
			println "--------------EMPEZANDO TEST de registrar usuario: --------------------------------"
		when:
			String url = "http://localhost:8080/testdocker/dockerUser/register"
			Map requestBody = ["name":"pepe", "surname":"pompin", "email":"pepepompin@pepona.com", "mercadolibreId":1234]
			def respJson = execute(["method":"POST" ,"uriWithQueryString":url, "body":requestBody]);
			
		then:
			println "-----------------------------------------------------"
			println respJson.body
			respJson.status == 200
			respJson.body.email == requestBody.get("email")
			respJson.body.name == requestBody.get("name")
			
	}

}
