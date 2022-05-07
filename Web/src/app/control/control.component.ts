import { Component, OnInit } from '@angular/core';
import {ControlService} from "../service/control.service";

@Component({
  selector: 'app-control',
  templateUrl: './control.component.html',
  styleUrls: ['./control.component.scss']
})
export class ControlComponent implements OnInit {
  successful = false;
  error: any = null;

  constructor(
    private controlService: ControlService
  ) { }

  ngOnInit(): void {
    this.controlService.messages$.subscribe((message: any) => {
      if (message == "ping") {
        this.controlService.send("pong");
        this.successful = true;
      }
    }, (err: any) => {
      if (!(err as CloseEvent)) {
        console.error(err);
        this.error = err;
      }
    });
  }

}
