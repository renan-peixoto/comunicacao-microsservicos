import UserServices from "../services/UserServices.js";

class UserController {
  async findByEmail(req, res) {
    let user = await UserServices.findByEmail(req);
    return res.status(user.status).json(user);
  }
}

export default new UserController();
