package com.event.domain.coupon.service;

import com.event.db.entity.Coupon;
import com.event.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PessimisticCouponService {

    private final CouponRepository couponRepository;

    public void decrease(Long couponId) {

        // 쿠폰 ID 기준으로 쿠폰 조회
        Coupon coupon = couponRepository.findByIdWithPessimisticLockByCouponId(couponId);

        // 재고 감소
        coupon.decrease();
    }
}
