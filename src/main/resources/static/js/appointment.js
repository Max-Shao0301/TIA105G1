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
//使用者的會員ID
let memberId;
// 當前畫面頁數
let currenStep = 0;
//訂單資料
const order = {
	apptTime:{},
	appDate:{},
	onLoc:'台北市', //暫時寫死
	offLoc:'新北市', //暫時寫死
	staffName:{},
	staffPhone:{},
	memName:{},
	memPhone:{},
	petType:{},
	petGender:{},
	petName:{},
	petWeigh:{},
	notes:{},
	petId:{},
	payment:100,  //暫時寫死
	memPoints:{}
};
const orderToDb = {
	memId : {},
	staffId : {},
	scheduleId : {},
	onLoc : {},
	offLoc : {},
	point : {},
	payment : {},
	payMethod : {} ,
	notes:{}
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
				<p class="remark">單趟費用起跳價 100 元，後續每公里 50 元</p>
			
				<!-- 下一步按鈕 -->
				<button type="submit" id="nextPage">下一步</button>
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
				<label><input type="radio" id="typeCat" name="petType" value="cat"> 貓</label>
				<label><input type="radio" id="typeDog" name="petType" value="dog"> 狗</label>
			</div>
	
			<div class="q_div" id="q3">
				<span class="question_title">毛小孩性別</span>
				<label><input type="radio" id="genderM" name="petGender" value="1"> 公</label>
				<label><input type="radio" id="genderF" name="petGender" value="2"> 母</label>
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
						<button type="button" id="No" class="petInfo_btn">否</button>
						<button type="button" id="Yes" class="petInfo_btn">是</button>
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
				<p><span class="label">上車地址：</span><span id="onLoc">還沒串API</span></p>
				<p><span class="label">目的地地址：</span><span id="offLoc">還沒串API</span></p>
				<p><span class="label">預約服務人員：</span><span id="staffName">${order.staffName}</span></p>
				<p><span class="label">服務人員聯絡電話：</span><span id="staffPhone">${order.staffPhone}</span></p>
				<p><span class="label">會員姓名：</span><span id="memName">${order.memName}</span></p>
				<p><span class="label">會員電話：</span><span id="memPhone">${order.memPhone}</span></p>
				<p><span class="label">毛小孩類別：</span><span id="petType">${order.petType}</span></p>
				<p><span class="label">毛小孩性別：</span><span id="petGender">${order.petGender}</span></p>
				<p><span class="label">毛小孩大名：</span><span id="petName">${order.petName}</span></p>
				<p><span class="label">毛小孩體重：</span><span id="petWeight">${order.petWeigh}</span></p>
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
			</div>`
//==========個別頁面的內容==========//



//到時候要刪掉的，取得測試會員ID  刪掉
let setMember=`
			<label for="memberId" class="selection">輸入測試的會員ID</label>
			<input type="text" id="memberId"  placeholder="輸入測試的會員ID" required>
			<button class="page_break" id="nextPage">下一步</button>`
function getMemberId(){
	memberId = $('#memberId').val();
	console.log(memberId);
}

async function getMemInfo(){
	let getMemInfo_URL = `http://localhost:8080/appointment/getMemInfo`;
    try{
		let res = await fetch(getMemInfo_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify({memId:memberId})
		});
		let data =await res.json();
		console.log(data);
		console.log(data.memName);
		
		order.memName = data.memName;
		order.memPhone = data.memPhone 
		order.memPoints = data.point
		console.log(order);

	} catch(error){
		console.log(error);
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

//==========step2==========//


let card_options = '';
let error_msg ;
//取得可上班員工
async function getCan_Work_Staff(){
	let getSchedule_URL = 'http://localhost:8080/appointment/getbookableStaff?';

	let date = $('#date_picker').val();
	let apptTime = $("#time_menu Option:selected").val();
	order.appDate = date;
	order.apptTime = apptTime;

	getSchedule_URL += `date=${date}&apptTime=${apptTime}`;
	console.log(getSchedule_URL);
	try{
		let res = await fetch(getSchedule_URL);
		let data =await res.json();
		console.log(data);

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
			alert('沒人上班')
			return false;
		}
		if(!res.ok){
			if ( data.status == 400) {
				console.log('-1');
				alert(data.errors[0].defaultMessage);
			}
			console.log(data.errors[0].defaultMessage);
			console.log('-2');
			
			return false;
		}
	} catch(error){
		return false;
	}
}

// function error_handling(error_msg){
// 	const back_msg = {
//         noStaff: '沒人上班',
//         error: {
//             '-1': '你沒給日期',
//             '-2': '日期格式有錯，請用月曆選單輸入',
// 			'-3': '請勿輸入今日以前的日期，請用月曆選單輸入'
//         }
//     };
// 	if(error_msg.result == 'noStaff'){
// 		alert(back_msg.noStaff);
// 	} else if (error_msg.result == 'error'){
// 		alert(back_msg.error[error_msg.date]);
// 	}
	
