var express = require('express');
var app = express();
var ejs = require("ejs");

var cookieParser=require("cookie-parser");
var session=require("express-session");
var bodyParser=require("body-parser");

app.use(express.static(__dirname+'/public'));
app.set("views",__dirname+"/controller/views");
app.engine("html",ejs.renderFile);
app.set('view engine', 'ejs');


app.use(cookieParser("keyboard cat"));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


app.listen(2000);


app.get("/",function(req,res,next){
  res.render("test",{});
});


