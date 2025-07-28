package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import io.micrometer.common.util.StringUtils;

import java.io.Serializable;

record OwnerId(String userId, String anonymousId) implements Serializable {

    OwnerId {
        if (StringUtils.isEmpty(anonymousId) || StringUtils.isEmpty(userId)) {
            throw new InvalidOwnerIdException("Owner ID cannot be null or empty");
        }
    }
}
