package com.sixcandoit.roomservice.repository.orders;

import com.sixcandoit.roomservice.entity.orders.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {

    public Optional<MenuEntity> findByIdx(Integer idx);

    //메뉴명만
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuName like %:keyword%")
    Page<MenuEntity> searchMenuTitle(@Param("keyword") String keyword, Pageable page);

    //메뉴설명만
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuContent like %:keyword%")
    Page<MenuEntity> searchMenuContent(@Param("keyword") String keyword, Pageable page);

    //메뉴명+메뉴설명
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuName like %:keyword% or u.menuContent like %:keyword%")
    Page<MenuEntity> searchMenuNameAndMenuContent(@Param("keyword") String keyword, Pageable page);

    //카테고리
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuCategory like %:keyword%")
    Page<MenuEntity> searchMenuCategory(@Param("keyword") String keyword, Pageable page);

    //전체
    @Query("SELECT u From MenuEntity u WHERE " +
            "u.menuName like %:keyword% or u.menuContent like %:keyword% or u.menuCategory like %:keyword%")
    Page<MenuEntity> searchMenuAll(@Param("keyword") String keyword, Pageable page);

}
