package requestbuilders;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.JsonObject;

import client.Stream;
import client.AppbaseWebsocketClient;

public class AppbaseWebsocketRequest {
	JsonObject json;
	String baseURL; 
	public AppbaseWebsocketRequest(JsonObject json,String baseURL) {
		super();
		this.json = json;
		this.baseURL=baseURL;
		changeURL();
	}
	
	public void changeURL(){
		System.out.println(baseURL.charAt(4));
		if(baseURL.charAt(4)=='s'){
			baseURL="wss"+baseURL.substring(5);
		}else{	
			baseURL="ws"+baseURL.substring(4);
		}
		System.out.println(baseURL);
	}
	
	public AppbaseWebsocketClient execute(Stream appbaseOnMessage){
		
		AppbaseWebsocketClient client = null;
		try {
			client = new AppbaseWebsocketClient(new URI(baseURL), appbaseOnMessage);
			client.connectBlocking();
			System.out.println(json.toString());
			client.send(json.toString());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client;
	}

}
