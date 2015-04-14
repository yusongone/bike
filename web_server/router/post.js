var express=require("express");

var user=express.Router();

module.exports=function(app){
  app.use("/",user);
  app.post("/record",function(req,res,next){
  });

  user.post("/user",function(){

  });

  user.post("/register",function(req,res,next){
    console.log(req.body.name,req.body.pass);
    res.send({"ok":"abc"});
  });

}
