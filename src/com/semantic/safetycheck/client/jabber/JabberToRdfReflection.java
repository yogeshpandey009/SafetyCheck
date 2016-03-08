package com.semantic.safetycheck.client.jabber;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;



import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.semantic.javaobjectstordf.JavaObjectRdfSerializer;

public class JabberToRdfReflection
{
   private static JabberToRdfReflection _me;

   /**
    * Retrieves an RDF representation of the list of friends for
    * the provided username and password on the provided server
    * @param server The Jabber server to connect to (e.g. gmail.com)
    * @param username The username of the profile to connect to
    * @param password The password corresponding to the username
    * @return The stream to write results to
    */
   public InputStream retrieveFriends(String server, String username,
      String password)
   {
      InputStream toReturn = null;
      XMPPConnection connection = null;
      try
      {
         //create a connection
         connection = new XMPPConnection(server);

         //connect and log in
         connection.connect();
         connection.login(username, password);

         //get the contact list
         Roster roster = connection.getRoster();
         roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
         Collection<RosterEntry> entries = roster.getEntries();

         try
         {
            /**
             * sleep because the roster loads asynchronously
             * Admitted hack; purely for illustrative purposes
             */
            Thread.sleep(5000);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }

         Collection<Object> objects = new LinkedList<Object>();
         for(RosterEntry entry : entries)
         {
        	 objects.add(entry);
         }
         
         JavaObjectRdfSerializer serializer = new JavaObjectRdfSerializer(
                 "http://www.jabber.org/entries#", 
                 "http://www.jabber.org/ontology#", 
                 "TURTLE", 
                 "org.jivesoftware.smack");
           
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         serializer.serialize(objects, outputStream);
         outputStream.flush();

         System.out.println(outputStream.toString());
         toReturn = new ByteArrayInputStream(outputStream.toByteArray());
      }
      catch(IOException e)
      {
    	  e.printStackTrace();
      }
      catch (XMPPException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if(null != connection)
         {
            connection.disconnect();
         }
      }
      return toReturn;
   }

   /**
    * This program connects to a jabber server with the 
    * provided username and password and retrieves a list
    * of the user's contacts, converting it to RDF and writing
    * it to the output file provided
    * @param args 
    *   jabber server - the server name of the jabber service
    *   username - the username of the account to which to connect
    *   password - the password for the account
    *   output file - the filename to write the results to
    */
   public static void main(String[] args)
   {
      //perform argument validation
      if(args.length < 4)
      {
         System.err.println("Usage: java JabberToRdf"
            + "[jabber server] [username] [password] [output file]");
         return;
      }

      String jabberServer = args[0];
      String username = args[1];
      String password = args[2];
      String outputFile = args[3];

      _me = new JabberToRdfReflection();

      PrintWriter writer = null;
      BufferedReader reader = null;
      InputStream turtle;

      try
      {
         String oneLine;
         writer = new PrintWriter(new FileOutputStream(outputFile));
         turtle = _me.retrieveFriends(jabberServer, username, password);

         if(null != turtle)
         {
            reader = new BufferedReader(new InputStreamReader(turtle));
            while (null != (oneLine = reader.readLine()))
            {
               writer.println(oneLine);
            }
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if(null != reader)
            {
               reader.close();
            }
            if(null != writer)
            {
               writer.close();
            }
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Get the String representation of the Presence Mode
    * @param mode the presence mode
    * @return the String representation
    */
   public String getMode(Presence.Mode mode)
   {
      String msg = null;

      if(Presence.Mode.available.equals(mode))
      {
         msg = "Available";
      }
      else if(Presence.Mode.away.equals(mode))
      {
         msg = "Away";
      }
      else if(Presence.Mode.chat.equals(mode))
      {
         msg = "Ready";
      }
      else if(Presence.Mode.dnd.equals(mode))
      {
         msg = "DoNotDisturb";
      }

      return msg;
   }

   /**
    * Get the String representation of the Presence type.
    * @param type The presence type
    * @return string representation of the presence
    */
   public String getType(Presence.Type type)
   {
      String msg = null;

      if(Presence.Type.available.equals(type))
      {
         msg = "Available";
      }
      else if(Presence.Type.unavailable.equals(type))
      {
         msg = "Unavailable";
      }

      return msg;
   }
}
