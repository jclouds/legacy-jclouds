package org.jclouds.aws.s3.functions;

import javax.inject.Singleton;
import javax.net.ssl.SSLHandshakeException;

import org.jclouds.http.functions.ReturnFalseOn404;

/**
 * S3 buckets are dns names. When we attempt to resolve them, it could throw a misleading
 * SSLHandshakeException when the bucket isn't found.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnFalseOn404OrSSLHandshakeException extends ReturnFalseOn404 {

   public Boolean apply(Exception from) {
      Boolean returnVal = super.apply(from);
      if (returnVal == null && from instanceof SSLHandshakeException) {
         return false;
      }
      return returnVal;
   }

}
