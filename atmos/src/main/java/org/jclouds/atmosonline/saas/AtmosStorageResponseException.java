/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.atmosonline.saas;

import org.jclouds.atmosonline.saas.domain.AtmosStorageError;
import org.jclouds.atmosonline.saas.handlers.ParseAtmosStorageErrorFromXmlContent;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * Encapsulates an Error from Atmos Storage Services.
 * 
 * @see AtmosStorageError
 * @see ParseAtmosStorageErrorFromXmlContent
 * @author Adrian Cole
 * 
 */
public class AtmosStorageResponseException extends HttpResponseException {

   private static final long serialVersionUID = 1L;

   private AtmosStorageError error;

   public AtmosStorageResponseException(HttpCommand command, HttpResponse response,
            AtmosStorageError error) {
      super(String.format("command %s failed with code %s, error: %s", command.getRequest()
               .getRequestLine(), response.getStatusCode(), error.toString()), command, response);
      this.setError(error);

   }

   public AtmosStorageResponseException(HttpCommand command, HttpResponse response,
            AtmosStorageError error, Throwable cause) {
      super(String.format("command %1$s failed with error: %2$s", command.getRequest()
               .getRequestLine(), error.toString()), command, response, cause);
      this.setError(error);

   }

   public AtmosStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AtmosStorageError error) {
      super(message, command, response);
      this.setError(error);

   }

   public AtmosStorageResponseException(String message, HttpCommand command, HttpResponse response,
            AtmosStorageError error, Throwable cause) {
      super(message, command, response, cause);
      this.setError(error);

   }

   public void setError(AtmosStorageError error) {
      this.error = error;
   }

   public AtmosStorageError getError() {
      return error;
   }

}
