package com.semantic.safetycheck.client.facebook;

public class FacebookSession
{
   final String _sessionKey;
   final String _sessionSecret;
   final String _uid;
   final long _expiration;
   
   public FacebookSession(String key, String secret, String uid, long expiration)
   {
      _sessionKey = key;
      _sessionSecret = secret;
      _uid = uid;
      _expiration = expiration * 1000;
   }
   
   /**
    * @return Returns the _sessionKey.
    */
   public String getSessionKey()
   {
      return _sessionKey;
   }
   /**
    * @return Returns the _sessionSecret.
    */
   public String getSessionSecret()
   {
      return _sessionSecret;
   }
   public String getUid()
   {
      return _uid;
   }
   public long getExpiration()
   {
      return _expiration;
   }
}
