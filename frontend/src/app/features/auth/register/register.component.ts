import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  registerForm: FormGroup;
  loading = false;
  error = '';
  success = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {

    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: [''],
      username: ['', Validators.required],   // NEW
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      accountType: ['', Validators.required]
    });

  }

  get f() { return this.registerForm.controls; }

  onSubmit() {

    if (this.registerForm.invalid) return;

    this.loading = true;
    this.error = '';
    this.success = '';

    const { email, password, firstName, lastName, username, accountType } =
      this.registerForm.getRawValue();

    this.authService.register({
      email,
      password,
      firstName,
      lastName: lastName || undefined,
      username,
      accountType
    }).subscribe({

      next: () => {
        this.loading = false;
        this.success = 'Registration successful! Please login to continue.';

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },

      error: (err) => {
        this.error = err.error?.message || 'Registration failed. Please try again.';
        this.loading = false;
      }

    });
  }
}
