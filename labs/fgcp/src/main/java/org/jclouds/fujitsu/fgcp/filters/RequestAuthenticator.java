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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.http.utils.Queries.queryParser;

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
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.Constants;
import org.jclouds.date.TimeStamp;
import org.jclouds.domain.Credentials;
import org.jclouds.fujitsu.fgcp.reference.RequestParameters;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequest.Builder;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.internal.SignatureWire;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;

/**
 * Generates and signs the access key id and adds the mandatory http header and request parameters to the request.
 * 
 * @author Dies Koper
 */
@Singleton
public class RequestAuthenticator implements HttpRequestFilter, RequestSigner {

   @Resource
   @Named(Constants.LOGGER_SIGNATURE)
   private Logger signatureLog = Logger.NULL;
   
   private final Supplier<Credentials> creds;
   private final LoadingCache<Credentials, Signature> signerCache;
   private final Provider<Calendar> calendarProvider;
   private final HttpUtils utils;
   private final String apiVersion;

   private final static String signatureVersion = "1.0";
   private final static String signatureMethod = "SHA1withRSA";

   @Inject
   public RequestAuthenticator(@TimeStamp Provider<Calendar> calendarProvider,
         SignatureForCredentials loader,
         @org.jclouds.location.Provider Supplier<Credentials> creds,
         HttpUtils utils, SignatureWire signatureWire,
         @ApiVersion String apiVersion) {
      this.calendarProvider = checkNotNull(calendarProvider);
      this.creds = checkNotNull(creds, "creds");
      // throw out the signature related to old keys
      this.signerCache = CacheBuilder.newBuilder().maximumSize(2).build(checkNotNull(loader, "loader"));
      this.utils = checkNotNull(utils, "utils");
      this.apiVersion = checkNotNull(apiVersion, "apiVersion");
   }

   /**
    * it is relatively expensive to create a new signing key. cache the relationship between current credentials so that
    * the signer is only recalculated once.
    */
   @VisibleForTesting
   static class SignatureForCredentials extends CacheLoader<Credentials, Signature> {
      private final Supplier<KeyStore> keyStore;

      @Inject
      public SignatureForCredentials(Supplier<KeyStore> keyStore) {
         this.keyStore = checkNotNull(keyStore, "keyStore");
      }

      @Override
      public Signature load(Credentials in) {
         String keyPassword = checkNotNull(in.credential,
               "credential supplier returned null for credential (keyPassword)");
         try {
            Signature signer = Signature.getInstance(signatureMethod);
            KeyStore keyStore = checkNotNull(this.keyStore.get(), "keyStore");
            String alias = keyStore.aliases().nextElement(); // there should be only one private key
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray());
            signer.initSign(privateKey);
            return signer;
         } catch (NoSuchAlgorithmException e) {
            throw propagate(e);
         } catch (KeyStoreException e) {
            throw propagate(e);
         } catch (UnrecoverableKeyException e) {
            throw propagate(e);
         } catch (InvalidKeyException e) {
            throw propagate(e);
         }
      }
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      checkNotNull(request, "request must be present");
      utils.logRequest(signatureLog, request, ">>");

      // create accesskeyid
      String accessKeyId = createStringToSign(request);
      String signature = sign(accessKeyId);

      // only "en" and "ja" are allowed
      String lang = Locale.JAPANESE.getLanguage().equals(Locale.getDefault().getLanguage()) ? Locale.JAPANESE
            .getLanguage() : Locale.ENGLISH.getLanguage();

      if (HttpMethod.GET.equals(request.getMethod())) {
         request = addQueryParamsToRequest(request, accessKeyId, signature,
               lang);
      } else {

         String payload = request.getPayload().getRawContent().toString();
         payload = createXmlElementWithValue(payload, RequestParameters.VERSION, apiVersion);
         payload = createXmlElementWithValue(payload, RequestParameters.LOCALE, lang);
         payload = createXmlElementWithValue(payload, RequestParameters.ACCESS_KEY_ID, accessKeyId);
         payload = createXmlElementWithValue(payload, RequestParameters.SIGNATURE, signature);

         // ensure there are no other query params left
         request.setPayload(payload);
         request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_XML);
      }

      // may need to do this elsewhere (see ConvertToGaeRequest)
      HttpRequest filteredRequest = request.toBuilder().replaceHeader(HttpHeaders.USER_AGENT, "OViSS-API-CLIENT")
            .build();

      utils.logRequest(signatureLog, filteredRequest, ">>->");
      return filteredRequest;
   }

   @VisibleForTesting
   HttpRequest addQueryParamsToRequest(HttpRequest request, String accessKeyId,
         String signature, String lang) {
      // url encode "+" (which comes from base64 encoding) or else it may be
      // converted into a %20 (space) which the API endpoint doesn't
      // expect/accept.
      accessKeyId = accessKeyId.replace("+", "%2B");
      signature = signature.replace("+", "%2B");

      Multimap<String, String> decodedParams = queryParser().apply(
            request.getEndpoint().getRawQuery());
      Builder<?> builder = request.toBuilder()
            .endpoint(request.getEndpoint())
            .method(request.getMethod());
      if (!decodedParams.containsKey("Version")) {
         builder.addQueryParam(RequestParameters.VERSION, apiVersion);
      }
      builder.addQueryParam(RequestParameters.LOCALE, lang)
            .addQueryParam(RequestParameters.ACCESS_KEY_ID, accessKeyId)
            // the addition of another param causes %2B's in prev. params to
            // convert to %20. Needs to be addressed if there are cases where
            // accessKeyId contains %2B's.
            // So signature should be added last:
            .addQueryParam(RequestParameters.SIGNATURE, signature);

      return builder.build();
   }

   String createXmlElementWithValue(String payload, String tag, String value) {
      String startTag = String.format("<%s>", tag);
      String endTag = String.format("</%s>", tag);

      return payload.replace(startTag + endTag, startTag + value + endTag);
   }

   public String sign(String stringToSign) {
      String signed;
      try {
         Signature signer = signerCache.get(checkNotNull(creds.get(), "credential supplier returned null"));
         signer.update(stringToSign.getBytes(UTF_8));
         signed = base64().withSeparator("\n", 61).encode(signer.sign());
      } catch (SignatureException e) {
         throw new HttpException("error signing request", e);
      } catch (ExecutionException e) {
         throw new HttpException("couldn't load key for signing request", e);
      }
      return signed;
   }

   @VisibleForTesting
   String generateAccessKeyId() {
      Calendar cal = calendarProvider.get();
      String timezone = cal.getTimeZone().getDisplayName(Locale.ENGLISH);
      String expires = String.valueOf(cal.getTime().getTime());

      String signatureData = String.format("%s&%s&%s&%s", timezone, expires, signatureVersion, signatureMethod);
      String accessKeyId = base64().withSeparator("\n", 61).encode(
            signatureData.getBytes(UTF_8));

      return accessKeyId;
   }

   @Override
   public String createStringToSign(HttpRequest input) {
      return generateAccessKeyId();
   }

}
