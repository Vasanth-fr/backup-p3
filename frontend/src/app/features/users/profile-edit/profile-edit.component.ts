import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile-edit',
  templateUrl: './profile-edit.component.html'
})
export class ProfileEditComponent implements OnInit {

  profileForm!: FormGroup;

  loading = false;
  saved = false;
  error = '';
  userId: number = 0;
  isBusinessOrCreator = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    public router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getCurrentUserId() ?? 0;

    this.profileForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      bio: [''],
      location: [''],
      website: [''],
      privacy: ['PUBLIC'],
      businessName: [''],
      category: [''],
      contactEmail: [''],
      contactPhone: [''],
      businessAddress: [''],
      businessHours: ['']
    });

    this.loadUser();
  }

  loadUser(): void {
    this.userService.getCurrentUserProfile()
      .subscribe(res => {
        this.profileForm.patchValue(res);
      });
  }

  onSubmit(): void {
    if (!this.profileForm) return;

    this.loading = true;
    this.saved = false;
    this.error = '';

    this.userService.updateProfile(this.profileForm.value)
      .subscribe({
        next: () => {
          this.saved = true;
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Update failed';
          this.loading = false;
        }
      });
  }
}