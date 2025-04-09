//@CrossOrigin(origins = "http://127.0.0.1:5500")
//==========全域參數宣告==========//
const steps = [
    "appointment",
    "serviceSelection",
    "petInformation",
    "agreement",
    "confirmation"
];
//抓視窗高用的
const windowHeight = window.innerHeight;
const documentHeight = document.documentElement.scrollHeight;  
//使用者的會員ID
let memberId;
// 當前畫面頁數
let currenStep = 1;
//訂單資料
const order = {
	apptTime:{},
	appDate:{},
	onLocation:{},
	offLocation:{},
	staffName:{},
	staffPhone:{},
	memName:{},
	memPhone:{},
	petType:{},
	petGender:{},
	petName:{},
	petWeight:{},
	notes:{},
	petId:{},
	payment:{},  //暫時寫死
	memPoints:{}
};
const orderToDb = {
	date:{},
	apptTime:{},
	schId : {},
	onLocation : {},
	offLocation : {},
	point : {},
	payment : {},
	payMethod : {} ,
	notes:{},
	petId:{}
};
let staffInfo;
//==========個別頁面的內容==========//
let appointment =`	
			<div class="agreement-container">
				<h1 class="title">預約時間與地點</h1>

				
				<!-- 選擇日期 -->
				<label for="date" class="selection">預約日期：</label>
				<input type="text" id="date_picker">
				<!-- 選擇時間 -->
				<label for="time-slot" class="selection">預約時段：</label>
				<select id="time_menu" name="time_menu" required>
					<!-- <option value="">請選擇時段</option> -->
					
				</select>
			
				<!-- 上車地址 -->
				<label for="pickup" class="selection">上車地址：</label>
				<input type="text" id="pickup" name="pickup" placeholder="請輸入上車地址" required>
			
				<!-- 下車地址 -->
				<label for="dropoff" class="selection">下車地址：</label>
				<input type="text" id="dropoff" name="dropoff" placeholder="請輸入下車地址" required>

				<!-- 費用說明 -->
				<p class="remark"><span id="scope">目前服務範圍：僅限雙北地區</span><br>請提供完整的地址資訊<br>單趟費用起跳價 100 元，後續每公里 50 元</p>
				<!-- 費用細節 -->
				<div class ="none" id="amouteInfo">
					<p>車程距離：<span id="driveDistance"></span> </p>
					<p>預估車程：<span id="driveTime"></span> </p>
					<p id="price">預估費用：<span id="amoute"></span> </p>
					<p class="remark">此預估車程及費用僅為參考，實際時間將依當下車況而定<br>實際費用請依訂單確認畫面為準</p>
				</div>
				<!-- 下一步按鈕 -->
				<button type="submit" id="nextPage">下一步</button>

				<div class="none" id="lightbox">
					<article id="lightboxMes">
					<button class="close_card_btn">&times;</button>
					<div >
						<button type="button" id="Yes" class="check_btn">確定</button>
					</div>
					</article>
				</div>
			</div>`;

let serviceSelection =`
			<h2 id='title'>服務人員選擇</h2>

			<div class="card_div">
				
				<div class="none" id="lightbox">
					<article>
						<button class="close_card_btn">&times;</button>
						<div class = "staff_card">
							<div class = "staffInfo_text">
							</div>
						</div>
						<div class = "nextPage_div">
							<button type="button" id="nextPage" class="page_break">下一步</button>
						</div>
					</article>
				</div>
			</div>
			<button class="select_service">lightbox_btn</button>
			<div class="backPage_container">
				<button  id="backPage" class="page_break">上一步</button>
			</div>`;
let petInformation =`
			<h1 class="title">毛小孩資料填寫</h1>
			<div class="q_div" id="q1">
				<label for="savedPets" class="question_title">選擇已儲存的毛小孩</label>
				<select id="savedPets" class="question_input">
					<option id="noPet"value="noPet">未選擇</option>
				</select>
			</div>
			<div class="q_div" id="q2">
				<span class="question_title">毛小孩類別</span>
				<div id="petType">
				<label><input type="radio" id="typeCat" name="petType" value="cat"> 貓</label>
				<label><input type="radio" id="typeDog" name="petType" value="dog"> 狗</label>
				</div>
			</div>
	
			<div class="q_div" id="q3">
				<span class="question_title">毛小孩性別</span>
				<div id="petGender">
				<label><input type="radio" id="genderM" name="petGender" value="1"> 公</label>
				<label><input type="radio" id="genderF" name="petGender" value="2"> 母</label>
				</div>
			</div>
			<div class="q_div" id="q4">
				<label for="petName" class="question_title">毛小孩大名</label>
				<input type="text" id="petName" class="input-text" placeholder="請輸入毛小孩大名">
			</div>

			<div class="q_div" id="q5">
				<label for="petWeight" class="question_title">毛小孩體重(kg)</label>
				<input type="text" id="petWeight" class="input-text" placeholder="例如：4kg">
			</div>
			<div class="q_div" id="q6">
				<label for="petNotes" class="question_title">其他毛小孩需要我們注意的事項(選填)：</label>
				<textarea id="petNotes" class="textarea" placeholder="例如：疾病、具攻擊性、容易受驚嚇等"></textarea>
			</div>
			<div class="page_break_div">
				<button class="page_break" id="backPage">上一步</button>
				<button class="page_break" id="nextPage">下一步</button>
			</div>
			<div class="none" id="lightbox">
				<article id="petInfo_Art">
					<button class="close_card_btn">&times;</button>
					<div >
						<button type="button" id="no" class="petInfo_btn">否</button>
						<button type="button" id="yes" class="petInfo_btn">是</button>
					</div>
				</article>
			</div>`
