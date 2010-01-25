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

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;

/**
 * Needed to sign and verify requests and responses.
 * 
 * @author Adrian Cole
 */
public class AWSUtils {

   @Inject
   RequestSigner signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            InputStream content) {
      AWSError error = (AWSError) factory.create(errorHandlerProvider.get()).parse(content);
      if ("SignatureDoesNotMatch".equals(error.getCode())) {
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
         error.setSignature(signer.signString(error.getStringSigned()));
      }
      return error;
   }

   public AWSError parseAWSErrorFromContent(HttpCommand command, HttpResponse response,
            String content) {
      return parseAWSErrorFromContent(command, response, new ByteArrayInputStream(content
               .getBytes()));
   }
}