// 當使用者點擊「忘記密碼」時，啟動模態框
$('#forgotPasswordLink').click(function() {
    $('#forgotPasswordModal').modal('show');
});

// 忘記密碼表單提交的處理邏輯
$('#forgotPasswordForm').on('submit', function(e) {
    e.preventDefault();
    var email = $('#email').val();
    alert("已發送密碼重設郵件至: " + email);
    $('#forgotPasswordModal').modal('hide');
});