package com.sixcandoit.roomservice.repository.cart;

import com.sixcandoit.roomservice.entity.cart.CartMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMenuRepository extends JpaRepository<CartMenuEntity, Integer> {

    public CartMenuEntity findByCartEntity_IdxAndIdx(Integer cartIdx, Integer menuIdx);

    public List<CartMenuEntity> findByCartEntity_Idx(Integer cartIdx);

//    @Query("select new com.sixcandoit.roomservice.dto.cart.CartDetailDTO(cm.idx, m.menuName, m.menuPrice, cm.count, m.menuImg)" +
//            " from CartMenuEntity cm" +
//            " join cm.menuEntity m where cm.cartEntity.idx = :cartIdx" +
//            " and m.menuImg.idx = cm.menuEntity.idx" +
//            " and if.repigYn = 'Y' " +
//            " order by cm.idx desc ")
//    public List<CartDetailDTO> findByCartDetailDTOList(Integer cartIdx);

}
