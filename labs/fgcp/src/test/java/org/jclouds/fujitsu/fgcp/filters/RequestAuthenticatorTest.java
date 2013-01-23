package org.jclouds.fujitsu.fgcp.filters;

import static org.testng.Assert.assertEquals;

import java.security.KeyStore;
import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Provider;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

public class RequestAuthenticatorTest {

   Provider<Calendar> calendarProvider = new Provider<Calendar>() {
      public Calendar get() {
         Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
         c.setTimeInMillis(1358747939000L); // Mon Jan 21 16:58:59 +1100 2013
         return c;
      }
   };
   Supplier<Credentials> creds = new Supplier<Credentials>() {
      @Override
      public Credentials get() {
         return null;
      }
   };
   Supplier<KeyStore> keystore = new Supplier<KeyStore>() {
      public KeyStore get() {
         return null;
      }
   };
   RequestAuthenticator a = new RequestAuthenticator(calendarProvider,
         new RequestAuthenticator.SignatureForCredentials(keystore), creds,
         new HttpUtils(0, 0, 0, 0), new SignatureWire(), "");

   @Test
   public void testGenerateAccessKeyIdWithNewline() throws Exception {
      String accessKeyId = a.generateAccessKeyId();
      assertEquals(
            accessKeyId,
            "RWFzdGVybiBTdGFuZGFyZCBUaW1lIChOZXcgU291dGggV2FsZXMpJjEzNTg3N\nDc5MzkwMDAmMS4wJlNIQTF3aXRoUlNB");
   }

   @Test
   public void testAddQueryParamsToRequest() throws Exception {
      String accessKeyId = "accessKeyId";
      String signature = "signature";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=accessKeyId&Signature=signature HTTP/1.1");
   }

   @Test
   public void testAddQueryParamsWithRealValuesToRequest() throws Exception {
      String accessKeyId = "RWFzdGVybiBTdGFuZGFyZCBUaW1lIChOZXcgU291dGggV2FsZXMpJjEzNTg4M\nzg4OTgwNTcmMS4wJlNIQTF3aXRoUlNB";
      String signature = "QFAmuZ0XyOjy6fmMLkMCH/xObY6Jhyltjo2hBcUrXHape8ecTmAlbCUO/+lKr\nQ3Qeu1cNqh8BXSnoc4vXR3aezR6V94aBlQ/4uowQuZP3S8yjnC0aPjWQ70JcB\nULR+qSGNmc97agOTMmIl4JJcukCBEEyLSzRDDe2ib2PqN11RA55GmAP/xx7qg\n0fj6ieauzuzImL1tJq03w0tPdCSuB6lnZe/81Z+Rbqwfl3kdGNBnV7YrdD3Qg\nRBDOKgA2okMlc5pzgk59i/O07ScfoJs7A58HnTZZ2KyVPFgHq6YGpCA2PqII6\nHUlqx6hkX9HFXIz+wz52gbSwBrqgloAw8w8Iw==";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=RWFzdGVybiBTdGFuZGFyZCBUaW1lIChOZXcgU291dGggV2FsZXMpJjEzNTg4M%0Azg4OTgwNTcmMS4wJlNIQTF3aXRoUlNB&Signature=QFAmuZ0XyOjy6fmMLkMCH/xObY6Jhyltjo2hBcUrXHape8ecTmAlbCUO/%2BlKr%0AQ3Qeu1cNqh8BXSnoc4vXR3aezR6V94aBlQ/4uowQuZP3S8yjnC0aPjWQ70JcB%0AULR%2BqSGNmc97agOTMmIl4JJcukCBEEyLSzRDDe2ib2PqN11RA55GmAP/xx7qg%0A0fj6ieauzuzImL1tJq03w0tPdCSuB6lnZe/81Z%2BRbqwfl3kdGNBnV7YrdD3Qg%0ARBDOKgA2okMlc5pzgk59i/O07ScfoJs7A58HnTZZ2KyVPFgHq6YGpCA2PqII6%0AHUlqx6hkX9HFXIz%2Bwz52gbSwBrqgloAw8w8Iw%3D%3D HTTP/1.1");
   }

   @Test
   public void testAddQueryParamsWithSlashes() throws Exception {
      String accessKeyId = "accessKeyId";
      String signature = "sig/na/ture";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=accessKeyId&Signature=sig/na/ture HTTP/1.1");
   }

   @Test
   public void testAddQueryParamsWithNewlines() throws Exception {
      String accessKeyId = "accessKey\nId";
      String signature = "sig\nna\nture";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=accessKey%0AId&Signature=sig%0Ana%0Ature HTTP/1.1");
   }

   @Test
   public void testAddQueryParamsWithPlus() throws Exception {
      String accessKeyId = "accessKey+Id";
      String signature = "sign+ature";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            // NOTE: AccessKeyId's "=" becomes a %20 (space) as explained in
            // addQueryParamsToRequest().
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=accessKey%20Id&Signature=sign%2Bature HTTP/1.1");
   }

   @Test
   public void testAddQueryParamsWithBase64Symbols() throws Exception {
      String accessKeyId = "accessKeyId\nWith/And+And=";
      String signature = "signature\nWith/And+And=";
      String lang = "en";

      HttpRequest request = HttpRequest
            .builder()
            .endpoint(
                  "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18")
            .method("GET").build();
      HttpRequest newRequest = a.addQueryParamsToRequest(request, accessKeyId,
            signature, lang);
      assertEquals(
            newRequest.getRequestLine(),
            "GET https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint?Version=2012-02-18&Locale=en&AccessKeyId=accessKeyId%0AWith/And%20And%3D&Signature=signature%0AWith/And%2BAnd%3D HTTP/1.1");
   }
}
