var express = require('express');

var bodyParser = require('body-parser');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io')(server);


server.listen(3002, function() {
	console.log('Server running at: http://localhost:3002');
	console.log('----------------------------------------');
	console.log('Send updates to /avg');
});


io.on('connection', function(socket) {
	console.log('New client connection');
});

app.use(express.static('public'));
app.use(bodyParser.json());

app.post("/avg",function(req,res){
	console.log("/avg")
	let avg = 0;
	try {
		console.log(req.body.data)
	avg = req.body.data[0].avg.value;
	} catch(e) {
		console.error(e)
	}
	io.sockets.emit('messages', {avg});
	res.sendStatus(200)
})