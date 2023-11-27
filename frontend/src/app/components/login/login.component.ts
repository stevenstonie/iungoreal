import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  credentials: any = {};

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.credentials = {
      username: '',
      password: ''
    };
  }

  login(): void {
    this.authService.login(this.credentials).subscribe(
      data => {
        console.log(data);
      },
      error => {
        console.log(error);
      }
    );
  }

}


// export class LoginComponent implements OnInit {
//   private static apiUrl = 'http://localhost:8080/api';
//   public title = "title";
//   public securedTitle = "securedTitle";

//   constructor(private testService: LoginTestService) { }

//   ngOnInit(): void {
//     this.testService.getMessage().subscribe(data => {
//       this.title = data.toString();
//     });
//     this.testService.getSecuredMessage().subscribe(data => {
//       this.securedTitle = data.toString();
//     });
//   }
// }
