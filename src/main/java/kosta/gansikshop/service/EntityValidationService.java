package kosta.gansikshop.service;

import kosta.gansikshop.domain.Cart;
import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.Member;
import kosta.gansikshop.repository.cart.CartRepository;
import kosta.gansikshop.repository.item.ItemRepository;
import kosta.gansikshop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntityValidationService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    // Member 검증
    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));
    }

    public Member validateMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // Item 검증
    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다."));
    }

    public Cart validateCart(Long cartItemId) {
        return cartRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 항목을 찾을 수 없습니다."));
    }

    // 동일 이메일 검증
    public boolean existEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 동일 상품 이름 검증
    public boolean existItemName(String name) {
        return itemRepository.existsByName(name);
    }

    // 상품 수정 시 자신을 제외한 상품 중 동일한 상품 이름이 있는지 검증
    public boolean existItemNameExceptMe(String name, Long itemId) {
        return itemRepository.existsByNameAndIdNot(name, itemId);
    }
}
