package trial;

import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonParser;

import client.AppbaseClient;
import okhttp3.Response;

public class Main {


	static JsonParser parser;
	static AppbaseClient appbase;
	int startId=1,endId;
	static String user = "vspynv5Dg", pass = "f54091f5-ff77-4c71-a14c-1c29ab93fd15",
			URL = "https://scalr.api.appbase.io", appName = "Trial1796",
			jsonDoc = "{\"department_id\": 1,\"department_name\": \"Books\",\"name\": \"A Fake Book on Network Routing\",\"price\": 5595}";

	static String type = "product", id = "1";

	public static void main(String[] args) {
		AppbaseClient ac=new AppbaseClient(URL, appName,user,pass);
		Response r=ac.prepareIndex(type, jsonDoc).execute();
		try {
			System.out.println(r.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}