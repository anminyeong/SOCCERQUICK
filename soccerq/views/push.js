var gcm = require('node-gcm');
var fs = require('fs');

var message = new gcm.Message();

var message = new gcm.Message({
    collapseKey: 'demo',
    delayWhileIdle: true,
    timeToLive: 3,
    data: {
        title: 'SoccerQuick',
        message: '심주용님이 5:5 매치신청을 하셧습니다.',
        custom_key1: 'custom data1',
        custom_key2: 'custom data2'
    }
});

var server_api_key = 'AIzaSyCj1xuymZIaqz4xb-QHSlfn-W7FzcWIEiI';
var sender = new gcm.Sender(server_api_key);
var registrationIds = [];

var token = 'fMXzjqykb7k:APA91bHqU_-MkK83Oj1JC7AG1Wj_fuIuu1wmsIV5jk4i81BQHGOPz1xMBBV8KnlXelgH70Q74Mi6xcyy8zMdaMHKun7mlkdKdQxzen5cdiqTEo7fKDxwMbd0EvRSJ_lcslX1X3MkMj4v';
registrationIds.push(token);

sender.send(message, registrationIds, 4, function (err, result) {
    console.log(result);
});