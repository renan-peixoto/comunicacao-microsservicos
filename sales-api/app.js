import express from "express";

import { connectMongoDb } from "./src/configs/db/mongoDbConfig.js";
import { createInitialData } from "./src/configs/db/initialData.js";
import checkToken from "./src/configs/auth/checkToken.js";
import orderRoutes from "./src/modules/sales/routes/OrderRoutes.js";
import { connectRabbitMq } from "./src/configs/rabbitmq/rabbitConfig.js";

import { sendProductStockUpdateQueue } from "./src/modules/product/rabbitmq/productStockUpdateSender.js";
const app = express();

const env = process.env;
const PORT = env.PORT || 8082;

connectMongoDb();
createInitialData();
connectRabbitMq();

app.use(express.json());
app.use(checkToken);
app.use(orderRoutes);

app.get("/test", (req, res) => {
  try {
    sendProductStockUpdateQueue([
      {
        productId: 1001,
        quantity: 3,
      },
      {
        productId: 1002,
        quantity: 2,
      },
      {
        productId: 1003,
        quantity: 1,
      },
    ]);
    return res.status(200).json({ status: 200 });
  } catch (error) {
    console.log(error);
    return res.status(500).json({ error: true });
  }
});

app.get("/api/status", async (req, res) => {
  return res.status(200).json({
    service: "Sales-API",
    status: "up",
    httpStatus: 200,
  });
});

app.listen(PORT, () => {
  console.info(`Server started sucessfully at port ${PORT}`);
});
