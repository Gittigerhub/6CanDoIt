package com.sixcandoit.roomservice.repository.menu;

import com.sixcandoit.roomservice.constant.MenuCategory;
import com.sixcandoit.roomservice.entity.menu.MenuEntity;
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
            "u.menuCategory = :keyword")
    Page<MenuEntity> searchMenuCategory(@Param("keyword") String keyword, Pageable page);

    // 목록 카테고리 선택
    @Query("SELECT u FROM MenuEntity u WHERE u.menuCategory = :categoryEnum and u.organizationJoin.idx = :organIdx")
    Page<MenuEntity> selectCate(@Param("categoryEnum") MenuCategory categoryEnum,@Param("organIdx") Integer organIdx, Pageable page);

    //전체
    @Query("SELECT u From MenuEntity u WHERE " +
            "(u.menuName like %:keyword% or u.menuContent like %:keyword%) or u.menuCategory = :keyword")
    Page<MenuEntity> searchMenuAll(@Param("keyword") String keyword, Pageable page);

    // ================================================

    //메뉴명만
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuName like %:keyword%")
    Page<MenuEntity> MenuName(@Param("keyword") String keyword, Pageable page);

    //메뉴설명만
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuContent like %:keyword%")
    Page<MenuEntity> MenuContent(@Param("keyword") String keyword, Pageable page);

    //메뉴명+메뉴설명
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuName like %:keyword% or u.menuContent like %:keyword%")
    Page<MenuEntity> MenuNameContent(@Param("keyword") String keyword, Pageable page);

    //카테고리
    @Query("SELECT u FROM MenuEntity u WHERE " +
            "u.menuCategory = :keyword")
    Page<MenuEntity> Category(@Param("keyword") String keyword, Pageable page);

    //목록 카테고리 선택
    @Query("SELECT u FROM MenuEntity u WHERE u.menuCategory = :categoryEnum")
    Page<MenuEntity> CategoryList(@Param("categoryEnum") MenuCategory categoryEnum, Pageable page);

    // 전체
    @Query("SELECT u From MenuEntity u where u.organizationJoin.idx = :organIdx")
    Page<MenuEntity> AllMenu(Pageable page, Integer organIdx);

    // 전체 + 검색어
    @Query("SELECT u From MenuEntity u WHERE " +
            "(u.menuName like %:keyword% or u.menuContent like %:keyword%) or u.menuCategory = :keyword")
    Page<MenuEntity> AllMenuKeyword(@Param("keyword") String keyword, Pageable page);

}