let agreement =`
			<div class="agreement_text">
				<h3 class="title">寵愛牠 服務同意書</h3>
				<div class="agreement-content">
					<h4>一、服務內容</h4>
					<ol>
						<li>本公司提供寵物接送服務，負責將寵物從指定地點安全送達至目的地。</li>
						<li>服務時間將依據雙方約定時間進行，如需更改，請提前 24 小時通知。</li>
					</ol>
					<h4>二、寵物健康與安全</h4>
					<ol>
						<li>寵物主人需提供寵物健康狀況及年齡，無病痛或疾病才可使用服務。</li>
						<li>如有特殊情況，請提前告知，以確保服務順利進行。</li>
						<li>若寵物於運輸途中出現異常反應或攻擊行為，可能會中止服務，費用恕不退還。</li>
					</ol>
					<h4>三、基本權益與賠償</h4>
					<ol>
						<li>本公司承諾提供安全且無損壞之服務。</li>
						<li>運輸過程中，將盡全力確保寵物安全。</li>
						<li>本公司對於突發事故或天災不承擔責任。</li>
					</ol>
					<h4>四、費用與取消政策</h4>
					<ol>
						<li>服務費用需於接送前支付，並開立發票。</li>
						<li>取消需提前通知，否則可能不予退款。</li>
					</ol>
					<h4>五、其他</h4>
					<ol>
						<li>本公司保留變更條款的權利。</li>
						<li>如遇不可抗力因素，將與客戶協商解決方案。</li>
					</ol>
				</div>
				<div class="agreement-footer">
					<label><input type="checkbox" id="checkAgree"> 我已閱讀並同意以上條款</label>
					<div class="page_break_div">
						<button class="page_break" id="backPage">上一步</button>
						<button class="page_break" id="nextPage">下一步</button>
					</div>
				</div>                 
			</div>`
let confirmation =`
			<h2 class="title">訂單確認</h2>
			<div class="order_info">
				<p><span class="label">預約項目：</span><span id="project">寵物接送</span></p>
				<p><span class="label">預約時間：</span><span id="appTime">${order.appDate} ${order.apptTime}:00</span></p>
				<p><span class="label">上車地址：</span><span id="onLocation"></span></p>
				<p><span class="label">目的地地址：</span><span id="offLocation"></span></p>
				<p><span class="label">預約服務人員：</span><span id="staffName">${order.staffName}</span></p>
				<p><span class="label">服務人員聯絡電話：</span><span id="staffPhone">${order.staffPhone}</span></p>
				<p><span class="label">會員姓名：</span><span id="memName">${order.memName}</span></p>
				<p><span class="label">會員電話：</span><span id="memPhone">${order.memPhone}</span></p>
				<p><span class="label">毛小孩類別：</span><span id="petType">${order.petType}</span></p>
				<p><span class="label">毛小孩性別：</span><span id="petGender">${order.petGender}</span></p>
				<p><span class="label">毛小孩大名：</span><span id="petName">${order.petName}</span></p>
				<p><span class="label">毛小孩體重：</span><span id="petWeight">${order.petWeight}</span></p>
				<p><span class="label">其他注意事項：</span><span id="petNotes">無</span></p>
			</div>
			<div class="pay_info">
				<p><span class="label">訂單金額：</span><span id="order_amount">${order.payment}元</span></p>
				<div class="points_info">
					<p><input type="checkbox" id="use_points"> <label for="use_points">使用點數折扣</label></p>
					<p class="points_total">剩餘點數：${order.memPoints}點</p>
				</div>
				<p><span class="label">總金額：</span><span id="total_amount">${order.payment}元</span></p>
			</div>
			<div class="page_break_div">
				<button class="page_break" id="backPage">上一步</button>
				<button class="page_break" id="payment">付款</button>
			</div>
			<div class="none" id="lightbox">
				<article id="lightboxMes">
				<button class="close_card_btn">&times;</button>
				<div >
					<button type="button" id="Yes" class="check_btn">確定</button>
				</div>
				</article>
			</div>`
