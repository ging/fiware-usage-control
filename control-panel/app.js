var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var sassMiddleware = require('node-sass-middleware');
const session = require("express-session"),
  SequelizeStore = require("connect-session-sequelize")(session.Store);
var indexRouter = require('./routes/index');
const methodOverride = require("method-override");
const http = require('http');
const bodyParser = require('body-parser');

var app = express();
const partials = require('express-partials');
const notifications = require('./controllers/notifications');
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(partials());

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
let server = http.createServer(app);

const sequelize = require("./model");
/*const sessionStore = new SequelizeStore({"db": sequelize,
  "table": "session",
  "checkExpirationInterval": 15 * 60 * 1000, // The interval at which to cleanup expired sessions in milliseconds. (15 minutes)
  "expiration": 4 * 60 * 60 * 1000});// The maximum age (in milliseconds) of a valid session. (4 hours)

app.use(session({"secret": "CSIC",
  "store": sessionStore,
  "resave": false,
  "saveUninitialized": true}));*/

app.use(methodOverride("_method", {"methods": ["POST", "GET"]}));
global.io = require('socket.io')(server);
global.io.on('connection', notifications);
app.use(sassMiddleware({
  src: path.join(__dirname, 'public'),
  dest: path.join(__dirname, 'public'),
  indentedSyntax: true, // true = .sass and false = .scss
  sourceMap: true
}));
app.use(express.static(path.join(__dirname, 'public')));


app.use('/', indexRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = {app:app,server:server};
