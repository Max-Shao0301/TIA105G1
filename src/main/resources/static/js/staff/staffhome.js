$(document).ready(function () {
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

    $('.order-row').each(function () {
        let status = $(this).find('.status-cell').data('status');
        let orderId = $(this).find('.status-cell').data('order-id');
        let statusCell = $(this).find('.status-cell');

        if (status == 1) {
            statusCell.html(
                '<button class="btn btn-success btn-sm upload-button" data-bs-toggle="modal" data-bs-target="#uploadModal" data-order-id="' +
                orderId + '">未結案</button>');
        } else if (status == 2) {
            statusCell.text('已結案');
        }
    });

    $('.order-row').each(function () {
        let status = $(this).find('.payment-cell').data('status');
        let star = $(this).find('.star-cell').data('star');
        let rating = $(this).find('.rating-cell').data('rating');
        let starCell = $(this).find('.star-cell');
        let ratingCell = $(this).find('.rating-cell');

        if (status == 1) {
            starCell.text('未結案');
        } else if (status == 2) {

            if (star !== undefined && star !== null) {
                let starsHTML = "";
                for (let i = 1; i <= 5; i++) {
                    if (i <= star) {
                        starsHTML += '<i class="fas fa-star star-icon"></i>';
                    } else {
                        starsHTML += '<i class="far fa-star star-icon empty-star"></i>';
                    }
                }
                starCell.html(starsHTML);
            } else { 
                let starsHTML = "";
                for (let i = 1; i <= 5; i++) {
                    starsHTML += '<i class="far fa-star star-icon empty-star"></i>'; 
                }
                starCell.html(starsHTML);
            }

            if (rating !== undefined && rating !== null) { 
                ratingCell.text(rating);
            }else {
                ratingCell.text("無評價");
            }
        }
    });

    $('#imageUpload').change(function () {
        let file = this.files[0];
        if (file) {
            let reader = new FileReader();
            reader.onload = function (event) {
                $('#imagePreview').attr('src', event.target.result).show();
            }
            reader.readAsDataURL(file);
        } else {
            $('#imagePreview').attr('src', '#').hide();
        }
    });

    $('#uploadModal').on('hidden.bs.modal', function (e) {
        $('#imageUpload').val('');
        $('#imagePreview').attr('src', '#').hide();
    });

    $('.upload-button').click(function () {
        let orderId = $(this).data('order-id');
        $('#orderIdInput').val(orderId);
    });

    $('.pet-button').click(function () {
        let orderId = $(this).data('order-id');
        let petInfo = "";
        $('.order-row').each(function () {
            if ($(this).find('.status-cell').data('order-id') == orderId) {
                $(this).find('.pet-info-data').each(function () {
                    let type = $(this).find('.pet-type').text();
                    let name = $(this).find('.pet-name').text();
                    let gender = $(this).find('.pet-gender').text();
                    let weight = $(this).find('.pet-weight').text();
                    petInfo += "<div class='pet-info-item'>";
                    petInfo += "<span class='pet-info-label'>類別:</span><span class='pet-info-value'>" + type + "</span>";
                    petInfo += "<span class='pet-info-label'>大名:</span><span class='pet-info-value'>" + name + "</span>";
                    if (gender) petInfo += "<span class='pet-info-label'>性別:</span><span class='pet-info-value'>" + gender + "</span>";
                    if (weight) petInfo += "<span class='pet-info-label'>體重:</span><span class='pet-info-value'>" + weight + "</span>";
                    petInfo += "</div>";
                });
            }
        });
        $('#petInfoModalBody').html("<h5 class='pet-info-modal-title'>寵物資訊</h5>" + petInfo);
    });
});