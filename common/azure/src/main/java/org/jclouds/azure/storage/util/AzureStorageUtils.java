/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azure.storage.util;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.azure.storage.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify Azure Storage requests and
 * responses.
 * 
 * @author Adrian Cole
 */
public class AzureStorageUtils {

   @Inject
   SharedKeyLiteAuthentication signer;

   @Inject
   ParseSax.Factory factory;

   @Inject
   Provider<ErrorHandler> errorHandlerProvider;

   public AzureStorageError parseAzureStorageErrorFromContent(HttpCommand command,
            HttpResponse response, InputStream content) throws HttpException {
      AzureStorageError error = factory.create(errorHandlerProvider.get()).parse(content);
      error.setRequestId(response.getFirstHeaderOrNull(AzureStorageHeaders.REQUEST_ID));
      if ("AuthenticationFailed".equals(error.getCode())) {
         error.setStringSigned(signer.createStringToSign(command.getCurrentRequest()));
         error.setSignature(signer.signString(error.getStringSigned()));
      }
      return error;
   }

}
