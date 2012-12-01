/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.fujitsu.fgcp.filters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.TimeStamp;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.http.utils.Queries;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Credential;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;

/**
 * Generates and signs the access key id and adds the mandatory http header and
 * request parameters to the request.
 * 
 * @author Dies Koper
 */
@Singleton
public class RequestAuthenticator implements HttpRequestFilter, RequestSigner {

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;

   final Provider<Calendar> calendarProvider;
   final Signature signer;
   final Provider<UriBuilder> builder;
   final String apiVersion;

   public String signatureVersion = "1.0";
   public String signatureMethod = "SHA1withRSA";

   private HttpUtils utils;

   @Inject
   public RequestAuthenticator(@TimeStamp Provider<Calendar> calendarProvider,
         Provider<KeyStore> keyStoreProvider,
         @Credential String keyPassword, Provider<UriBuilder> builder,
         HttpUtils utils, SignatureWire signatureWire,
         @ApiVersion String apiVersion) throws NoSuchAlgorithmException,
         InvalidKeyException, KeyStoreException, UnrecoverableKeyException {
      this.calendarProvider = checkNotNull(calendarProvider);
      this.builder = checkNotNull(builder);
      this.utils = checkNotNull(utils, "utils");
      this.apiVersion = checkNotNull(apiVersion, "apiVersion");

      signer = Signature.getInstance(signatureMethod);

      KeyStore keyStore = checkNotNull(keyStoreProvider).get();
      String alias = keyStore.aliases().nextElement(); // there should be only
                                           // one private key
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias,
            keyPassword.toCharArray());

      signer.initSign(privateKey);
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request, "request must be present");

      utils.logRequest(signatureLog, request, ">>");

      // create accesskeyid
      String accessKeyId = createStringToSign(request);
      String signature = sign(accessKeyId);

      // leaving this in for now for reference in case I need it for multipart
      // POSTs
      // add parameters. Note that in case of a GET, url escaping is required
      /*
       * Multimap<String, String> decodedParams = null; if
       * (HttpMethod.GET.equals(request.getMethod())) { decodedParams =
       * ModifyRequest.parseQueryToMap(request.getEndpoint().getRawQuery());
       * 
       * } else if (HttpMethod.POST.equals(request.getMethod())) {
       * decodedParams =
       * ModifyRequest.parseQueryToMap(request.getPayload().getRawContent()
       * .toString()); }
       * 
       * checkNotNull(decodedParams, "no query params found");
       * System.out.println("filter: request params before: " +
       * decodedParams.toString()); addAuthenticationParams(decodedParams,
       * accessKeyId, signature); addLocaleParam(decodedParams);
       * System.out.println("filter: request params after : " +
       * decodedParams.toString()); if (signatureWire.enabled())
       * signatureWire.output(decodedParams);
       * 
       * 
       * request = setPayload(request, decodedParams);
       */
      // only "en" and "ja" are allowed
      String lang = Locale.JAPANESE.getLanguage().equals(
            Locale.getDefault().getLanguage()) ? Locale.JAPANESE
            .getLanguage() : Locale.ENGLISH.getLanguage();

      if (HttpMethod.GET.equals(request.getMethod())) {
         Multimap<String, String> decodedParams = Queries
               .parseQueryToMap(request.getEndpoint().getRawQuery());

         if (!decodedParams.containsKey(RequestParameters.VERSION)) {
            decodedParams.put(RequestParameters.VERSION, apiVersion);
         }
         decodedParams.put(RequestParameters.LOCALE, lang);
         decodedParams.put(RequestParameters.ACCESS_KEY_ID, accessKeyId);
         decodedParams.put(RequestParameters.SIGNATURE, signature);
         request = request.toBuilder().replaceQueryParams(decodedParams)
               .build();
      } else {

         String payload = request.getPayload().getRawContent().toString();
         payload = createXmlElementWithValue(payload,
               RequestParameters.VERSION, apiVersion);
         payload = createXmlElementWithValue(payload,
               RequestParameters.LOCALE, lang);
         payload = createXmlElementWithValue(payload,
               RequestParameters.ACCESS_KEY_ID, accessKeyId);
         payload = createXmlElementWithValue(payload,
               RequestParameters.SIGNATURE, signature);

         // ensure there are no other query params left
         request.setPayload(payload);
         request.getPayload().getContentMetadata()
               .setContentType(MediaType.TEXT_XML);
      }

      // may need to do this elsewhere (see ConvertToGaeRequest)
      HttpRequest filteredRequest = request.toBuilder()
            .replaceHeader(HttpHeaders.USER_AGENT, "OViSS-API-CLIENT")
            .build();

      utils.logRequest(signatureLog, filteredRequest, ">>->");

      return filteredRequest;
   }

   String createXmlElementWithValue(String payload, String tag, String value) {
      String startTag = String.format("<%s>", tag);
      String endTag = String.format("</%s>", tag);

      return payload.replace(startTag + endTag, startTag + value + endTag);
   }

   /*
    * HttpRequest setPayload(HttpRequest request, Multimap<String, String>
    * decodedParams) {
    * request.setPayload(ModifyRequest.makeQueryLine(decodedParams, null)); //
    * request.getPayload().getContentMetadata().setContentType(
    * "application/x-www-form-urlencoded"); return request; }
    */

   @VisibleForTesting
   public String sign(String stringToSign) {
      String signed;

      try {
         signer.update(stringToSign.getBytes(Charsets.UTF_8));
         signed = CryptoStreams.base64(signer.sign());
      } catch (SignatureException e) {
         throw new HttpException("error signing request", e);
      }
      // if (signatureWire.enabled())
      // signatureWire.input(Strings2.toInputStream(signed));

      return signed;
   }

   @VisibleForTesting
   public String generateAccessKeyId() {
      Calendar cal = calendarProvider.get();
      String timezone = cal.getTimeZone().getDisplayName(Locale.ENGLISH);
      String expires = String.valueOf(cal.getTime().getTime());

      String signatureData = String.format("%s&%s&%s&%s", timezone, expires,
            signatureVersion, signatureMethod);
      String accessKeyId = CryptoStreams.base64(signatureData.getBytes(Charsets.UTF_8));
      return accessKeyId.replace("\n", "\r\n");
   }

   @Override
   public String createStringToSign(HttpRequest input) {
      return generateAccessKeyId();
   }

}
