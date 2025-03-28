$(document).ready(function() {

    $(".timeslotTd").each(function() {
		
        // 是幾點到幾點
        var text = $(this).text();
        var firstOneOrTwoIndex = text.search(/[12]/);
        var lastOneOrTwoIndex = text.search(/[12](?=[^12]*$)/);
		
		if((lastOneOrTwoIndex+1) <= 9){
			lastOneOrTwoIndex = "0" + (lastOneOrTwoIndex+1);
		}else{
			lastOneOrTwoIndex = (lastOneOrTwoIndex+1);
		}
		
		if(firstOneOrTwoIndex <= 9){
			firstOneOrTwoIndex = "0" + firstOneOrTwoIndex ;
		}
			
		$(this).text(firstOneOrTwoIndex + ":00 ~ " + lastOneOrTwoIndex + ":00");
		
		// 如果如果有預約就不能修改
		if (text.search(/[2]/) !== -1) {
  			$('<span>已預約</span>').insertAfter($(this).closest('tr').find('input[name="action"]'));
			$(this).closest('tr').find(".delete").remove();

		}
		
    });
});

