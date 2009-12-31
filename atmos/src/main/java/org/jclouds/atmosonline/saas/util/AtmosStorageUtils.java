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
package org.jclouds.atmosonline.saas.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.atmosonline.saas.domain.AtmosStorageError;
import org.jclouds.atmosonline.saas.filters.SignRequest;
import org.jclouds.atmosonline.saas.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify Atmos Storage requests and
 * responses.
 * 
 * @author Adrian Cole
 */
public class AtmosStorageUtils {

   @Inject
   SignRequest signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AtmosStorageError parseAtmosStorageErrorFromContent(HttpCommand command,
            HttpResponse response, InputStream content) throws HttpException {
      AtmosStorageError error = (AtmosStorageError) factory.create(errorHandlerProvider.get())
               .parse(content);
      if (error.getCode() == 1032) {
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
      }
      return error;

   }

   public AtmosStorageError parseAtmosStorageErrorFromContent(HttpCommand command,
            HttpResponse response, String content) throws HttpException {
      return parseAtmosStorageErrorFromContent(command, response, new ByteArrayInputStream(content
               .getBytes()));
   }

}