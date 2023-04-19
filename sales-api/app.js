import express  from "express";

import {connectMongoDb} from './src/configs/db/mongoDbConfig.js';
import {createInitialData} from './src/configs/db/initialData.js';
import checkToken from './src/configs/auth/checkToken.js';
import {connectRabbitMq} from './src/configs/rabbitmq/rabbitConfig.js';



const app = express();

const env = process.env;
const PORT = env.PORT || 8082;

connectMongoDb();
createInitialData();   
connectRabbitMq()

app.use(checkToken);

app.get('/api/status', async (req, res) => {
    return res.status(200).json({
        service: "Sales-API",
        status: 'up',
        httpStatus: 200
    })
})

app.listen(PORT, () => {
    console.info(`Server started sucessfully at port ${PORT}`);
})