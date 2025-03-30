
let getResults_URL = "http://localhost:8080/appointment/checkPayment";
let orderHtml =`<h2 class="title">訂單確認</h2>
                <div class="order_info">
                    <p><span class="label">預約項目：</span><span id="project">寵物接送</span></p>
                    <p><span class="label">預約時間：</span><span id="appTime">:00</span></p>
                    <p><span class="label">上車地址：</span><span id="onLocation"></span></p>
                    <p><span class="label">目的地地址：</span><span id="offLocation"></span></p>
                    <p><span class="label">預約服務人員：</span><span id="staffName"></span></p>
                    <p><span class="label">服務人員聯絡電話：</span><span id="staffPhone"></span></p>
                    <p><span class="label">會員姓名：</span><span id="memName"></span></p>
                    <p><span class="label">會員電話：</span><span id="memPhone"></span></p>
                    <p><span class="label">毛小孩類別：</span><span id="petType"></span></p>
                    <p><span class="label">毛小孩性別：</span><span id="petGender"></span></p>
                    <p><span class="label">毛小孩大名：</span><span id="petName"></span></p>
                    <p><span class="label">毛小孩體重：</span><span id="petWeight">kg</span></p>
                    <p><span class="label">其他注意事項：</span><span id="petNotes"></span></p>
                </div>
                <div class="pay_info">
                    <p><span class="label">訂單金額：</span><span id="order_amount">222元</span></p>
                    <p><span class="label">折抵點數：</span><span id="order_point">222點</span><br></p>
                    <p><span class="label">總金額：</span><span id="total_amount">222元</span></p>
                </div>
                <div class="page_break_div">
                    <button class="page_break" id="payment">返回首頁</button>
                </div>`
let count = 0;
let checkPayment =  setInterval(async function(){
    console.log(1);
    try {
		let res = await fetch(getResults_URL)
		let data = await res.json();
		console.log(data);
        order = data.order;
        console.log(order);
        if(data){
            clearInterval(checkPayment);
            orderHtml=`<h2 class="title">訂單確認</h2>
                <div class="order_info">
				<p><span class="label">預約項目：</span><span id="project">寵物接送</span></p>
				<p><span class="label">預約時間：</span><span id="appTime">${order.date} ${order.apptTime}:00</span></p>
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
                    <p><span class="label">訂單金額：</span><span id="order_amount">${order.payment}</span></p>
                    <p><span class="label">折抵點數：</span><span id="order_point">${order.point}</span><br></p>
                    <p><span class="label">總金額：</span><span id="total_amount">${order.payment - order.point }</span></p>
                </div>
                <div class="page_break_div">
                    <button class="page_break" id="payment"  onclick="window.location.href='/';">返回首頁</button>
                </div>`;
        $(".body_text").append(orderHtml);
        }
        count++;
		if(data.pay == '1'){
			alert('有付款');
		}
		else if(data.pay == '0'){
            alert('未付款');
		}

        if (count >= 10) {
            clearInterval(intervalId);
        }
    }catch(error){
        console.log(error);
    }
    // clearInterval(checkPayment);
},500);


$(document).ready( checkPayment());