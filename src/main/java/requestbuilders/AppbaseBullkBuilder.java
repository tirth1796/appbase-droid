package requestbuilders;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import client.AppbaseClient;
import okhttp3.Response;

public class AppbaseBullkBuilder {
	ArrayList<AppbaseRequestBuilder> req;

	public AppbaseBullkBuilder() {
		req = new ArrayList<AppbaseRequestBuilder>();
	}

	public AppbaseBullkBuilder add(AppbaseRequestBuilder a) {
		req.add(a);
		return this;
	}

	private String escapedJson(String json) {
		JsonParser p = new JsonParser();
		return p.parse(json).toString();

	}

	public Response execute() {
		String jsonBody = "";
		AppbaseRequestBuilder curr = null;
		AppbaseClient ac=null;
		for (int i = 0; i < req.size(); i++) {
			JsonObject j = new JsonObject();
			curr = req.get(i);
			if (curr == null) {
				System.err.println("null AppbaseRequestBuilderObject at possition" + i);
			} else {
				if(ac==null){
					ac=curr.ac;
				}
				if (curr.method == AppbaseRequestBuilder.Rest) {
					System.err.println("AppbaseBulkBuilder does not support the method at position " + i);
				} else {
					j.addProperty("_type", curr.type);
					if (curr.id != null)
						j.addProperty("_id", curr.id);
					JsonObject a = new JsonObject();
					if (curr.method == AppbaseRequestBuilder.Index) {
						a.add("index", j);
						jsonBody += a.toString() + "\n";
						jsonBody += escapedJson(curr.body) + "\n";
					} else if (curr.method == AppbaseRequestBuilder.Update) {
						a.add("update", j);
						jsonBody += a.toString() + "\n";
						jsonBody += escapedJson(curr.body) + "\n";
					} else if (curr.method == AppbaseRequestBuilder.Delete) {
						a.add("delete", j);
						jsonBody += a.toString() + "\n";
					}
				}
			}
		}
		System.out.println(jsonBody);
		return ac.prepareBulkExecute( jsonBody).execute();
	}
}