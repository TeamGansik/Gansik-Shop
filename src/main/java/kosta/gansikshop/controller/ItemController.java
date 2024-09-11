package kosta.gansikshop.controller;

import kosta.gansikshop.config.security.CustomUserDetails;
import kosta.gansikshop.dto.image.ItemImgRequestDto;
import kosta.gansikshop.dto.item.ItemDetailDto;
import kosta.gansikshop.dto.item.ItemRequestDto;
import kosta.gansikshop.dto.item.ItemSummaryDto;
import kosta.gansikshop.service.EntityValidationService;
import kosta.gansikshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EntityValidationService entityValidationService;

    /** 상품 등록 (여러 이미지 등록 가능) */
    @PostMapping
    public ResponseEntity<?> saveItem(@RequestPart("item") ItemRequestDto requestDto,
                                      @RequestPart(value = "files") List<MultipartFile> files) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            // MultipartFile 리스트를 ItemImageDto 리스트로 변환
            List<ItemImgRequestDto> imgRequestDtoList = files.stream()
                    .map(file -> ItemImgRequestDto.builder()
                            .file(file)
                            .isRepImg(files.indexOf(file) == 0)
                            .build())
                    .collect(Collectors.toList());

            itemService.saveItem(requestDto, imgRequestDtoList);
            return ResponseEntity.status(HttpStatus.CREATED).body("상품이 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 상품 수정 */
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId,
                                        @RequestPart("item") ItemRequestDto requestDto,
                                        @RequestPart(value = "files") List<MultipartFile> files) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            // MultipartFile 리스트를 ItemImageDto 리스트로 변환
            List<ItemImgRequestDto> imgRequestDtoList = files != null ? files.stream()
                    .map(file -> ItemImgRequestDto.builder()
                            .file(file)
                            .isRepImg(files.indexOf(file) == 0) // 첫 번째 파일을 대표 이미지로 설정
                            .build())
                    .collect(Collectors.toList()) : Collections.emptyList();

            // 서비스로 update 요청
            itemService.updateItem(itemId, requestDto, imgRequestDtoList);
            return ResponseEntity.status(HttpStatus.OK).body("상품 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 상품 삭제 */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            itemService.deleteItem(itemId);
            return ResponseEntity.ok().body("상품이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 통합 상품 검색 API
     *
     * @param keyword 검색 키워드 (선택적)
     * @param category 검색할 카테고리 (선택적)
     * @param page 페이지 번호
     * @param size 페이지당 아이템 수
     * @return 검색된 상품 목록
     */
    @GetMapping
    public ResponseEntity<?> getItems(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            PagedResourcesAssembler<ItemSummaryDto> assembler) {

        try {
            // keyword&category 동시에 제공되면 예외 처리
            if ((keyword != null && !keyword.isEmpty()) && (category != null && !category.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청입니다.");
            }

            // Pageable 객체 생성 (PageRequest 페이지와 사이즈를 처리)
            Pageable pageable = PageRequest.of(page, size);

            // 검색 수행
            Page<ItemSummaryDto> items = itemService.searchItems(keyword, category, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(assembler.toModel(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * 상품 상세 조회 API
     *
     * @param itemId 상품 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId) {
        try {
            ItemDetailDto itemDetail = itemService.getItem(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(itemDetail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
