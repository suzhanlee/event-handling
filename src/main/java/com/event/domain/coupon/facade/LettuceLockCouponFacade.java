package com.event.domain.coupon.facade;

import com.event.domain.coupon.repository.RedisLockRepository;
import com.event.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockCouponFacade {

    private final RedisLockRepository redisLockRepository;
    private final CouponService couponService;

    public void decrease(Long couponId) throws InterruptedException {
        while (!redisLockRepository.lock(couponId)) {
            Thread.sleep(100);
        }

        try {
            couponService.decrease(couponId);
        } finally {
            redisLockRepository.unlock(couponId);
        }
    }

}
