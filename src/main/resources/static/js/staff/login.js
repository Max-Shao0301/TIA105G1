// 忘記密碼彈窗
$('#forgotPasswordLink').click(function() {
    $('#forgotPasswordModal').modal('show');
});


// 修改確認
$('#changePasswordForm').on('submit', function (e) {
    var newPassword = $('#newPassword').val();
    var checkNewPassword = $('#checkNewPassword').val();

    if (newPassword !== checkNewPassword) {
        alert("密碼不一致，請再試一次！");
        e.preventDefault();
    }
});
