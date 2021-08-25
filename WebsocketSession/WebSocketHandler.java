import java.util.HashMap;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Controller
//@ServerEndpoint("/websocket")
public class WebSocketHandler extends TextWebSocketHandler {

	HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션을 담아둘 맵
	private static int i;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		i++;

		if (session != null) {
			System.out.println(session.getId() + " 연결 성공 => 총 접속 인원 : " + i + "명");
			System.out.println("client is connected. sessionId == [" + session.getId() + "]");
			sessionMap.put(session.getId(), session);

		}
		System.out.println(sessionMap);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 소켓 종료
		super.afterConnectionClosed(session, status);
		if (session != null) {
			sendMessageToAll("** [USER-" + session.getId() + "] is disconnected. **", session);
			sessionMap.remove(session.getId());
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String msg = message.getPayload();

		String replymessage = msg;

		JSONObject obj = jsonToObjectParser(msg);
		obj.put("sessionId", session.getId());

		if (session != null) {
			System.out.println("send : " + replymessage);
			System.out.println("receive " + session.getId() + " : " + msg);
      
			sendMessageToAll(obj.toJSONString(), session);
		}

	}

	private boolean sendMessageToAll(String message, WebSocketSession session) {

		for (String key : sessionMap.keySet()) {
			
			WebSocketSession wss = sessionMap.get(key);
			
			if (!sessionMap.get(key).getId().equals(session.getId())) {
				try {
					wss.sendMessage(new TextMessage(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else {
				try {
					wss.sendMessage(new TextMessage(message));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

		}

		return true;
	}

	private static JSONObject jsonToObjectParser(String jsonStr) {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(jsonStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
