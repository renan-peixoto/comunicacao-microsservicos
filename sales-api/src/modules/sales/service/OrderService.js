import OrderRepository from "../repositories/OrderRepository.js";
import { sendProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import { PENDING, ACCEPTED, REJECTED } from "../status/OrderStatus.js";
import {
  BAD_REQUEST,
  SUCCESS,
  INTERNAL_SERVER_ERROR,
} from "../../../configs/constants/httpStatus.js";
import OrderException from "../exceptions/OrderException.js";
import ProductClient from "../../product/rabbitmq/client/ProductClient.js";

class OrderService {
  async createOrder(req) {
    try {
      let orderData = req.body;
      this.validadeOrderData(orderData);
      const { authUser } = req;
      const { authorization } = req.headers;
      let order = this.createInitialOrderData(orderData, authUser);
      await this.validateProductStock(order, authorization);

      let createdOrder = await OrderRepository.save(order);
      this.sendMessage(createdOrder);
      let response = {
        status: SUCCESS,
        createdOrder,
      };
      console.info(
        `Response to POST login with data ${JSON.stringify(response)}`
      );
      return response;
    } catch (err) {
      return {
        status: err.status ? err.status : INTERNAL_SERVER_ERROR,
        message: err.message,
      };
    }
  }

  createInitialOrderData(orderData, authUser) {
    return {
      status: PENDING,
      user: authUser,
      createdAt: new Date(),
      updatedAt: new Date(),
      products: orderData.products,
    };
  }

  async updateOrder(orderMessage) {
    try {
      const order = JSON.parse(orderMessage);
      if (order.salesId && order.status) {
        let existingOrder = await OrderRepository.findById(order.salesId);
        if (existingOrder && order.status !== existingOrder.status) {
          existingOrder.status = order.status;
          existingOrder.updatedAt = new Date();
          await OrderRepository.save(existingOrder);
        }
      } else {
        console.warn("The order message was not complete.");
      }
    } catch (err) {
      console.error("Could not parse order message from queue.");
      console.error(err.message);
    }
  }

  validadeOrderData(data) {
    if (!data || !data.products) {
      throw new OrderException(BAD_REQUEST, "The products must be informed.");
    }
  }

  async validateProductStock(order, token) {
    let stockIsOk = await ProductClient.checkProductStock(
      order.products,
      token
    );
    if (!stockIsOk) {
      throw new OrderException(
        BAD_REQUEST,
        "The stock is out for the products"
      );
    }
  }

  sendMessage(createdOrder) {
    const message = {
      salesId: createdOrder.id,
      products: createdOrder.products,
    };
    sendProductStockUpdateQueue(message);
  }

  async findById(req) {
    try {
      const { id } = req.params;
      this.validateInformedId(id);
      const existingOrder = await OrderRepository.findById(id);
      if (!existingOrder) {
        throw new OrderException(BAD_REQUEST, "The order was not found.");
      }
      return {
        status: SUCCESS,
        existingOrder,
      };
    } catch (err) {
      return {
        status: err.status ? err.status : INTERNAL_SERVER_ERROR,
        message: err.message,
      };
    }
  }

  async findAll() {
    try {
      const orders = await OrderRepository.findAll();
      if (!orders) {
        throw new OrderException(BAD_REQUEST, "No orders were not found.");
      }
      return {
        status: SUCCESS,
        orders,
      };
    } catch (err) {
      return {
        status: err.status ? err.status : INTERNAL_SERVER_ERROR,
        message: err.message,
      };
    }
  }

  async findByProductId() {
    try {
      const { productId } = req.params;
      this.validateInformedProductId(productId);
      const orders = await OrderRepository.findByProductId(productId);
      if (!orders) {
        throw new OrderException(BAD_REQUEST, "No orders were not found.");
      }
      return {
        status: SUCCESS,
        salesIds: orders.map((order) => {
          return order.id;
        }),
      };
    } catch (err) {
      return {
        status: err.status ? err.status : INTERNAL_SERVER_ERROR,
        message: err.message,
      };
    }
  }

  validateInformedId(id) {
    if (!id) {
      throw new OrderException(BAD_REQUEST, "the order ID must be indormed!");
    }
  }

  validateInformedProductId(productId) {
    if (!productId) {
      throw new OrderException(
        BAD_REQUEST,
        "the order's productId must be indormed!"
      );
    }
  }
}

export default new OrderService();