//==========個別頁面的內容==========//



//到時候要刪掉的，取得測試會員ID  刪掉
// let setMember=`
// 			<label for="memberId" class="selection">輸入測試的會員ID</label>
// 			<input type="text" id="memberId"  placeholder="輸入測試的會員ID" required>
// 			<button class="page_break" id="nextPage">下一步</button>`
// function getMemberId(){
// 	memberId = $('#memberId').val();
// 	// console.log(memberId);
// }
async function getMemInfo(){
	let getMemInfo_URL = `http://localhost:8080/appointment/getMemInfo`;
    try{
		let res = await fetch(getMemInfo_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			// body: JSON.stringify({memId:memberId})
		});
		let data =await res.json();
		memberId = data.memId;
		console.log(data);
		console.log(memberId);
		
		order.memName = data.memName;
		order.memPhone = data.memPhone 
		order.memPoints = data.point
		console.log(order);

	} catch(error){
		console.log(error);
		alert('網頁錯誤，請重新整理');
	}
}
//==========step1==========//
// 預約時間的時間選項迴圈
function time_menu(){
	let menu =  $('#time_menu') 
    menu.empty();
	let time_options = '';
	for(let i = 8; i<=20; i++){
		let time = `${i}:00`;
		time_options += `<option value="${i}">${time}</option>`
	}
	menu.append(time_options);
}
//初始化地圖
let map;
let directionsService;
let directionsRenderer;
let pickup;
let dropoff;
function initMap() {

	map = new google.maps.Map(document.getElementById("map"), {
		center: { lat: 25.033964, lng: 121.564468 }, // 台北 101
		zoom: 13
	});
	directionsService = new google.maps.DirectionsService();
	directionsRenderer = new google.maps.DirectionsRenderer({
		polylineOptions: {
			strokeColor: "#0f53ff",  // 設定路線顏色（深藍）
			strokeOpacity: 1.0,       // 不透明度
			strokeWeight: 6           // 設定線條粗細
		}
	});
	directionsRenderer.setMap(map);
	

	const pickupInput = document.getElementById("pickup");
	const dropoffInput = document.getElementById("dropoff");

	
	const pickupAutocomplete = new google.maps.places.Autocomplete(pickupInput);
	const dropoffAutocomplete = new google.maps.places.Autocomplete(dropoffInput);

	// **監聽 place_changed 確保選擇完整地址**
	pickupAutocomplete.addListener("place_changed", function() {
		const place = pickupAutocomplete.getPlace();
		if (!place.formatted_address) return;
		pickupInput.value = place.formatted_address;
		calculateRoute(); 
	});

	dropoffAutocomplete.addListener("place_changed", function() {
		const place = dropoffAutocomplete.getPlace();
		if (!place.formatted_address) return;
		dropoffInput.value = place.formatted_address;
		calculateRoute();
	});

	pickupInput.addEventListener('change', calculateRoute);
	dropoffInput.addEventListener('change', calculateRoute);
}
//路線計算
function calculateRoute() {

	pickup = document.getElementById("pickup").value.trim();
    dropoff = document.getElementById("dropoff").value.trim();

	if (!pickup || !dropoff) {
		return;
	}
	if(pickup == dropoff){
		errorLightBox(`<p>上下車地點請勿輸入相同地址</p>`);
		return;
	}
	const req = {
		origin: pickup,
		destination: dropoff,
		travelMode: google.maps.TravelMode.DRIVING
	};

	directionsService.route(req, (res, status) => {
		if (status === google.maps.DirectionsStatus.OK) {
			directionsRenderer.setDirections(res);
			const driveDistance = res.routes[0].legs[0].distance.value;
			const driveTime = res.routes[0].legs[0].duration.text;
			const roundedDistance = parseFloat((driveDistance / 1000).toFixed(1));
			const amoute = (roundedDistance > 1 ? roundedDistance*50 +100 : 100);
			
			$('#amouteInfo').removeClass('none');
			$('#driveDistance').text(`${roundedDistance}公里`)
			$('#driveTime').text(`${driveTime}`)
			$('#amoute').text(`${amoute} 元`)
		}
	});
}

async function getAmoute(){
	let pickupToBackend = document.getElementById("pickup").value.trim();
    let dropoffToBackend = document.getElementById("dropoff").value.trim();

	if(pickupToBackend == dropoffToBackend){
		errorLightBox(`<p>上下車地點請勿輸入相同地址</p>`);
		return;
	}
	let getAmoute_URL = `http://localhost:8080/appointment/calculateAmoute?origin=${pickup}&destination=${dropoff}`
	try{
		let res = await fetch(getAmoute_URL);
		let data = await res.text();  
		// console.log(data);
		if(!res.ok){
			// console.log(data);
			if(data == "OutOfRange"){
				errorLightBox(`<p>輸入地址超出雙北範圍<br>請輸入完整地址後再試一次</p>`)
			}
			return false;
		}
		order.onLocation = pickupToBackend;
		order.offLocation = dropoffToBackend;
		order.payment = data;
		return true;
	}catch(error){
		console.log(error);
		alert('網頁錯誤，請重新整理');
		return false;
	}
}

