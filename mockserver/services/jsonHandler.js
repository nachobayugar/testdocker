function getContent(request, callback){
    var data = '';
    
    request.addListener('data', function(chunk) {
    	data += chunk;
    });
    
    request.addListener('end', function() {
        var resource = data != '' ? JSON.parse(data) : null;
        callback(resource);
    });
    
}

function showNotFoundResponse(object, response){
    showResponse(object, 404, response);
}

function showOKResponse(object, response){
    showResponse(object, 200, response);
}

function showPostOKResponse(object, response){
    showResponse(object, 201, response);
}

function showBadRequestResponse(object,response){
    showResponse(object, 400, response);
}


function showResponse(object, statusCode, response){
    response.writeHead(statusCode, {
        'Content-Type' : 'application/json; charset=utf-8'
    });
    response.write(JSON.stringify(object));
    response.end();
}

exports.getContent = getContent;
exports.showResponse = showResponse;
exports.showNotFoundResponse = showNotFoundResponse;
exports.showOKResponse = showOKResponse;
exports.showPostOKResponse = showPostOKResponse;
exports.showBadRequestResponse = showBadRequestResponse;