// }
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
	// getMember_Pet_URL +=`memberId=${memberId}` 
	console.log(getMember_Pet_URL)
	try{
		let res = await fetch(getMember_Pet_URL);
		 data = await res.json();
		console.log(data);  
		console.log(res.ok);
		console.log(data.length);
		if(res.ok && data.length > 0){
			// petData = data;
			
			data.forEach(function(pet){
				let id = pet.petId;
				let name = pet.petName;
				savedPets_Op +=`<option id="petId${id}" value='${JSON.stringify(pet)}'>${name}</option>`
				
			})
			// console.log(savedPets_Op);
			// console.log(petData);
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
				<label><input type="radio" id="typeCat" name="petType" value="cat"> 貓</label>
				<label><input type="radio" id="typeDog" name="petType" value="dog"> 狗</label>
			</div>
	
			<div class="q_div" id="q3">
				<span class="question_title">毛小孩性別</span>
				<label><input type="radio" id="genderM" name="petGender" value="1"> 公</label>
				<label><input type="radio" id="genderF" name="petGender" value="2"> 母</label>
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
		console.log(error);
	}
}

//跳頁更新寵物
let petInfo;

// let petServlet_URL = 'http://localhost:8081/TIA105G1/petServlet';
let addPet_URL ='http://localhost:8080/appointment/postPet';
let updatePet_URL ='http://localhost:8080/appointment/putPet'
  function checkPetInfoChange(){
	return new Promise((resolve, reject) => {
		thisPetGender = $('input[name="petGender"]:checked').val() || null;
		thisPetName = $('#petName').val() || null;
		thisPetType = $('input[name="petType"]:checked').val() || null;
		thisPetWeight = $('#petWeight').val().match(/\d+(\.\d+)?/g) ;
		thisPetWeight = thisPetWeight ? parseFloat(thisPetWeight[0]) : null;
		thisPetNotes = $('#petNotes').val();
		let petInfoBtn_No = $("#petInfo_Art").find("#no")
		let petInfoBtn_Yes =$("#petInfo_Art").find("#yes")
		if(thisPetGender == null || thisPetName == null || thisPetType == null || thisPetWeight == null){
			alert('資料請物留空 請再檢查一次');
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
					memberId : memberId,
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
					if(data.result == "成功更新" ){
						resolve(true);
						return;
					} else{
						$("#lightbox").addClass("none");
						$('article').children('p').remove();

						// if (Object.values(data).some(val => val == "-1")){
						// 	alert('資料請物留空 請再檢查一次');
						// } else if(Object.values(data).some(val => val == "-2")){
						// 	alert('網頁發生問體 請重新整理後再試一次');
						// } else{
						// 	alert('哭啊');
						// }
						resolve(false);
						return;
					}
				} catch (error) {
					alert('哭啊');
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
					// changePageBoolean = true;
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
				console.log(thisPetDate);
				try {
					let res = await fetch(updatePet_URL, {
						method: "PUT",
						headers: {
							"Content-Type": "application/json"
						},
						body: JSON.stringify(thisPetDate)
					});
					let data = await res.json();
					console.log(data);
					if(data.result == "成功更新" ){
						resolve(true);
						return;
					} else{
						$("#lightbox").addClass("none");
						$('article').children('p').remove();

						// if (Object.values(data).some(val => val == "-1")){
						// 	alert('資料請物留空 請再檢查一次');
						// } else if(Object.values(data).some(val => val == "-2")){
						// 	alert('網頁發生問體 請重新整理後再試一次');
						// } else{
						// 	alert('哭啊');
						// }
						resolve(false);
						return;
					}
				} catch (error) {
					alert('哭啊');
				}	
				})
			} else{
				resolve(true);
				return;
			}
		}
	})
}

//寵物頁面燈箱
function petInfoLightBox(text){
	$('article').children('p').remove();
	$('#petInfo_Art').prepend(text);
		$('#lightbox').removeClass('none');
		$("#lightbox").on("click", function(){
			$("#lightbox").addClass("none");
			$('article').children('p').remove();
		});
		$('#lightbox > .petInfo_Art').click(function(e){
			e.stopPropagation();
		})
		$('.close_card_btn').click(function(){
			$("#lightbox").addClass("none");
			$('article').children('p').remove();
		})
}

