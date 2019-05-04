var express = require('express');
var router = express.Router();
var apiController = require('../controllers/apiController');
var eventsController = require('../controllers/eventsController');
var policiesController = require('../controllers/policiesController');
/* GET home page. */
router.get('/', eventsController.aggregate, 
	policiesController.countApps, 
	policiesController.countPolicies, 
	eventsController.countUsers, 
	eventsController.countJobs, 
	(req, res, next) => res.render('index', {data: req.data || {}, info: req.info || {}}));

router.get('/policies', policiesController.index);

router.get('/events', eventsController.index);

router.post('/report', apiController.report);

module.exports = router;
