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
    const name = document.querySelector('input[name="name"]').value;
    const plateNumber = document.getElementById('plate_number').value;
    const gender = document.querySelector('input[name="gender"]:checked') ? 
                   document.querySelector('input[name="gender"]:checked').nextSibling.textContent.trim() : '未選擇';
    const phone = document.querySelector('input[name="phone"]').value;
    const email = document.querySelector('input[type="email"]').value;
    const selfIntro = document.querySelector('textarea').value;

    // 檢查必填欄位
    if (!name || !plateNumber || !phone || !email) {
        alert('請完整填寫必填欄位!');
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
        自我介紹: ${selfIntro || '未填寫'}
        
        確定要送出嗎？
    `;

    if (confirm(message)) {
        alert('履歷已送出，感謝您的加入!');
        // 這裡可以進一步做資料傳送，例如用 AJAX 或 fetch API 發送到伺服器
        document.getElementById('the_form').submit();  // 真正送出表單\
        window.location.href = '/';

    }
});