var express = require('express');
var mongodb = require('mongodb');
var app = express();

var MongoClient = require('mongodb').MongoClient;
var db;

// Initialize connection once
var f=(new Date()).getTime();
var counter=1000000;
MongoClient.connect("mongodb://localhost:27017/test",{
}, function(err, database) {
  if(err) throw err;

  db = database;
  console.log(db.options);

  // Start the application after the database connection is ready
  app.listen(3000);
  console.log("Listening on port 3000");
  for(var i=0;i<counter;i++){
    test();
  }
});

module.exports=function(){
    
}

// Reuse database object in request handlers
app.get("/", function(req, res) {
  db.collection("cc").find({}, function(err, docs) {
        ff++;
        if(ff==1000){
          var fefe=(new Date()).getTime();
          console.log(fefe-f);
        }
          res.send("a");
  });
});

var ff=0;
function test(){
  return;
}
