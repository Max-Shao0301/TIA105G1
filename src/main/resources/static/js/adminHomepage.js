//jQuery表格套件
let orderTable = new DataTable("#myTable");
let memberTable = new DataTable("#myTable1");
let employeeTable = new DataTable("#myTable2");
let resumeTable = new DataTable("#myTable3");

function showContent(sectionId) {
    // 先隱藏所有內容
    const sections = document.querySelectorAll(".content");
    const homepage_el = document.querySelector("#homepage_img");
    sections.forEach((section) => section.classList.remove("active"));
    homepage_el.style.display = "none";

    // 顯示被點擊的內容
    document.getElementById(sectionId).classList.add("active");
}
// 顯示指定的內容區塊
function showContent(sectionId) {
    const sections = document.querySelectorAll(".content");
    const homepage_el = document.querySelector("#homepage_img");
    // 隱藏所有內容區塊
    sections.forEach((section) => section.classList.remove("active"));
    homepage_el.style.display = "none";  // 預設隱藏首頁圖像

    // 顯示對應區塊
    if (sectionId === "homepage_img") {
        homepage_el.style.display = "block";  // 顯示首頁圖像
    } else {
        document.getElementById(sectionId).classList.add("active");
    }
}

$(document).ready(function () {
    //彈窗
    let modal = $("#resumeModal");
    modal.hide(); // 確保一開始是隱藏的

    // 點擊檢視按鈕時顯示彈窗
    $(document).on("click", ".view-resume", function (e) {
        e.stopPropagation(); // 防止事件冒泡影響其他點擊行為
        let modal = $("#resumeModal");

        $("#modal-name").text($(this).data("name"));
        $("#modal-gender").text($(this).data("gender") == 1 ? "男性" : "女性");
        $("#modal-phone").text($(this).data("phone"));
        $("#modal-email").text($(this).data("email"));
        $("#modal-plate").text($(this).data("plate"));
        $("#modal-introduction").text($(this).data("introduction"));
        $("#modal-license").attr("src", $(this).data("license"));

        modal.fadeIn(); // 使用 fadeIn() 顯示
        $(".selected-row").removeClass("selected-row"); // 移除之前選中的行
        $(this).closest("tr").addClass("selected-row"); // 標記選中的行
    });

    // 點擊 X 或背景關閉彈窗
    $(document).on("click", ".close, #resumeModal", function (event) {
        if (
            $(event.target).is(".close") ||
            $(event.target).is("#resumeModal")
        ) {
            modal.fadeOut(); // 使用 fadeOut() 隱藏
        }
    });
    let currentTab = sessionStorage.getItem("currentTab"); // 讀取儲存的頁籤

    if (currentTab) {
        // 如果有儲存的頁籤，顯示對應區塊
        showContent(currentTab);
    } else {
        // 預設顯示第一個頁籤（這裡是訂單管理）
        showContent("homepage_img");
    }

    //聘用婉拒按鈕
    $(document).on("click", "#approve-btn, #reject-btn", function (event) {

        sessionStorage.setItem("currentTab", "resume"); // 記錄為履歷審核

        let selectedRow = $(".selected-row"); // 取得被選中的履歷行
        let applyId = selectedRow.find("td:nth-child(1)").text();
        $("input[name='applyId']").val(applyId); // 設置 apply_id 隱藏欄位的值


        if ($(event.target).is("#approve-btn")) {
            alert("該應徵者已被聘用");
            $("#rs").attr("value", "1");

        } else if ($(event.target).is("#reject-btn")) {

            alert("該應徵者已被婉拒！");
            $("#rs").attr("value", "0");

        }
        modal.fadeOut();
    });


    //停權 監聽所有表格內的停權按鈕（使用事件委派）
    $(document).on("click", ".fa-ban", function (event) {
        let tableType = $(this).closest("table").data("type"); // 取得表格的 data-type 屬性
        sessionStorage.setItem("currentTab", tableType); // 根據表格類型記錄當前頁面
        let row = $(this).closest("tr"); // 取得當前行
        let user = row.find("td:nth-child(2)").text(); // 取得用戶姓名

        if (!confirm(`確定要將用戶: ${user} 停權嗎？`)) {
            event.preventDefault();
        }


    });


});