//錯誤處理燈箱
function errorLightBox(text){
	$('article').children('p').remove();
	$('#lightboxMes').prepend(text);
	$('#lightbox').removeClass('none');
	$("#lightbox").off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
	});
	$('#lightbox > article').off('click').on('click',function(e){
		e.stopPropagation();
	})
	$('.close_card_btn').off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
	})
    $('.check_btn').off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
	})
}
// 請輸入以下資訊：
// -預約日期
// -上車地點
// -下車地點
//前端驗證input是否都有輸入
function checkVal_s1(){
	let errorMsg = "請輸入以下資訊：<br>"
	let isValid = true;
	pickup = $('#pickup').val().trim();
    dropoff = $('#dropoff').val().trim();
	let datePicker = $('#date_picker').val().trim();

	$('#pickup, #dropoff, #date_picker').removeClass("noVal");
	if(!datePicker){
		errorMsg += "-預約日期<br>";
		$('#date_picker').addClass("noVal")
		isValid = false;
	}
	if(!pickup){
		 errorMsg += "-上車地點<br>";
		$('#pickup').addClass("noVal")
		isValid = false;
	}
	if(!dropoff){
		errorMsg += "-下車地點<br>";
		$('#dropoff').addClass("noVal")
		isValid = false;
	}
	if(!isValid){
		errorLightBox(`<p>${errorMsg}</p>`)
	}
    $('#pickup, #dropoff, #date_picker').off('focus').on('focus', function() {
        $(this).removeClass("noVal");
    });
	return isValid;
}
//==========step2==========//


let card_options = '';
//取得可上班員工
async function getCan_Work_Staff(){
	let getSchedule_URL = 'http://localhost:8080/appointment/getbookableStaff?';

	let date = $('#date_picker').val().trim();
	let apptTime = $("#time_menu Option:selected").val();
	order.appDate = date;
	order.apptTime = apptTime;

	getSchedule_URL += `date=${date}&apptTime=${apptTime}`;
	// console.log(getSchedule_URL);
	try{
		let res = await fetch(getSchedule_URL);

		let data =await res.json();
		// console.log(data);

		if(data.length > 0 ){
			for(let i = 0; i <data.length ; i++){
				let staff_name = data[i].staffName;
				let staff_introduction = data[i].introduction;
				let man_img = "/images/staff_man.jpg";
				let woman_img = "/images/staff_woman.jpg"
				card_options += `<div class='service_card'>
										<img class = "staff_img" src = "${data[i].staffGender == 1 ? man_img : woman_img}" alt="">
									<div class ='staff_info'>
										<p class = "staff_name" >${staff_name}</p>
										<p class = "staff_introduction">${staff_introduction}</p>
										<input type="hidden" class='staff_info_json' value='${JSON.stringify(data[i])}' >
									</div>
								</div>`
			};
			return true;
		}
		if(data.length == 0){
			errorLightBox(`<p>此時段無服務人員<br>請更換時段後再試一次</p>`)
			return false;
		}
		if(!res.ok){
			if ( data.status == 400) {
				errorLightBox(`<p>僅能預約今日之後的日期<br>並請確認日期格式</p>`)
			}
			// console.log(data.errors[0].defaultMessage);
			// console.log('-2');
			
			return false;
		}
	} catch(error){
		alert('網頁錯誤，請重新整理');
		return false;
	}
}

// 選擇服務人員的選項迴圈
function s_card(){
	$('.card_div').append(card_options);
}

