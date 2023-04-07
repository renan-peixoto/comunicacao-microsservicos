import UserServices from "../services/UserServices.js";

class UserController {
  async getAccessToken(req, res) {
    let accessToken = await UserServices.getAccessToken(req);
    return res.status(accessToken.status).json(accessToken);
  }

  async findByEmail(req, res) {
    let user = await UserServices.findByEmail(req);
    return res.status(user.status).json(user);
  }
}

export default new UserController();
