export interface IUser {
  id: number;
  username: string;
  email: string;
  contactNumber: string;
  lastActive: string;
}

export interface User extends IUser {
  firstName: string;
  lastName: string;
  password: string;
  admin: boolean;
  token: string;
  userInfoId: number;
}

export interface ListUser extends IUser {
  name: string;
}
