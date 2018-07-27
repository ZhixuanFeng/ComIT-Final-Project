package com.fengzhixuan.timoc.data.repository;

import com.fengzhixuan.timoc.data.entity.Offer;
import com.fengzhixuan.timoc.data.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OfferRepository extends PagingAndSortingRepository<Offer, Long>, JpaSpecificationExecutor
{
    Offer findById(long id);

    List<Offer> findByUser(User user);

    List<Offer> findByExpDate(Date date);

    Offer findByCardId(long id);

    //    @Query(value = "SELECT o FROM offer O WHERE o.indecks BETWEEN :suit * 13 and :suit * 13 + 12", nativeQuery = true)
//    List<Offer> findBySuit(@Param("suit") int suit);
//
//    @Query(value = "SELECT o FROM offer O WHERE o.indecks = :rank - 1 or o.indecks = :rank + 12 or o.indecks = :rank + 25 or o.indecks = :rank + 38", nativeQuery = true)
//    List<Offer> findByRank(@Param("rank") int rank);
//
//    List<Offer> findByIndecks(int indecks);

    //@Query(value = "SELECT o FROM offer O WHERE o.indecks BETWEEN :indecks1 and :indecks2", nativeQuery = true)
    //List<Offer> findByIndecksRange(@Param("indecks1") int indecks1, @Param("indecks2") int indecks2);
}
