package com.authenhub.service.interfaces;

import com.authenhub.bean.facebook.post.FacebookPostListResponse;
import com.authenhub.bean.facebook.post.FacebookPostRequest;
import com.authenhub.bean.facebook.post.FacebookPostResponse;
import com.authenhub.bean.facebook.post.FacebookScheduledPostRequest;

public interface IFacebookPostService {
    /**
     * Lấy danh sách bài viết của trang hoặc người dùng
     * @param targetId ID trang hoặc người dùng
     * @param userId ID người dùng
     * @param limit Số lượng bài viết tối đa
     * @param offset Vị trí bắt đầu
     * @return Danh sách bài viết
     */
    FacebookPostListResponse getPosts(String targetId, String userId, int limit, int offset);
    
    /**
     * Lấy chi tiết bài viết
     * @param postId ID bài viết
     * @param userId ID người dùng
     * @return Chi tiết bài viết
     */
    FacebookPostResponse getPostDetails(String postId, String userId);
    
    /**
     * Tạo bài viết mới
     * @param targetId ID trang hoặc người dùng
     * @param request Thông tin bài viết
     * @param userId ID người dùng
     * @return Bài viết đã tạo
     */
    FacebookPostResponse createPost(String targetId, FacebookPostRequest request, String userId);
    
    /**
     * Cập nhật bài viết
     * @param postId ID bài viết
     * @param request Thông tin cập nhật
     * @param userId ID người dùng
     * @return Bài viết sau khi cập nhật
     */
    FacebookPostResponse updatePost(String postId, FacebookPostRequest request, String userId);
    
    /**
     * Xóa bài viết
     * @param postId ID bài viết
     * @param userId ID người dùng
     * @return true nếu xóa thành công
     */
    boolean deletePost(String postId, String userId);
    
    /**
     * Lên lịch đăng bài
     * @param targetId ID trang hoặc người dùng
     * @param request Thông tin bài viết và thời gian đăng
     * @param userId ID người dùng
     * @return Bài viết đã lên lịch
     */
    FacebookPostResponse schedulePost(String targetId, FacebookScheduledPostRequest request, String userId);
}
