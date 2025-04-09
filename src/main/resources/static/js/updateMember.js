const  phone = document.querySelector("#memPhone").value
const name = document.querySelector("#memName").value
const address = document.querySelector("#address").value

const districtData = {
	Taipei: ["中正區", "大同區", "中山區", "松山區", "大安區", "萬華區", "信義區", "士林區", "北投區", "內湖區", "南港區", "文山區"],
	NewTaipei: ["板橋區", "新莊區", "中和區", "永和區", "土城區", "蘆洲區", "三重區",	"新店區", "汐止區", "樹林區", "鶯歌區", "三峽區", "林口區", "五股區",
		"泰山區", "深坑區", "石碇區", "坪林區", "三芝區", "石門區", "金山區",	"萬里區", "淡水區", "八里區", "瑞芳區", "貢寮區", "雙溪區", "平溪區", "烏來區"
	]
};

  function updateDistricts() {
    const city = document.getElementById("city").value;
    const districtSelect = document.getElementById("district");

    districtSelect.innerHTML = '<option value="">請選擇區域</option>';

    if (city && districtData[city]) {
      districtData[city].forEach(district => {
        const option = document.createElement("option");
        option.value = district;
        option.text = district;
        districtSelect.appendChild(option);
      });

      // 第一次預設選回來
      const selectedDistrict = districtSelect.getAttribute("data-selected");
      if (selectedDistrict) {
        districtSelect.value = selectedDistrict;
        districtSelect.removeAttribute("data-selected");
      }
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
      if (!phone || !address) {
          alert(`歡迎 ${name} 使用第三方登入，請先填寫完整個人資料～`);
      }
    updateDistricts(); // 首次載入自動帶入
    document.getElementById("city").addEventListener("change", updateDistricts);
  });

  const cancelBtn = document.querySelector(".btn.cancel");
    if (cancelBtn) {
      cancelBtn.addEventListener("click", () => {
        window.location.href = "/member";
      });
    }


	const flagInput = document.getElementById("member-updated-flag");
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