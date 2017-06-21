var jsonHandler = require("../services/jsonHandler");
var resources = [];
var resourceId = 1;

function getResource(type, id, response){
    
    var resource = {"id":1234,"nickname":"TESTUSER","registration_date":"2014-08-18T22:18:11.000-04:00","country_id":"AR","status":{"site_status":"active"}}
    if(id!=1234){
        showNotFoundResponse(response, type, id);
        return;
    }
    
    jsonHandler.showOKResponse(resource, response);
};

function showNotFoundResponse(response, type, id){
    jsonHandler.showNotFoundResponse({msg: type + ' not found: ' + id}, response);
}

exports.getResource = getResource;
exports.showNotFoundResponse = showNotFoundResponse;
