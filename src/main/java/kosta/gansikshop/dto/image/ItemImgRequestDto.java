package kosta.gansikshop.dto.image;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImgRequestDto {
    private MultipartFile file;
    private boolean isRepImg;

    @Builder
    private ItemImgRequestDto(MultipartFile file, boolean isRepImg) {
        this.file = file;
        this.isRepImg = isRepImg;
    }

    public boolean hasFile() {
        return file != null && !file.isEmpty();
    }
}
