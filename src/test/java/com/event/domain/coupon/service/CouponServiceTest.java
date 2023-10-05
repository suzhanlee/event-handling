package com.event.domain.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.event.db.entity.Coupon;
import com.event.domain.coupon.facade.LettuceLockCouponFacade;
import com.event.domain.coupon.facade.OptimisticLockCouponFacade;
import com.event.domain.coupon.facade.RedissonLockCouponFacade;
import com.event.domain.coupon.repository.CouponRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private PessimisticCouponService pessimisticCouponService;
    @Autowired
    private OptimisticLockCouponFacade optimisticLockCouponFacade;
    @Autowired
    private LettuceLockCouponFacade lettuceLockCouponFacade;
    @Autowired
    private RedissonLockCouponFacade redissonLockCouponFacade;

    @BeforeEach
    void setUp() {
        Coupon coupon = Coupon.create(1L, 100L);
        couponRepository.saveAndFlush(coupon);
    }

    @AfterEach
    void tearDown() {
        couponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 하나씩 감소 시키기")
    void decreaseOneCoupon() {
        // when
        couponService.decrease(1L);
        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(99L);

    }

    @Test
    @DisplayName("쿠폰 100개 순차적으로 감소 시키기")
    void decreaseOneHundredCoupon() {
        // when
        for (int i = 0; i < 100; i++) {
            couponService.decrease(1L);
        }
        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);

    }

    @Test
    @DisplayName("동시에 쿠폰 100개 순차적으로 감소 시키기")
    void decreaseAtTheSameTime() throws InterruptedException {
        // when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                    try {
                        couponService.decrease(1L);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            );
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("동시에 쿠폰 100개 순차적으로 감소 시키기")
    void decreaseAtTheSameTimeWithOptimisticLock() throws InterruptedException {
        // when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                    try {
                        optimisticLockCouponFacade.decrease(1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            );
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);

    }

    @Test
    @DisplayName("동시에 쿠폰 100개 순차적으로 감소 시키기")
    void decreaseAtTheSameTimeWithPessimisticLock() throws InterruptedException {
        // when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                    try {
                        pessimisticCouponService.decrease(1L);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            );
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();


        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);

    }

    @Test
    @DisplayName("LettuceLock 사용")
    void decreaseAtTheSameTimeWithLettuceLock() throws InterruptedException {
        // when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                    try {
                        lettuceLockCouponFacade.decrease(1L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            );
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();


        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);

    }

    @Test
    @DisplayName("RedissonLock 사용")
    void decreaseAtTheSameTimeWithRedissonLock() throws InterruptedException {
        // when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                    try {
                        redissonLockCouponFacade.decrease(1L);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            );
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();


        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);

    }

    @Test
    @DisplayName("임시 optimistic test")
    void test() throws InterruptedException {

        Thread thread = new Thread(() -> {
            try {
                optimisticLockCouponFacade.decrease(1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();

    }
}