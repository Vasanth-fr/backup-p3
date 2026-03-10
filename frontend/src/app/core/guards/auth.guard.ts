import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }
    this.router.navigate(
      ['/login'],
      {
        queryParams: { returnUrl: state.url },
        replaceUrl: true
      }
    );
    return false;
  }
}

@Injectable({ providedIn: 'root' })
export class GuestGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) return true;
    this.router.navigate(['/feed'], { replaceUrl: true });
    return false;
  }
}

@Injectable({ providedIn: 'root' })
export class BusinessGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isBusinessOrCreator()) return true;
    this.router.navigate(['/feed'], { replaceUrl: true });
    return false;
  }
}
