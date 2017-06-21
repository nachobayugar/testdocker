var http = require("http");
var urlMapper = require("./urlMapper");

function start() {
  function onRequest(request, response) {
    urlMapper.findRequestHandler(request, response);
  }

  http.createServer(onRequest).listen(8888);
  console.log("Server has started.");
}

exports.start = start;