function orderPetSet (){
	order.petGender = thisPetGender;
	order.petName = thisPetName;
	order.petType = thisPetType;
	order.petWeigh = thisPetWeight;
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
				<p><span class="label">上車地址：</span><span id="onLoc">${order.onLoc}</span></p>
				<p><span class="label">目的地地址：</span><span id="offLoc">${order.offLoc}</span></p>
				<p><span class="label">預約服務人員：</span><span id="staffName">${order.staffName}</span></p>
				<p><span class="label">服務人員聯絡電話：</span><span id="staffPhone">${order.staffPhone}</span></p>
				<p><span class="label">會員姓名：</span><span id="memName">${order.memName}</span></p>
				<p><span class="label">會員電話：</span><span id="memPhone">${order.memPhone}</span></p>
				<p><span class="label">毛小孩類別：</span><span id="petType">${order.petType}</span></p>
				<p><span class="label">毛小孩性別：</span><span id="petGender">${order.petGender}</span></p>
				<p><span class="label">毛小孩大名：</span><span id="petName">${order.petName}</span></p>
				<p><span class="label">毛小孩體重：</span><span id="petWeight">${order.petWeigh}kg</span></p>
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
			</div>`
}

async function setOrders(){
	let setOrders_URL = 'http://localhost:8081/TIA105G1/ordersServlet';
	orderToDb.memId = memberId;
	orderToDb.staffId = staffInfo.staffId;
	orderToDb.scheduleId = staffInfo.scheduleId;
	orderToDb.onLoc = order.onLoc;
	orderToDb.offLoc = order.offLoc;
	orderToDb.point = 0;
	orderToDb.payment = order.payment;
	orderToDb.payMethod = 1;
	orderToDb.notes = order.notes;
	console.log(orderToDb);
	try {
		let res = await fetch(setOrders_URL, {
			method: "POST",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(orderToDb)
		});
		const html = await res.text();
		// 將返回的 HTML 插入到頁面中
		$(".body_text").append(html);
		// 自動提交表單
		document.getElementById('allPayAPIForm').submit();
	}catch(error){
		console.log(error);
	}
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
			case 0:
				getMemberId();
				getMemInfo();
				currenStep ++;
					runStep();
				break;
			case 1:
				let staffAvailable = await getCan_Work_Staff();
					console.log()
				if(staffAvailable){
					console.log(1);
					currenStep ++;
					runStep();
				} else{
					// error_handling(error_msg);
					console.log('error');
				};
				break;
			case 2: 
				savedPets_Op=""
				await getMember_Pet();
				currenStep ++;
				runStep();
				break;
			case 3: 
				// console.log(await checkPetInfoChange());
				if(await checkPetInfoChange()){
					orderPetSet();
					currenStep ++;
					runStep();
				}else{

				}
				break;
			case 4:
				if(currenStep == 4 & check_for_agreement()){
				console.log(order);
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
function step1_js(){
	time_menu();
	$( function() {
		$("#date_picker" ).datepicker();
		$('#date_picker').datepicker('option','showAnim','slideDown');
		$('#date_picker').datepicker('option', 'minDate', 0);
	});
		
	// $('#date_picker').change(function(){
	// 	 console.log($(this).val());
	// })
	
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
		console.log(staff_info)
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
		console.log(staffInfo);
	})
}

function step3_js(){
	//寵物下拉選單
	let pets = $(savedPets);
	pets.on('change', function(){
		if(pets.val() == 'noPet'){
			$('input[name="petType"]').prop('checked', false);  // 清除寵物類型的選擇
            $('input[name="petGender"]').prop('checked', false);  // 清除性別的選擇
            $('#petWeight').val('');  // 清空體重輸入框
            $('#petName').val('');  // 清空寵物名稱輸入框
			petInfo = null;
		}	else{
			 petInfo = JSON.parse($(this).val());
			 console.log(petInfo);
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
	$('#payment').on('click', function(){
		setOrders();
	})
}

function runStep () {
	$('.body_text').fadeOut(100,function(){
		switch(currenStep){
			case 0:
				$(".body_text").html(setMember);
				break;
			case 1:
				$(".body_text").html(appointment);
				changeCss("/css/appointment.css");
				step1_js();
				break;
			case 2:
				$(".body_text").html(serviceSelection);
				changeCss("/css/serviceSelection.css");
				step2_js();
				break;
			case 3:
				$(".body_text").html(petInformation)
				changeCss("/css/pet_information.css");
				step3_js();
				break;
			case 4:
				$('.body_text').html(agreement)
				changeCss("/css/agreement.css")
				break;
			case 5:
				$('.body_text').html(confirmation)
				changeCss("/css/confirmation.css")
				step5_js()
				break;
			}
		changePage();
		$('.body_text').fadeIn(800); 
		window.scrollTo({ top: windowHeight / 2, behavior: "smooth" });
	})
}
$(function(){
	runStep();
})





