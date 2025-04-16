
document.addEventListener('DOMContentLoaded', function () {
	console.log('🚨 JS loaded!');
    const memId = window.memId || 0;

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    const bellIcon = document.getElementById('bellIcon');
    const bellBadge = document.getElementById('bellBadge');
    const reminderDropdown = document.getElementById('reminderDropdown');
    const reminderList = document.getElementById('reminderList');
    const bellWrapper = document.getElementById('bellWrapper');

	stompClient.connect({}, function () {
	  console.log('✅ WebSocket connected');

	  // 訂閱即時訊息
	  stompClient.subscribe("/topic/reminder/" + memId, function (msg) {
		
	    const text = msg.body
		
	    showReminder(text);

	    // 更新紅點
	    let count = parseInt(bellBadge.innerText || '0') + 1;
	    bellBadge.innerText = count;
	    bellBadge.style.display = 'inline';
	  });

	  // 撈 Redis 離線訊息
	  fetch('/reminder/offline/' + memId)
	    .then(res => res.json())
	    .then(messages => {
	      messages.forEach(showReminder);
	      if (messages.length > 0) {
	        bellBadge.style.display = 'inline';
	        bellBadge.innerText = messages.length;
	      }
	    });
	});

	  // ✅ 改為手動 class 控制，避免 click 穿透
	  let isDropdownOpen = false;

	  bellIcon.addEventListener('click', function (e) {
	    e.stopPropagation();
	    if (!isDropdownOpen) {
	      reminderDropdown.classList.add('show');
	      isDropdownOpen = true;
	    } else {
	      reminderDropdown.classList.remove('show');
	      isDropdownOpen = false;
	    }
	    bellBadge.innerText = '0';
	    bellBadge.style.display = 'none';
	  });

	  // 點其他地方關閉提醒（點到 wrapper 本身也不會關閉）
	  document.addEventListener('click', function (e) {
	    if (!bellWrapper.contains(e.target)) {
	      reminderDropdown.classList.remove('show');
	      isDropdownOpen = false;
	    }
	  });
	});
	
	
	function showReminder(text) {
	  const li = document.createElement('li');
	  text = text.replace(/\n/g, '<br>')            // 換行符轉為 <br>
	  		.replace(/  /g, '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');   // 兩個空格轉為 &nbsp;
	  li.innerHTML = text; // ✅ 讓 <br>、&nbsp; 生效
	  li.classList.add('bellmargin');
	  reminderList.prepend(li);
	}



