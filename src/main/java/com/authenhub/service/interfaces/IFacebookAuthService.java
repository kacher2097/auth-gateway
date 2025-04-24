package com.authenhub.service.interfaces;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;

public interface IFacebookAuthService {
    /**
     * Tạo URL xác thực OAuth cho Facebook
     * @param redirectUri URI callback sau khi xác thực
     * @return URL xác thực
     */
    String createAuthorizationUrl(String redirectUri);
    
    /**
     * Xử lý callback từ Facebook sau khi xác thực
     * @param code Authorization code từ Facebook
     * @param redirectUri URI callback đã sử dụng
     * @return Thông tin token
     */
    FacebookTokenInfo handleAuthorizationCallback(String code, String redirectUri);
    
    /**
     * Lấy thông tin token hiện tại của người dùng
     * @param userId ID người dùng
     * @return Thông tin token
     */
    FacebookTokenInfo getUserToken(String userId);
    
    /**
     * Làm mới token khi hết hạn
     * @param userId ID người dùng
     * @return Thông tin token mới
     */
    FacebookTokenInfo refreshToken(String userId);
    
    /**
     * Hủy token
     * @param userId ID người dùng
     */
    void revokeToken(String userId);
}
