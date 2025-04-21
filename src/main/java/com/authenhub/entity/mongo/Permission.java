package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "permissions")
public class Permission {
    @Id
    private String id;
    
    private String name;        // Tên quyền, ví dụ: "user:create"
    private String displayName; // Tên hiển thị, ví dụ: "Create User"
    private String description; // Mô tả quyền
    private String category;    // Phân loại quyền, ví dụ: "User Management"
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Phương thức tiện ích để tạo permission key
    public static String createPermissionKey(String resource, String action) {
        return resource + ":" + action;
    }
}
