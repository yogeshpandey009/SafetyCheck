package com.google.pubsubhubbub;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class Subscriber {

	DefaultHttpClient httpClient = null;
	Web webserver = null;
	String contextPath = "/push/";

	public Subscriber(Web webserver) {
		
		this.webserver = webserver;
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 200);
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
		connPerRoute.setDefaultMaxPerRoute(50);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		httpClient = new DefaultHttpClient(cm, params);

		httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

			public long getKeepAliveDuration(HttpResponse response,
					HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						try {
							return Long.parseLong(value) * 1000;
						} catch (NumberFormatException ignore) {
						}
					}
				}
				// default keepalive is 60 seconds. This is higher than usual
				// since the number of hubs it should be talking to should be
				// small
				return 30 * 1000;
			}
		});
	}

	/*
	 * @throws IOException If an input or output exception occurred
	 * 
	 * @param The Hub address you want to publish it to
	 * 
	 * @param The topic_url you want to publish
	 * 
	 * @return HTTP Response code. 200 is ok. Anything else smells like trouble
	 */
	public int subscribe(String hub, String topic_url,String hostname,String verifytoken,String lease_seconds) throws Exception {
		if (topic_url != null) {
			
			String callbackserverurl= hostname + contextPath;
			System.out.println("Callback: " + callbackserverurl);
			System.out.println("TopicURL: " + topic_url);
			HttpPost httppost = new HttpPost(hub);	
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("hub.callback", callbackserverurl));
			nvps.add(new BasicNameValuePair("hub.mode", "subscribe"));
			nvps.add(new BasicNameValuePair("hub.topic", topic_url));
			nvps.add(new BasicNameValuePair("hub.verify", "sync"));
			if (lease_seconds != null)
				nvps.add(new BasicNameValuePair("hub.lease_seconds", lease_seconds));
			//For future https implementation
			//if ((secret !=null) && (secret.getBytes("utf8").length < 200))
			//	nvps.add(new BasicNameValuePair("hub.hub.secret", secret));
			if (verifytoken !=null)
				nvps.add(new BasicNameValuePair("hub.verify_token", verifytoken));
					
			webserver.addAction("subscribe",topic_url, verifytoken);
				
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httppost.setHeader("User-agent", "RSS pubsubhubbub 0.3");

			//create the thread and start it running
			GetThread thread = new GetThread(httpClient, httppost);
			thread.start();
			thread.join();

			if (thread.httpresponse != null){
				return thread.httpresponse.getStatusLine().getStatusCode();
			} else {
				return 400;
			}
		}
		return 400;
	}
	
	/*
	 * @throws IOException If an input or output exception occurred
	 * 
	 * @param The Hub address you want to unpublish it to
	 * 
	 * @param The topic_url you want to unpublish
	 * 
	 * @return HTTP Response code. 200 is ok. Anything else smells like trouble
	 */
	public int unsubscribe(String hub, String topic_url,String hostname,String verifytoken) throws Exception {
		if (topic_url != null) {
				
			String callbackserverurl= hostname + contextPath;
			
			HttpPost httppost = new HttpPost(hub);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("hub.callback", callbackserverurl));
			nvps.add(new BasicNameValuePair("hub.mode", "unsubscribe"));
			nvps.add(new BasicNameValuePair("hub.topic", topic_url));
			nvps.add(new BasicNameValuePair("hub.verify", "sync"));
			//For future https implementation
			//if ((secret !=null) && (secret.getBytes("utf8").length < 200))
			//	nvps.add(new BasicNameValuePair("hub.hub.secret", secret));
			if (verifytoken !=null)
				nvps.add(new BasicNameValuePair("hub.verify_token", verifytoken));
							
			webserver.addAction("unsubscribe",topic_url,verifytoken);

			httppost.setEntity(new UrlEncodedFormEntity(nvps));

			httppost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httppost.setHeader("User-agent", "RSS pubsubhubbub 0.3");

			//create the thread and start it running
			GetThread thread = new GetThread(httpClient, httppost);
			thread.start();
			thread.join();

			if (thread.httpresponse != null){
				return thread.httpresponse.getStatusLine().getStatusCode();
			} else {
				return 400;
			}
		}
		return 400;
	}
}
