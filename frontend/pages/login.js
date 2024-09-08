document.addEventListener('DOMContentLoaded', () => {
    const loginButton = document.querySelector('.login-button');
    const emailInput = document.querySelector('input[placeholder="아이디"]');
    const passwordInput = document.querySelector('input[placeholder="비밀번호"]');
    let refreshTokenTimeout; // Refresh Token 갱신 타이머 변수

    // 로그인 버튼 클릭 이벤트
    loginButton.addEventListener('click', async function(event) {
        event.preventDefault();
        const email = emailInput.value;
        const password = passwordInput.value;

        if (!email || !password) {
            alert('이메일과 비밀번호를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/members/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                }),
            });

            if (response.status === 400) {
                const errorMessage = await response.text();
                alert('로그인 실패: ' + errorMessage);
            } else if (response.ok) {
                const data = await response.json();
                console.log(data);
                if (data.accessToken && data.refreshToken) { // 대소문자에 맞춰 필드명 수정
                    // 토큰 저장
                    localStorage.setItem('accessToken', data.accessToken); // 저장 시 대소문자도 맞추기
                    localStorage.setItem('refreshToken', data.refreshToken);
                    alert('로그인 성공!');
                    window.location.href = '../main.html'; // 로그인 후 이동할 페이지
                
                    // Access Token 자동 갱신 설정 (14분 30초 후 갱신)
                    scheduleTokenRefresh(14.5 * 60 * 1000);
                } else {
                    alert('로그인 실패: 응답 데이터에 문제가 있습니다.');
                }
                
            } else {
                const errorMessage = await response.text();
                alert('로그인 중 오류가 발생했습니다: ' + errorMessage);
            }
        } catch (error) {
            alert('로그인 중 오류가 발생했습니다. 다시 시도해주세요.');
            console.error(error);
        }
    });

    // Access Token 갱신 예약 함수
    function scheduleTokenRefresh(timeout) {
        if (refreshTokenTimeout) {
            clearTimeout(refreshTokenTimeout); // 기존 타이머 제거
        }
        refreshTokenTimeout = setTimeout(async () => {
            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) {
                    alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
                    window.location.href = 'login.html';
                    return;
                }

                const response = await fetch('http://localhost:8080/api/members/refresh-token', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Refresh-Token': refreshToken,
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('accessToken', data.newToken); // 새로운 Access Token 저장
                    // 14분 30초 후 다시 갱신 요청
                    scheduleTokenRefresh(14.5 * 60 * 1000); 
                } else {
                    alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
                    window.location.href = '../login.html'; // 로그아웃 처리
                }
            } catch (error) {
                alert('토큰 갱신 중 오류가 발생했습니다. 다시 시도해주세요.');
                console.error(error);
                window.location.href = 'login.html'; // 오류 발생 시 로그아웃 처리
            }
        }, timeout);
    }

    // 비밀번호 입력 필드에서 엔터 키를 누르면 로그인 버튼 클릭
    passwordInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault(); // 기본 엔터 동작 방지
            loginButton.click(); // 로그인 버튼 클릭
        }
    });
});
