var p_count_el = document.getElementById("p_count");
var p_count_value_el = document.getElementById("p_count_value");
var the_form_el = document.getElementById("the_form");
var p_name_el = document.getElementById("p_name");

var preview_el = document.getElementById("preview");
preview_el.addEventListener("dragover",function(e){
    e.preventDefault();
})
// 拖曳進去
preview_el.addEventListener("dragenter", function(e){
    preview_el.classList.add("-on"); // 加上 class
});
// 拖曳離開
preview_el.addEventListener("dragleave", function(e){
    preview_el.classList.remove("-on"); // 移除 class
});
// 拖曳效果整個結束
preview_el.addEventListener("drop", function(e){
e.preventDefault();
e.target.classList.remove("-on");
// console.log(e.dataTransfer.files[0])
preview_img(e.dataTransfer.files[0]);
});

//===============================透過 File 物件顯示預覽圖================================ 
var preview_img = function(file){
    //console.log(file);
    var reader = new FileReader(); // 用來讀取檔案
    reader.readAsDataURL(file); // 讀取檔案
    reader.addEventListener("load", function () {
      //console.log(reader.result);
      let img_str = `<img src="${reader.result}" class="preview_img">`;
      preview_el.innerHTML = img_str;
    });
  };

  var licence_photo_el = document.getElementById("licence_photo");
  preview_el.addEventListener("click",function(){
    licence_photo_el.click();
  });
  licence_photo_el.addEventListener("change", function(e){
    //console.log( this );
    if(this.files.length > 0){
      preview_img(this.files[0]);
    }else{
      preview_el.innerHTML = '<span class="text">請上傳駕照</span>';
    }
  });
    

document.getElementById('applybtn').addEventListener('click', function(event) {
    event.preventDefault();  // 防止表單直接送出

    // 收集表單資料
    const name = document.querySelector('input[name="applyName"]').value;
    const plateNumber = document.getElementById('plate_number').value;
    const gender = document.querySelector('input[name="applyGender"]:checked') ?
                   document.querySelector('input[name="applyGender"]:checked').nextSibling.textContent.trim() : '未選擇';
    const phone = document.querySelector('input[name="applyPhone"]').value;
    const email = document.querySelector('input[type="email"]').value;
    const selfIntro = document.querySelector('textarea').value;
    const license = document.querySelector('input[type="file"]').files[0];

    // 姓名驗證（僅允許 2-20 個中文字或英文名稱）
    const namePattern = /^[\u4E00-\u9FA5A-Za-z\s]{2,20}$/;

    // 車牌號碼驗證
    const plateNumberPattern = /^[A-Z]{2,3}\d{3,4}|\d{3}[A-Z]{2,3}$/;

    //手機驗證
    const phonePattern = /^09\d{8}$/;

    //Email驗證
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;





    // 檢查必填欄位
    if (!name || !plateNumber || !phone || !email || !gender || !selfIntro || !license) {
        alert('請完整填寫所有欄位!');
        return;
    }
    // 檢查姓名格式
    if (!namePattern.test(name)) {
        alert('請輸入正確的姓名（2-20 個中文字或英文）!');
        return;
    }
    // 檢查車牌格式
    if (!plateNumberPattern.test(plateNumber)) {
        alert('請輸入正確的車牌號碼（例如：ABC1234 或 123ABC）!');
        return;
    }
    //檢查手機格式
    if (!phonePattern.test(phone)) {
        alert('請輸入有效的手機號碼（格式：09XXXXXXXX）!');
        return;
    }
    //檢查email格式
    if (!emailPattern.test(email)) {
        alert('請輸入有效的電子郵件地址!');
        return;
    }
    // 自我介紹驗證（至少 50 個字）
    if (selfIntro.length < 50) {
        alert('自我介紹需至少 50 個字!');
        return;
    }


    // 顯示彈窗確認資料
    const message = `
        您的資料如下：
        姓名: ${name}
        車牌號碼: ${plateNumber}
        性別: ${gender}
        聯絡電話: ${phone}
        電子信箱: ${email}
        自我介紹: ${selfIntro}
        
        確定要送出嗎？
    `;

    if (confirm(message)) {
        alert('履歷已送出，感謝您的加入!');
        // 這裡可以進一步做資料傳送，例如用 AJAX 或 fetch API 發送到伺服器
        document.getElementById('the_form').submit();  // 真正送出表單\
        // window.location.href = '/'; //暫時關閉測試履歷新增

    }
});