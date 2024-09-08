// 검색 버튼 클릭 이벤트
$(document).ready(function() {
    $('.search-btn').on('click', function(e) {
        e.preventDefault();  // 페이지 새로고침 방지

        // 입력된 검색어 가져오기
        let searchQuery = $('.search-txt').val();  

        if (searchQuery.trim() === '') {
            // 검색어가 비어 있을 경우
            alert('검색어를 입력해주세요.');
        } else {
            // 검색어가 있을 때 - 실제 검색을 수행하거나 페이지 이동
            console.log('검색어:', searchQuery);

            fetch('https://example.com/api/search', {  // 실제 API 엔드포인트로 변경
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ query: searchQuery })  // 검색어를 서버로 전송
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('네트워크 응답에 문제가 있습니다.');
                }
                return response.json(); 
            })
            .then(data => {
                console.log('검색 결과:', data);
                
                let HTML = '';
                data.forEach(elemnet => {
                    html+=`
                    <li>
                        <a href="https://${elemnet.id...}>
                        </a>
                    </li>`;
                }) ;
                displaySearchResults(data);  // 검색 결과를 화면에 출력하는 함수 호출
            })
            .catch(error => {
                console.error('요청 중 에러 발생:', error);
            });
        };
    
        // 검색 결과를 화면에 표시하는 함수
        function displaySearchResults(data) {
            // 검색 결과를 표시할 HTML 구조를 생성
            let resultsContainer = $('.search-results');  // 결과를 표시할 영역 선택
            resultsContainer.empty();  // 이전 결과 제거
    
            if (data.results && data.results.length > 0) {
                data.results.forEach(result => {
                    let resultItem = `<div class="result-item">
                                        <h3>${result.title}</h3>
                                        <p>${result.description}</p>
                                      </div>`;
                    resultsContainer.append(resultItem);  // 결과 항목 추가
                });
            } else {
                resultsContainer.append('<p>검색 결과가 없습니다.</p>');  // 결과가 없을 때
            }
        }
    });
});