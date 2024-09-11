package kosta.gansikshop.service;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import kosta.gansikshop.dto.image.ItemImgRequestDto;
import kosta.gansikshop.dto.item.ItemDetailDto;
import kosta.gansikshop.dto.item.ItemRequestDto;
import kosta.gansikshop.dto.item.ItemSummaryDto;
import kosta.gansikshop.repository.image.ItemImgRepository;
import kosta.gansikshop.repository.item.ItemRepository;
import kosta.gansikshop.repository.order.OrderRepository;
import kosta.gansikshop.service.image.ImgStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final OrderRepository orderRepository;
    private final ImgStorageService imgStorageService;
    private final EntityValidationService entityValidationService;

    /** 상품 등록 */
    @Transactional
    public void saveItem(ItemRequestDto requestDto, List<ItemImgRequestDto> imgRequestDtoList) {
        // 상품 이름 중복 체크
        if (entityValidationService.existItemName(requestDto.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 상품이 존재합니다.");
        }

        // 이미지가 없는 경우 예외 처리
        if (imgRequestDtoList == null || imgRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("상품 등록 시 최소 한 개의 이미지를 업로드해야 합니다.");
        }

        // 상품 생성 및 저장
        Item item = Item.createItem(
                requestDto.getName(),
                requestDto.getPrice(),
                requestDto.getStockQuantity(),
                requestDto.getCategory()
        );
        itemRepository.save(item);

        // 대표 이미지 여부 확인
        boolean hasRepImg = imgRequestDtoList.stream().anyMatch(ItemImgRequestDto::isRepImg);

        // 대표 이미지가 없는 경우 예외 처리
        if (!hasRepImg) {
            throw new IllegalArgumentException("상품 등록 시 대표 이미지를 하나 이상 업로드해야 합니다.");
        }

        // 이미지 저장
        for (ItemImgRequestDto imgRequestDto : imgRequestDtoList) {
            MultipartFile file = imgRequestDto.getFile();
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("업로드된 이미지가 비어 있습니다.");
            }
            String imgUrl = imgStorageService.store(file);
            String uniqueFileName = Paths.get(imgUrl).getFileName().toString();
            ItemImg itemImg = ItemImg.createItemImg(item, uniqueFileName, file.getOriginalFilename(), imgUrl, imgRequestDto.isRepImg() ? "Y" : "N");
            itemImgRepository.save(itemImg);
        }
    }

    /**
     * 주어진 itemId, DTO 및 이미지 DTO 기반으로 상품을 업데이트합니다.
     * <p>
     * 이 메서드는 다음 작업을 수행 합니다:
     * <ul>
     *     <li>데이터베이스에서 기존 상품을 조회 합니다.</li>
     *     <li>제공된 이미지 DTO 검증을 진행하여 대표 이미지가 하나 이상 제공되었는지 확인합니다.</li>
     *     <li>제공된 대표 이미지가 기존의 대표 이미지와 다른지 확인합니다.</li>
     *     <li>대표 이미지가 변경된 경우:
     *         <ul>
     *             <li>새로운 대표 이미지가 이전의 일반 이미지였던 경우, 해당 이미지를 대표 이미지로 설정합니다.</li>
     *             <li>새로운 대표 이미지가 완전히 새로운 이미지인 경우, 이전의 대표 이미지를 일반 이미지로 변경하고, 새로운 대표 이미지를 설정합니다.</li>
     *         </ul>
     *     </li>
     *     <li>일반 이미지의 변경 사항을 처리하며, 제공된 이미지와 기존 이미지 간의 일관성을 보장합니다.</li>
     *     <li>대표 이미지가 제공되지 않는 경우 예외를 발생시킵니다.</li>
     *     <li>이미지 관련 수정이 발생했는지 여부를 추적하기 위해 플래그를 사용합니다.</li>
     * </ul>
     * <p>
     * 또한, 이미지 외의 필드가 변경되었는지 확인합니다. 이미지 관련 작업이 수행되지 않았고, 다른 필드에도 변경이 없으면
     * 상품 수정이 일어나지 않았다고 간주하고 예외를 발생시킵니다.
     *
     * @param itemId 수정할 상품의 ID. 유효한 기존 상품의 IBID 합니다.
     * @param requestDto 업데이트된 상품 세부 정보가 포함된 DTO. 대표 이미지가 포함되어야 합니다.
     * @param imgRequestDtoList 상품의 이미지 DTO 목록. 대표 이미지가 최소한 하나 포함되어야 합니다.
     *
     * @throws IllegalArgumentException If the item is not found, no representative image is provided, or invalid image operations are detected.
     */
    @Transactional
    public void updateItem(Long itemId, ItemRequestDto requestDto, List<ItemImgRequestDto> imgRequestDtoList) {
        Item findItem = entityValidationService.validateItem(itemId);

        // DTO 비어 있는 경우 예외 처리
        if (imgRequestDtoList == null || imgRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException("상품 당 반드시 한 장 이상의 이미지를 업로드 해야 합니다.");
        }

        // 대표 이미지 찾기
        ItemImgRequestDto newRepImgDto = imgRequestDtoList.stream().filter(ItemImgRequestDto::isRepImg).findFirst().orElse(null);

        // 대표 이미지가 없는 경우 예외 발생
        if (newRepImgDto == null || !newRepImgDto.hasFile()) {
            throw new IllegalArgumentException("대표 이미지는 반드시 지정되어야 합니다.");
        }

        // 기존 이미지 로드
        List<ItemImg> existingImages = itemImgRepository.findByItemWithImages(findItem);

        // 새로 들어온 대표 이미지 이름
        String newRepImgName = newRepImgDto.getFile().getOriginalFilename();
        boolean isImageModified = false;

        // 기존 대표 이미지와 DTO 대표 이미지 비교
        ItemImg existingRepImg = existingImages.stream()
                .filter(ItemImg::isRepImgYn)
                .findFirst()
                .orElse(null);

        if (existingRepImg != null) {
            String existingRepImgName = existingRepImg.getOriginImgName();
            if (!existingRepImgName.equals(newRepImgName)) {
                // DTO 대표 이미지가 기존 대표 이미지와 다른 경우
                if (existingImages.stream().anyMatch(img -> img.getOriginImgName().equals(newRepImgName))) {
                    // DTO 대표 이미지가 기존 일반 이미지인 경우
                    existingRepImg.updateRepImgYn("N");
                    itemImgRepository.save(existingRepImg);
                    isImageModified = true;
                    ItemImg newRepImg = existingImages.stream()
                            .filter(img -> img.getOriginImgName().equals(newRepImgName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("대표 이미지를 찾을 수 없습니다."));
                    newRepImg.updateRepImgYn("Y");
                    itemImgRepository.save(newRepImg);
                } else {
                    // DTO 대표 이미지가 기존에 없는 완전히 새로운 이미지인 경우
                    existingRepImg.updateRepImgYn("N");
                    itemImgRepository.save(existingRepImg);
                    isImageModified = true;
                    addItemImg(findItem, newRepImgDto);
                }
            }
        } else {
            // 기존에 대표 이미지가 없는 경우
            addItemImg(findItem, newRepImgDto);
            isImageModified = true;
        }

        // 기존 일반 이미지 처리
        Set<String> dtoImageNames = imgRequestDtoList.stream()
                .filter(dto -> !dto.isRepImg() && dto.hasFile())
                .map(dto -> dto.getFile().getOriginalFilename())
                .collect(Collectors.toSet());

        for (ItemImg existingImage : existingImages) {
            if (!existingImage.isRepImgYn()) {
                boolean isImageInDto = dtoImageNames.contains(existingImage.getOriginImgName());
                if (!isImageInDto) {
                    // 수정 폼에 없는 이미지는 삭제
                    imgStorageService.delete(existingImage.getImgUrl());
                    itemImgRepository.delete(existingImage);
                    isImageModified = true;
                }
            }
        }

        // 새로 들어온 일반 이미지 추가
        for (ItemImgRequestDto imageDto : imgRequestDtoList) {
            if (!imageDto.isRepImg() && imageDto.hasFile()) {
                String imageName = imageDto.getFile().getOriginalFilename();
                boolean isImageExists = existingImages.stream()
                        .anyMatch(existingImage -> existingImage.getOriginImgName().equals(imageName));

                if (!isImageExists) {
                    MultipartFile file = imageDto.getFile();
                    String imgUrl = imgStorageService.store(file);
                    String imgFileNameInDb = Paths.get(imgUrl).getFileName().toString();
                    ItemImg newItemImg = ItemImg.createItemImg(findItem, imgFileNameInDb, file.getOriginalFilename(), imgUrl, "N");
                    itemImgRepository.save(newItemImg);
                    isImageModified = true;
                }
            }
        }

        // 상품 정보 수정 여부 확인
        boolean isNameChanged = !findItem.getName().equals(requestDto.getName());
        boolean isPriceChanged = !(findItem.getPrice() == requestDto.getPrice());
        boolean isStockQuantityChanged = !(findItem.getStockQuantity() == requestDto.getStockQuantity());
        boolean isCategoryChanged = !findItem.getCategory().equals(requestDto.getCategory());

        boolean isItemUpdated = isNameChanged || isPriceChanged || isStockQuantityChanged || isCategoryChanged;

        // 상품 수정 시 이미 등록된 다른 상품의 이름으로 등록 불가
        boolean isValidate = !entityValidationService.existItemNameExceptMe(requestDto.getName(), itemId);

        if (isItemUpdated && isValidate) {
            findItem.update(requestDto.getName(), requestDto.getPrice(), requestDto.getStockQuantity(), requestDto.getCategory());
        }

        // 상품명 검증 통과 못하면 예외
        if (!isValidate) {
            throw new IllegalArgumentException("이미 같은 이름의 상품이 존재합니다.");
        }

        // 상품 속성 값 변경 없이 이미지만 변경되어도 상품 수정시간 및 수정자 변경
        if (!isItemUpdated && isImageModified) {
            findItem.updateModified();
        }

        // 상품 수정이 실제로 일어나지 않은 경우 예외 처리
        if (!isItemUpdated && !isImageModified) {
            throw new IllegalArgumentException("수정된 내용이 없습니다.");
        }
    }

    // 수정 폼으로 들어온 이미지들 중 기존에 없던 이미지면 새로운 이미지로 저장
    private void addItemImg(Item findItem, ItemImgRequestDto newRepImgDto) {
        MultipartFile newRepFile = newRepImgDto.getFile();
        String newRepImgUrl = imgStorageService.store(newRepFile);
        String newRepImgNameInDb = Paths.get(newRepImgUrl).getFileName().toString();
        ItemImg newRepImg = ItemImg.createItemImg(findItem, newRepImgNameInDb, newRepImgDto.getFile().getOriginalFilename(), newRepImgUrl, "Y");
        itemImgRepository.save(newRepImg);
    }

    /** 상품 조회 (전체, keyword, category 검색) */
    @Transactional(readOnly = true)
    public Page<ItemSummaryDto> searchItems(String keyword, String category, Pageable pageable) {
        // Item 엔티티를 검색하고, ItemSummaryDto 변환
        Page<Item> itemPage = itemRepository.searchItems(keyword, category, pageable);

        // Item ItemSummaryDto 변환하여 반환
        return itemPage.map(ItemSummaryDto::createSummaryDto);
    }

    /** 상품 조회 (단건 상세 조회) */
    @Transactional(readOnly = true)
    public ItemDetailDto getItem(Long itemId) {
        Item item = entityValidationService.validateItem(itemId);
        return ItemDetailDto.createDetailDto(item);
    }

    /** 상품 삭제 */
    @Transactional
    public void deleteItem(Long itemId) {
        // 해당 상품이 존재 하는지 확인
        Item item = entityValidationService.validateItem(itemId);

        // 해당 상품이 포함된 주문이 있는지 확인
        boolean hasOrderItems = orderRepository.existsByOrderItemsItem(item);
        if (hasOrderItems) {
            throw new IllegalArgumentException("고객의 주문 내역에 존재하는 상품은 삭제할 수 없습니다.");
        }

        // 상품에 연결된 이미지들 삭제
        List<ItemImg> itemImages = itemImgRepository.findByItemWithImages(item);
        for (ItemImg itemImg : itemImages) {
            imgStorageService.delete(itemImg.getImgUrl());  // 로컬 저장소에서 이미지 삭제
            itemImgRepository.delete(itemImg);  // 데이터베이스에서 이미지 정보 삭제 // 주석 처리하면 로컬에서 삭제 안됨
        }

        // 상품 삭제
        itemRepository.delete(item);
    }
}
