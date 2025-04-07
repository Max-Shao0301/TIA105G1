// Email 驗證碼發送與驗證功能
document.addEventListener('DOMContentLoaded', () => {
  const sendBtn = document.getElementById('sendCodeBtn');
  const verifyBtn = document.getElementById('verifyCodeBtn');
  const nextBtn = document.getElementById('nextBtn');
  const emailInput = document.getElementById('emailInput');
  const codeInput = document.getElementById('emailCodeInput');
  const emailMsg = document.getElementById('emailMsg');
  const codeMsg = document.getElementById('codeMsg');

  let countdownTimer;

  //寄送驗證碼
  sendBtn.addEventListener('click', function () {
    const email = emailInput.value.trim();
    if (!email) {
      showMessage('請輸入 Email', 'text-danger');
      return;
    }

    sendBtn.disabled = true;
    sendBtn.textContent = '寄送中...';
    sendBtn.classList.add('disabled-state');

    fetch('/sendCode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email })
    })
    .then(async res => {
      const text = await res.text();
      if (!res.ok) {
        showMessage(text, 'text-danger');
        resetButton();
        return;
      }
      showMessage(text, 'text-success');
      startCountdown();
    })
    .catch(() => {
      showMessage('🐾 系統錯誤，請稍後再試', 'text-danger');
      resetButton();
    });
  });

  //驗證驗證碼
  verifyBtn.addEventListener('click', function () {
    const email = emailInput.value.trim();
    const code = codeInput.value.trim();

    if (!email && !code) {
      showCodeMessage('請輸入 Email 和驗證碼', 'text-danger');
      return;
    }
    if (!email) {
      showCodeMessage('請輸入 Email', 'text-danger');
      return;
    }
    if (!code) {
      showCodeMessage('請輸入驗證碼', 'text-danger');
      return;
    }

    fetch('/checkCode', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email, code })
    })
    .then(async res => {
      const text = await res.text();
      if (!res.ok) {
        showCodeMessage(text, 'text-danger');
        return;
      }

      clearInterval(countdownTimer);
	  window.location.replace('/registerMember');
	 

    })
    .catch(() => {
      showCodeMessage('🐾 系統錯誤，請稍後再試', 'text-danger');
    });
  });

  //顯示 Email 寄送訊息
  function showMessage(message, className) {
    emailMsg.innerHTML = `<i class="icofont-paw"></i> ${message}`;
    emailMsg.className = `wrong ${className}`;
  }

  //顯示驗證訊息
  function showCodeMessage(message, className) {
    codeMsg.innerHTML = `<i class="icofont-paw"></i> ${message}`;
    codeMsg.className = `wrong ${className}`;
  }

  //倒數計時
  function startCountdown() {
    let seconds = 60;
    updateButtonText(seconds);

    countdownTimer = setInterval(() => {
      seconds--;
      updateButtonText(seconds);

      if (seconds <= 0) {
        clearInterval(countdownTimer);
        resetButton('重新寄送');
      }
    }, 1000);
  }

  //更新寄送按鈕文字
  function updateButtonText(seconds) {
    sendBtn.innerHTML = `重新寄送<br>驗證碼 (${seconds}s)`;
  }

  //還原寄送按鈕
  function resetButton(text = '寄送驗證碼') {
    sendBtn.disabled = false;
    sendBtn.classList.remove('disabled-state');
    sendBtn.textContent = text;
  }

  //下一步點擊跳轉
  nextBtn.addEventListener('click', () => {
    window.location.href = '/registerMember';
  });
});
