package com.sixcandoit.roomservice.repository.cart;

import com.sixcandoit.roomservice.dto.cart.CartDetailDTO;
import com.sixcandoit.roomservice.entity.cart.CartMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenuEntity, Integer> {

    // 장바구니 -> 장바구니에 넣기로 한 메뉴가 카트에 있는지 조회
    @Query("select c from CartMenuEntity c where c.cartEntity.idx = :cartIdx and c.menuEntity.idx = :menuIdx")
    Optional<CartMenuEntity> findByCartMenu(@Param("cartIdx") Integer cartIdx, @Param("menuIdx") Integer menuIdx);

    public List<CartMenuEntity> findByCartEntity_Idx(Integer cartIdx);

    @Query("select new com.sixcandoit.roomservice.dto.cart.CartDetailDTO(cm.idx, m.menuName, m.menuPrice, cm.count, im.url)" +
            " from CartMenuEntity cm, ImageFileEntity im" +
            " join cm.menuEntity m where cm.cartEntity.idx = :cartIdx" +
            " and im.menuJoin.idx = cm.menuEntity.idx" +
            " and im.repimageYn = 'Y' " +
            " order by cm.idx desc ")
    public List<CartDetailDTO> findByCartDetailDTOList(Integer cartIdx);

}
