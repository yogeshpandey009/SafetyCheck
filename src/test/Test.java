package test;

import java.io.IOException;
import java.net.InetAddress;

import com.google.pubsubhubbub.Subscriber;
import com.google.pubsubhubbub.Web;


public class Test {

	private static Web webserver;
	private static Subscriber sbcbr;
	private static String hostname = null;
	private static Integer webserverPort = 8181;

    private static void startServer(){
    	try {
    		webserver = new Web(webserverPort);
			
			sbcbr = new Subscriber(webserver);
			
			InetAddress addr = InetAddress.getLocalHost(); 
			hostname = addr.getHostName();
			hostname = "http://" + hostname + ":" + Integer.toString(webserverPort) + "/";
		   
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("WebServer can not start");
		}

    }

	public static void main(String[] args) {
		try {
			
			   //String hub = "http://myhub.example.com/endpoint";
			   //String hub_topic = "http://publisher.example.com/topic.xml";
			   String hub = "http://pubsubhubbub.appspot.com";
			   String hub_topic = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom";
			   
			   startServer();

			   int statusCode = sbcbr.subscribe(hub, hub_topic, hostname, null, null);
			   
			   if (statusCode == 204){
				   System.out.println("the status code of the subscription is 204: the request was verified and that the subscription is active");
			   } else if (statusCode == 202){
				   System.out.println("the status code of the subscription is 202: the subscription has yet to be verified (i.e., the hub is using asynchronous verification)");
			   } else{
				   System.out.println("the status code of the subscription is:" + statusCode);   
			   }
			   
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
