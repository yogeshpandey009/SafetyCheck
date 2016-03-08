package com.semantic.safetycheck.client.facebook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.semantic.safetycheck.client.facebook.FacebookException.ErrorType;

public class FacebookRESTClient
{
   private static final String S_FBM_AUTH_CREATETOKEN = "facebook.auth.createToken";
   private static final String S_FBM_AUTH_CREATESESSION = "facebook.auth.getSession";
   private static final String S_FBM_FRIENDS_GET = "facebook.friends.get";
   private static final String S_FBM_USERS_GETINFO = "facebook.users.getInfo";
   
   private final String _secret;
   private final String _apiKey;
   private final URL _serverUrl;
   private final URL _loginUrl;
   private final URL _xslFile;

   private DocumentBuilder _documentBuilder = null;
   protected DocumentBuilder getDocumentBuilder()
   {
      if(null == _documentBuilder)
      {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         
         try
         {
            _documentBuilder = dbf.newDocumentBuilder();
         }
         catch (ParserConfigurationException e)
         {
            e.printStackTrace();
         }
      }
      return _documentBuilder;
   }
   
   public FacebookRESTClient(String apiKey, String secret, URL serverUrl, URL loginUrl, URL xslFile)
   {
      _secret = secret;
      _apiKey = apiKey;
      _serverUrl = serverUrl;
      _loginUrl = loginUrl;
      _xslFile = xslFile;
   }

   /**
    * Get the XSL transformed copy of the populated friends XML
    * document
    * @param session The session for this method call
    * @param uids The friends to get data for
    * @param fields The fields to populate in the data
    * @return
    * @throws FacebookException
    */
   public InputStream getPopulatedFriendsRdf(FacebookSession session,
      String uids, String fields, String format)
   throws FacebookException
   {
      InputStream toReturn = null;
      
      try
      {
         InputStream xmlFileInputStream =  
            getPopulatedFriendsXml(session, uids, fields);
         InputStream xslInputStream = _xslFile.openStream();
         ByteArrayOutputStream transformOutputStream = 
            new ByteArrayOutputStream(); 
         
         //apply the XSLT to the XML file to generate the output RDF
         TransformerFactory factory = TransformerFactory.newInstance();
         StreamSource xslSource = new StreamSource(xslInputStream);
         StreamSource xmlSource = new StreamSource(xmlFileInputStream);
         StreamResult outResult = new StreamResult(transformOutputStream);
         
         Transformer transformer = factory.newTransformer(xslSource);
         transformer.transform(xmlSource, outResult);
         transformOutputStream.close();
         
         ByteArrayInputStream iStream = 
            new ByteArrayInputStream(transformOutputStream.toByteArray());
         ByteArrayOutputStream oStream = new ByteArrayOutputStream();
         
         Model model = ModelFactory.createDefaultModel();
         model.read(iStream, null, "RDF/XML");
         model.write(oStream, "TURTLE");
         oStream.flush();
         
         toReturn = new ByteArrayInputStream(oStream.toByteArray());
      }
      catch(IOException e)
      {
         throw new FacebookException(e);
      }
      catch (TransformerConfigurationException e)
      {
         throw new FacebookException(e);
      }
      catch (TransformerException e)
      {
         throw new FacebookException(e);
      }
      
      return toReturn;
   }
   
   /**
    * Get an XML document representing the friends in the uids list
    * with all the fields in the fields field O_o
    * @param session The session
    * @param uids The uids of the friends to return
    * @param fields The fields for which to return info
    * @return XML document with all the info, or a facebook error document
    * @throws FacebookException 
    */
   public InputStream getPopulatedFriendsXml(FacebookSession session, 
      String uids, String fields) 
      throws FacebookException
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("uids", uids);
      params.put("fields", fields);
      
      InputStream inStream = null;
      try
      {
         inStream = apiRequest(S_FBM_USERS_GETINFO, params, session);
      }
      catch(IOException e)
      {
         throw new FacebookException(String.format(
            "Error trying Facebook Web Service method: %1$s ",
            S_FBM_USERS_GETINFO), e);
      }
      
