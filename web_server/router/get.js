var express=require("express");

var home=express.Router();
var userPage=express.Router();


module.exports=function(app){
  app.use("/",home);
  app.use("/",userPage);

  home.get("/",function(req,res,next){
    res.render("index",{});
  });

  home.get("/login",function(req,res,next){
    res.render("login",{});
  });

  userPage.get("/:username",function(req,res,next){
    res.render("singlePage",{});
  });

  userPage.get("/auth",function(){
  });
}
