import bcrypt from "bcrypt";
import User from "../../modules/user/model/User.js";

export async function createInitialData() {
  try {
    await User.sync({ force: true });

    let password = await bcrypt.hash("123456", 10);

    await User.create({
      name: "User Teste 1",
      email: "testeuser1@email.com",
      password: password,
    });

    await User.create({
      name: "User Teste 2",
      email: "testeuser2@email.com",
      password: password,
    });
  } catch (err) {
    console.log(err);
  }
}
