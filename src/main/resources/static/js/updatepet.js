let petInformation;
let savedPets_Op;
let pets;
let petType;
let petGender ;
let petName;
let petWeight;
let nextPage;
//儲存輸入框的資料用的變數
let thisPetGender;
let thisPetName;
let thisPetType;
let thisPetWeight;
async function getMember_Pet() {
	let getMember_Pet_URL = 'http://localhost:8080/member/getMemberPet';
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
			<h1 class="title">修改毛小孩資料</h1>
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
			<div class="page_break_div">
					<button disabled class="page_break  noChange" id="nextPage">儲存</button>
				</div>
				<div class="none" id="lightbox">
					<article id="petInfo_Art">
						<button class="close_card_btn">&times;</button>
						<div >
							<button  type="button" id="yes" class="petInfo_btn">確定</button>
						</div>
					</article>
				</div>`
		}
	} catch(error){
		alert('網頁錯誤，請重新整理');
		console.log(error);
	}
}



let petInfo;
let petInfoBtn_No;
let petInfoBtn_Yes;
let updatePet_URL ='http://localhost:8080/member/putPet'
  function checkPetInfoChange(){
	return new Promise(async (resolve, reject) => {
		thisPetGender = $('input[name="petGender"]:checked').val() || null;
		thisPetName = $('#petName').val().trim() || null;
		thisPetType = $('input[name="petType"]:checked').val()|| null;
		thisPetWeight = $('#petWeight').val().match(/\d+(\.\d+)?/g) ;
		thisPetWeight = thisPetWeight ? parseFloat(thisPetWeight[0]) : null;
		petInfoBtn_No = $("#petInfo_Art").find("#no")
		petInfoBtn_Yes =$("#petInfo_Art").find("#yes")
		if(pets.val() == 'noPet') {
			petInfoLightBox(`<p id="newPet_P" >請選擇要更新的寵物</p>`);
            pets.addClass('noVal');
            pets.off('change').on('change', function() {
                $(this).removeClass("noVal");
                petMenu.call(this);
            });
            resolve(false);
			return ;
		}
        if(!(checkVal_s3())){
			resolve(false);
			return ;
		}
        if (thisPetGender != petInfo.petGender || thisPetName != petInfo.petName || thisPetType != petInfo.type || thisPetWeight != petInfo.weight){
            petInfoBtn_No.removeClass('none');
            petInfoBtn_Yes.text('是');
            petInfoBtn_Yes.removeClass('newPet');
            let thisPetDate = {
                petId : petInfo.petId,
                petName : thisPetName,
                type : thisPetType,
                petGender : thisPetGender, 
                weight : thisPetWeight
            }
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
        } else{
            petInfoLightBox(`<p id="newPet_P" >請確認資料有變更再按儲存</p>`);
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
		petType.addClass('redText');
		isValid = false;
	}
	if(!thisPetGender){
		 errorMsg += "-毛小孩性別<br>";
         petGender.addClass('redText');
		isValid = false;
	}
	if(!thisPetName){
		errorMsg += "-毛小孩大名<br>";
		petName.addClass("noVal")
		isValid = false;
	}
	if(!thisPetWeight){
		errorMsg += "-毛小孩體重<br>";
		petWeight.addClass("noVal")
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
		petType.removeClass('redText');
	});
	$('input[name="petGender"]').off('change').on('change', function() {
		petGender.removeClass('redText');
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
    $('#yes').off('click').on('click',function(){
        $("#lightbox").addClass("none");
            $('article').children('p').remove();
            $(this).removeClass('check_btn');
    })
}
let  petMenu = function(){
    if(pets.val() == 'noPet'){
        $('input[name="petType"]').prop('checked', false);  // 清除寵物類型的選擇
        $('input[name="petGender"]').prop('checked', false);  // 清除性別的選擇
        $('#petWeight').val('');  // 清空體重輸入框
        $('#petName').val('');  // 清空寵物名稱輸入框
        nextPage.addClass('noChange').prop("disabled", true);
        petInfo = null;
    }else{
        petInfo = JSON.parse($(this).val());
        petType.removeClass('redText');
        petGender.removeClass('redText');
        petName.removeClass("noVal");
        petWeight.removeClass("noVal");
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
        petWeight.val(`${petInfo.weight}kg`);
        petName.val(petInfo.petName);

        petType.off('change').on('change', function(){
            thisPetType = $('input[name="petType"]:checked').val()|| null;
            if(thisPetType != petInfo.type){
                nextPage.removeClass('noChange').prop("disabled", false);
            }else{
                nextPage.addClass('noChange').prop("disabled", true);
            }
        });
        petGender.off('change').on('change', function(){
            thisPetGender = $('input[name="petGender"]:checked').val() || null;
            if(thisPetGender != petInfo.petGender){
                nextPage.removeClass('noChange').prop("disabled", false);
            }else{
                nextPage.addClass('noChange').prop("disabled", true);
                
            }
        });
     
        petName.off('change').on('change', function(){
            thisPetName = $('#petName').val().trim() || null;
            if(thisPetName != petInfo.petName ){
                nextPage.removeClass('noChange').prop("disabled", false);
            }else{
                nextPage.addClass('noChange').prop("disabled", true);
            }
        });
        petWeight.off('change').on('change', function(){
            thisPetWeight = $('#petWeight').val().match(/\d+(\.\d+)?/g) ;
            thisPetWeight = thisPetWeight ? parseFloat(thisPetWeight[0]) : null;
            if(thisPetWeight != petInfo.weight){
                nextPage.removeClass('noChange').prop("disabled", false);
            }else{
                nextPage.addClass('noChange').prop("disabled", true);
            }
        });
    };
}
async function init (){
	await getMember_Pet();
    $('.body_text').html(petInformation);
    pets = $('#savedPets');
    petType = $('#petType');
    petGender = $('#petGender');
    petName = $('#petName');
    petWeight =  $('#petWeight');
    nextPage = $('#nextPage');
    pets.off('change').on('change', petMenu);
    $('#nextPage').off('click').on('click', async function(){
        if(await checkPetInfoChange()){
            petInfoLightBox(`<p id = "updatePet_P">變更完成</p>`)
            $('#yes').off('cilck').on('click',function(){
                window.location.href ="http://localhost:8080/member";
            })
        }
    })
}
init();

		
	