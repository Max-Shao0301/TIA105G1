$("form#loginForm").on("submit", function (event) {
  event.preventDefault(); // 防止表單默認提交

  const username = $("input#username").val();
  const password = $("input#password").val();

  $.ajax({
    url: '/admin/login',  // 後端登入 API
    type: 'POST',
    data: {
      account: username,
      password: password
    },
    success: function(response) {
      // 如果後端回傳 "success"，顯示登入成功的提示
      if (response === "success") {
        alert("登入成功");
        // 你可以在這裡跳轉到其他頁面，例如管理面板
        window.location.href = "/admin/home/page";  // 跳轉到管理面板
      }
    },
    error: function() {
      // 如果後端回傳 "failure" 或發生錯誤，顯示登入失敗的提示
      alert("登入失敗");
      $("input#username").val(""); // 清空輸入框
      $("input#password").val(""); // 清空輸入框
    }
  });
});
