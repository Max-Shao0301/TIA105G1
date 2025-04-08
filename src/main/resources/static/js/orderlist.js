$(document).ready(function () {
  // 查看訂單詳情按鈕
  $(document).on('click', '.check', function (e) {
    e.preventDefault();
    const orderId = $(this).data('id');

    $('#orderDetailModal').modal('show');

    $.ajax({
      url: `/orders/detail/${orderId}`,
      method: 'GET',
      success: function (order) {
        $('#orderId').text(order.orderId);
        $('#orderStatus').text(convertStatus(order.orderStatus));
        $('#appointmentTime').text(order.appointmentTime);
        $('#createdTime').text(order.createdTime);
        $('#onLocation').text(order.onLocation);
        $('#offLocation').text(order.offLocation);
        $('#carNumber').text(order.carNumber);
        $('#staffName').text(order.staffName);
        $('#petName').text(order.pet);
        $('#payment').text(order.payment + " 元");
        $('#payMethod').text(convertPayMethod(order.payMethod));
        $('#point').text(order.point != null ? order.point + " 點" : "無");
        $('#notes').text(order.notes || "無");
		if (!order.rating) {
		  $('#rating').html("尚未評價");
		} else {
		  const filledStars = '<span class="show-star filled">&#9733;</span>'.repeat(order.star);
		  const emptyStars = '<span class="show-star empty">&#9733;</span>'.repeat(5 - order.star);
		  const comment = `<span class="comment-text">${order.rating}</span>`;
		$('#rating').html(`${filledStars}${emptyStars}&nbsp;&nbsp;&nbsp;${comment}`);
		}
        $('#cancelOrderId').val(order.orderId);

        // 如果訂單已取消或已完成，就隱藏按鈕
        if (order.orderStatus === 0 || order.orderStatus === 2) {
          $('.cancelOrderBtn').hide();
        } else {
          $('.cancelOrderBtn').show();
        }

		if (order.orderStatus === 2) {
		  $('.cancelOrderBtn').hide();

		  if (!order.rating) {
		    $('#reviewBtn').show();
		    $('#ratedBadge').hide();
		  } else {
		    $('#reviewBtn').hide();
		    $('#ratedBadge').show();
		  }
		} else {
		  $('.cancelOrderBtn').hide();
		  $('#reviewBtn').hide();
		  $('#ratedBadge').hide();
		}

		
        handleCancelBtn(order.appointmentTime);
      },
      error: function () {
        alert("載入失敗，請稍後再試！");
      }
    });
  });

  // 綁定點擊取消訂單按鈕，打開自訂彈窗
  $(document).on('click', '.cancelOrderBtn', function (e) {
    e.preventDefault();
    $('#customCancelOverlay').fadeIn();
  });

  // 關閉彈窗
  $('#cancelDismissBtn').click(function () {
    $('#customCancelOverlay').fadeOut();
  });

  // 確認取消
  $('#confirmCancelSubmitBtn').click(function () {
    const orderId = $('#cancelOrderId').val();

    $.ajax({
      url: `/order/cancelOrder`,
      method: 'GET',
      data: { orderId: orderId },
      success: function () {
        $('#customCancelOverlay').fadeOut();
        $('#orderDetailModal').modal('hide');
        $('#cancelSuccessModal').modal('show');

        
      },
      error: function () {
        alert("取消訂單失敗，請稍後再試！");
      }
    });
  });
});

// 狀態轉換
function convertStatus(code) {
  switch (code) {
    case 0: return '已取消';
    case 1: return '未完成';
    case 2: return '已完成';
    default: return '未知';
  }
}

function convertPayMethod(code) {
  switch (code) {
    case 0: return '點數';
    case 1: return '刷卡';
    case 2: return '點數＋刷卡';
    default: return '未知';
  }
}


// 判斷是否能取消
function handleCancelBtn(appointmentTimeStr) {
  const now = new Date();
  const appointmentTime = new Date(appointmentTimeStr);
  const diffMins = Math.floor((appointmentTime - now) / 60000);

  if (diffMins <= 30 && diffMins >= 0) {
      $('.cancelOrderBtn')
        .prop('disabled', true)
        .addClass('disabled-btn')
        .text(`無法取消（剩 ${diffMins} 分）`);
    } else if (diffMins < 0) {
      $('.cancelOrderBtn')
        .prop('disabled', true)
        .addClass('disabled-btn')
        .text(`無法取消`);
    } else {
      $('.cancelOrderBtn')
        .prop('disabled', false)
        .removeClass('disabled-btn')
        .text('取消訂單');
    }
}
$('#cancelSuccessModal').on('hidden.bs.modal', function () {
  location.reload();
});

// 點擊評價按鈕開啟 modal
$(document).on('click', '#reviewBtn', function () {
  $('#reviewModal').modal('show');
});

// 點擊星星切換樣式
$(document).on('click', '.star', function () {
  const rating = $(this).data('value');
  $('#selectedRating').val(rating);
  $('.star').each(function (i) {
    if (i < rating) {
      $(this).html('&#9733;') // 實心
             .removeClass('empty')
             .addClass('filled');
    } else {
      $(this).html('&#9734;') // 空心
             .removeClass('filled')
             .addClass('empty');
    }
  });

});

// 送出評價
$('#submitReviewBtn').click(function () {
  const star = $('#selectedRating').val();
  const comment = $('#reviewText').val().trim();
  const orderId = $('#cancelOrderId').val();

  
  if (!star && comment === '') {
    alert("請選擇星星並填寫評論！");
    return;
  }

  if (!star) {
    alert("請選擇星星！");
    return;
  }

  if (comment === '') {
    alert("請填寫評論！");
    return;
  }
 
  
   $.ajax({
     url: '/order/submitComment',
     method: 'POST',
     contentType: 'application/json',
     data: JSON.stringify({
       orderId: orderId,
       star: star,
       rating: comment
	    }),
		success: function () {
		  alert("評價送出成功！");
		  $('#reviewModal').modal('hide');
		  $('#orderDetailModal').modal('hide');
		  $('#selectedRating').val('');
		  $('#reviewText').val('');
		  $('.star').html('&#9734;');
		  location.reload();
	       },
	       error: function () {
	         alert("評價失敗，請稍後再試！");
	       }
	     });
	   });
	   
	   $('#reviewModal').on('hidden.bs.modal', function () {
	     if ($('#orderDetailModal').hasClass('show')) {
	       $('body').addClass('modal-open');
	     }
	   });
	   
	   