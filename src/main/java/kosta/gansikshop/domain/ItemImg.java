package kosta.gansikshop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_img_id")
    private Long id;

    private String imgName;         // 이미지 파일명
    private String originImgName;   // 원본 이미지 파일명
    private String imgUrl;          // 이미지 조회 경로
    private String repImgYn;        // 대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    private ItemImg(Item item, String imgName, String originImgName, String imgUrl, String repImgYn) {
        this.setItem(item);
        this.imgName = imgName;
        this.originImgName = originImgName;
        this.imgUrl = imgUrl;
        this.repImgYn = repImgYn;
    }

    /** 연관 관계 메서드 */
    protected void setItem(Item item) {
        if (this.item != null) {
            this.item.getImages().remove(this);
        }
        this.item = item;
        if (item != null) {
            item.getImages().add(this);
        }
    }

    public static ItemImg createItemImg(Item item, String imgName,
                                        String originImgName, String imgUrl, String repImgYn) {
        return ItemImg.builder()
                .item(item)
                .imgName(imgName)
                .originImgName(originImgName)
                .imgUrl(imgUrl)
                .repImgYn(repImgYn)
                .build();
    }

    /**
     * 이미지 정보 수정 메서드
     */
    public void updateItemImg(String originImgName, String imgName, String imgUrl, String repImgYn) {
        this.originImgName = originImgName != null ? originImgName : this.originImgName;
        this.imgName = imgName != null ? imgName : this.imgName;
        this.imgUrl = imgUrl != null ? imgUrl : this.imgUrl;
        this.repImgYn = repImgYn != null ? repImgYn : this.repImgYn;
    }

    /**
     * 대표 이미지 수정 메서드
     */
    public void updateRepImgYn(String repImgYn) {
        this.repImgYn = repImgYn;
    }

    /**
     * 대표 이미지 여부 확인 메서드
     */
    public boolean isRepImgYn() {
        return "Y".equals(this.repImgYn);
    }
}
