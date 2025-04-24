package com.authenhub.service.interfaces;

import com.authenhub.bean.facebook.page.FacebookPageRequest;
import com.authenhub.bean.facebook.page.FacebookPageResponse;

import java.util.List;

public interface IFacebookPageService {
    /**
     * Lấy danh sách trang mà người dùng quản lý
     * @param userId ID người dùng
     * @return Danh sách trang
     */
    List<FacebookPageResponse> getUserPages(String userId);
    
    /**
     * Lấy thông tin chi tiết của trang
     * @param pageId ID trang
     * @param userId ID người dùng
     * @return Thông tin trang
     */
    FacebookPageResponse getPageDetails(String pageId, String userId);
    
    /**
     * Cập nhật thông tin trang
     * @param pageId ID trang
     * @param request Thông tin cập nhật
     * @param userId ID người dùng
     * @return Thông tin trang sau khi cập nhật
     */
    FacebookPageResponse updatePageInfo(String pageId, FacebookPageRequest request, String userId);
    
    /**
     * Đồng bộ danh sách trang từ Facebook
     * @param userId ID người dùng
     * @return Danh sách trang đã đồng bộ
     */
    List<FacebookPageResponse> syncPages(String userId);
}
