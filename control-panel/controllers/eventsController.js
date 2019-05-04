const {models} = require("../model");

const Sequelize = require("sequelize");
const converter = require("json-2-csv");


exports.index = (req, res, next) => {
	models.event.findAll({order: [["createdAt", "DESC"]], limit: 50})
	.then(events=>{
		if(req.query.csv) {
			const eventsCsv = events
			.map(ev => ({
				type: ev.type, 
				userId: ev.userId, 
				jobId: ev.jobId, 
				msg: ev.msg, 
				date: ev.createdAt
			}));
			converter.json2csv(eventsCsv,
			    (err, csvText) => {
			        if (err) { next(err); return; }
			        res.setHeader("Content-Type", "text/csv");
			        res.setHeader("Content-Disposition", "attachment; filename=\"events-" + Date.now()+".csv\"");
			        res.write(csvText);
			        res.end();
			    },
			    { "delimiter": { "field": ";" }});
		} else {
			res.render("events", {events});
		}
		
	})
	.catch(e=>next(e))
}

exports.aggregate = (req, res, next) => {
	models.event.findAll({
	  attributes: ['type',[Sequelize.literal('COUNT(type)'), 'count']],
	  group: 'type'
	}).then( count => {
		req.data = { types: {}, punishments: {}, history: { dates: [], count: []} };
		JSON.parse(JSON.stringify(count)).map(d => {req.data.types[d.type] = parseInt(d.count);});
		models.event.findAll({
		  attributes: ['punishment',[Sequelize.literal('COUNT(punishment)'), 'count']],
		  group: 'punishment'
		}).then( count => {
			JSON.parse(JSON.stringify(count)).map(d => {req.data.punishments[d.punishment] = parseInt(d.count);})
			models.event.findAll({
			  attributes: [[Sequelize.fn('strftime', Sequelize.literal("'%d-%m-%Y'"), Sequelize.col('createdAt')),"date"],[Sequelize.literal('COUNT(id)'), 'count']],
			  group: ["date"],
			  order: ["createdAt"]
			}).then( count => {
				JSON.parse(JSON.stringify(count)).map((d) => {
					req.data.history.dates.push(d.date);
					req.data.history.count.push(parseInt(d.count));
				})
				next();
			})
		})
	}).
	catch(e=>next(e))
}

exports.countUsers = (req, res, next) => {
	req.info = req.info || {}
	models.event.findAll({
			attributes: [[Sequelize.literal('COUNT(DISTINCT(userId))'), 'count']],
		}).then(events => {

		var ev = JSON.parse(JSON.stringify((events||[])[0]))
		req.info = {
			...req.info, activeUsers: parseInt(ev.count||0)
		};
		next()
	}).
		catch(e=>next(e))
}

exports.countJobs = (req, res, next) => {
	req.info = req.info || {}
	models.event.findAll({
			attributes: [[Sequelize.literal('COUNT(DISTINCT(jobId))'), 'count']],
		}).then(events => {

		var ev = JSON.parse(JSON.stringify((events||[])[0]))
		req.info = {
			...req.info, activeJobs: parseInt(ev.count||0)
		};
		next()
	}).
		catch(e=>next(e))
}