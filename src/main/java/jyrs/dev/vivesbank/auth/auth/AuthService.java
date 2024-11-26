package jyrs.dev.vivesbank.auth.auth;

import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSingInInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;

public interface AuthService {
    JwtAuthResponse singUp(UserSignUpRequest request) throws UserAuthNameOrEmailExisten;
    JwtAuthResponse signIn(UserSignInRequest request) throws AuthSingInInvalid;
}
