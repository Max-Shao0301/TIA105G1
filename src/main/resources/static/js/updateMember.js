
  const districtData = {
    Taipei: ["中正區", "大同區", "中山區", "松山區", "大安區", "萬華區"],
    NewTaipei: ["板橋區", "新莊區", "中和區", "永和區", "三重區", "土城區"]
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