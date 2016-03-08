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

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

public class JabberToRdf
{
   private static JabberToRdf _me;

   /**
    * Retrieves an RDF representation of the list of friends for
    * the provided username and password on the provided server
    * @param server The Jabber server to connect to (e.g. gmail.com)
    * @param username The username of the profile to connect to
    * @param password The password corresponding to the username
    * @return The stream to write results to
    */
   public InputStream retrieveFriends(String server, String username,
      String password) throws XMPPException
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

         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         //create a turtle writer
         TurtleWriter writer = TurtleWriter.createTurtleWriter(baos);

         //add the prefixes for this document
         writer.addPrefix("j", "http://www.jabber.org/ontology#");
         writer.addPrefix("", "http://www.jabber.org/data#");

         for(RosterEntry entry : entries)
         {
            //open the individual
            writer.openIndividual("", entry.getUser(), "j", "Contact");
            writer.addLiteral("j", "user", entry.getUser(), "xsd:string");
            //write their name if they have one
            if(null != entry.getName())
            {
               writer
                  .addLiteral("rdfs", "label", entry.getName(), "xsd:string");
               writer.addLiteral("j", "name", entry.getName(), "xsd:string");
            }
            

            //write their presence state
            Presence p = roster.getPresence(entry.getUser());
            String type = getType(p.getType());
            String mode = getMode(p.getMode());
            String status = p.getStatus();

            if(null != type)
            {
               writer.addReference("j", "presenceType", "j", type);
            }
            if(null != mode)
            {
               writer.addReference("j", "presenceMode", "j", mode);
            }
            if(null != status)
            {
               writer.addLiteral("j", "status", status, "xsd:string");
            }

            writer.closeIndividual();
         }

         writer.close();

         System.out.println(baos.toString());
         toReturn = new ByteArrayInputStream(baos.toByteArray());
      }
      finally
      {
         if(null != connection)
         {
        	 try
        	 {
        		 connection.disconnect();
        	 }
        	 catch(Exception e){}
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

      _me = new JabberToRdf();

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
      catch (Exception e)
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
