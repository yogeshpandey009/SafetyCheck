package com.semantic.safetycheck.facebook;

public class FacebookException extends RuntimeException
{
   ErrorType _errorType = ErrorType.Unknown;
   
   enum ErrorType
   {
      Unknown,
      ServiceNotAvailable,
      MaximumRequestsReached,
      RemoteHostNotAllowed,
      ParameterMissingOrInvalid,
      APIKeyInvalid,
      IncorrectSignature,
      SessionExpired,
      SessionKeyInvalid,
   }
   
   public FacebookException(String message, Throwable t)
   {
      super(message, t);
   }
   
   public FacebookException(String message)
   {
      super(message);
   }
   
   public FacebookException(Throwable t)
   {
      super(t);
   }
   
   public FacebookException(String message, Throwable t, ErrorType e)
   {
      super(message, t);
      _errorType = e;
   }
   
   public FacebookException(String message, ErrorType e)
   {
      super(message);
      _errorType = e;
   }
   
   public FacebookException(Throwable t, ErrorType e)
   {
      super(t);
      _errorType = e;
   }
   
   public FacebookException(FacebookError e)
   {
      super(String.format(
         "Facebook REST Web Service returned an error code: %1$s message:%2$s",
         e.getErrorCode(), e.getErrorMessage()));
      
      if("1".equals(e.getErrorCode()))
      {
          _errorType = ErrorType.Unknown;
      }
      else if("2".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.ServiceNotAvailable;
      }
      else if("4".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.MaximumRequestsReached;
      }
      else if("5".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.RemoteHostNotAllowed;
      }
      else if("100".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.ParameterMissingOrInvalid;
      }
      else if("101".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.APIKeyInvalid;
      }
      else if("102".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.SessionKeyInvalid;
      }
      else if("104".equals(e.getErrorCode()))
      {
         _errorType = ErrorType.IncorrectSignature;
      }
   }
}
