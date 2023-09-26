#!/usr/bin/env node

var debug = require('debug')('passport-mongo');
var app = require('./app');

const https = require('https');
const http = require('http');
const fs = require('fs');

app.set('port', process.env.PORT || 5003);

var server = app.listen(app.get('port'), function () {
  console.log('Express server listening on port ' + server.address().port);
  debug('Express server listening on port ' + server.address().port);
});
