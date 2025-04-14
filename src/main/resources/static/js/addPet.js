let petInfo;
let petInfoBtn_No;
let petInfoBtn_Yes;
let addPet_URL ='http://localhost:8080/member/postPet';
 function  checkPetInfoChange(){
	return new Promise(async (resolve, reject) => {
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
            let thisPetDate = {
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
                    resolve(true);
                    return;
                } else{
                    $("#lightbox").addClass("none");
                    $('article').children('p').remove();
                    resolve(false);
                    return;
                }
            } catch (error) {
                console.log(error);
                alert('網頁錯誤，重新整理');
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
$('#yes').on('click',function(){
    window.location.href ="http://localhost:8080/member";
})

$('#nextPage').off('click').on('click', async function(){
    if(await checkPetInfoChange()){
        petInfoLightBox(`<p id = "updatePet_P">新增完成</p>`)
    }
})
$('#backPage').on('click', function(){
	window.location.href ="http://localhost:8080/member";
})