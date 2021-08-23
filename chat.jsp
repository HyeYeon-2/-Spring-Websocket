<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" href="/mycode/resources/plugins/bootstrap/bootstrap.css">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://bootswatch.com/_vendor/prismjs/themes/prism-okaidia.css">
<link rel="stylesheet" href="https://bootswatch.com/_assets/css/custom.min.css">
<link rel="stylesheet" href="/mycode/resources/css/chat.css">
<link rel="stylesheet" href="/mycode/resources/css/style.css">
</head>
<body>

	<div id="wrap">

		<!-- 채팅방 -->
		<div id="chat-room" class="card mb-3 direct-chat direct-chat-primary" style="width: 25rem;">

			<div class="card-header" style="cursor: move;">
				<h3 class="card-title mt-2">Direct Chat</h3>

				<div class="card-tools">
					<button type="button" class="btn btn-tool" data-card-widget="collapse">
						<i class="fas fa-minus"></i>
					</button>
					<button type="button" class="btn btn-tool" data-card-widget="remove">
						<i class="fas fa-times"></i>
					</button>
				</div>
			</div>
			<!-- /.card-header -->
			<div class="card-body">

				<div id="chating" class="direct-chat-messages"></div>

				<div class="card-footer">
					<form action="#" method="post">
						<div id="name" class="input-group">
							<input type="text" id="userName" name="name" placeholder="name">

						</div>
						<div class="input-group">
							<input type="text" id="chatting" name="message" placeholder="Type Message ..." class="form-control">
							<span class="input-group-append">
								<button type="button" class="btn btn-primary" onclick="sendMessage();">Send</button>
							</span>
						</div>
					</form>
				</div>
				<!-- /.card-footer-->
			</div>

		</div>

	</div>

	<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
	<script src="https://cdn.jsdelivr.net/sockjs/latest/sockjs.min.js"></script>
	<script src="/mycode/resources/js/chat.js"></script>
	<script>

		function chat() {

		}
		var webSocket = new WebSocket('ws://' + location.host
				+ '/mycode/websocket');

		var messageTextArea = document.getElementById("chating");

		//웹 소켓이 연결되었을 때 호출되는 이벤트
		webSocket.onopen = function(data) {
			messageTextArea.value += "Server connect...\n";
		};

		//웹 소켓이 닫혔을 때 호출되는 이벤트
		webSocket.onclose = function(data) {
			messageTextArea.value += "Server Disconnect...\n";
		};

		//웹 소켓이 에러가 났을 때 호출되는 이벤트
		webSocket.onerror = function(data) {
			messageTextArea.value += "error...\n";
		};

		//웹 소켓에서 메시지가 날라왔을 때 호출되는 이벤트

		webSocket.onmessage = function(data) {
			// console.log(data);
			var info = JSON.parse(data.data);
			// console.log("!!!! : "+info.msg);
			// $("#sessionId").val(info.sessionId)

			if ($('#userName').val() == info.user) {
				var send = "";
				send += '<div class="direct-chat-msg right">';
				send += '<div class="direct-chat-infos clearfix">';
				send += '<span class="direct-chat-name float-right">'
						+ info.user + '</span>';
				send += '</div>';
				send += '<div class="direct-chat-infos clearfix">';
				send += '<div class="direct-chat-text float-right">' + info.msg
						+ '</div>';
				send += '<span class="direct-chat-timestamp float-right">'
						+ info.time + '</span>';
				send += '</div> </div>';
				$('#chating').append(send);
			} else {
				var receive = "";
				receive += '<div class="direct-chat-msg">';
				receive += '<span class="direct-chat-name">' + info.user
						+ '</span>';
				receive += '<div class="direct-chat-infos clearfix">';
				receive += '<div class="direct-chat-text float-left">'
						+ info.msg + '</div>';
				receive += '<span class="direct-chat-timestamp float-left">'
						+ info.time + '</span>';
				receive += '</div> </div>';
				$('#chating').append(receive);

			}

			// 스크롤바 하단에 고정
			document.getElementById('chating').scrollTop = document
					.getElementById('chating').scrollHeight;
		};

		document.addEventListener("keypress", function(e) {
			if (e.keyCode == 13) { //enter press
				sendMessage();
			}
		});

		//Send 버튼을 누르면 실행되는 함수
		function sendMessage() {
			var message = document.getElementById("chatting");

			var today = new Date();

			var hours = ('0' + today.getHours()).slice(-2);
			var minutes = ('0' + today.getMinutes()).slice(-2);

			var op = {
				user : $('#userName').val(),
				msg : $('#chatting').val(),
				time : hours + ':' + minutes
			};
			
			if ($('#chatting').val().trim() != "") {
				//웹소켓으로 보냄
				webSocket.send(JSON.stringify(op));
			}
			//textMessage객체의 값 초기화
			message.value = "";
		}

		//웹소켓 종료
		function disconnect() {
			webSocket.close();
		}
	</script>

</body>
</html>