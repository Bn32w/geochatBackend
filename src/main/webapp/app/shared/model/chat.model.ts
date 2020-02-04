import { Moment } from 'moment';

export interface IChat {
  id?: string;
  message?: string;
  srcUser?: string;
  dstUser?: string;
  date?: Moment;
}

export class Chat implements IChat {
  constructor(public id?: string, public message?: string, public srcUser?: string, public dstUser?: string, public date?: Moment) {}
}
