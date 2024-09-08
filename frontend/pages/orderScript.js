document.addEventListener('DOMContentLoaded', async () => {
    try {
        // 사용자 이름과 주문 내역을 로드
        await loadUserData();
        await loadOrderData();
    } catch (error) {
        console.error('오류 발생:', error);
    }
});

async function loadUserData() {
    try {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) throw new Error('No access token found');

        const response = await fetch('http://localhost:8080/api/members', {
            method: 'GET',
            headers: {
                'Authorization': accessToken,
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) throw new Error('Failed to fetch user data');

        const userData = await response.json();
        console.log('User Data:', userData);

        // 사용자 이름 표시
        const userNameElement = document.querySelector('.introduce .name strong');
        if (userNameElement) {
            userNameElement.textContent = userData.name;
        }
    } catch (error) {
        console.error('사용자 정보 로딩 중 오류 발생:', error);
        const userNameElement = document.querySelector('.introduce .name strong');
        if (userNameElement) {
            userNameElement.textContent = '사용자';
        }
    }
}

async function loadOrderData() {
    try {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) throw new Error('No access token found');

        const response = await fetch('http://localhost:8080/api/orders', {
            method: 'GET',
            headers: {
                'Authorization': accessToken,
                'Content-Type': 'application/json',
            },
        });

        if (response.status === 204) {
            // 주문이 없는 경우
            document.querySelector('#orderContent .content').innerHTML = `
                <div class="introduce">주문한 제품이 없네요...</div>
                <button onclick="location.href='item-sort.html'">주문하러 가기</button>
            `;
            return;
        }

        if (!response.ok) throw new Error('주문 데이터 로딩 중 오류 발생');

        const orderData = await response.json();
        console.log('Parsed Order Data:', orderData);

        const { content, totalOrderPrice } = orderData;
        let orderHTML = '';

        content.forEach(order => {
            const { orderItems, createTime } = order;

            orderItems.forEach(item => {
                // 이미지 URL을 웹 서버에서 접근할 수 있는 URL로 수정
                const imageUrl = item.itemImageUrl.replace(/C:\\Users\\정현수\\IdeaProjects\\gansik-shop\\uploads\\/, 'http://localhost:8080/uploads/');

                orderHTML += `
                    <div class="collect">
                        <div class="date">${new Date(createTime).toLocaleDateString()}</div>
                        <div class="item-detail">
                            <div class="item-info">
                                <img src="${imageUrl}" alt="${item.itemName}">
                                <div class="item-name">
                                    <div class="order-number">주문번호: ${item.itemId}</div>
                                    <h4>${item.itemName}</h4>
                                </div>
                            </div>
                            <div class="item-option">
                                <div class="option">
                                    <span class="result">${item.quantity}개</span>
                                </div>
                                <div class="option-delete">
                                    <span class="won">${item.totalPrice.toLocaleString()}원</span>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
        });

        document.querySelector('#orderContent .content').innerHTML = orderHTML;

        // 총 주문 금액 요소 선택 및 업데이트
        const totalOrderPriceElement = document.querySelector('.total-order-price');
        if (totalOrderPriceElement) {
            totalOrderPriceElement.textContent = `총 주문 금액: ${totalOrderPrice.toLocaleString()}원`;
        } else {
            console.warn('총 주문 금액을 표시할 요소가 없습니다.');
        }

    } catch (error) {
        console.error('주문 데이터 로딩 중 오류 발생:', error);
        document.querySelector('#orderContent .content').innerHTML = `
            <div class="introduce">주문 데이터를 로딩하는 중 오류가 발생했습니다.</div>
            <button onclick="location.href='item-sort.html'">주문하러 가기</button>
        `;
    }
}

document.addEventListener('DOMContentLoaded', loadOrderData);


