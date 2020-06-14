import { Injectable } from '@angular/core'
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http'
import { Observable } from 'rxjs'

import { AuthenticationService } from './../services/authentication.service'

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authenticationService: AuthenticationService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    // add authorization header with jwt token if available
    const jwt = this.authenticationService.jwtResponse
    if (jwt?.token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${jwt.token}`,
        },
      })
    }
    return next.handle(request)
  }
}
