package kosta.gansikshop.dto.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPageResponseDto {
    private List<OrderResponseDto> content;
    private int totalOrderPrice; // 전체 총합
    private Pageable pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
    private boolean first;
    private int numberOfElements;
    private boolean empty;

    @Builder
    private OrderPageResponseDto(List<OrderResponseDto> content, int totalOrderPrice,
                                 Pageable pageable,
                                 boolean last, int totalPages,
                                 long totalElements, int size,
                                 int number, boolean first,
                                 int numberOfElements, boolean empty) {
        this.content = content;
        this.totalOrderPrice = totalOrderPrice;
        this.pageable = pageable;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.number = number;
        this.first = first;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
    }

    public static OrderPageResponseDto createOrderPageResponseDto(List<OrderResponseDto> content, int totalOrderPrice,
                                                                  Pageable pageable, boolean last,
                                                                  int totalPages, long totalElements,
                                                                  int size, int number,
                                                                  boolean first, int numberOfElements,
                                                                  boolean empty) {
        return OrderPageResponseDto.builder()
                .content(content)
                .totalOrderPrice(totalOrderPrice)
                .pageable(pageable)
                .last(last)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .size(size)
                .number(number)
                .first(first)
                .numberOfElements(numberOfElements)
                .empty(empty)
                .build();
    }
}
