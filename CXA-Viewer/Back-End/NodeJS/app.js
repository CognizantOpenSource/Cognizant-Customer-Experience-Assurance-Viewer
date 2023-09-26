// dependencies
var express = require('express');
var morgan = require('morgan');
var fs = require('fs');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var hash = require('bcrypt-nodejs');
var path = require('path');
var cors = require('cors');
var validator = require('express-validator');
var helmet = require('helmet');
// create instance of express
var app = express();
const hsts = require('hsts');//HTTP Strict Transport Security middleware

//Strict-Transport-Security header to the response use HTTPS for the next period of time
app.use(hsts({
  maxAge: 604800, // 1 week in seconds
  includeSubDomains: true
}));

//Create a session middleware
var expSession = require('express-session');
var sessionFileStore = require('session-file-store')(expSession);

var config = require('./configuration.json');
// const uuidv5 = require('uuid/v5');
const uuidv4 = require('uuid/v4');

app.use(expSession({
  genid: function (req) {
   
    return uuidv4();
  },
  store: new sessionFileStore,
  secret: config.sessionSecretKey,
  saveUninitialized: true,
  resave: false,
  cookie: {
    path: "/",
    httpOnly: true,
    secure: false, // true if https
    maxAge: config.maxAge//45000 //45secs //1800000 //30 mins //3000000 //50mins //900000 //15 mins //604800000 //7days //86400000 //1day
  }
})
);


/* // setup the logger
app.use(morgan('combined', { stream: accessLogStream })) */
//Print in console as well
app.use(morgan("dev")); //log to console on development


var compression = require('compression');

//compression for responses, remove while using Nginx
app.use(compression());

//Code to handle XSS using express-validator
//app.use(validator());
app.use(function (req, res, next) {
  for (var item in req.body) {
    req.sanitize(item).escape();
  }
  next();
});

app.use(helmet());
//Required to handle XSS - Content security policy (CSP)
app.use(helmet.contentSecurityPolicy({
  directives: {
    defaultSrc: ["'self'"],
    styleSrc: ["'self'", "'unsafe-inline'"]
  }
}));

//Dev
//enable CORS for all requests
app.use(cors());//comment while Prod

app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});//comment while Prod

//Prod
app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
  res.header("Strict-Transport-Security", "max-age=604800"); 

  //console.log("method: " + req.method);
  if ((req.method == "GET") || (req.method == "POST") || (req.method == "PUT") || (req.method == "DELETE")) {
    next();
  } else {//405 Method Not Allowed
    // fs.readFile(__dirname + "/error_pages/405.html", function (error, data) {
    //   res.writeHead(200, {
    //     'Content-Type': 'text/html'
    //   });
    //   res.write(data);
    //   res.end();
    // });
  }
});



app.use(function (err, req, res, next) {
  if (res.headersSent) return next(err);
  //res.status(500).json("Internal Server Error");
  // console.log("500 Internal Server Error");
  // fs.readFile(__dirname + "/error_pages/500.html", function (error, data) {
  //   res.writeHead(200, {
  //     'Content-Type': 'text/html'
  //   });
  //   res.write(data);
  //   res.end();
  //});
});

// require routes
//var samplesRoutes = require('./routes/samplesAPI.js');
var lighthouseRoutes = require('./routes/lighthouseAPI.js');


var jsonParser = bodyParser.json({ limit: '500Mb', type: 'application/json' });
var urlencodedParser = bodyParser.urlencoded({ extended: true, limit: '500Mb', type: 'application/x-www-form-urlencoding' });

app.use(jsonParser);
app.use(urlencodedParser);

//Catching all errors so that node does not crash
process.on('uncaughtException', function (err) {
  console.error(err);
  console.log("Node NOT Exiting...");
});

// routes
/*app.use('/', express.Router().get('/', (req, res) => {
  res.status(200).send("IT'S WORKING!");//.json({ "status": "Test stopped" });
}));*/
app.use(function (err, req, res, next) {
  if (res.headersSent) return next(err);
  //res.status(500).json("Internal Server Error");
  console.log("500 Internal Server Error");
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header("Strict-Transport-Security", "max-age=604800"); // 1 week in seconds   
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
});
app.get('/', function (req, res) {//Production
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header("Strict-Transport-Security", "max-age=604800"); // 1 week in seconds   
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
  //res.sendFile(path.join(__dirname, '../public', '/'));
});

// app.use('/login', loginRouter);
// app.use('/lighthouseAPI/', verifyToken, lighthouseRoutes);
app.use('/lighthouseAPI/', lighthouseRoutes);
//app.use('/sentimentalAPI/',sentimentRoutes);

/*app.get('/', function(req, res) {
  res.sendFile(path.join(__dirname, '../release', '/'));
  res.sendFile(path.join(__dirname, '../src', '/'));
});*/

// error hndlers
app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header("Strict-Transport-Security", "max-age=604800"); // 1 week in seconds   
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');
  //var err = new Error('Not Found');
  //err.status = 404;
  //next(err);

  console.log("404 Not Found");
  // fs.readFile(__dirname + "/error_pages/404.html", function (error, data) {
  //   res.writeHead(200, {
  //     'Content-Type': 'text/html'
  //   });
  //   res.write(data);
  //   res.end();
  // });
});

app.use(function (err, req, res) {
  res.header('Access-Control-Allow-Methods', 'DELETE, PUT, GET, POST');
  res.header("Strict-Transport-Security", "max-age=604800"); // 1 week in seconds   
  res.header('Cache-Control', 'private, no-cache, no-store, must-revalidate');

  console.log(err.message);
  /*  res.status(err.status || 500);
   res.end(JSON.stringify({
     message: err.message,
     error: {}
   })); */
  // fs.readFile(__dirname + "/error_pages/500.html", function (error, data) {
  //   res.writeHead(200, {
  //     'Content-Type': 'text/html'
  //   });
  //   res.write(data);
  //   res.end();
  // });
});



module.exports = app;
