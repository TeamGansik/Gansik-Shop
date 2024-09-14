package kosta.gansikshop.service;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.dto.image.ItemImgRequestDto;
import kosta.gansikshop.dto.item.ItemDetailDto;
import kosta.gansikshop.dto.item.ItemRequestDto;
import kosta.gansikshop.dto.item.ItemSummaryDto;
import kosta.gansikshop.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final EntityValidationService entityValidationService;
    private final ItemImgService itemImgService;

    /** 상품 등록 */
    @Transactional
    public void saveItem(ItemRequestDto requestDto, List<ItemImgRequestDto> imgRequestDtoList) {
        // 상품 이름 중복 체크
        if (entityValidationService.existItemName(requestDto.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 상품이 존재합니다.");
        }

        // 상품 생성 및 저장
        Item item = Item.createItem(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getStockQuantity(),
                requestDto.getCategory()
        );
        itemRepository.save(item);

        // 이미지 처리 (ItemImgService에 위임)
        boolean isImageSaved = itemImgService.processItemImages(item, imgRequestDtoList);
        if (!isImageSaved) {
            throw new IllegalArgumentException("상품 등록 시 최소 한 개의 이미지를 업로드해야 합니다.");
        }
    }

    /** 상품 수정 */
    @Transactional
    public void updateItem(Long itemId, ItemRequestDto requestDto, List<ItemImgRequestDto> imgRequestDtoList) {
        Item findItem = entityValidationService.validateItem(itemId);

        // 이미지 처리 (ItemImgService에 위임)
        boolean isImageModified = itemImgService.processItemImages(findItem, imgRequestDtoList);

        // 상품 정보 수정 여부 확인
        boolean isItemUpdated = !findItem.getName().equals(requestDto.getName())
                || findItem.getPrice() != requestDto.getPrice()
                || findItem.getStockQuantity() != requestDto.getStockQuantity()
                || !findItem.getCategory().equals(requestDto.getCategory());

        // 중복된 이름으로 수정 불가
        boolean isValidate = !entityValidationService.existItemNameExceptMe(requestDto.getName(), itemId);

        if (isItemUpdated && isValidate) {
            findItem.update(requestDto.getName(), requestDto.getPrice(), requestDto.getStockQuantity(), requestDto.getCategory());
        } else if (!isValidate) {
            throw new IllegalArgumentException("이미 같은 이름의 상품이 존재합니다.");
        }

        // 수정이 없으면 예외 발생
        if (!isItemUpdated && !isImageModified) {
            throw new IllegalArgumentException("수정된 내용이 없습니다.");
        }

        // 상품 속성은 변경되지 않았지만 이미지가 변경된 경우 수정 시간 업데이트
        if (!isItemUpdated) {
            findItem.updateModified();
        }
    }

    /** 상품 삭제 */
    @Transactional
    public void deleteItem(Long itemId) {
        Item item = entityValidationService.validateItem(itemId);

        // 주문 내역에 포함된 상품 삭제 불가
        if (entityValidationService.existsOrderItemsInItem(item)) {
            throw new IllegalArgumentException("고객의 주문 내역에 존재하는 상품은 삭제할 수 없습니다.");
        }

        // 상품 이미지 삭제 (ItemImgService에 위임)
        itemImgService.deleteItemImages(item);

        // 상품 삭제
        itemRepository.delete(item);
    }

    /** 상품 조회 (전체, keyword, category 검색) */
    @Transactional(readOnly = true)
    public Page<ItemSummaryDto> searchItems(String keyword, String category, Pageable pageable) {
        return itemRepository.searchItems(keyword, category, pageable).map(ItemSummaryDto::createSummaryDto);
    }

    /** 상품 조회 (단건 상세 조회) */
    @Transactional(readOnly = true)
    public ItemDetailDto getItem(Long itemId) {
        Item item = entityValidationService.validateItem(itemId);
        return ItemDetailDto.createDetailDto(item);
    }
}
