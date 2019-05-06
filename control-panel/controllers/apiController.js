const {models} = require("../model");

const Sequelize = require("sequelize");

exports.report = (req, res, next) => {
	
	const { type, userId, jobId, msg, punishment } = req.body;

	const event = models.event.build({ type, userId, jobId, msg, punishment	}); 
	/************* DEV *************/ 
	// res.end();
	// global.io.emit("report", event);
	/*******************************/
	  
	event.save({"fields": ["type", "userId", "jobId", "msg", "punishment"]}).
	    then((ev) => {
	    	res.end();
	    	global.io.emit("report", ev);
	    })
	    .catch(e=>{
	    	console.error(e);
	    	res.status(500);
	    	res.send(e);
	    })
	
}