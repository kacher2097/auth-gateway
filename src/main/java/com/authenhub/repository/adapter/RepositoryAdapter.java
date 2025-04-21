package com.authenhub.repository.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interface chung cho các repository adapter
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface RepositoryAdapter<T, ID> {
    
    /**
     * Lưu một entity
     * @param entity Entity cần lưu
     * @return Entity đã lưu
     */
    T save(T entity);
    
    /**
     * Lưu nhiều entity
     * @param entities Danh sách entity cần lưu
     * @return Danh sách entity đã lưu
     */
    List<T> saveAll(Iterable<T> entities);
    
    /**
     * Tìm entity theo ID
     * @param id ID của entity
     * @return Optional chứa entity nếu tìm thấy
     */
    Optional<T> findById(ID id);
    
    /**
     * Kiểm tra entity có tồn tại không
     * @param id ID của entity
     * @return true nếu entity tồn tại
     */
    boolean existsById(ID id);
    
    /**
     * Lấy tất cả entity
     * @return Danh sách tất cả entity
     */
    List<T> findAll();
    
    /**
     * Lấy tất cả entity với phân trang
     * @param pageable Thông tin phân trang
     * @return Page chứa các entity
     */
    Page<T> findAll(Pageable pageable);
    
    /**
     * Đếm số lượng entity
     * @return Số lượng entity
     */
    long count();
    
    /**
     * Xóa entity theo ID
     * @param id ID của entity cần xóa
     */
    void deleteById(ID id);
    
    /**
     * Xóa entity
     * @param entity Entity cần xóa
     */
    void delete(T entity);
    
    /**
     * Xóa nhiều entity
     * @param entities Danh sách entity cần xóa
     */
    void deleteAll(Iterable<T> entities);
    
    /**
     * Xóa tất cả entity
     */
    void deleteAll();
}
