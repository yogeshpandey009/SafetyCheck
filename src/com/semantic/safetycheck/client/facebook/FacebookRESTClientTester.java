package com.semantic.safetycheck.client.facebook;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class FacebookRESTClientTester
{
   private static final String SECRET = "debf70c0309cbded364447a176ec45fa";
   private static final String APIKEY = "2ad234ca44ecb66fe5648c0a15b01ae4";
   
//   private static final String SECRET = "c49cd05234c66b1a2c52eb32c6724c21";
//   private static final String APIKEY = "8c6def0daceda379e4fa115a9a39ca6f";
   
   private static final String FACEBOOK_WS_URL = "http://api.facebook.com/restserver.php";
   private static final String FACEBOOK_LOGIN_URL = "http://api.facebook.com/login.php";
   private static final String FIREFOX_LOCATION = "/Applications/Firefox.app";
//   private static final String FIREFOX_LOCATION = "\"C:\\Program Files\\Mozilla Firefox\\firefox.exe\"";
   private static final String XSL_LOCATION = "file:conf/facebook.xsl";
//   private static final String XSL_LOCATION = "file:conf/facebook-mapped.xsl";
   
   private static final String S_FIELDS_LIST = 
      "about_me,activities,affiliations,birthday,books,"
    + "current_location,education_history,first_name,hometown_location,hs_info,"
    + "interests,locale,meeting_for,meeting_sex,movies,music,name,notes_count,"
    + "pic,pic_big,pic_small,pic_square,political,profile_update_time,quotes,"
    + "relationship_status,religion,sex,significant_other_id,status,timezone,"
    + "tv,wall_count,work_history";
   
   public static void main(String[] args) throws IOException
   {
      InputStream in = System.in;
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      FacebookRESTClient client = null;
      FacebookSession session = null;
      String authToken = null;
      
      try
      {
         client = new FacebookRESTClient(
            APIKEY, SECRET, 
            new URL(FACEBOOK_WS_URL),
            new URL(FACEBOOK_LOGIN_URL),
            new URL(XSL_LOCATION));
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }

      String inLine = "";
      do
      {
         try
         {
            if (inLine.toLowerCase().equals("authenticate"))
            {
               System.out.println("Authenticating");
               
               authToken = client.getAuthToken();
               URL loginURL = client.getLoginUrl(authToken);
               System.out.println("Authentication URL (Paste to browser to log in):");
               System.out.println(loginURL.toString());
               
               Runtime.getRuntime().exec(
                  String.format("%1$s %2$s", 
                     FIREFOX_LOCATION, loginURL.toString()));
               
            }
            else if(inLine.toLowerCase().equals("listfriends-xml"))
            {
            	System.out.println("Listing friends XML");
                
                //get a new session if the current one is null or expired
                if(null == session 
                   || session.getExpiration() < System.currentTimeMillis() + 5000)
                {
                   session = client.createSession(authToken);
                }
                
                //list friends
                Collection<String> uids = client.getFriendsList(session);
                
                //then make a call to get EVERYTHING!!!
                StringBuilder builder = new StringBuilder();
                boolean ftt = true;
                for(String uid : uids)
                {
                   builder.append(((ftt)? "" : ",") + uid);
                   ftt = false;
                }

                String uid = builder.toString();
                String field = S_FIELDS_LIST;
                
                InputStream inStream = 
                   client.getPopulatedFriendsXml(session, uid, field);
                
                BufferedReader r = 
                   new BufferedReader(new InputStreamReader(inStream));
                
                OutputStream out = new FileOutputStream("./output.xml");
                PrintStream writer = new PrintStream(out);
                String cl = null;
                while(null != (cl = r.readLine()))
                {
                   writer.println(cl);
                }
                writer.close();
              
            }
            else if(inLine.toLowerCase().equals("listfriends-rdf"))
            {
               System.out.println("Listing friends");
                              
               //get a new session if the current one is null or expired
               if(null == session 
                  || session.getExpiration() < System.currentTimeMillis() + 5000)
               {
                  session = client.createSession(authToken);
               }
               
               //list friends
               Collection<String> uids = client.getFriendsList(session);
               
               //then make a call to get EVERYTHING!!!
               StringBuilder builder = new StringBuilder();
               boolean ftt = true;
               for(String uid : uids)
               {
                  builder.append(((ftt)? "" : ",") + uid);
                  ftt = false;
               }

               String uid = builder.toString();
               String field = S_FIELDS_LIST;
               
               InputStream inStream = 
                  client.getPopulatedFriendsRdf(session, uid, field, "TURTLE");
               
               BufferedReader r = 
                  new BufferedReader(new InputStreamReader(inStream));
               
               OutputStream out = new FileOutputStream("./output.ttl");
               PrintStream writer = new PrintStream(out);
               String cl = null;
               while(null != (cl = r.readLine()))
               {
                  writer.println(cl);
               }
               writer.close();
               
            }
            else
            {
               printCommands(System.out);
            }
         }
         catch (FacebookException e)
         {
            e.printStackTrace();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         
         try
         {
            System.out.print(">");
            inLine = reader.readLine();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         
      } while (inLine != null && !inLine.equals("exit"));
      
   }  
   
   public static void printCommands(OutputStream out)
   {
      PrintStream writer = new PrintStream(out);
      writer.println("COMMANDS");
      writer.println(" authenticate");
      writer.println(" listfriends-xml");
      writer.println(" listfriends-rdf");
      writer.println(" exit");

   }
}
