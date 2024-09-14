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
        // this.item과 파라미터로 들어온 item이 다를 때만 처리
        if (this.item != item) {
            // 기존 item과의 관계를 끊음
            if (this.item != null) {
                this.item.getImages().remove(this);
            }
            // 새로운 item과의 관계를 설정
            this.item = item;
            if (item != null) {
                item.getImages().add(this);
            }
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
