$(document).ready(function () {
  // 查看訂單詳情按鈕
  $(document).on('click', '.check', function (e) {
    e.preventDefault();
    const orderId = $(this).data('id');

    // 重置按鈕狀態
    $('.cancelOrderBtn, #reviewBtn, #ratedBadge').hide();

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

        if (order.orderStatus === 0) {
          // 訂單取消，顯示取消時間
          const updateTime = order.updateTime;
          $('#rating').html(`<span class="cancel-time">${updateTime} 取消</span>`);
          $('td.clo:contains("訂單評論")').text("取消時間");
        } else {
          $('td.clo:contains("取消時間")').text("訂單評論");
          if (!order.rating) {
            $('#rating').html("尚未評價");
          } else {
            const filledStars = '<span class="show-star filled">&#9733;</span>'.repeat(order.star);
            const emptyStars = '<span class="show-star empty">&#9733;</span>'.repeat(5 - order.star);
            const comment = `<span class="comment-text">${order.rating}</span>`;
            $('#rating').html(`${filledStars}${emptyStars}&nbsp;&nbsp;&nbsp;${comment}`);
          }
        }

        $('#cancelOrderId').val(order.orderId);

        if (order.orderStatus === 1) {
          $('.cancelOrderBtn').show();
        }

        if (order.orderStatus === 2) {
          if (!order.rating) {
            $('#reviewBtn').show();
            $('#ratedBadge').hide();
          } else {
            $('#reviewBtn').hide();
            $('#ratedBadge').show();
          }
        }

        handleCancelBtn(order.appointmentTime);
      },
      error: function () {
        alert("載入失敗，請稍後再試！");
      }
    });
  });

  // 點擊「取消訂單」按鈕 → 開啟自訂 overlay
  $(document).on('click', '.cancelOrderBtn', function (e) {
    e.preventDefault();
    $('#customCancelOverlay').fadeIn();
  });

  // 關閉取消彈窗
  $('#cancelDismissBtn').click(function () {
    $('#customCancelOverlay').fadeOut();
  });

  // 確認取消訂單
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

  // 點擊「我要評價」按鈕，手動補 backdrop 並顯示 modal
  $(document).on('click', '#reviewBtn', function () {
    // 加入遮罩（如果還沒加）
    if ($('.review-backdrop').length === 0) {
      $('<div class="review-backdrop"></div>').appendTo('body');
    }

    // 顯示 modal
    $('#reviewModal').modal('show');
  });
  
  
  // 關閉評論彈窗，移除 backdrop 並回到訂單彈窗狀態
  $('#reviewModal').on('hidden.bs.modal', function () {
    $('.review-backdrop').remove();

    if ($('#orderDetailModal').hasClass('show')) {
      $('body').addClass('modal-open');
    }
  });

  // 評價星星點擊
  $(document).on('click', '.star', function () {
    const rating = $(this).data('value');
    $('#selectedRating').val(rating);
    $('.star').each(function (i) {
      if (i < rating) {
        $(this).html('&#9733;').removeClass('empty').addClass('filled');
      } else {
        $(this).html('&#9734;').removeClass('filled').addClass('empty');
      }
    });
  });

  // 評價送出
  $('#submitReviewBtn').click(function () {
    const star = $('#selectedRating').val();
    const comment = $('#reviewText').val().trim();
    const orderId = $('#cancelOrderId').val();

    if (!star || comment === '') {
      alert("請選擇星星並填寫評論！");
      return;
    }

    $.ajax({
      url: '/order/submitComment',
      method: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({ orderId, star, rating: comment }),
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

  // 取消按鈕可用性檢查
  function handleCancelBtn(appointmentTimeStr) {
    const now = new Date();
    const appointmentTime = new Date(appointmentTimeStr);
    const diffMins = Math.floor((appointmentTime - now) / 60000);

    const btn = $('.cancelOrderBtn');
    if (diffMins <= 30 && diffMins >= 0) {
      btn.prop('disabled', true).addClass('disabled-btn').text(`無法取消（剩 ${diffMins} 分）`);
    } else if (diffMins < 0) {
      btn.prop('disabled', true).addClass('disabled-btn').text('無法取消');
    } else {
      btn.prop('disabled', false).removeClass('disabled-btn').text('取消訂單');
    }
  }

  // 成功取消後，關閉提示時刷新
  $('#cancelSuccessModal').on('hidden.bs.modal', function () {
    location.reload();
  });
});

// 狀態轉換
function convertStatus(code) {
  switch (code) {
    case 0: return '已取消';
    case 1: return '未完成';
    case 2: return '已完成';
	case 3: return '未付款';
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


$(document).ready(function() {
  // 初始化 nice-select
  $('select').niceSelect();

  // 強制刷新目前選項（解決 always 顯示 5 的問題）
  $('select').niceSelect('update');

  // 綁定變更事件（雖然有寫 onchange，也可加這個保險）
  $('#pageSize').on('change', function() {
    updatePageSize();
  });
});

function updatePageSize() {
  const selectedSize = document.getElementById("pageSize").value;
  const params = new URLSearchParams(window.location.search);
  params.set("page", 1); // 回到第一頁
  params.set("pageSize", selectedSize);
  window.location.href = "/orderList?" + params.toString();
}


let searchTimer;

function searchOrders() {
  clearTimeout(searchTimer);

  searchTimer = setTimeout(() => {
    const keyword = document.getElementById("searchInput").value.trim();

    fetch(`/orders/search?keyword=${encodeURIComponent(keyword)}`)
      .then(res => res.text())
      .then(html => {
        const tbody = document.querySelector("#orderTable tbody");
        const pagination = document.getElementById("paginationWrapper");
        const pagination2 = document.getElementById("paginationWrapper2");

        tbody.innerHTML = html;

        if (keyword !== "") {
          if (pagination) pagination.style.display = "none";
          if (pagination2) pagination2.style.display = "none";
          if (tbody.querySelectorAll("tr").length === 0) {
            tbody.innerHTML = `<tr><td colspan="6">查無符合的訂單</td></tr>`;
          }
        } else {
          if (pagination) pagination.style.display = "block";
          if (pagination2) pagination2.style.display = "block";
        }
      });
  }, 300);
}

let currentSortField = null;
let currentSortAsc = true;

function sortOrders(field) {
  const table = document.getElementById("orderTable").getElementsByTagName("tbody")[0];
  const rows = Array.from(table.rows);

  // 決定升冪還是降冪
  if (currentSortField === field) {
    currentSortAsc = !currentSortAsc;
  } else {
    currentSortField = field;
    currentSortAsc = true;
  }

  rows.sort((a, b) => {
    let valA, valB;

    switch (field) {
      case "orderId":
        valA = parseInt(a.cells[0].innerText.trim());
        valB = parseInt(b.cells[0].innerText.trim());
        break;
      case "orderDate":
        valA = new Date(a.cells[1].innerText.trim());
        valB = new Date(b.cells[1].innerText.trim());
        break;
      case "status":
        valA = a.cells[4].innerText.trim();
        valB = b.cells[4].innerText.trim();
        break;
      default:
        return 0;
    }

    if (valA < valB) return currentSortAsc ? -1 : 1;
    if (valA > valB) return currentSortAsc ? 1 : -1;
    return 0;
  });

  // 清空並重新插入排序後的列
  table.innerHTML = "";
  rows.forEach(row => table.appendChild(row));
}
