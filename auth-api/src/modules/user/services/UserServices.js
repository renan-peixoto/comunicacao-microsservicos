import * as httpStatus from "../../../config/constants/httpStatus.js";
import UserException from "../exceptions/UserException.js";
import UserRepository from "../repositories/UserRepository.js";

class UserService {
  async findByEmail(req) {
    try {
      const { email } = req.params;
      this.validateRequestData(email);
      let user = await UserRepository.findByEmail(email);

      this.validateUserNotFound(user);
      return {
        status: httpStatus.SUCCESS,
        user: {
          id: user.id,
          name: user.name,
          email: user.email,
        },
      };
    } catch (err) {
      return {
        status: err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR,
        message: err.status,
      };
    }
  }

  validateUserNotFound(user) {
    if (!user) {
      throw new UserException(httpStatus.BAD_REQUEST, "User not found.");
    }
  }

  validateRequestData(email) {
    if (!email) {
      throw new UserException(
        httpStatus.BAD_REQUEST,
        "User email was not informed."
      );
    }
  }
}

export default new UserService();
