package com.event.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    private Long id;

    private Long quantity;

    public static Coupon create(Long id, Long quantity) {
        Coupon coupon = new Coupon();
        coupon.id = id;
        coupon.quantity = quantity;
        return coupon;
    }


    public void decrease() {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량이 부족합니다.");
        }
        quantity -= 1;
    }
}
