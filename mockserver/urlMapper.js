var usersController = require("./controllers/usersController");

var url = require('url');

var urlMapper = {
    handlers : [ {
        uriRegExp : '^/users.*?$',
        actions : [{
            method : 'GET',
            handle : usersController.getUser
        } ]
    }]
,
    findRequestHandler : function(request, response) {
        var handlerFound = false;
        var urlObject = url.parse(request.url);
        var pathname = urlObject.pathname;        
        for ( var i = 0; i < urlMapper.handlers.length; i++) {
            var h = urlMapper.handlers[i];
            var regExp = new RegExp(h.uriRegExp);
            if (regExp.exec(pathname) && !handlerFound) {
                for ( var j = 0; j < h.actions.length; j++) {
                    var action = h.actions[j];
                    if (action.method === request.method) {
                        action.handle(request, response);
                        if (request.url.indexOf("/ping") == -1) {
                            console.log('mockserver puro: ' + action.method + ' ' + request.url);
                        }
                        handlerFound = true;
                    }
                }
            }
        }

    }
};

exports.findRequestHandler = urlMapper.findRequestHandler;