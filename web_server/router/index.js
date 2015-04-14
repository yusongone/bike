var put=require("./put");
var get=require("./get");
var del=require("./delete");
var post=require("./post");
module.exports=function(app){
  console.log("router initialized");
  get(app);
  put(app);
  del(app);
  post(app);
}
