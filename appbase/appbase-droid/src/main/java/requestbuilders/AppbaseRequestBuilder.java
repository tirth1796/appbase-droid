package requestbuilders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import client.AppbaseClient;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppbaseRequestBuilder {
	Request.Builder builder;
	AppbaseClient ac;
	URI uri;

	public AppbaseRequestBuilder(AppbaseClient a) {
		this.ac = a;
		builder = new Builder();
	}

	public AppbaseRequestBuilder addHeader(String name, String value) {
		builder.addHeader(name, value);
		return this;
	}

	public Request build() {
		return builder.build();
	}

	public AppbaseRequestBuilder cacheControl(CacheControl cacheControl) {
		builder.cacheControl(cacheControl);
		return this;
	}

	public AppbaseRequestBuilder delete() {
		builder.delete();
		return this;
	}

	public AppbaseRequestBuilder delete(RequestBody body) {
		builder.delete(body);
		return this;
	}

	public boolean equals(Object obj) {
		return builder.equals(obj);
	}

	public AppbaseRequestBuilder get() {
		builder.get();
		return this;
	}

	public int hashCode() {
		return builder.hashCode();
	}

	public AppbaseRequestBuilder head() {
		builder.head();
		return this;
	}

	public AppbaseRequestBuilder header(String name, String value) {
		builder.header(name, value);
		return this;
	}

	public AppbaseRequestBuilder headers(Headers headers) {
		builder.headers(headers);
		return this;
	}

	public AppbaseRequestBuilder method(String method, RequestBody body) {
		builder.method(method, body);
		return this;
	}

	public AppbaseRequestBuilder patch(RequestBody body) {
		builder.patch(body);
		return this;
	}

	public AppbaseRequestBuilder post(RequestBody body) {
		builder.post(body);
		return this;
	}

	public AppbaseRequestBuilder put(RequestBody body) {
		builder.put(body);
		return this;
	}

	public AppbaseRequestBuilder removeHeader(String name) {
		builder.removeHeader(name);
		return this;
	}

	public AppbaseRequestBuilder tag(Object tag) {
		builder.tag(tag);
		return this;
	}

	public String toString() {
		return builder.toString();
	}

	// Set URL before execute
	public AppbaseRequestBuilder url(HttpUrl url) {
		uri = url.uri();
		return this;
	}

	public AppbaseRequestBuilder url(String url) {
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			uri = null;
		}
		return this;
	}

	public AppbaseRequestBuilder url(URL url) {
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			uri = null;
		}

		return this;
	}

	public AppbaseRequestBuilder addQueryParams(List<Param> parameters) {
		if(parameters==null){
			return this;
		}
		for (int i = 0; i < parameters.size(); i++) {
			try {
				appendUri(uri, parameters.get(i).key + "=" + parameters.get(i).value);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public AppbaseRequestBuilder addQueryParam(String key, String value) {

		try {
			appendUri(uri, key + "=" + value);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return this;
	}

	private static URI appendUri(URI oldUri, String appendQuery) throws URISyntaxException {

		String newQuery = oldUri.getQuery();
		if (newQuery == null) {
			newQuery = appendQuery;
		} else {
			newQuery += "&" + appendQuery;
		}

		URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(), oldUri.getPath(), newQuery,
				oldUri.getFragment());

		return newUri;
	}

	public Response execute() {
		builder.url(uri.toString());
		try {
			return ac.newCall(builder.build()).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
