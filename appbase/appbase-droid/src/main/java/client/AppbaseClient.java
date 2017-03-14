package client;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.elasticsearch.index.query.QueryBuilder;
import org.java_websocket.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import interceptor.BasicAuthInterceptor;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import requestbuilders.AppbaseRequestBuilder;
import requestbuilders.AppbaseWebsocketRequest;
import requestbuilders.Param;

public class AppbaseClient {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public Authenticator authenticator() {
		return ok.authenticator();
	}

	public Cache cache() {
		return ok.cache();
	}

	public CertificatePinner certificatePinner() {
		return ok.certificatePinner();
	}

	public int connectTimeoutMillis() {
		return ok.connectTimeoutMillis();
	}

	public ConnectionPool connectionPool() {
		return ok.connectionPool();
	}

	public List<ConnectionSpec> connectionSpecs() {
		return ok.connectionSpecs();
	}

	public CookieJar cookieJar() {
		return ok.cookieJar();
	}

	public Dispatcher dispatcher() {
		return ok.dispatcher();
	}

	public Dns dns() {
		return ok.dns();
	}

	public boolean equals(Object obj) {
		return ok.equals(obj);
	}

	public boolean followRedirects() {
		return ok.followRedirects();
	}

	public boolean followSslRedirects() {
		return ok.followSslRedirects();
	}

	public int hashCode() {
		return ok.hashCode();
	}

	public HostnameVerifier hostnameVerifier() {
		return ok.hostnameVerifier();
	}

	public List<Interceptor> interceptors() {
		return ok.interceptors();
	}

	public List<Interceptor> networkInterceptors() {
		return ok.networkInterceptors();
	}

	public Builder newBuilder() {
		return ok.newBuilder();
	}

	public Call newCall(Request request) {
		return ok.newCall(request);
	}

	public List<Protocol> protocols() {
		return ok.protocols();
	}

	public Proxy proxy() {
		return ok.proxy();
	}

	public Authenticator proxyAuthenticator() {
		return ok.proxyAuthenticator();
	}

	public ProxySelector proxySelector() {
		return ok.proxySelector();
	}

	public int readTimeoutMillis() {
		return ok.readTimeoutMillis();
	}

	public boolean retryOnConnectionFailure() {
		return ok.retryOnConnectionFailure();
	}

	public SocketFactory socketFactory() {
		return ok.socketFactory();
	}

	public SSLSocketFactory sslSocketFactory() {
		return ok.sslSocketFactory();
	}

	public String toString() {
		return ok.toString();
	}

	public int writeTimeoutMillis() {
		return ok.writeTimeoutMillis();
	}

	private OkHttpClient ok;
	private String baseURL, app, URL, basicauth = null;
	private static final String SEPARATOR = "/";

	/**
	 * Constructor when the elasticsearch setup requires a user name and a
	 * password
	 * 
	 * @param URL
	 *            The base URL(example: "http://scalr.api.appbase.io").
	 * @param appName
	 *            application name (example: "myFirstApp")
	 * @param userName
	 *            the user name provided for the application
	 * @param password
	 *            the password corresponding to the userName
	 * 
	 */
	public AppbaseClient(String baseURL, String app, String username, String password) {
		initialize(username, password);
		this.baseURL = baseURL;
		this.app = app;
		constructURL();

	}

	private void setBasicAuth(String username, String password) {
		basicauth = "Basic " + new String(Base64.encodeBytes((username + ":" + password).getBytes()));
	}

	private void initialize(String username, String password) {
		setBasicAuth(username, password);
		ok = getUnsafeOkHttpClient().addInterceptor(new BasicAuthInterceptor(username, password)).build();

	}

