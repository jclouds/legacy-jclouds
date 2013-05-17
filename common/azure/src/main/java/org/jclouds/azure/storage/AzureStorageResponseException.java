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
package org.jclouds.azure.storage;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * Encapsulates an Error from Azure Storage Services.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingRESTError.html" />
 * @see AzureStorageError
 * @see org.jclouds.aws.handlers.ParseAzureStorageErrorFromXmlContent
 * @author Adrian Cole
 * 
 */
public class AzureStorageResponseException extends HttpResponseException {

   private AzureStorageError error = new AzureStorageError();

   public AzureStorageResponseException(HttpCommand command, HttpResponse response, AzureStorageError error) {
      super(String.format("command %s failed with code %s, error: %s", command.toString(), response
               .getStatusCode(), error.toString()), command, response);
      this.setError(error);

   }

   public AzureStorageResponseException(HttpCommand command, HttpResponse response, AzureStorageError error,
            Throwable cause) {
      super(String.format("command %1$s failed with error: %2$s", command.toString(), error
               .toString()), command, response, cause);
      this.setError(error);

   }

   public AzureStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AzureStorageError error) {
      super(message, command, response);
      this.setError(error);

   }

   public AzureStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AzureStorageError error, Throwable cause) {
      super(message, command, response, cause);
      this.setError(error);

   }

   public void setError(AzureStorageError error) {
      this.error = error;
   }

   public AzureStorageError getError() {
      return error;
   }

}
