import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ControlComponent} from "./control/control.component";

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: ControlComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
