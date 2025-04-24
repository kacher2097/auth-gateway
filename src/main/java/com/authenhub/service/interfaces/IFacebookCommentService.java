package com.authenhub.service.interfaces;

import com.authenhub.bean.facebook.comment.FacebookAutoReplyRuleRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentFilterRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentResponse;
import com.authenhub.entity.mongo.FacebookAutoReplyRule;
import com.authenhub.entity.mongo.FacebookCommentFilter;

import java.util.List;

public interface IFacebookCommentService {
    /**
     * Lấy danh sách comment của bài viết
     * @param postId ID bài viết
     * @param userId ID người dùng
     * @param limit Số lượng comment tối đa
     * @param offset Vị trí bắt đầu
     * @return Danh sách comment
     */
    List<FacebookCommentResponse> getComments(String postId, String userId, int limit, int offset);
    
    /**
     * Tạo comment mới
     * @param postId ID bài viết
     * @param request Nội dung comment
     * @param userId ID người dùng
     * @return Comment đã tạo
     */
    FacebookCommentResponse createComment(String postId, FacebookCommentRequest request, String userId);
    
    /**
     * Xóa comment
     * @param commentId ID comment
     * @param userId ID người dùng
     * @return true nếu xóa thành công
     */
    boolean deleteComment(String commentId, String userId);
    
    /**
     * Thêm quy tắc trả lời tự động
     * @param request Quy tắc trả lời
     * @param userId ID người dùng
     * @return Quy tắc đã thêm
     */
    FacebookAutoReplyRule addAutoReplyRule(FacebookAutoReplyRuleRequest request, String userId);
    
    /**
     * Lấy danh sách quy tắc trả lời tự động
     * @param targetId ID trang hoặc bài viết
     * @param userId ID người dùng
     * @return Danh sách quy tắc
     */
    List<FacebookAutoReplyRule> getAutoReplyRules(String targetId, String userId);
    
    /**
     * Xóa quy tắc trả lời tự động
     * @param ruleId ID quy tắc
     * @param userId ID người dùng
     * @return true nếu xóa thành công
     */
    boolean deleteAutoReplyRule(String ruleId, String userId);
    
    /**
     * Thêm quy tắc lọc comment
     * @param request Quy tắc lọc
     * @param userId ID người dùng
     * @return Quy tắc đã thêm
     */
    FacebookCommentFilter addCommentFilterRule(FacebookCommentFilterRequest request, String userId);
}
