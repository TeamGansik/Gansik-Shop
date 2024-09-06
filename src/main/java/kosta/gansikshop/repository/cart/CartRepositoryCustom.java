package kosta.gansikshop.repository.cart;

import kosta.gansikshop.domain.Cart;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryCustom {
    List<Cart> findCartDetails(Long memberId, Optional<Long> itemId);
}
