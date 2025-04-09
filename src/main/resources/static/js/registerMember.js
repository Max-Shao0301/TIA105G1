// 縣市對應的區域資料
const districtData = {
	Taipei: ["中正區", "大同區", "中山區", "松山區", "大安區", "萬華區", "信義區", "士林區", "北投區", "內湖區", "南港區", "文山區"],
	NewTaipei: ["板橋區", "新莊區", "中和區", "永和區", "土城區", "蘆洲區", "三重區",	"新店區", "汐止區", "樹林區", "鶯歌區", "三峽區", "林口區", "五股區",
		"泰山區", "深坑區", "石碇區", "坪林區", "三芝區", "石門區", "金山區",	"萬里區", "淡水區", "八里區", "瑞芳區", "貢寮區", "雙溪區", "平溪區", "烏來區"
	]
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
window.onload = function() {
	updateDistricts();
};



// Email 驗證碼發送與驗證功能

document.addEventListener('DOMContentLoaded', () => {
	const sendBtn = document.getElementById('sendCodeBtn');
	const verifyBtn = document.getElementById('verifyCodeBtn');
	const emailInput = document.getElementById('emailInput');
	const codeInput = document.getElementById('emailCodeInput');
	const emailMsg = document.getElementById('emailMsg');
	const codeMsg = document.getElementById('codeMsg');

	let countdownTimer;

	//發送驗證碼

	sendBtn.addEventListener('click', function() {
		// 清除錯誤訊息
		const emailError = document.getElementById('emailError');
		if (emailError) {
			emailError.innerHTML = '';
		}

		const email = emailInput.value.trim();
		if (!email) {
			showMessage('請輸入 Email', 'text-danger');
			return;
		}

		sendBtn.disabled = true;
		sendBtn.textContent = '寄送中...';
		sendBtn.classList.add('disabled-state');

		fetch('/sendCode', {
			method: 'POST',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			body: new URLSearchParams({ email })
		})
			.then(async res => {
				const text = await res.text();
				if (!res.ok) {
					showMessage(text, 'text-danger');
					resetButton();
					return;
				}
				showMessage(text, 'text-success');
				startCountdown();
			})
			.catch(() => {
				showMessage('🐾 系統錯誤，請稍後再試', 'text-danger');
				resetButton();
			});

	});
	//驗證驗證碼
	verifyBtn.addEventListener('click', function() {
		// 清除錯誤訊息
		const emailError = document.getElementById('emailError');
		if (emailError) {
			emailError.innerHTML = '';
		}


		const email = emailInput.value.trim();
		const code = codeInput.value.trim();

		if (!email || !code) {
			showCodeMessage('請輸入 Email 和驗證碼', 'text-danger');
			return;
		}

		fetch('/checkCode', {
			method: 'POST',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			body: new URLSearchParams({ email, code })
		})
			.then(async res => {
				const text = await res.text();
				if (!res.ok) {
					showCodeMessage(text, 'text-danger');
					return;
				}

				showCodeMessage(text, 'text-success');

				//中止倒數
				clearInterval(countdownTimer);
				// 鎖定所有欄位
				emailInput.blur(); // 移除 focus 狀態
				emailInput.disabled = true;
				codeInput.disabled = true;
				verifyBtn.disabled = true;
				verifyBtn.textContent = '已驗證成功';
				sendBtn.disabled = true;
				sendBtn.textContent = '已驗證';
				sendBtn.classList.add('disabled-state');
				sendBtn.disabled = true;
				sendBtn.classList.add("disabled-state");
				sendBtn.innerHTML = "已驗證<br>信箱鎖定";
			})
			.catch(() => {
				showCodeMessage('🐾 系統錯誤，請稍後再試', 'text-danger');
			});
	});

	//顯示 Email 寄送訊息
	function showMessage(message, className) {
		emailMsg.innerHTML = `<i class="icofont-paw"></i> ${message}`;
		emailMsg.className = `wrong ${className}`;
	}

	// 顯示驗證結果訊息
	function showCodeMessage(message, className) {
		codeMsg.innerHTML = `<i class="icofont-paw"></i> ${message}`;
		codeMsg.className = `wrong ${className}`;
	}

	// 倒數計時功能
	function startCountdown() {
		let seconds = 60;
		updateButtonText(seconds);

		countdownTimer = setInterval(() => {
			seconds--;
			updateButtonText(seconds);

			// 如果已經驗證就不繼續倒數
			if (sendBtn.innerHTML === '已驗證<br>信箱鎖定') {
				clearInterval(countdownTimer);
				return;
			}

			if (seconds <= 0) {
				clearInterval(countdownTimer);
				resetButton('重新寄送');
			}
		}, 1000);
	}

	// 更新寄送按鈕文字
	function updateButtonText(seconds) {
		sendBtn.innerHTML = `重新寄送<br>驗證碼 (${seconds}s)`;
	}

	// 還原寄送按鈕狀態
	function resetButton(text = '寄送驗證碼') {
		sendBtn.disabled = false;
		sendBtn.classList.remove('disabled-state');
		sendBtn.textContent = text;
	}
});

