import { Router } from "express";
import OrderController from "../controller/OrderController.js";

const router = new Router();

router.post("/api/order/create", OrderController.createOrder);
router.get("/api/order/:id", OrderController.findById);
router.get("/api/order/product/:productId", OrderController.findByProductId);
router.get("/api/orders", OrderController.findAll);

export default router;
