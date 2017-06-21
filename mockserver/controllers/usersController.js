var url = require('url');
var resourceManager = require("../services/resourceManager");
var jsonHandler = require("../services/jsonHandler");
var userId = 1;
var usersController = {
        
        getUser : function(request, response){
            var pathname = url.parse(request.url).pathname;
            var uriRegExp = new RegExp('/users/(\\w+)');
            uriRegExp.exec(pathname);
            var userId = RegExp.$1;
            resourceManager.getResource('user', userId, response);
        }
};

exports.getUser = usersController.getUser;