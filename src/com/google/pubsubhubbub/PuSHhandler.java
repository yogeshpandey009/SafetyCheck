package com.google.pubsubhubbub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//The Jetty webserver version used for this project is 7.0.1.v20091125
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

//Rome libraries (https://rome.dev.java.net/)
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class PuSHhandler extends AbstractHandler 
{	 
	enum MessageStatus { ERROR, OK_Challenge, OK };
	
	public PuSHhandler() {
	}
    
	private void processFeeds(String hubtopic, SyndFeed feed){
		
		//checks if the channel is already created
		System.out.println("processFeed: " + feed.getTitle());
		//NOTE! This code should be changed for your case//
	    
	}
	
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response)
			throws IOException, ServletException 
	{
		
		String hubmode = null, hubtopic = null, hubchallenge = null, hubverify = null, hublease = null;
		ArrayList<String> approvedActions;
		MessageStatus stsMessage = MessageStatus.ERROR;
	        
		if (request != null){ 
			System.out.println("Request from: " + request.getRemoteAddr());
			//verification if the method is a GET or a POST
			if (request.getMethod().equals("GET")){

				if (request.getParameter("hub.mode") != null) {
					hubmode = request.getParameter("hub.mode");
				}
				if (request.getParameter("hub.topic") != null) {
					hubtopic = request.getParameter("hub.topic");
				}
				if (request.getParameter("hub.challenge") != null) {
					hubchallenge = request.getParameter("hub.challenge");
				}
				if (request.getParameter("hub.verify_token") != null) {
					hubverify = request.getParameter("hub.verify_token");
				}
				if (request.getParameter("hub.lease_seconds") != null) {
					hublease = request.getParameter("hub.lease_seconds");
				}
			
				if (((hubmode.equals("subscribe")) || (hubmode.equals("unsubscribe")))
						&& (hubmode.length() > 0) && (hubtopic.length() > 0) && (hubchallenge.length() > 0)) {
					
						approvedActions = Web.getApprovedActions();
							
						for (String action : approvedActions){
							
							if (action.startsWith(hubmode + ":" + hubtopic + ":"
									+ hubverify)) {
								
								stsMessage = MessageStatus.OK_Challenge;
								approvedActions.remove(action);
								break;
							}
						}	
						
						//NOTE! This code should be changed for your case//
						Object channel = null;//NOTE! here you should check if the channel already exist in your subscriber. 
						
					    if (hubmode.equals("subscribe")){
					    	
					    	if (channel != null){
					    		//stsMessage=MessageStatus.ERROR;
					    	} else {
					    		if (stsMessage.equals(MessageStatus.OK_Challenge)){
					    			//create a new channel 
					    		}else {
					    			//Subscription Refreshing 
					    			stsMessage = MessageStatus.OK_Challenge;
					    		}
					    	}
					    	
					    } else if (hubmode.equals("unsubscribe")){
					    	
					    	if (channel != null){
					    		stsMessage = MessageStatus.OK_Challenge;
					    	} else {
					    		if (stsMessage.equals(MessageStatus.OK_Challenge)){
					    			//remove subscription channel
					    		}else {
					    			//stsMessage=MessageStatus.ERROR;
					    		}
					    	}
					    }
					}
			//feeds received from the hub		
			} else if (request.getMethod().equals("POST")) {
				
				//receive an atom or an RSS feed
				if (request.getContentType().contains("application/atom+xml") || request.getContentType().contains("application/rss+xml")){
					
					//read the body data and convert it to an InputStream
					InputStream in= request.getInputStream();
					
					//create the new SyndFeed object
					try {
						SyndFeedInput input = new SyndFeedInput();
			            SyndFeed feed = input.build(new XmlReader(in));
			              		
			            List<SyndLinkImpl> linkList = feed.getLinks();
			            
			            for (SyndLinkImpl link : linkList) {
			            	
			            	if (link.getRel().equals("self")){
			            		hubtopic = link.getHref().toString();
			                }
			            }
			            
			            if (hubtopic == null){
			            	hubtopic= feed.getUri();
			            }
			            //check if the channel exist. If so, add feeds to the channel
						processFeeds(hubtopic, feed);
					} catch (FeedException e) {
						e.printStackTrace();
					}
					stsMessage = MessageStatus.OK;
				} 			
			}
			
			//Send message to the HUB
			response.setContentType("application/x-www-form-urlencoded");
			
			switch(stsMessage) {
				case OK:
					response.setStatus(HttpServletResponse.SC_OK);
				    break;
				case OK_Challenge:
					response.setStatus(HttpServletResponse.SC_OK);
				 	response.getWriter().print(hubchallenge);
				    break;
				default:
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				    break;
			}
			  
			baseRequest.setHandled(true);
		}	
	}
}	