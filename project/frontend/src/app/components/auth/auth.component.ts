import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent {
  loginForm: FormGroup;
  registerForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });

    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required],
      username: ['', Validators.required]
    });
  }

  login() {
    const credentials = this.loginForm.value;
    this.authService.login(credentials).subscribe(response => {
      this.router.navigate(['/']);
    });
  }

  register() {
    const user = this.registerForm.value;

    if (user.password !== user.repeatPassword) {
      window.alert('Passwords do not match');
      return;
    }
    this.authService.register(user).subscribe(response => {
      this.router.navigate(['/']);
    });
  }
}
