const {models} = require("../model");

const Sequelize = require("sequelize");
const converter = require("json-2-csv");


exports.index = (req, res, next) => {
	models.policy.findAll({order: [["createdAt", "DESC"]]}).then(policies=>{
		res.render("policies",{policies});
	})
}

exports.countApps = (req, res, next) => {
	req.info = {}
	models.policy.findAll({
			attributes: [[Sequelize.literal('COUNT(DISTINCT(appId))'), 'count']],
		}).then(policies => {

		var policy = JSON.parse(JSON.stringify((policies||[])[0]))
		req.info = {
			...req.info, activeApps: parseInt(policy.count||0)
		};
		next()
	}).
		catch(e=>next(e))
}
exports.countPolicies = (req, res, next) => {
	req.info = req.info || {};
	models.policy.findAll({
			attributes: [[Sequelize.literal('COUNT(DISTINCT(id))'), 'count']],
		}).then(policies => {

		var policy = JSON.parse(JSON.stringify((policies||[])[0]))
		req.info = {
			...req.info, activePolicies: parseInt(policy.count||0)
		};
		next()
	}).
		catch(e=>next(e))
}