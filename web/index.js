// function createTable(data) {
//     const body = document.body;
//     tbl = document.createElement('table');
//     tbl.style.width = '100%';
//     tbl.style.border = '1px solid black';

//     rows = data.length
//     cols = 6

//     var orderArrayHeader = ["id", "pm2.5", "pm10", "genTime", "recTime", "transTime"];
//     var thead = document.createElement('thead');
//     tbl.appendChild(thead);
//     for (var i=0; i<orderArrayHeader.length; i++) {
//         thead.appendChild(document.createElement("th")).
//               appendChild(document.createTextNode(orderArrayHeader[i]));
//     }
  
//     for (let i = 0; i < rows; i++) {
//       const tr = tbl.insertRow();
//       let td;
//       td = tr.insertCell(); td.appendChild(document.createTextNode(data[i]._id)); td.style.border = '1px solid black';
//       td = tr.insertCell(); td.appendChild(document.createTextNode(data[i].pm25)); td.style.border = '1px solid black';
//       td = tr.insertCell(); td.appendChild(document.createTextNode(data[i].pm10)); td.style.border = '1px solid black';
//       td = tr.insertCell(); td.appendChild(document.createTextNode(new Date(data[i].genTime))); td.style.border = '1px solid black';
//       td = tr.insertCell(); td.appendChild(document.createTextNode(new Date(data[i].recTime))); td.style.border = '1px solid black';
//       td = tr.insertCell(); td.appendChild(document.createTextNode(new Date (data[i].transTime))); td.style.border = '1px solid black';


//     }
//     body.appendChild(tbl);
//   }
  

// fetch("http://localhost:3000/getAlerts").then((res)=>{
//     return res.json()
// }).then((data)=>{
//     createTable(data)
// })

function createTable(data) {
  const body = document.body;
  tbl = document.createElement('table');
  tbl.style.width = '100%';
  tbl.style.border = '1px solid black';



  var orderArrayHeader = ["id", "pm2.5", "pm10", "genTime", "recTime", "transTime", "mongoTime"];
  rows = data.length
  var thead = document.createElement('thead');
  tbl.appendChild(thead);
  for (var i=0; i<orderArrayHeader.length; i++) {
      thead.appendChild(document.createElement("th")).
            appendChild(document.createTextNode(orderArrayHeader[i]));
  }

  for (let i = 0; i < rows; i++) {
    var gd = new Date(data[i].genTime)
    var rd = new Date(data[i].recTime)
    var transd = new Date (data[i].transTime)
    var mongotime = new Date(data[i].mongoTimestamp)
    
    const tr = tbl.insertRow();
    let td;
    td = tr.insertCell(); td.appendChild(document.createTextNode(i+1)); td.style.border = '1px solid black';td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(data[i].pm25)); td.style.border = '1px solid black';td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(data[i].pm10)); td.style.border = '1px solid black';td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(gd.toLocaleDateString()+"--"+"--"+gd.toTimeString().split(' ')[0]+":"+gd.getMilliseconds())); td.style.border = '1px solid black'; td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(rd.toLocaleDateString()+"--"+"--"+rd.toTimeString().split(' ')[0]+":"+rd.getMilliseconds())); td.style.border = '1px solid black'; td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(transd.toLocaleDateString()+"----"+transd.toTimeString().split(' ')[0]+":"+transd.getMilliseconds())); td.style.border = '1px solid black'; td.style.textAlign = 'center';
    td = tr.insertCell(); td.appendChild(document.createTextNode(mongotime.toLocaleDateString()+"----"+mongotime.toTimeString().split(' ')[0]+":"+mongotime.getMilliseconds())); td.style.border = '1px solid black'; td.style.textAlign = 'center';
  }
  body.appendChild(tbl);
}


fetch("http://localhost:4000/getAlerts").then((res)=>{
  return res.json()
}).then((data)=>{
console.log("calling the table");
  createTable(data)
})