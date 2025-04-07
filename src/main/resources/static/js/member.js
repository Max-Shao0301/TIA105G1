document.addEventListener("DOMContentLoaded", function () {
  const logoutBtn = document.querySelector("form[action='/logout'] .button2"); // 登出按鈕
  const logoutForm = document.querySelector("form[action='/logout']");

  const popup = document.getElementById("logout-confirm-popup");
  const confirmBtn = document.getElementById("logout-confirm-yes");
  const cancelBtn = document.getElementById("logout-confirm-no");

  if (logoutBtn && popup && confirmBtn && cancelBtn) {
    logoutBtn.addEventListener("click", function (e) {
      e.preventDefault(); // 阻止直接提交
      popup.style.display = "flex";
    });

    confirmBtn.addEventListener("click", function () {
      logoutForm.submit(); // 使用者按確認才真的提交
    });

    cancelBtn.addEventListener("click", function () {
      popup.style.display = "none";
    });
  }
});