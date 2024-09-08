$(() => {
    let result = 1;
    // '-' 버튼 클릭 시
    $('.dash').click(() => {
        if (result > 1) { // 수량이 1 이상일 때만 감소
            result--;
            $("#result").text(result); // 수량 업데이트
            updateTotal(); // 총 금액 업데이트
        }
    });

    // '+' 버튼 클릭 시
    $('.plus').click(() => {
        result++;
        $("#result").text(result); // 수량 업데이트
        updateTotal(); // 총 금액 업데이트
    });

    // 총 금액 업데이트 함수
    function updateTotal() {
        let won = parseInt($("#won").text().replace(/,/g, '')); // 상품 가격
        let total = won * result; // 총액 계산
        let formattedTotal = total.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','); // 천 단위 콤마 추가
        $("#money").text(formattedTotal); // 총 금액 업데이트
    }

    // 페이지 로드 시 총 금액 초기화
    updateTotal();


    $(document).ready(function () {
        $('#buy').click(function () {
          if (confirm("구매 하시겠습니까?")) {
            alert("구매가 완료되었습니다.");
          }
        });
      
        $('#basket').click(function () {
          alert("장바구니에 담겼습니다.");
        });
    });
});