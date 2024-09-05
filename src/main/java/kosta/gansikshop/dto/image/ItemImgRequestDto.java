package kosta.gansikshop.dto.image;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ItemImgRequestDto {
    private MultipartFile file;
    private boolean isRepImg;

    public boolean hasFile() {
        return file != null && !file.isEmpty();
    }
}
