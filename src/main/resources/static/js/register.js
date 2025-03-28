// 縣市對應的區域資料
const districtData = {
    Taipei: ["中正區", "大同區", "中山區", "松山區", "大安區", "萬華區"],
    NewTaipei: ["板橋區", "新莊區", "中和區", "永和區", "三重區", "土城區"]
};

// 動態更新區域下拉選單
function updateDistricts() {
    const city = document.getElementById("city").value;
    const districtSelect = document.getElementById("district");

    // 清空區域選單
    districtSelect.innerHTML = '<option value="">請選擇區域</option>';

    if (city && districtData[city]) {
        districtData[city].forEach(district => {
            const option = document.createElement("option");
            option.value = district;
            option.text = district;
            districtSelect.appendChild(option);
        });

        // 若已經有選好的區域值，重新填入
        const selectedDistrict = districtSelect.getAttribute("data-selected");
        if (selectedDistrict) {
            districtSelect.value = selectedDistrict;
        }
    }
}

// 頁面載入時自動填入區域（若已有值）
window.onload = function () {
    updateDistricts();
};