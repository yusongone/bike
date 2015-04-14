var express = require('express');
var app = express();
var ejs = require("ejs");

var cookieParser=require("cookie-parser");
var session=require("express-session");
var bodyParser=require("body-parser");
var router=require("./router")(app);
var connect_mongo=require("connect-mongo")(session);
var session_conf=require("./config.json").session_conf;


app.set("views",__dirname+"/controller/views");
app.engine("html",ejs.renderFile);
app.set('view engine', 'ejs');

app.use(function(req,res,next){
  console.log("eeee");
  next();
});

app.use(express.static(__dirname+'/public'));
app.use(cookieParser("keyboard cat"));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(session({
      secret:"keyboard cat",
      saveUninitialized:true
      ,resave:true
      ,maxAge:1000*60*30
      ,store:new connect_mongo({
        db:session_conf.dbname,
        host:session_conf.path,
        port:session_conf.port,  // optional, default: 27017
        username:session_conf.user, // optional
        password:session_conf.pass, // optional
        collection:session_conf.collection,// optional, default: sessions
        safe:true
      })
}));


app.listen(2000,function(){
  console.log("linsten on 2000");
});




