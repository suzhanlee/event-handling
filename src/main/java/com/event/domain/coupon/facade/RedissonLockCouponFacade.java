package com.event.domain.coupon.facade;

import com.event.domain.coupon.service.CouponService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonLockCouponFacade {

    private final CouponService couponService;
    private final RedissonClient redissonClient;

    public void decrease(Long couponId) {
        RLock lock = redissonClient.getLock(couponId.toString());

        try {
            boolean available = lock.tryLock(60, 1, TimeUnit.SECONDS);
            if (available) {
                couponService.decrease(couponId);
                return;
            }

            System.out.println("**** lock 획득 실패 ****");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
