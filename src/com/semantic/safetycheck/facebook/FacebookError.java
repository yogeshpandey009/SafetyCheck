package com.semantic.safetycheck.facebook;

public class FacebookError
{
   String _errorCode;
   String _errorMessage;
   
   public FacebookError(String code, String message)
   {
      _errorCode = code;
      _errorMessage = message;
   }

   /**
    * @return Returns the _errorCode.
    */
   public String getErrorCode()
   {
      return _errorCode;
   }

   /**
    * @param code The _errorCode to set.
    */
   public void setErrorCode(String code)
   {
      _errorCode = code;
   }

   /**
    * @return Returns the _errorMessage.
    */
   public String getErrorMessage()
   {
      return _errorMessage;
   }

   /**
    * @param message The _errorMessage to set.
    */
   public void setErrorMessage(String message)
   {
      _errorMessage = message;
   }
   
   
}
