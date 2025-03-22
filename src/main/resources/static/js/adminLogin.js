$("form#loginForm").on("submit", function (event) {
  event.preventDefault(); // 防止表單默認提交

  const username = $("input#username").val();
  const password = $("input#password").val();

  $.ajax({
    url: "/admin/login",  // 後端登入 API
    type: "POST",
    data: {
      account: username,
      password: password
    },
    success: function(response) {
      // 檢查後端回傳的 JSON 資料
      if (response.status === "success") {
        alert(response.message);  // 顯示登入成功訊息
        window.location.href = "/admin/home/page";  // 跳轉到管理面板
      } else {
        alert(response.message); // 顯示錯誤訊息（但這種情況應該不會發生，因為錯誤會走 error）
      }
    },
    error: function(xhr) {
      // 如果後端回傳 401（未授權），顯示登入失敗訊息
      if (xhr.status === 401) {
        alert("帳號或密碼錯誤");
      } else {
        alert("發生未知錯誤，請稍後再試");
      }
      $("input#username").val(""); // 清空輸入框
      $("input#password").val(""); // 清空輸入框
    }
  });
});