      return inStream;
   }
   
   /**
    * Get the list of friends for this user as an XML document
    * in an <code>InputStream</code>
    * @param session The session that is authenticated for this
    * user
    * @return Collection of uid's for the friends of this user
    * @throws FacebookException 
    */
   public Collection<String> getFriendsList(FacebookSession session)
      throws FacebookException
   {
      Collection<String> friends = new LinkedList<String>();
      Document doc = domApiRequest(
            S_FBM_FRIENDS_GET, 
            new HashMap<String, String>(), session);
      
      Map<String, Collection<String>> pairs = 
         getFullElementMap(doc, "friends_get_response");
      
      if(pairs.containsKey("uid"))
      {
         for(String uid : pairs.get("uid"))
         {
            friends.add(uid);
         }
      }
      
      return friends;
   }
   
   /**
    * Get a new auth_token from the facebook api
    * @param serverUrl The url of the facebook server to hit for the auth_token
    * @return a new auth_token
    * @throws FacebookException
    */
   public String getAuthToken() throws FacebookException
   {
      Document doc = domApiRequest(S_FBM_AUTH_CREATETOKEN, 
         new HashMap<String, String>(), null); 
      
      //try to get the auth_token out of the response document
      String authToken = null;
      NodeList list = doc.getElementsByTagName("auth_createToken_response");
      if(list.getLength() > 0)
      {
         authToken = list.item(0).getTextContent();
      }
       
      if(null == authToken)
      {
         throw new FacebookException(
            String.format(
               "Response document did not contain an authentication token: %1$s",
               doc.getTextContent()));
      }
      return authToken;
   }
   
   /**
    * Get the login url for this authentication
    * @param authToken
    * @return
    */
   public URL getLoginUrl(String authToken)
      throws FacebookException
   {
      String sUrl = String.format(
         "%1$s?api_key=%2$s&auth_token=%3$s",
         _loginUrl.toString(), _apiKey, authToken);
      
      URL url = null;
      try
      {
         url = new URL(sUrl);
      }
      catch(MalformedURLException e)
      {
         throw new FacebookException(
            String.format(
               "Could not generate valid login url: authToken: %1$s, loginUrl: %2$s",
               authToken, _loginUrl.toString()));
      }
      return url;
   }
   
   /**
    * Creates a new session, assumes authentication has already
    * taken place
    * @param authToken The authentication token for the new session
    * @return A new session object
    */
   public FacebookSession createSession(String authToken) 
      throws FacebookException
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("auth_token", authToken);
      Document doc = domApiRequest(S_FBM_AUTH_CREATESESSION, params, null);
      
      //get the session info out of the document
      FacebookSession session = null;
      
      Map<String, String> pairs = 
         getUniqueElementMap(doc,"auth_getSession_response");
      
      String key = null;
      String uid = null;
      String expires = null;
      String secret = null;

      if(pairs.containsKey("session_key"))
      {
         key = pairs.get("session_key");
      }
      if(pairs.containsKey("uid"))
      {
         uid = pairs.get("uid");
      }
      if(pairs.containsKey("expires"))
      {
         expires = pairs.get("expires");
      }
      if(pairs.containsKey("secret"))
      {
         secret = pairs.get("secret");
      }

      if(null != key
         && null != uid
         && null != expires
         && null != secret)
      {
         session = new FacebookSession(
            key, secret, uid, Long.parseLong(expires));
      }
       
      if(null == session)
      {
         throw new FacebookException(
            String.format(
               "Response document did not contain session info: %1$s",
               doc.getTextContent()));
      }
      
      return session;
   }
    
   /**
    * Call the provided url and POST the parameters in the map
    * @param params The parameters for the POST
    * @param url The url to call
    * @return An <code>InputStream</code> with the results of the call
    * @throws IOException
    */
   public InputStream request(Map<String, String> params, URL url)
      throws IOException
   {
      StringBuilder postContent = new StringBuilder();
      for(String k : params.keySet())
      {
         if(postContent.length() > 0)
         {
            postContent.append("&");
         }
         
         postContent.append(String.format("%1$s=%2$s", k, params.get(k)));
      }

      HttpURLConnection conn = 
         (HttpURLConnection)url.openConnection();
      
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.connect();
      conn.getOutputStream().write(postContent.toString().getBytes());

      return conn.getInputStream();
   }
   
   /**
    * Submit a request to the Facebook REST web service, calling
    * the provided method name and sending the provided parameters.
    * @param name The name of the method to call
    * @param params The parameters to POST in the call
    * @param isSession Whether or not this call is part of a session
    * @return The result <code>InputStream<code>
    * @throws IOException
    */
   public InputStream apiRequest(String name, Map<String, String> params,
      FacebookSession session)
      throws IOException
   {
      if(null != session && session.getExpiration() < System.currentTimeMillis() + 2000)
      {
         throw new FacebookException("Session expired.", ErrorType.SessionExpired);
      }
      
      params.put("v", "1.0");
      params.put("method", name);
      params.put("api_key", _apiKey);
      
      if(null != session)
      {
         params.put("session_key", session.getSessionKey());
         params.put("call_id", "" + System.currentTimeMillis());
      }

      params.put("sig", generateSig(params, session));
      
      return request(params, _serverUrl);
   }

   /**
    * Wraps the apiRequest in a <code>Document</code>
    * @param name The name of the api method to call
    * @param params The parameters to send to the api method call
    * @param session The session to use for the call, or null
    * @return The <code>Document</code> containing the response
    */
   public Document domApiRequest(String name, Map<String, String> params, FacebookSession session)
   {
      InputStream inStream = null;
      try
      {
         //make the request to get the auth_token
         inStream = apiRequest(
            name, 
            params, session);
      }
      catch(IOException e)
      {
         throw new FacebookException(
            String.format("Call to Facebook API Method: %1$s failed.", 
               name),e);
      }
      
      Document doc = null;
      try
      {
         //build a DOM model around the response
         doc = getDocumentBuilder().parse(inStream);
         System.out.println(doc.getTextContent());
      }
      catch (SAXException e)
      {
         throw new FacebookException(
            String.format("Failed to parse reponse to API Method: %1$s.", 
               name),e);
      }
      catch (IOException e)
      {
         throw new FacebookException(
            String.format("Failed to read reponse to API Method: %1$s.", 
               name),e);
      }

      FacebookError error = getError(doc);
      if(null != error)
      {
         throw new FacebookException(error);
      }
      
      return doc;
   }

   /**
    * Generate a map of name to value collections for the given element in
    * the provided document
    * @param doc The xml document to search
    * @param element The specific element to search under
    * @return 
    */
   private Map<String, Collection<String>> getFullElementMap(Document doc, String element)
   {
      Map<String, Collection<String>> pairs = new HashMap<String, Collection<String>>();
      
      NodeList list = doc.getElementsByTagName(element);
      if(list.getLength() > 0)
      {
         NodeList childNodes = list.item(0).getChildNodes();
         for(int i = 0; i<childNodes.getLength(); i++)
         {
            Node n = childNodes.item(i);
            String nodeName = n.getNodeName();
            String nodeValue = n.getTextContent();
            
            Collection<String> collection = null;
            if(pairs.containsKey(nodeName))
            {
               collection = pairs.get(nodeName);
            }
            else
            {
               collection = new LinkedList<String>();
               pairs.put(nodeName, collection);
            }
            collection.add(nodeValue);
         }
      }
      
      return pairs;
   }
   
   /**
    * Same as <code>getChildNameValuePairs</code> only this assumes unique
    * names
    * @param doc
    * @param element
    * @return
    */
   private Map<String, String> getUniqueElementMap(Document doc, String element)
   {
      Map<String, String> pairs = new HashMap<String, String>();
      
      NodeList list = doc.getElementsByTagName(element);
      if(list.getLength() > 0)
      {
         NodeList childNodes = list.item(0).getChildNodes();
         for(int i = 0; i<childNodes.getLength(); i++)
         {
            Node n = childNodes.item(i);
            String nodeName = n.getNodeName();
            String nodeValue = n.getTextContent();
            
            if(!pairs.containsKey(nodeName))
            {
               pairs.put(nodeName, nodeValue);
            }
         }
      }
      
      return pairs;
   }
   
   /**
    * Generate the signature for the Facebook REST web service method
    * call. More information on how to properly make this call can be
    * found at http://wiki.developers.facebook.com/index.php/How_Facebook_Authenticates_Your_Application
    * @param params
    * @param isSession
    * @return
    */
   private String generateSig(Map<String, String> params, FacebookSession session)
   {
      String sig = null;
      
      List<String> keys = new LinkedList<String>();
      for(String k : params.keySet())
      {
         keys.add(k);
      }
      Collections.sort(keys);

      StringBuilder builder = new StringBuilder();
      for(String k : keys)
      {
         builder.append(String.format("%1$s=%2$s", k, params.get(k)));
      }
      
      if(null != session)
      {
         builder.append(session.getSessionSecret());
      }
      else
      {
         builder.append(_secret);
      }
      
      try
      {
         java.security.MessageDigest md = 
            java.security.MessageDigest.getInstance("MD5");
         
         md.update(builder.toString().getBytes());
         
         StringBuilder result = new StringBuilder();
         for (byte b : md.digest()) 
         { 
            result.append(Integer.toHexString((b & 0xf0) >>> 4));
            result.append(Integer.toHexString(b & 0x0f));
         }
         sig = result.toString();
      }
      catch (java.security.NoSuchAlgorithmException ex)
      {
         ex.printStackTrace();
      }
      
      return sig;
   }
   
   /**
    * Gets the error message from the document if there is one
    * If there is no error, returns null.
    * @param doc The response xml document to parse for the error
    * @return A <code>FacebookError</code> if there is one, null
    * if there are no errors
    */
   private FacebookError getError(Document doc)
   {
      FacebookError error = null;
      
      Map<String, String> pairs = 
         getUniqueElementMap(doc, "error_response");

      String code = null;
      String msg = null;
      if(pairs.containsKey("error_code"))
      {
         code = pairs.get("error_code");
      }
      if(pairs.containsKey("error_msg"))
      {
         msg = pairs.get("error_msg");
      }
      
      if(null != code && null != msg)
      {
         error = new FacebookError(code, msg);
      }
      
      return error;
   }
   
   /**
    * This is a debug method used to get the full contents of the input stream
    * as a string
    * @param inStream The <code>InputStream<code> to read
    * @return The contents of the stream
    * @throws IOException
    */
//   private String getResponseFromStream(InputStream inStream) throws IOException
//   {
//      InputStreamReader reader = new InputStreamReader(inStream);
//      StringBuilder builder = new StringBuilder();
//      char[] buff = new char[256]; int amt = 0;
//      while(-1 != (amt = reader.read(buff)))
//      {
//         builder.append(buff, 0, amt);
//      }
//      return builder.toString();
//   }
   
}
