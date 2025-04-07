document.addEventListener("DOMContentLoaded", function () {
  // 🔹 欄位元素
  const passwordInput = document.getElementById("password");
  const confirmInput = document.getElementById("confirm-password");
  const oldPwdInput = document.getElementById("old-password");

  const errorPasswordLength = document.getElementById("error-password-length");
  const errorPasswordLengthText = document.getElementById("error-password-length-text");
  const successPasswordLength = document.getElementById("success-password-length");

  const matchMsg = document.getElementById("match-msg");
  const matchMsgText = document.getElementById("match-msg-text");

  // 🔸 隱藏錯誤訊息
  function hideOldPasswordErrors() {
    const beanError = document.getElementById("error-oldPassword-bean");
    const customError = document.getElementById("error-oldPassword-custom");
    if (beanError) beanError.style.display = "none";
    if (customError) customError.style.display = "none";
  }

  function hidePasswordErrors() {
    const beanError = document.getElementById("error-password-bean");
    if (beanError) beanError.style.display = "none";
    if (errorPasswordLength) errorPasswordLength.style.display = "none";
  }

  function hideConfirmPasswordErrors() {
    const beanError = document.getElementById("error-confirmPassword-bean");
    const customError = document.getElementById("error-confirmPassword-custom");
    if (beanError) beanError.style.display = "none";
    if (customError) customError.style.display = "none";
  }

  // 🔸 驗證邏輯
  function checkPasswordLength() {
    const val = passwordInput.value.trim();
    if (!val) {
      errorPasswordLength.style.display = "none";
      successPasswordLength.style.display = "none";
      return;
    }

    if (val.length >= 8 && val.length <= 16) {
      errorPasswordLength.style.display = "none";
      successPasswordLength.style.display = "flex";
    } else {
      successPasswordLength.style.display = "none";
      errorPasswordLength.style.display = "flex";
      errorPasswordLengthText.textContent = "密碼長度需為 8-16 字元";
    }
  }

  function checkPasswordMatch() {
    const pwd = passwordInput.value.trim();
    const confirmPwd = confirmInput.value.trim();

    if (!pwd || !confirmPwd) {
      matchMsg.style.display = "none";
      return;
    }

    matchMsg.style.display = "flex";

    if (pwd === confirmPwd) {
      matchMsg.classList.remove("status-error");
      matchMsg.classList.add("status-success");
      matchMsgText.textContent = "密碼一致";
    } else {
      matchMsg.classList.remove("status-success");
      matchMsg.classList.add("status-error");
      matchMsgText.textContent = "兩次密碼不一致";
    }
  }

  // 🔸 綁定事件
  if (passwordInput) {
    passwordInput.addEventListener("input", () => {
      hidePasswordErrors();
      checkPasswordLength();
      checkPasswordMatch();
    });
  }

  if (confirmInput) {
    confirmInput.addEventListener("input", () => {
      hideConfirmPasswordErrors();
      checkPasswordMatch();
    });
  }

  if (oldPwdInput) {
    oldPwdInput.addEventListener("input", hideOldPasswordErrors);
  }

  const cancelBtn = document.querySelector(".btn.cancel");
  if (cancelBtn) {
    cancelBtn.addEventListener("click", () => {
      window.location.href = "/member";
    });
  }


  const flagInput = document.getElementById("password-updated-flag");
  const isUpdated = flagInput && flagInput.value === "true";

  if (isUpdated) {
    const popup = document.getElementById("success-popup");
    const confirmBtn = document.getElementById("success-popup-confirm");

    if (popup && confirmBtn) {
      popup.style.display = "flex";

      confirmBtn.addEventListener("click", () => {
        window.location.href = "/member";
      });
    }
  }
});
