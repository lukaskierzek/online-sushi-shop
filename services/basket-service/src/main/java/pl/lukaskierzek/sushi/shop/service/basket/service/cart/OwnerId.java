package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import io.micrometer.common.util.StringUtils;

record OwnerId(String userId, String anonymousId) {

    OwnerId {
        if (StringUtils.isEmpty(anonymousId) || StringUtils.isEmpty(userId)) {
            throw new InvalidOwnerIdException("Owner ID cannot be null or empty");
        }
    }
}
