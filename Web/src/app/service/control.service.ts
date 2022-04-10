import { Injectable } from '@angular/core';
import {webSocket, WebSocketSubject} from "rxjs/webSocket";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ControlService {
  private socket$ = new (webSocket as any)({
    url: environment.controlUrl,
    deserializer: (event: MessageEvent) => {
      const stringMessage = event.data as string;
      let jsonMessage = null;

      try {
        jsonMessage = JSON.parse(event.data);
      } catch {
        return stringMessage;
      }

      return jsonMessage;
    },
    serializer: (value: any) => {
      return value;
    }
  });
  private _messages$ = this.socket$.asObservable();

  constructor() { }

  get messages$() {
    return this._messages$;
  }

  send(message: string) {
    this.socket$.next(message);
  }
}