	private static Builder getUnsafeOkHttpClient() {
		try {
			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}
			} };

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
			builder.hostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			return builder;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor when the elasticsearch setup does not require user name and
	 * password
	 * 
	 * @param URL
	 *            The base URL(example: "http://scalr.api.appbase.io").
	 * @param appName
	 *            application name (example: "myFirstApp")
	 */
	public AppbaseClient(String URL, String app) {
		initialize();
		this.baseURL = URL;
		this.app = app;
		constructURL();
	}

	private void initialize() {
		ok = getUnsafeOkHttpClient().build();
	}

	private String constructURL() {
		if (this.baseURL.endsWith("/")) {
			this.URL = this.baseURL + this.app;
			return this.URL;

		} else {
			this.URL = this.baseURL + "/" + this.app;
		}
		return this.URL;
	}

	// getURls

	/**
	 * Returns the constructed URL based on the type argument.
	 * 
	 * @param type
	 * @return constructed URL
	 */

	public String getURL(String type) {
		return URL + SEPARATOR + type;
	}

	/**
	 * Returns the constructed URL for multiple types
	 * 
	 * @param type
	 * @return constructed URL
	 */
	public String getURL(String[] type) {
		String returnURL = URL + SEPARATOR;
		for (int i = 0; i < type.length; i++) {
			returnURL += type[i] + ",";
		}
		return returnURL.substring(0, returnURL.length() - 1);
	}

	/**
	 * Returns the constructed URL based on the type and id.
	 * 
	 * @param type
	 * @param id
	 * @return constructed URL with type and id
	 */
	public String getURL(String type, String id) {
		return URL + SEPARATOR + type + SEPARATOR + id;
	}

	/**
	 * returns the constructed URL by adding a search term as its query
	 * parameter.
	 * 
	 * @param term
	 *            the term to be searched
	 * @return Search URL with the term as a query parameter
	 */
	public String getSearchUrl(String term) {
		return URL + SEPARATOR + "_search?q=" + term;
	}

	/**
	 * 
	 * returns the constructed URL for a type by adding the search term as its
	 * query parameter.
	 * 
	 * @param type
	 * @param term
	 *            the term to be searched
	 * @return Search URL with the term as a query parameter
	 */
	public String getSearchUrl(String type, String term) {
		return URL + SEPARATOR + type + SEPARATOR + "_search?q=" + term;
	}


	/**
	 * Setter for URL
	 * 
	 * @param URL
	 */
	public void setURL(String URL) {
		this.baseURL = URL;
		constructURL();
	}

	public void setApp(String app) {
		this.app = app;
		constructURL();
	}

	/**
	 * Prepare the request for indexing a document without providing the id. Id
	 * will be automatically created.
	 * 
	 * @param type
	 *            type of the object
	 * @param jsonDoc
	 *            the object to be indexed
	 * @return request builder with the provided configurations
	 */
	public AppbaseRequestBuilder prepareIndex(String type, String jsonDoc) {
		return new AppbaseRequestBuilder(this).url(getURL(type)).post(createBody(jsonDoc));
	}

	private RequestBody createBody(String jsonDoc) {
		return RequestBody.create(JSON, jsonDoc);
	}

	/**
	 * Prepare the request for indexing a document without providing the id. Id
	 * will be automatically created.
	 * 
	 * @param type
	 *            type of the object
	 * @param jsonDoc
	 *            the object to be indexed
	 * @return request builder with the provided configurations
	 */
	public AppbaseRequestBuilder prepareIndex(String type, byte[] jsonDoc) {
		return prepareIndex(type, new String(jsonDoc));
	}

	/**
	 * Prepare the request for indexing a document without providing the id. Id
	 * will be automatically created.
	 * 
	 * @param type
	 *            type of the object
	 * @param jsonDoc
	 *            the object to be indexed
	 * @return request builder with the provided configurations
	 */
	public AppbaseRequestBuilder prepareIndex(String type, JsonObject jsonDoc) {
		return prepareIndex(type, jsonDoc.toString());
	}

	/**
	 * Prepare the request for indexing a document without providing the id. Id
	 * will be automatically created.
	 * 
	 * @param type
	 *            type of the object
	 * @param jsonDoc
	 *            the object to be indexed
	 * @return request builder with the provided configurations
	 */
	public AppbaseRequestBuilder prepareIndex(String type, Map<String, Object> jsonDoc) {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(jsonDoc);
		return prepareIndex(type, json);
	}

	/**
	 * To prepare the index. To have control on when it is executed or to add
	 * parameters or queries
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return returns the AppbaseRequestBuilderer object which can be executed
	 */

	public AppbaseRequestBuilder prepareIndex(String type, String id, String jsonDoc) {

		return new AppbaseRequestBuilder(this).url(getURL(type, id)).put(createBody(jsonDoc));
	}

	/**
	 * To prepare the index. To have control on when it is executed or to add
	 * parameters or queries
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return returns the AppbaseRequestBuilderer object which can be executed
	 */

	public AppbaseRequestBuilder prepareIndex(String type, String id, byte[] jsonDoc) {
		return prepareIndex(type, id, new String(jsonDoc));
	}

	/**
	 * To prepare the index. To have control on when it is executed or to add
	 * parameters or queries
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return returns the AppbaseRequestBuilderer object which can be executed
	 */

	public AppbaseRequestBuilder prepareIndex(String type, String id, JsonObject jsonDoc) {
		return prepareIndex(type, id, jsonDoc.toString());
	}

	/**
	 * To prepare the index. To have control on when it is executed or to add
	 * parameters or queries
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return returns the AppbaseRequestBuilderer object which can be executed
	 */

	public AppbaseRequestBuilder prepareIndex(String type, String id, Map<String, String> jsonDoc) {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(jsonDoc);
		return prepareIndex(type, id, json);
	}

	/**
	 * To prepare a {@link AppbaseRequestBuild} object to update a document. We
	 * can pass just the portion of the object to be updated. parameters is a
	 * list of parameters which are the name value pairs which will be added
	 * during the execution
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param parameters
	 *            A list of all the parameters for a specific update
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */

	public AppbaseRequestBuilder prepareUpdate(String type, String id, List<Param> parameters, String jsonDoc) {
		return new AppbaseRequestBuilder(this).url(getURL(type, id) + SEPARATOR + "_update").post(createBody(jsonDoc))
				.addQueryParams(parameters);
	}

	/**
	 * To prepare a {@link AppbaseRequestBuild} object to update a document. We
	 * can pass just the portion of the object to be updated. parameters is a
	 * list of parameters which are the name value pairs which will be added
	 * during the execution
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param parameters
	 *            A list of all the parameters for a specific update
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */

	public AppbaseRequestBuilder prepareUpdate(String type, String id, List<Param> parameters, byte[] jsonDoc) {
		return prepareUpdate(type, id, parameters, new String(jsonDoc));
	}

	/**
	 * To prepare a {@link AppbaseRequestBuild} object to update a document. We
	 * can pass just the portion of the object to be updated. parameters is a
	 * list of parameters which are the name value pairs which will be added
	 * during the execution
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param parameters
	 *            A list of all the parameters for a specific update
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */

	public AppbaseRequestBuilder prepareUpdate(String type, String id, List<Param> parameters, JsonObject jsonDoc) {
		return prepareUpdate(type, id, parameters, jsonDoc.toString());

	}

	/**
	 * To prepare a {@link AppbaseRequestBuild} object to update a document. We
	 * can pass just the portion of the object to be updated. parameters is a
	 * list of parameters which are the name value pairs which will be added
	 * during the execution
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param parameters
	 *            A list of all the parameters for a specific update
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */

	public AppbaseRequestBuilder prepareUpdate(String type, String id, List<Param> parameters,
			Map<String, Object> jsonDoc) {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(jsonDoc);
		return prepareUpdate(type, id, parameters, json);

	}

	/**
	 * To prepare an {@link AppbaseRequestBuild} object to delete a document.
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */
	public AppbaseRequestBuilder prepareDelete(String type, String id) {
		return new AppbaseRequestBuilder(this).url(getURL(type, id)).delete();
	}

	/**
	 * When multiple requests need to be executed but in a sequence to reduce
	 * the bandwidth usage.
	 * 
	 * @param requestBuilders
	 *            an array of AppbaseRequestBuilders which need to be executed
	 * @return Array of the listenable futures containing the responses of
	 *         individual requests
	 */
	public Response[] bulkExecute(AppbaseRequestBuilder[] requestBuilders) {
		Response[] response = new Response[requestBuilders.length];
		for (int i = 0; i < requestBuilders.length; i++) {
			response[i] = (requestBuilders[i].execute());
		}
		return response;
	}

	/**
	 * When multiple requests need to be executed but in a sequence to reduce
	 * the bandwidth usage.
	 * 
	 * @param requestBuilders
	 *            an array of AppbaseRequestBuilders which need to be executed
	 * @return Array of the listenable futures containing the responses of
	 *         individual requests
	 */
	public Response[] bulkExecute(ArrayList<AppbaseRequestBuilder> requestBuilders) {
		Response[] response = new Response[requestBuilders.size()];
		for (int i = 0; i < requestBuilders.size(); i++) {
			response[i] = (requestBuilders.get(i).execute());
		}
		return response;
	}

	/**
	 * 
	 * Prepare an {@link AppbaseRequestBuild} object to get the indexed objects
	 * by specifying type and id
	 * 
	 * @param type
	 *            type of the required object
	 * @param id
	 *            id of the required object
	 * @return the {@link AppbaseRequestBuild} object having the required
	 *         configuration for get to execute
	 */
	public AppbaseRequestBuilder prepareGet(String type, String id) {
		return new AppbaseRequestBuilder(this).url(getURL(type, id)).get();
	}

	/**
	 * Get the mappings of the Application
	 * 
	 * @return returns the json document as {@link String} of the mappings
	 */
	public String getMappings() {
		Response f = prepareGetMappings().execute();

		try {
			return new String(f.body().bytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object to get the mappings of the
	 * Application
	 * 
	 * @return returns the json document as {@link String} of the mappings
	 */
	public AppbaseRequestBuilder prepareGetMappings() {
		return new AppbaseRequestBuilder(this).url(this.URL + SEPARATOR + "_mapping").get();
	}

	/**
	 * Method to get an array of types
	 * 
	 * @return String containing JsonArray of the types
	 */
	public String getTypes() {

		String result = getMappings();
		System.out.println(result);
		JsonParser parser = new JsonParser();
		JsonObject object = parser.parse(result).getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entries = object.getAsJsonObject(app).getAsJsonObject("mappings")
				.entrySet();// will return members
							// of your object
		JsonArray ret = new JsonArray();
		for (Map.Entry<String, JsonElement> entry : entries) {
			if (!entry.getKey().equals("_default_"))
				ret.add(entry.getKey());
		}
		return ret.toString();
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object for searching by adding the
	 * search body
	 * 
	 * @param type
	 *            type in which the search must take place
	 * @param body
	 *            the query body (example: {"query":{"term":{ "price" : 5595}}}
	 *            )
	 * @return returns the search result corresponding to the query
	 */
	public AppbaseRequestBuilder prepareSearch(String type, String body) {
		return new AppbaseRequestBuilder(this).url(getURL(type) + SEPARATOR + "_search").post(createBody(body));
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object for searching without
	 * adding the body which will need to be manually added
	 * 
	 * @param type
	 *            type in which the search must take place
	 * @param body
	 *            the query body (example: {"query":{"term":{ "price" : 5595}}}
	 *            )
	 * @return returns the search result corresponding to the query
	 */

	public AppbaseRequestBuilder prepareSearch(String type, QueryBuilder qb) {
		return prepareSearch(type, qb.toString());
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object for searching by adding the
	 * query body within multiple types
	 * 
	 * @param type
	 *            array of all the types in which the search must take place
	 * @param body
	 *            the query body (example: {"query":{"term":{ "price" : 5595}}})
	 * @return returns the search result corresponding to the query
	 */

	public AppbaseRequestBuilder prepareSearch(String[] type, String body) {
		return new AppbaseRequestBuilder(this).url(getURL(type) + SEPARATOR + "_search").post(createBody(body));
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object for searching without
	 * adding the body which will need to be manually added within multiple
	 * types
	 * 
	 * @param type
	 *            array of all the types in which the search must take place
	 * @param body
	 *            the query body (example: {"query":{"term":{ "price" : 5595}}}
	 *            )
	 * @return returns the search result corresponding to the query
	 */
	public AppbaseRequestBuilder prepareSearch(String[] type, QueryBuilder qb) {
		return prepareSearch(type, qb.toString());
	}

	/**
	 * Prepare an {@link AppbaseRequestBuild} object to search by passing the
	 * query as a List of param objects. This will be added like query
	 * parameters not in the body.
	 * 
	 * @param type
	 *            type in which the search must take place
	 * @param body
	 *            List of Parameter objects which
	 * @return returns the search result corresponding to the query
	 */

	public AppbaseRequestBuilder prepareSearch(String type, List<Param> parameters) {
		return new AppbaseRequestBuilder(this).url(getURL(type) + SEPARATOR + "_search").addQueryParams(parameters)
				.post(null);

	}

	public AppbaseRequestBuilder prepareSearchStreamToURL(String type, String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}


	private JsonObject getSearchStreamJson(String type, String request) {
		JsonObject json = new JsonObject();
		///////////how to get uid
		
		json.addProperty("id", "c");
		if (basicauth != null)
			json.addProperty("authorization", basicauth);
		json.addProperty("path", app+SEPARATOR+type+SEPARATOR+"_search?streamonly=true");

		JsonParser parser = new JsonParser();
		
		json.add("body", parser.parse(request).getAsJsonObject());
		json.addProperty("method", "POST");
		return json;
	}
	
	
	public AppbaseWebsocketRequest prepareSearchStream(String type, String request) {
		return new AppbaseWebsocketRequest(getSearchStreamJson(type, request),baseURL);
	}

}
