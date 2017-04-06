package trial;

import java.io.IOException;
import com.google.gson.JsonParser;

import client.AppbaseClient;

public class Main {

	static JsonParser parser;
	static AppbaseClient appbase;
	int startId = 1, endId;
	static String user = "vspynv5Dg", pass = "f54091f5-ff77-4c71-a14c-1c29ab93fd15",
			URL = "https://vspynv5Dg:f54091f5-ff77-4c71-a14c-1c29ab93fd15@scalr.api.appbase.io", appName = "Trial1796",
			query = "{\"query\":{\"term\":{\"price\" : 5595}}}",
			jsonDoc = "{\"department_id\": 1,\"department_name\": \"Books\",\"name\": \"A Fake Book on Network Routing\",\"price\": 5595}";

	static String type = "product", id = "1";

	public static void main(String[] args) {
		AppbaseClient ac = new AppbaseClient(URL, appName);
		try {
			System.out.println(ac.prepareIndex(type, jsonDoc).execute().body().string());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// AppbaseClient ac = new AppbaseClient(URL, appName, user, pass);
		//// AppbaseBullkBuilder bulk = new AppbaseBullkBuilder();
		// // bulk.add(ac.prepareIndex(type,id, jsonDoc));
		// // bulk.add(ac.prepareDelete(type, id));
		// // try {
		// // System.out.println(bulk.execute().body().string());
		// // } catch (IOException e) {
		// // e.printStackTrace();
		// // }
		// JsonObject j=new JsonObject();
		// JsonArray a=new JsonArray();
		// a.add("as");a.add("as1");a.add("as2");
		// j.add("arr", a);
		// XContentBuilder builder = null;
		// try {
		// builder = XContentFactory.jsonBuilder().startObject()
		// .field("user", "sid")
		// .field("postDate", new Date())
		// .field("message", "trying out Elasticsearch")
		// .endObject();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// okhttp3.Response r = null;
		// try {
		// r = ac.prepareIndex("hello", builder.string()).execute();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// try {
		// System.out.println("response: " + r.body().string());
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}
}