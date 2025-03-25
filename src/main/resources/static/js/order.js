document.addEventListener('DOMContentLoaded', function() {
    const textarea = document.querySelector('.review-input');
    const charCount = document.querySelector('.char-count');
    const submitBtn = document.querySelector('.submit-btn');
    const ratingInputs = document.querySelectorAll('input[name="rating"]');
    
    let rating = 0;

    // 監聽星級評分
    ratingInputs.forEach(input => {
      input.addEventListener('change', (e) => {
        rating = parseInt(e.target.value);
        checkSubmitButton();
      });
    });

    // 監聽文字輸入
    textarea.addEventListener('input', function() {
      const length = this.value.length;
      charCount.textContent = `${length}/300`;
      
      // 如果超過300字，截斷文字
      if (length > 300) {
        this.value = this.value.slice(0, 300);
      }
      
      checkSubmitButton();
    });

    // 檢查是否可以提交
    function checkSubmitButton() {
      const length = textarea.value.length;
      submitBtn.disabled = length > 300 || rating === 0;
    }

    // 提交評論
    submitBtn.addEventListener('click', function() {
      const reviewData = {
        rating: rating,
        comment: textarea.value
      };
      
      // 這裡可以添加您的提交邏輯，例如發送到後端API
      console.log('提交的評論數據：', reviewData);
      alert('評論提交成功！');
      
      // 重置表單
      rating = 0;
      textarea.value = '';
      charCount.textContent = '0/300';
      ratingInputs.forEach(input => input.checked = false);
      checkSubmitButton();
    });
  });

  function getOrderDetails() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderData = urlParams.get('order');

    if (orderData) {
        const order = JSON.parse(decodeURIComponent(orderData));
        document.getElementById('orderDetails').innerHTML = `
            <p><strong>訂單編號：</strong> ${order.orderId}</p>
            <p><strong>訂單日期：</strong> ${order.orderDate}</p>
            <p><strong>上車地點：</strong> ${order.pickupLocation}</p>
            <p><strong>下車地點：</strong> ${order.dropoffLocation}</p>
            <p><strong>服務人員：</strong> ${order.staff}</p>
            <p><strong>訂單狀態：</strong> ${order.status}</p>
        `;
    }
}

function goBack() {
    window.history.back();
}

getOrderDetails();