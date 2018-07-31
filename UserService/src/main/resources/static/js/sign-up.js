$(document).ready(function() {
    // ajax 설정.
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    function checkByAjax(button, value, url, tipObj) {
        if (!value)
            return;

        button.prop('disabled', true);

        $.ajax({ url: url,
                 type: 'POST',
                 data: {
                    'data': value
                 },
                 dataType: 'JSON'
        })
        .done(function(json) {
            if (json.success)
                tipObj.removeClass("err-tip")
            else
                tipObj.addClass("err-tip")
            tipObj.text(json.message);
        })
        .fail(function(xhr, status, errorThrown) {
            alert( "Sorry, there was a problem! please try again." );
            location.href = '/sign-up';
        })
        .always(function(xhr, status) {
            button.prop("disabled", false);
        });
    }

    // click 이벤트 설정.
    $('#email-dup-check').on('click', function(events) {
        checkByAjax($(self), $('#email').val(), '/email-dup-check', $('#email-tip'))
    });

    $('#nickname-dup-check').on('click', function(events) {
        checkByAjax($(self), $('#nickname').val(), '/nickname-dup-check', $('#nickname-tip'))
    });
});
