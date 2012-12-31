package org.jclouds.fallbacks;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.jclouds.date.DateCodec;
import org.jclouds.date.internal.DateServiceDateCodecFactory.DateServiceRfc822Codec;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.RetryAfterException;
import org.testng.annotations.Test;

import com.google.common.base.Ticker;
import com.google.common.net.HttpHeaders;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class HeaderToRetryAfterExceptionTest {

   @Test(expectedExceptions = RuntimeException.class)
   public void testArbitraryExceptionDoesntConvert(){
      fn.create(new RuntimeException());
   }
   
   public void testHttpResponseExceptionWithoutResponseDoesntPropagate(){
      fn.create(new HttpResponseException("message", command, null));
   }

   public void testHttpResponseExceptionWithoutRetryAfterHeaderDoesntPropagate(){
      fn.create(new HttpResponseException(command, HttpResponse.builder().statusCode(500).build()));
   }

   public void testHttpResponseExceptionWithMalformedRetryAfterHeaderDoesntConvert(){
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "Fri, 31 Dec 1999 23:59:59 ZBW").build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry now")
   public void testHttpResponseExceptionWithRetryAfterDate() {
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "Fri, 31 Dec 1999 23:59:59 GMT").build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry in 700 seconds")
   public void testHttpResponseExceptionWithRetryAfterOffset(){
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "700").build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry in 86400 seconds")
   public void testHttpResponseExceptionWithRetryAfterPastIsZero(){
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "Sun, 2 Jan 2000 00:00:00 GMT").build()));
   }

   public static HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://stub").build());

   static DateCodec rfc822 = new DateServiceRfc822Codec(new SimpleDateFormatDateService());
   
   static Ticker y2k = new Ticker(){

      @Override
      public long read() {
         return MILLISECONDS.toNanos(rfc822.toDate("Sat, 1 Jan 2000 00:00:00 GMT").getTime());
      }
      
   };
   
   public static HeaderToRetryAfterException fn = HeaderToRetryAfterException.create(y2k, rfc822);


}
