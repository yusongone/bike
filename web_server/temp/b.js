function init(){
console.dir(d);
  var DOM="<div class='box'>";
  var DOMB="<div class='box'>";
  var DOMC="<div class='box'>";
var l=0;
  for(var i=210;i<d.length;i++){
    l+=2.3*d[i].lap;
    var value=d[i].speed;
    var valueP=(d[i].prassure-101800)*0.5;
    //var value=(d[i].shake);
    var valuec=(d[i].temp);
    var classss=""
    if(d[i].speed!=0&&d[i-1].speed!=0&&d[i].time-d[i-1].time>6){
      classss="a";
    }
    var atemp="<div data="+d[i].time+" class='item "+ classss+"' style='margin-top:"+(400-value*0.3)+"px;height:"+value*0.3+"px'></div>";
    var btemp="<div  class='itemb'  style='margin-top:"+(400-valueP)+"px;height:"+valueP+"px'></div>";
    var ctemp="<div  class='itemc "+ classss+"'  style='margin-top:"+(400-valuec)+"px;height:"+valuec+"px'></div>";
    DOM+=atemp;
    DOMB+=btemp;
    DOMC+=ctemp;
  }
  console.log(l);
  DOM+="</div>"
  DOMB+="</div>"
  DOMC+="</div>"
   $(".page").append(DOMC);
   $(".page").append(DOMB);
   $(".page").append(DOM);

  $(".item").hover(function(){
    var time=$(this).attr("data");
    $("#time").text((new Date(time*1000)).toLocaleTimeString());
  },function(){
  });
}
