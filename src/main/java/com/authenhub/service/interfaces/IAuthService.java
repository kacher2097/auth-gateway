package com.authenhub.service.interfaces;

import com.authenhub.bean.ChangePasswordRequest;
import com.authenhub.bean.ForgotPasswordRequest;
import com.authenhub.bean.OAuth2CallbackRequest;
import com.authenhub.bean.RegisterRequest;
import com.authenhub.bean.ResetPasswordRequest;
import com.authenhub.bean.SocialLoginRequest;
import com.authenhub.bean.auth.AuthRequest;
import com.authenhub.bean.auth.AuthResponse;
import com.authenhub.bean.auth.UserInfo;

/**
 * Interface for authentication service operations
 */
public interface IAuthService {
    
    /**
     * Register a new user
     *
     * @param request register request
     * @return auth response
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Login user
     *
     * @param request auth request
     * @return auth response
     */
    AuthResponse login(AuthRequest request);
    
    /**
     * Social login
     *
     * @param request social login request
     * @return auth response
     */
    AuthResponse socialLogin(SocialLoginRequest request);
    
    /**
     * Get current user
     *
     * @param token auth token
     * @return user info
     */
    Object getCurrentUser(String token);
    
    /**
     * Handle OAuth2 callback
     *
     * @param request OAuth2 callback request
     * @return auth response
     */
    AuthResponse handleOAuth2Callback(OAuth2CallbackRequest request);
    
    /**
     * Forgot password
     *
     * @param request forgot password request
     */
    void forgotPassword(ForgotPasswordRequest request);
    
    /**
     * Reset password
     *
     * @param request reset password request
     */
    void resetPassword(ResetPasswordRequest request);
    
    /**
     * Change password
     *
     * @param request change password request
     */
    void changePassword(ChangePasswordRequest request);
    AuthResponse refreshToken(String token);
}
