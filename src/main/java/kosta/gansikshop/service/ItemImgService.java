package kosta.gansikshop.service;

import kosta.gansikshop.domain.Item;
import kosta.gansikshop.domain.ItemImg;
import kosta.gansikshop.dto.image.ItemImgRequestDto;
import kosta.gansikshop.repository.image.ItemImgRepository;
import kosta.gansikshop.service.image.ImgStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;
    private final ImgStorageService imgStorageService;

    /** 상품 이미지 처리 */
    @Transactional
    public boolean processItemImages(Item item, List<ItemImgRequestDto> imgRequestDtoList) {
        List<ItemImg> existingImages = itemImgRepository.findByItemWithImages(item);
        boolean isImageModified = false;

        // 대표 이미지 처리
        ItemImgRequestDto newRepImgDto = imgRequestDtoList.stream()
                .filter(ItemImgRequestDto::isRepImg)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("대표 이미지를 지정해야 합니다."));

        isImageModified = updateRepImage(item, newRepImgDto, existingImages);

        // 일반 이미지 처리
        isImageModified = processGeneralImages(item, imgRequestDtoList, existingImages) || isImageModified;

        return isImageModified;
    }

    /** 대표 이미지 처리 로직 */
    private boolean updateRepImage(Item item, ItemImgRequestDto newRepImgDto, List<ItemImg> existingImages) {
        String newRepImgName = newRepImgDto.getFile().getOriginalFilename();
        ItemImg existingRepImg = existingImages.stream()
                .filter(ItemImg::isRepImgYn)
                .findFirst()
                .orElse(null);

        boolean isImageModified = false;

        if (existingRepImg != null) {
            if (!existingRepImg.getOriginImgName().equals(newRepImgName)) {
                existingRepImg.updateRepImgYn("N");
                itemImgRepository.save(existingRepImg);

                ItemImg newRepImg = existingImages.stream()
                        .filter(img -> img.getOriginImgName().equals(newRepImgName))
                        .findFirst()
                        .orElse(null);

                if (newRepImg != null) {
                    newRepImg.updateRepImgYn("Y");
                    itemImgRepository.save(newRepImg);
                } else {
                    addItemImg(item, newRepImgDto, "Y");
                }
                isImageModified = true;
            }
        } else {
            addItemImg(item, newRepImgDto, "Y");
            isImageModified = true;
        }

        return isImageModified;
    }

    /** 일반 이미지 처리 로직 */
    private boolean processGeneralImages(Item item, List<ItemImgRequestDto> imgRequestDtoList, List<ItemImg> existingImages) {
        Set<String> dtoImageNames = imgRequestDtoList.stream()
                .filter(dto -> !dto.isRepImg() && dto.hasFile())
                .map(dto -> dto.getFile().getOriginalFilename())
                .collect(Collectors.toSet());

        boolean isImageModified = false;

        // 기존 이미지 삭제
        for (ItemImg existingImage : existingImages) {
            if (!existingImage.isRepImgYn()) {
                boolean isImageInDto = dtoImageNames.contains(existingImage.getOriginImgName());
                if (!isImageInDto) {
                    imgStorageService.delete(existingImage.getImgUrl());
                    itemImgRepository.delete(existingImage);
                    isImageModified = true;
                }
            }
        }

        // 새로 들어온 이미지 추가
        for (ItemImgRequestDto imageDto : imgRequestDtoList) {
            if (!imageDto.isRepImg() && imageDto.hasFile()) {
                String imageName = imageDto.getFile().getOriginalFilename();
                boolean isImageExists = existingImages.stream()
                        .anyMatch(existingImage -> existingImage.getOriginImgName().equals(imageName));

                if (!isImageExists) {
                    addItemImg(item, imageDto, "N");
                    isImageModified = true;
                }
            }
        }

        return isImageModified;
    }

    /** 이미지 추가 로직 */
    private void addItemImg(Item item, ItemImgRequestDto imageDto, String repImgYn) {
        MultipartFile file = imageDto.getFile();
        String imgUrl = imgStorageService.store(file);
        String imgFileNameInDb = Paths.get(imgUrl).getFileName().toString();
        ItemImg newItemImg = ItemImg.createItemImg(item, imgFileNameInDb, file.getOriginalFilename(), imgUrl, repImgYn);
        itemImgRepository.save(newItemImg);
    }

    /** 상품의 이미지 삭제 */
    @Transactional
    public void deleteItemImages(Item item) {
        List<ItemImg> itemImages = itemImgRepository.findByItemWithImages(item);
        for (ItemImg itemImg : itemImages) {
            imgStorageService.delete(itemImg.getImgUrl());
            itemImgRepository.delete(itemImg);
        }
    }
}
