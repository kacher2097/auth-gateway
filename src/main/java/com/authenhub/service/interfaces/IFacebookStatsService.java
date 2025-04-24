package com.authenhub.service.interfaces;

import com.authenhub.bean.facebook.stats.FacebookEngagementResponse;
import com.authenhub.bean.facebook.stats.FacebookPageStatsResponse;
import com.authenhub.bean.facebook.stats.FacebookPostStatsResponse;

import java.sql.Timestamp;

public interface IFacebookStatsService {
    /**
     * Lấy thống kê của bài viết
     * @param postId ID bài viết
     * @param userId ID người dùng
     * @return Thống kê bài viết
     */
    FacebookPostStatsResponse getPostStats(String postId, String userId);
    
    /**
     * Lấy thống kê của trang
     * @param pageId ID trang
     * @param userId ID người dùng
     * @return Thống kê trang
     */
    FacebookPageStatsResponse getPageStats(String pageId, String userId);
    
    /**
     * Lấy dữ liệu tương tác theo thời gian
     * @param id ID trang hoặc bài viết
     * @param type Loại ("page" hoặc "post")
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param userId ID người dùng
     * @return Dữ liệu tương tác
     */
    FacebookEngagementResponse getEngagementData(String id, String type, Timestamp startDate, Timestamp endDate, String userId);
}
