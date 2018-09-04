"use strict";

$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $('#create').click(function () {
        $.post('room/create', function (code) {
            tryCode(code);
        });
    });

    $('[data-toggle="popover"]').popover();
    let errPopover = $('#err');
    let $input = $('#code');
    $input.on('input', function(e){
        let code = $input.val().toUpperCase();
        $input.val(code);
        if (code.length === 4) {
            tryCode(code);
        }
        else {
            errPopover.popover('hide');
        }
    });

    function tryCode(code) {
        $.post('room/enter', {code: code}, function (response) {
            if (response === 'OK') {
                window.location.replace('/room?code=' + code);
            }
            else if (response.length < 20) {
                errPopover.attr('data-content', response);
                errPopover.popover('show');
            }
        });
    }
});