package com.event.domain.coupon.facade;

import com.event.domain.coupon.service.OptimisticCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockCouponFacade {

    private final OptimisticCouponService optimisticCouponService;

    public void decrease(Long couponId) throws InterruptedException {
        while (true) {
            try {
                optimisticCouponService.decrease(couponId);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