//==========step3==========//
//取得user的寵物
let savedPets_Op ="";
// let petData;
async function getMember_Pet() {
	
	let getMember_Pet_URL = 'http://localhost:8080/appointment/getMemberPet';
	console.log(memberId);
	getMember_Pet_URL +=`?memId=${memberId}` 
	console.log(getMember_Pet_URL)
	try{
		let res = await fetch(getMember_Pet_URL);
		 data = await res.json();
		 console.log(data);  
		if(res.ok && data.length > 0){
			// petData = data;
			
			data.forEach(function(pet){
				let id = pet.petId;
				let name = pet.petName;
				savedPets_Op +=`<option id="petId${id}" value='${JSON.stringify(pet)}'>${name}</option>`
				
			})
			petInformation =`
			<h1 class="title">毛小孩資料填寫</h1>
			<div class="q_div" id="q1">
				<label for="savedPets" class="question_title">選擇已儲存的毛小孩</label>
				<select id="savedPets" class="question_input">
					<option id="noPet"value="noPet">未選擇</option>
					${savedPets_Op}
				</select>
			</div>
			<div class="q_div" id="q2">
				<span class="question_title">毛小孩類別</span>
				<div id="petType">
				<label><input type="radio" id="typeCat" name="petType" value="cat"> 貓</label>
				<label><input type="radio" id="typeDog" name="petType" value="dog"> 狗</label>
				</div>
			</div>
	
			<div class="q_div" id="q3">
				<span class="question_title">毛小孩性別</span>
				<div id="petGender">
				<label><input type="radio" id="genderM" name="petGender" value="1"> 公</label>
				<label><input type="radio" id="genderF" name="petGender" value="2"> 母</label>
				</div>
			</div>
			<div class="q_div" id="q4">
				<label for="petName" class="question_title">毛小孩大名</label>
				<input type="text" id="petName" class="input-text" placeholder="請輸入毛小孩大名">
			</div>

			<div class="q_div" id="q5">
				<label for="petWeight" class="question_title">毛小孩體重(kg)</label>
				<input type="text" id="petWeight" class="input-text" placeholder="例如：4kg">
			</div>
			<div class="q_div" id="q6">
				<label for="petNotes" class="question_title">其他毛小孩需要我們注意的事項(選填)：</label>
				<textarea id="petNotes" class="textarea" placeholder="例如：疾病、具攻擊性、容易受驚嚇等"></textarea>
			</div>
			<div class="page_break_div">
				<button class="page_break" id="backPage">上一步</button>
				<button class="page_break" id="nextPage">下一步</button>
			</div>
			<div class="none" id="lightbox">
				<article id="petInfo_Art">
					<button class="close_card_btn">&times;</button>
					<div >
						<button type="button" id="no" class="petInfo_btn">否</button>
						<button type="button" id="yes" class="petInfo_btn">是</button>
					</div>
				</article>
			</div>`
		}
	} catch{
		alert('網頁錯誤，請重新整理');
		console.log(error);
	}
}
//跳頁更新寵物
let petInfo;
let petInfoBtn_No;
let petInfoBtn_Yes;
let addPet_URL ='http://localhost:8080/appointment/postPet';
let updatePet_URL ='http://localhost:8080/appointment/putPet'
  function checkPetInfoChange(){
	return new Promise((resolve, reject) => {
		thisPetGender = $('input[name="petGender"]:checked').val() || null;
		thisPetName = $('#petName').val().trim() || null;
		thisPetType = $('input[name="petType"]:checked').val()|| null;
		thisPetWeight = $('#petWeight').val().match(/\d+(\.\d+)?/g) ;
		thisPetWeight = thisPetWeight ? parseFloat(thisPetWeight[0]) : null;
		thisPetNotes = $('#petNotes').val();
		petInfoBtn_No = $("#petInfo_Art").find("#no")
		petInfoBtn_Yes =$("#petInfo_Art").find("#yes")
		if(!(checkVal_s3())){
			resolve(false);
			return ;
		}
		if($('#savedPets').val() == 'noPet') {
			// if(!($("#petInfo_Art").find("#no").hasClass('none'))){
			petInfoBtn_No.addClass('none');
			petInfoBtn_Yes.text('確定');
			petInfoBtn_Yes.addClass('newPet');

			petInfoLightBox(`<p id="newPet_P" >此毛小孩資料將存於會員資料中<br>以利下次填寫</p>`);
			$(petInfoBtn_Yes).off('click').on('click',async function(){
				let thisPetDate = {
					memId : memberId,
					type : thisPetType,
					petName : thisPetName,
					weight : thisPetWeight, 
					petGender : thisPetGender
				}
				try {
					let res = await fetch(addPet_URL, {
						method: "POST",
						headers: {
							"Content-Type": "application/json"
						},
						body: JSON.stringify(thisPetDate)
					});
					let data = await res.json();
					if(data.result == "成功新增" ){
						order.petId = data.petId;
						console.log(data);
						console.log(order);
						console.log(order.petId);
						resolve(true);
						return;
					} else{
						$("#lightbox").addClass("none");
						$('article').children('p').remove();
						resolve(false);
						return;
					}
				} catch (error) {
					alert('網頁錯誤，重新整理');
				}
				
			})
		}
		if(petInfo != null){
			order.petId = petInfo.petId;
			if (thisPetGender != petInfo.petGender || thisPetName != petInfo.petName || thisPetType != petInfo.type || thisPetWeight != petInfo.weight){
				petInfoBtn_No.removeClass('none');
				petInfoBtn_Yes.text('是');
				petInfoBtn_Yes.removeClass('newPet');

				petInfoLightBox(`<p id="updatePet_P" >是否將變更儲存至寵物資料</p>`);
				$(petInfoBtn_No).off('click').on('click',function(){
					resolve(true);
					return;
				})
				$(petInfoBtn_Yes).off('click').on('click',async function(){
				let thisPetDate = {
					petId : petInfo.petId,
					petName : thisPetName,
					type : thisPetType,
					petGender : thisPetGender, 
					weight : thisPetWeight
				}
				// console.log(thisPetDate);
				try {
					let res = await fetch(updatePet_URL, {
						method: "PUT",
						headers: {
							"Content-Type": "application/json"
						},
						body: JSON.stringify(thisPetDate)
					});
					let data = await res.json();
					// console.log(data);
					if(data.result == "成功更新" ){
						resolve(true);
						return;
					} else{
						$("#lightbox").addClass("none");
						$('article').children('p').remove();
						resolve(false);
						return;
					}
				} catch (error) {
					alert('網頁錯誤，請重新整理');
				}	
				})
			} else{
				resolve(true);
				return;
			}
		}
	})
}
//檢查資料是否都有輸入
function checkVal_s3(){
	let errorMsg = "請輸入以下資訊：<br>"
	let isValid = true;
	

	$('#pickup, #dropoff, #date_picker').removeClass("noVal");
	if(!thisPetType){
		errorMsg += "-毛小孩類別<br>";
		$('#petType').addClass('redText');
		isValid = false;
	}
	if(!thisPetGender){
		 errorMsg += "-毛小孩性別<br>";
		$('#petGender').addClass('redText');
		isValid = false;
	}
	if(!thisPetName){
		errorMsg += "-毛小孩大名<br>";
		$('#petName').addClass("noVal")
		isValid = false;
	}
	if(!thisPetWeight){
		errorMsg += "-毛小孩體重<br>";
		$('#petWeight').addClass("noVal")
		isValid = false;
	}
	if(!isValid){
		petInfoBtn_No.addClass('none');
		petInfoBtn_Yes.text('確定');
		petInfoBtn_Yes.addClass('check_btn');
		petInfoLightBox(`<p>${errorMsg}</p>`)
	}
	$('#petName, #petWeight').off('focus').on('focus', function() {
		$(this).removeClass("noVal");
	});
	$('input[name="petType"]').off('change').on('change', function() {
		$('#petType').removeClass('redText');
	});
	$('input[name="petGender"]').off('change').on('change', function() {
		$('#petGender').removeClass('redText');
	});
	return isValid;
}

