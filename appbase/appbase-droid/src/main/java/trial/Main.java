package trial;

import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonParser;

import client.AppbaseClient;
import client.Stream;

public class Main {

	static JsonParser parser;
	static AppbaseClient appbase;
	int startId = 1, endId;
	static String user = "vspynv5Dg", pass = "f54091f5-ff77-4c71-a14c-1c29ab93fd15", URL = "https://scalr.api.appbase.io",
			appName = "Trial1796", query = "{\"query\":{\"term\":{\"price\" : 5595}}}",
			jsonDoc = "{\"department_id\": 1,\"department_name\": \"Books\",\"name\": \"A Fake Book on Network Routing\",\"price\": 5595}";

	static String type = "product", id = "1";

	public static void main(String[] args) {
		
		AppbaseClient ac = new AppbaseClient(URL, appName, user, pass);
		ac.prepareSearchStream(type, query).execute(new Stream() {
			
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMessage(String message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(Exception ex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClose(int code, String reason, boolean remote) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
