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

    if (!credentials.email || !credentials.password) {
      alert('Cannot have empty credentials');
      return;
    }

    this.authService.login(credentials).subscribe(response => {
      this.insertEmailInStorageAndNavigateToMainPage(credentials.email);
    });
  }

  register() {
    const credentials = this.registerForm.value;

    if (!credentials.email || !credentials.password || !credentials.repeatPassword || !credentials.username) {
      alert('Cannot have empty credentials');
      return;
    }
    if (credentials.password !== credentials.repeatPassword) {
      alert('Passwords do not match');
      return;
    }

    if (!credentials.email.match(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9-]+\.[a-zA-Z]{2,}$/)) {
      alert('Email doesn\'t have the right format. It should contain @, ., and only letters, digits, underscores or dashes are allowed.');
      return;
    }
    
    if (!credentials.username.match(/^[a-zA-Z0-9_-]+$/)) {
      alert('Username contains unsupported characters. Only letters, digits, and underscores are allowed.');
      return;
    }

    this.authService.register(credentials).subscribe(response => {
      this.insertEmailInStorageAndNavigateToMainPage(credentials.email);
    });
  }

  // -------------------------------------

  private insertEmailInStorageAndNavigateToMainPage(email: string) {
    localStorage.setItem('email', email);
    this.router.navigate(['/']);
  }
}