//寵物頁面燈箱
function petInfoLightBox(text){
	$('article').children('p').remove();
	$('#petInfo_Art').prepend(text);
	$('#lightbox').removeClass('none');
	$("#lightbox").off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
		$(this).removeClass('check_btn');
	});
	$('#lightbox > article').off('click').on('click',function(e){
		e.stopPropagation();
	})
	$('.close_card_btn').off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
		$(this).removeClass('check_btn');
	})
	$('.check_btn').off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
		$(this).removeClass('check_btn');
	})
}

function orderPetSet (){
	order.petGender = thisPetGender;
	order.petName = thisPetName;
	order.petType = thisPetType;
	order.petWeight = thisPetWeight;
	order.notes = thisPetNotes;
}
//==========step4==========//
// 檢查同意書是否勾選
function check_for_agreement(){
	if($('#checkAgree').prop('checked')){
		return true;
	} else{
		$('#checkAgree').addClass('notCheck')
		alert('請勾選同意條款')
		return false;
	}
}

//==========step5==========//

function setConfirmation(){
	confirmation =`
			<h2 class="title">訂單確認</h2>
			<div class="order_info">
				<p><span class="label">預約項目：</span><span id="project">寵物接送</span></p>
				<p><span class="label">預約時間：</span><span id="appTime">${order.appDate} ${order.apptTime}:00</span></p>
				<p><span class="label">上車地址：</span><span id="onLocation">${order.onLocation}</span></p>
				<p><span class="label">目的地地址：</span><span id="offLocation">${order.offLocation}</span></p>
				<p><span class="label">預約服務人員：</span><span id="staffName">${order.staffName}</span></p>
				<p><span class="label">服務人員聯絡電話：</span><span id="staffPhone">${order.staffPhone}</span></p>
				<p><span class="label">會員姓名：</span><span id="memName">${order.memName}</span></p>
				<p><span class="label">會員電話：</span><span id="memPhone">${order.memPhone}</span></p>
				<p><span class="label">毛小孩類別：</span><span id="petType">${order.petType == "cat"? "貓":"狗"}</span></p>
				<p><span class="label">毛小孩性別：</span><span id="petGender">${order.petGender == 1 ? "公": "母"}</span></p>
				<p><span class="label">毛小孩大名：</span><span id="petName">${order.petName}</span></p>
				<p><span class="label">毛小孩體重：</span><span id="petWeight">${order.petWeight}kg</span></p>
				<p><span class="label">其他注意事項：</span><span id="petNotes">${order.notes}</span></p>
			</div>
			<div class="pay_info">
				<p><span class="label">訂單金額：</span><span id="order_amount">${order.payment}元</span></p>
				<div class="points_info">
					<p><input type="checkbox" id="use_points"> <label for="use_points">使用點數折扣</label></p>
					<p class="points_total">剩餘點數：${order.memPoints}點</p>
				</div>
				<p><span class="label">總金額：</span><span id="total_amount">${order.payment}元</span></p>
			</div>
			<div class="page_break_div">
				<button class="page_break" id="backPage">上一步</button>
				<button class="page_break" id="payment">付款</button>
			</div>
			<div class="none" id="lightbox">
				<article id="lightboxMes">
				<button class="close_card_btn">&times;</button>
				<div >
					<button type="button" id="Yes" class="check_btn">確定</button>
				</div>
				</article>
			</div>`
}

