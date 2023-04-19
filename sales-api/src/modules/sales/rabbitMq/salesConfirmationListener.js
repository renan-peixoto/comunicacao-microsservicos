import amqp from 'amqplib/callback_api.js';

import { RABBIT_MQ_URL } from '../../../configs/constants/secrets.js';
import { SALES_CONFIRMATION_QUEUE } from '../../../configs/rabbitmq/queue.js';

export function listenToSalesConfirmationQueue() {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if (error) {
            throw error
        }
        console.info("Listening to Sales Confirmation Queue...")
        connection.createChannel((error, channel) => {
            if (error) {
                throw error
            }
            channel.consume(
                SALES_CONFIRMATION_QUEUE,
                (message) => {
                    console.info(
                        `Receiving message from queue: ${message.content.toString()}`
                    );
                }, 
                {
                    noAck: true,
                }
            );
        });
    });
}