package com.event.domain.coupon.repository;

import com.event.db.entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id =:couponId")
    Coupon findByIdWithPessimisticLockByCouponId(@Param("couponId") Long couponId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select c from Coupon c where c.id =:couponId")
    Coupon findByIdWithOptimisticLockByCouponId(@Param("couponId") Long couponId);
}