function setOrder(){
	orderToDb.apptTime = order.apptTime;
	orderToDb.date = order.appDate;
	orderToDb.schId = staffInfo.schId;
	orderToDb.onLocation = order.onLocation;
	orderToDb.offLocation = order.offLocation;
	orderToDb.point = 0;
	orderToDb.payment = order.payment;
	orderToDb.payMethod = 1;
	orderToDb.notes = order.notes;
	orderToDb.petId = order.petId;
	// console.log(orderToDb);
	$('#use_points').on('click',function(){
		if ($('#use_points').prop("checked")&& order.memPoints!= 0) {
		    orderToDb.payMethod = 0;
			orderToDb.point = order.memPoints;
			$('#total_amount').text(`${order.payment-order.memPoints>= 0 ? order.payment - order.memPoints : 0}元`)
			$('.points_total').text(`剩餘點數：${order.memPoints - order.payment >= 0 ? order.memPoints - order.payment : 0}點`)
		} else {
		    orderToDb.payMethod = 1;
			orderToDb.point = 0;
			$('#total_amount').text(`${order.payment}元`)
			$('.points_total').text(`剩餘點數：${order.memPoints}點`)
		}
	})
}

async function OrderToDb(){
	let setOrders_URL = 'http://localhost:8080/appointment/postCheckout';
	try {
		let res = await fetch(setOrders_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(orderToDb)
		});
		// console.log(res);
		
		let data = await res.json();
		console.log(data);
		if(!res.ok){
			if(data.error == "schError"){
				refreshLightBox(`<p>此服務人員已被預約<br>請選擇其他服務人員或時段後再次下單</p>`)
			}else{
				refreshLightBox(`<p>預約資料有誤<br>請重新整理後再次下單</p>`)
			}
		}
		if(data.NoPayment){
			window.location.href = data.NoPayment;
		}
		if(data.ECPay){
			$(".body_text").append(data.ECPay);
			document.getElementById('allPayAPIForm').submit();
		}
	}catch(error){
		alert('網頁錯誤，請重新整理');
		console.log(error);
	}
}
//重新整理網頁用的燈箱
function refreshLightBox(text){
	$('article').children('p').remove();
	$('#lightboxMes').prepend(text);
	$('#lightbox').removeClass('none');
	$("#lightbox").off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
	});
	$('#lightbox > article').off('click').on('click',function(e){
		e.stopPropagation();
	})
	$('.close_card_btn').off('click').on('click',function(){
		$("#lightbox").addClass("none");
		$('article').children('p').remove();
	})
    $('.check_btn').off('click').on('click',function(){
		location.reload();
	})
}
//==========全頁邏輯==========//
// 跳頁更換css
function changeCss(cssFile){
	$('#page_css').attr('href',cssFile);
}
// 按下按鈕跳頁
function changePage (){
	$("#nextPage").on('click', async function(){
		switch (currenStep){
			case 1:
				if( checkVal_s1()){
					if(await getAmoute()&& await getCan_Work_Staff() ){
						currenStep ++;
						runStep();
					}else{
						$('#date_picker').datepicker('destroy');
						$('#date_picker').datepicker({
							showAnim: 'slideDown',
							minDate: 0
						});
					}
				}else{
					$('#date_picker').datepicker('destroy');
					$('#date_picker').datepicker({
						showAnim: 'slideDown',
						minDate: 0
					});
				}
				break;
			case 2: 
				savedPets_Op=""
				currenStep ++;
				runStep();
				break;
			case 3: 
				if(await checkPetInfoChange()){
					orderPetSet();
					currenStep ++;
					runStep();
				}else{

				}
				break;
			case 4:
				if(currenStep == 4 & check_for_agreement()){
				setConfirmation();
				currenStep ++;
				runStep();
				}
				break;
			case 5:
				currenStep ++;
				runStep();
				break;	
		}
	});

	$('#backPage').on('click', function(){
		switch (currenStep){
			case 1:
				break;
			case 2: 
				card_options="";
				break;
			case 3:
				// savedPets_Op =""; //刪掉
				break;
			case 4:
				savedPets_Op=""
				petInfo = null;
				getMember_Pet();
				break;
			case 5:
				break;	
		}
		currenStep --;
		runStep();
	})
}

