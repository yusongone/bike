var fs=require("fs");
fs.readFile("./tt1.txt",function(err,data){
  var d=data.toString();
  split(d);
});

function split(d){
  var finalAry=[];
  var ary=d.split(",");
  for(var i=0;i<ary.length;i++){
    var tempAry=ary[i].split("|"); 
    if(tempAry[5]>1428768000&&tempAry.length>1){
      var json={};
      json["speed"]=tempAry[0];
      json["prassure"]=tempAry[1];
      json["shake"]=tempAry[2];
      json["temp"]=tempAry[3];
      json["lap"]=tempAry[4];
      json["time"]=tempAry[5];
      finalAry.push(json);
    }
  }
  var stream = fs.createWriteStream("f.json");
  var text=JSON.stringify(finalAry);
  stream.write(text);
  parseJson(finalAry);
}


function parseJson(finalAry){
  console.log(finalAry.length);
}
