import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthComponent } from './auth.component';
import { AuthService } from 'src/app/services/auth.service';

describe('AuthComponent', () => {
  let component: AuthComponent;
  let fixture: ComponentFixture<AuthComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceMock = jasmine.createSpyObj('AuthService', ['login', 'register']);
    const routerMock = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [AuthComponent],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthComponent);
    component = fixture.componentInstance;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('login', () => {
    it('should call authService.login and navigate to main-page', () => {
      const credentials = { email: 'test@example.com', password: 'password' };
      authServiceSpy.login.and.returnValue(of({}));
      
      component.loginForm.setValue(credentials);
      component.login();

      expect(authServiceSpy.login).toHaveBeenCalledWith(credentials);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  describe('register', () => {
    it('should call authService.register and navigate to /', () => {
      const user = { email: 'test@example.com', username: 'username', password: 'password' };
      authServiceSpy.register.and.returnValue(of({}));

      component.registerForm.setValue(user);
      component.register();

      expect(authServiceSpy.register).toHaveBeenCalledWith(user);
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });
});