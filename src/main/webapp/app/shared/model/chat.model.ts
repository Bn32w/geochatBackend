export interface IChat {
  id?: string;
  message?: string;
  srcUser?: string;
  dstUser?: string;
}

export class Chat implements IChat {
  constructor(public id?: string, public message?: string, public srcUser?: string, public dstUser?: string) {}
}
