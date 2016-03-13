package test;

import java.net.URI;

import org.diretto.util.push.NotificationCallback;
import org.diretto.util.push.Subscriber;
import org.diretto.util.push.Subscription;
import org.diretto.util.push.impl.SubscriberImpl;

import com.sun.syndication.feed.synd.SyndFeed;

public class SubscriberTest {

	public static void main(String[] args) {
		
		Subscriber subscriber = new SubscriberImpl("52.32.136.81",8585);
		Subscription subscription = subscriber.subscribe(URI.create("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.atom"));

		subscription.setNotificationCallback(new NotificationCallback() {
			
			@Override
			public void handle(SyndFeed feed) {
				// TODO Auto-generated method stub
				System.out.println(feed.getTitle());
			}
		});
	}
}
