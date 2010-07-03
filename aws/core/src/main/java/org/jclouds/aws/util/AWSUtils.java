/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
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

   @Inject
   AWSUtils(RequestSigner signer, Factory factory, Provider<ErrorHandler> errorHandlerProvider) {
      this.signer = signer;
      this.factory = factory;
      this.errorHandlerProvider = errorHandlerProvider;
   }

   public AWSError parseAWSErrorFromContent(HttpRequest request, HttpResponse response,
            InputStream content) {
      AWSError error = (AWSError) factory.create(errorHandlerProvider.get()).parse(content);
      if ("SignatureDoesNotMatch".equals(error.getCode())) {
         error.setStringSigned(signer.createStringToSign(request));
         error.setSignature(signer.sign(error.getStringSigned()));
      }
      return error;
   }

   public AWSError parseAWSErrorFromContent(HttpRequest request, HttpResponse response,
            String content) {
      return parseAWSErrorFromContent(request, response, new ByteArrayInputStream(content
               .getBytes()));
   }
}