// 個別頁面的js註冊事件
async function step1_js(){
	await getMemInfo();
	await getMember_Pet();
	time_menu();
	if (!$('#date_picker').hasClass('hasDatepicker')) {
		$('#date_picker').datepicker({
			showAnim: 'slideDown',
			minDate: 0
		});
	}
	initMap();
}
function step2_js(){
	s_card();
	var staff_info;
	var staff_img;
	//服務人員選擇卡燈箱
	$(".service_card").click(function(){
		event.stopPropagation()
		staff_info = $(this).find('.staff_info').html();
		staff_img = $(this).find('img').prop('outerHTML')
		// console.log(staff_info)
		$('.select_service').click(); //啟用燈箱按鈕
		// e.stopPropagation();
	})
	$('.select_service').click(function(){
		$('.staff_card').prepend(staff_img);
		$('.staffInfo_text').prepend(staff_info);
		$('#lightbox').removeClass('none');
	})
	$("#lightbox").on("click", function(){
		$("#lightbox").addClass("none");
		$('.staffInfo_text').children('p').remove();
		$('.staff_card').children('img').remove();
	});
	$('#lightbox > article').click(function(e){
		e.stopPropagation();
	})
	$('.close_card_btn').click(function(){
		$("#lightbox").addClass("none");
		$('.staffInfo_text').children('p').remove();
		$('.staff_card').children('img').remove();
	})

	$('#nextPage').on('click',function(){
		order.staffName = $(this).closest('article').find('.staff_name').text();
		staffInfo = JSON.parse($(this).closest('article').find('.staff_info_json').val())
		order.staffPhone = staffInfo.staffPhone;
		// console.log(staffInfo);
	})
}

function step3_js(){
	//寵物下拉選單
	let pets = $('#savedPets');
	pets.on('change', function(){
		if(pets.val() == 'noPet'){
			$('input[name="petType"]').prop('checked', false);  // 清除寵物類型的選擇
            $('input[name="petGender"]').prop('checked', false);  // 清除性別的選擇
            $('#petWeight').val('');  // 清空體重輸入框
            $('#petName').val('');  // 清空寵物名稱輸入框
			petInfo = null;
		}	else{
				petInfo = JSON.parse($(this).val());
				$('#petType').removeClass('redText');
				$('#petGender').removeClass('redText');
				$('#petName').removeClass("noVal");
				$('#petWeight').removeClass("noVal");
				switch (petInfo.type){
					case "dog":
						$('#typeDog').prop('checked', true);
						break;
					case "cat":
						$('#typeCat').prop('checked', true);
						break;
				}
				switch (petInfo.petGender){
					case 1:
						$('#genderM').prop('checked', true);
						break;
					case 2:
						$('#genderF').prop('checked', true);
						break;
				}
			$('#petWeight').val(`${petInfo.weight}kg`);
			$('#petName').val(petInfo.petName);
		}
	})
	
}

function step5_js(){
	setOrder();
	$('#payment').on('click', function(){
		$('article').children('p').remove();
		$('#lightboxMes').prepend(`<p>請於十分鐘內付款完畢否則將視為取消訂單<br>若不慎關閉付款頁面請重新下單</p>`);
		$('#lightbox').removeClass('none');
		$("#lightbox").off('click').on('click',function(){
			$("#lightbox").addClass("none");
			$('article').children('p').remove();
		});
		$('#lightbox > article').off('click').on('click',function(e){
			e.stopPropagation();
		})
		$('.close_card_btn').off('click').on('click',function(){
			$("#lightbox").addClass("none");
			$('article').children('p').remove();
		})
		$('.check_btn').off('click').on('click',function(){
			OrderToDb();
		})
		
	})
}

let body_text = $('.body_text');

function runStep () {
	body_text.fadeOut(100,function(){
		switch(currenStep){
			case 1:
				body_text.html(appointment);
				changeCss("/css/appointment.css");
				$('#body').append(`<div id="map"></div>`);
				step1_js();
				break;
			case 2:
				body_text.html(serviceSelection);
				$('#map').remove();
				changeCss("/css/serviceSelection.css");
				step2_js();
				break;
			case 3:
				body_text.html(petInformation)
				changeCss("/css/pet_information.css");
				step3_js();
				break;
			case 4:
				body_text.html(agreement)
				changeCss("/css/agreement.css")
				break;
			case 5:
				body_text.html(confirmation)
				changeCss("/css/confirmation.css")
				step5_js()
				break;
			}
		changePage();
		body_text.fadeIn(800); 
		window.scrollTo({ top: windowHeight/5, behavior: "smooth" });
	})
}
$(function(){
	runStep();
})





