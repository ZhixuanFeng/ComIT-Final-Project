"use strict";

var roomCode;
$(document).ready(function() {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    var $overlay = $('#overlay');
    $('#player_list').hide();
    $('#start_bt').hide();
    $overlay.show();

    $('#ol_create').click(function () {
        $.post('room/create', function (code) {
            roomCode = code;

            connect(roomCode);

            $('#player_list').show();
            $('#start_bt').show();
            $overlay.hide();

            $('#room_code').text(roomCode);
        });
    });

    var $input = $('#ol_code_input');
    $input.on('input', function(e){
        var inputText = $input.val().toUpperCase();
        $input.val(inputText);
        if (inputText.length == 4) {
            connect(inputText);
            /*$.post('room/join', {code: inputText}, function (result) {
                if (result.status === 'OK') {
                    $('#player_list').show();
                    $('#start_bt').show();
                    $overlay.hide();

                    $('#room_code').text(inputText);
                }
            });*/
        }
        else {
            $('#ol_message').text('');
        }
    });
});

var stompClient = null;

function connect(code) {
    var socket = new SockJS('/room');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/room/' + code, onMessageReceived);

        stompClient.send('/app/room.enter/' + code, {}, code);
    }, onError);
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
}

function onError(error) {
    if (typeof(error) == "string" && error.indexOf('Whoops!') !== -1) {
        return;  // lost connection
    }
    if ($('#ol_message').is(':visible')) {
        $('#ol_message').text(error.headers.message);
    }
    // disconnect();
}

function onMessageReceived(message) {
    console.log(message);
}

function sendMessage() {
    stompClient.send("/room", {},
        JSON.stringify({'from':from, 'text':text}));
}