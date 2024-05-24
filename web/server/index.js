const express = require('express')
const  {MongoClient} = require('mongodb')
var cors = require('cors')

const app = express()
const port = 4000
app.use(cors())



async function dbAction(){

    const uri = "mongodb+srv://mrckd1999:chandan123@cluster0.iuq3qrc.mongodb.net/?retryWrites=true&w=majority";


    const client = new MongoClient(uri);

    try {
        // Connect to the MongoDB cluster
        await client.connect();
        const database = client.db("alerts");
        const dummy = database.collection("Dummy");
        const cursor = dummy.find();
        var arr = []
        for await (const doc of cursor) {
            arr.push(doc)
        }
        return arr

    } catch (e) {
        console.error(e);
    } finally {
        await client.close();
    }
}



app.get('/getAlerts', async (req, res) => {
    var ret = await dbAction()
  res.send(ret)
})

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})