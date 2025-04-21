package com.authenhub.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role {
    @Id
    private String id;
    
    private String name;        // Tên vai trò, ví dụ: "admin"
    private String displayName; // Tên hiển thị, ví dụ: "Administrator"
    private String description; // Mô tả vai trò
    private boolean isSystem;   // Đánh dấu vai trò hệ thống (không thể xóa)
    private Set<String> permissionIds = new HashSet<>(); // Danh sách ID của các quyền
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
