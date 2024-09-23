package kosta.gansikshop.controller;

import kosta.gansikshop.aop.PublicApi;
import kosta.gansikshop.aop.SecurityAspect;
import kosta.gansikshop.exception.ResourceNotFoundException;
import kosta.gansikshop.dto.image.ItemImgRequestDto;
import kosta.gansikshop.dto.item.ItemDetailDto;
import kosta.gansikshop.dto.item.ItemRequestDto;
import kosta.gansikshop.dto.item.ItemSummaryDto;
import kosta.gansikshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    /** 상품 등록 (여러 이미지 등록 가능) */
    @PostMapping
    public ResponseEntity<?> saveItem(@RequestPart("item") ItemRequestDto requestDto,
                                      @RequestPart(value = "files") List<MultipartFile> files) {
        Long memberId = SecurityAspect.getCurrentMemberId();

        List<ItemImgRequestDto> imgRequestDtoList = files.stream()
                .map(file -> ItemImgRequestDto.builder()
                        .file(file)
                        .isRepImg(files.indexOf(file) == 0) // 첫 번째 파일을 대표 이미지로 설정
                        .build())
                .collect(Collectors.toList());

        itemService.saveItem(requestDto, imgRequestDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body("상품이 등록되었습니다.");
    }

    /** 상품 수정 */
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestPart("item") ItemRequestDto requestDto,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Long memberId = SecurityAspect.getCurrentMemberId();

        // MultipartFile 리스트를 ItemImageDto 리스트로 변환
        List<ItemImgRequestDto> imgRequestDtoList = files != null ? files.stream()
                .map(file -> ItemImgRequestDto.builder()
                        .file(file)
                        .isRepImg(files.indexOf(file) == 0) // 첫 번째 파일을 대표 이미지로 설정
                        .build())
                .collect(Collectors.toList()) : Collections.emptyList();

        itemService.updateItem(itemId, requestDto, imgRequestDtoList);
        return ResponseEntity.status(HttpStatus.OK).body("상품 정보가 수정되었습니다.");
    }

    /** 상품 삭제 */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().body("상품이 삭제되었습니다.");
    }

    /** 통합 상품 검색 API */
    @PublicApi
    @GetMapping
    public ResponseEntity<?> getItems(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            PagedResourcesAssembler<ItemSummaryDto> assembler) {

        if ((keyword != null && !keyword.isEmpty()) && (category != null && !category.isEmpty())) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ItemSummaryDto> items = itemService.searchItems(keyword, category, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(items));
    }

    /** 상품 상세 조회 API */
    @PublicApi
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {
        ItemDetailDto itemDetail = itemService.getItem(itemId);
        if (itemDetail == null) {
            throw new ResourceNotFoundException("상품을 찾을 수 없습니다. ID: " + itemId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(itemDetail);
    }
}
