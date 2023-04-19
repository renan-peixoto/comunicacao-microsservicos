import Order from '../../modules/sales/model/Order.js'

export async function createInitialData() {
    await Order.collection.drop();
    
    await Order.create({
        products: [
            {
                productId: 1001,
                quantity: 2,
            },
            {
                productId: 1002,
                quantity: 1,
            },
            {
                productId: 1003,
                quantity: 1,
            }
        ],
        user: {
            id: "dflabsdljfbasdfadf",
            name: "User test",
            email: "usertest@email.com"
        },
        status: "APPROVED",
        createdAt: new Date(),
        updatedAt: new Date(),
    });
    await Order.create({
        products: [
            {
                productId: 1001,
                quantity: 4,
            },
            {
                productId: 1003,
                quantity: 2,
            },
        ],
        user: {
            id: "gdsfghsngçvjkasre",
            name: "User test2",
            email: "usertest2@email.com"
        },
        status: "REJECTED",
        createdAt: new Date(),
        updatedAt: new Date()
    });
    let initialData = await Order.find();
    console.info(
        `Initial data was created: ${JSON.stringify(initialData, undefined, 4)}`
        )
}