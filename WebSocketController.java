package com.hy.mycode.chat.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websocket")
public class WebSocketController {
//	private Session session;
	private static HashMap<String, Session> sessionMap = new HashMap<>();
	private static int i;

	@OnOpen
	public void handleOpen(Session session) {
		i++;
		if (session != null) {
			System.out.println(session.getId() + " 연결 성공 => 총 접속 인원 : " + i + "명");
			System.out.println("client is connected. sessionId == [" + session.getId() + "]");
			sessionMap.put(session.getId(), session);
			
			// sendMessageToAll("** [USER-" + session.getId() + "] is connected. **", session);
		}
		System.out.println(sessionMap);
	}

	@OnMessage
	public String handleMessage(String message, Session session) {

		String replymessage = message;

		JSONObject obj = jsonToObjectParser(message);
		obj.put("sessionId", session.getId());
		
		if (session != null) {
			System.out.println("send : " + replymessage);
			System.out.println("receive " + session.getId() + " : " + message);

			//sendMessageToAll("[USER-" + session.getId() + "] : " + message, session);
//			sendMessageToAll(message, session);
			sendMessageToAll(obj.toJSONString(), session);
		}

		return null;
	}

	@OnClose
	public void handleClose(Session session) {
		i--;
		if (session != null) {
			System.out.println("client is disconnected. sessionId == [" + session.getId() + "]");
			sessionMap.remove(session.getId());
			// sendMessageToAll("** [USER-" + session.getId() + "] is disconnected. **", session);
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace();
	}

	private void sendMessageToAll(String message, Session session) {
		System.out.println(session);

		for (String key : sessionMap.keySet()) {

			if (!sessionMap.get(key).getId().equals(session.getId())) {
				try {
					// 상대방
					sessionMap.get(key).getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					// 나   
					sessionMap.get(key).getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
//			sessionMap.get(key).getAsyncRemote().sendText(message);
		}

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
