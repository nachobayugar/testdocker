package testdocker

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

class DockerUserController {

    MemcachedService memcachedService
	RedisService redisService
	
	def register(){
		Map requestBody = request.JSON
		Map apiResponse = getUser(requestBody.get("mercadolibreId"))
		println "apiResponse: ${apiResponse}"
		if(apiResponse.status == 404){
			renderNotFoundResponse("user is not a user of mercadolibre")
		}
		else if(apiResponse.status == 200){
			if(apiResponse.body.status.site_status != "active"){
				renderBadRequestResponse()
			}
			else{
				println "before registerDockerUser"
				Map userInfo = registerDockerUser(requestBody)
				println "userRegistered: ${userInfo}"
				storeInCache(userInfo)
				renderOkResponse(200, userInfo)
			}
		}
	}
	
	def get(){
		Map userInfo = findDockerUser(params.id as Long)
		if(userInfo==null){
			renderBadRequestResponse()
		}
		else{
			renderOkResponse(200, userInfo)
		}
		
	}
	
	def update(){
		if(params.id==null){
			renderBadRequestResponse("User id cannot be null")
			return
		}
		try{
			Long.parseLong(params.id)
		}	
		catch(Exception e){
			renderBadRequestResponse("User id should be a number")
			return
		}

		try{
			Map requestBody = request.JSON
			redisService.set("${params.id}", (requestBody as JSON).toString())
		}
		catch(Exception e2){
			println e2
			renderInternalErrorResponse("Could not process user ${params.id}")
			return
		}
		renderAcceptedResponse("User data saved to be processed")
	}

	def doUpdate(){
		if(params.id==null){
			renderBadRequestResponse("User id cannot be null")
		}
		try{
			Long.parseLong(params.id)
		}
		catch(Exception e){
			renderBadRequestResponse("User id should be a number")
		}

		try{
			String data = redisService.get("${params.id}")
			JSONObject dataJson = new JSONObject(data)
			DockerUser du = DockerUser.get(Long.parseLong(params.id))
			if(dataJson.get("name") != null){
				du.name = dataJson.get("name")
			}
			if(dataJson.get("surname") != null){
				du.surname = dataJson.get("surname")
			}
			if(dataJson.get("name") != null){
				du.email = dataJson.get("email")
			}
			if(dataJson.get("conferencesQuantity") != null){
				du.conferencesQuantity = dataJson.get("conferencesQuantity")
			}
			du.save()
			redisService.del("${params.id}")
			Map userInfo = ["id":du.id, "name":du.name, "surname":du.surname, "email":du.email, "conferencesQuantity":du.conferencesQuantity, "registrationDate": du.registrationDate]
			storeInCache(userInfo)
			renderOkResponse(200, userInfo)
		}
		catch(Exception e2){
			renderInternalErrorResponse("Could not process user ${params.id}")
		}
	}
	
	private storeInCache(Map userInfo){
		memcachedService.set(userInfo.id.toString(), userInfo)
	}
	
	private Map findDockerUser(Long id){
		JSONObject dockerUserJson = null
		try{
			def memcachedInfo = memcachedService.get(id.toString())
			if(memcachedInfo != null){
				dockerUserJson = new JSONObject(memcachedInfo.toString())
				return ["id":dockerUserJson.id, "name":dockerUserJson.name, "surname":dockerUserJson.surname, "email":dockerUserJson.email, "conferencesQuantity":dockerUserJson.conferencesQuantity, "registrationDate": dockerUserJson.registrationDate]
			}
		}
		catch(Exception e){
			println "Exception getting user ${id} from memcached"
		}
		
		DockerUser du = DockerUser.get(id)
		if(du == null){
			return null
		}
		else{
			Map mapToReturn = ["id":du.id, "name":du.name, "surname":du.surname, "email":du.email, "conferencesQuantity":du.conferencesQuantity, "registrationDate": du.registrationDate]
			try{
				storeInCache(mapToReturn)
			}
			catch(Exception e){
				println "Exception saving user ${id} from memcached"
			}
			return mapToReturn
		}
	}
	
	private Map registerDockerUser(Map userInfo){
		DockerUser du = new DockerUser()
		du.name = userInfo.get("name")
		du.surname = userInfo.get("surname")
		du.email = userInfo.get("email")
		du.conferencesQuantity = 0
		du.registrationDate = new Date()
		println du.validate()
		println du.validationErrorsMap
		du.save()
		return ["id":du.id, "name":du.name, "surname":du.surname, "email":du.email, "conferencesQuantity":du.conferencesQuantity, "registrationDate": du.registrationDate]
	}
	
	private Map getUser(Long userId){
		String baseUrl = grails.util.Holders.config.baseUrl
		String getUserUrl = "${baseUrl}/users/${userId}"
		Map requestMap = ["method":"GET" ,"uriWithQueryString":getUserUrl]
		Map apiResponse = utils.HttpClient.executeRequest(requestMap)
		println apiResponse
		return apiResponse
	}
	
	private Map renderOkResponse(int status, Map body){
		response.setHeader("content-type", "application/json")
		response.setStatus(status)
		render body as JSON
		return body
	}
	
	private Map renderBadRequestResponse(String message=null){
		response.setHeader("content-type", "application/json")
		response.setStatus(400)
		Map responseBody = ["message": (message==null) ? "user is not an active user of mercadolibre" : message]
		render responseBody as JSON
		return responseBody
	}
	
	private Map renderNotFoundResponse(String message=null){
		response.setHeader("content-type", "application/json")
		response.setStatus(404)
		Map responseBody = ["message": (message==null) ? "user not found" : message]
		render responseBody as JSON
		return responseBody
	}
	
	private Map renderAcceptedResponse(String message=null){
		response.setHeader("content-type", "application/json")
		response.setStatus(202)
		Map responseBody = ["message": (message==null) ? "accepted" : message]
		render responseBody as JSON
		return responseBody
	}
	
	private Map renderInternalErrorResponse(String message=null){
		response.setHeader("content-type", "application/json")
		response.setStatus(500)
		Map responseBody = ["message": (message==null) ? "server error" : message]
		render responseBody as JSON
		return responseBody
	}
}
