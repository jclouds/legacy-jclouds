/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.util;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RequestSigner;

/**
 * Needed to sign and verify requests and responses.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSUtils {
   private final RequestSigner signer;
   private final ParseSax.Factory factory;
   private final Provider<ErrorHandler> errorHandlerProvider;
   private final String requestId;
   private final String requestToken;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   AWSUtils(@Named(PROPERTY_HEADER_TAG) String headerTag, RequestSigner signer, Factory factory,
            Provider<ErrorHandler> errorHandlerProvider) {
      this.signer = signer;
      this.factory = factory;
      this.errorHandlerProvider = errorHandlerProvider;
      this.requestId = String.format("x-%s-request-id", headerTag);
      this.requestToken = String.format("x-%s-id-2", headerTag);
   }

   public AWSError parseAWSErrorFromContent(HttpRequest request, HttpResponse response) {
      // HEAD has no content
      if (response.getPayload() == null)
         return null;
      // Eucalyptus and Walrus occasionally return text/plain
      if (response.getPayload().getContentType() != null
               && response.getPayload().getContentType().indexOf("text") != -1)
         return null;
      try {
         AWSError error = (AWSError) factory.create(errorHandlerProvider.get()).setContext(request)
                  .apply(response);
         if (error.getRequestId() == null)
            error.setRequestId(response.getFirstHeaderOrNull(requestId));
         error.setRequestToken(response.getFirstHeaderOrNull(requestToken));
         if ("SignatureDoesNotMatch".equals(error.getCode())) {
            error.setStringSigned(signer.createStringToSign(request));
            error.setSignature(signer.sign(error.getStringSigned()));
         }
         return error;
      } catch (HttpException e) {
         logger.warn(e, "error parsing error");
         return null;
      }
   }
}