import { Component, OnInit } from '@angular/core';
import { TestServiceService } from 'src/app/services/test-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  private static apiUrl = 'http://localhost:8080/';
  public title = "title";
  public securedTitle = "securedTitle";

  constructor(private testService: TestServiceService) { }

  ngOnInit(): void {
    this.testService.getMessage().subscribe(data => {
      this.title = data.toString();
    });
    this.testService.getSecuredMessage().subscribe(data => {
      this.securedTitle = data.toString();
    });
  }
}
