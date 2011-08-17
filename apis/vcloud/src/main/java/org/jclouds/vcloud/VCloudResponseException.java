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
package org.jclouds.vcloud;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.vcloud.domain.VCloudError;

/**
 * Encapsulates an VCloud Error.
 * 
 * @author Adrian Cole
 * 
 */
public class VCloudResponseException extends HttpResponseException {

   private static final long serialVersionUID = 1L;

   private org.jclouds.vcloud.domain.VCloudError error;

   public VCloudResponseException(HttpCommand command, HttpResponse response, VCloudError error) {
      super(String.format("request %s failed with code %s, error: %s", command.getCurrentRequest().getRequestLine(), response
               .getStatusCode(), error.toString()), command, response);
      this.setError(error);

   }

   public VCloudResponseException(HttpCommand command, HttpResponse response, VCloudError error, Throwable cause) {
      super(String.format("request %1$s failed with error: %2$s", command.getCurrentRequest().getRequestLine(), error
               .toString()), command, response, cause);
      this.setError(error);

   }

   public VCloudResponseException(String message, HttpCommand command, HttpResponse response, VCloudError error) {
      super(message, command, response);
      this.setError(error);

   }

   public VCloudResponseException(String message, HttpCommand command, HttpResponse response, VCloudError error,
            Throwable cause) {
      super(message, command, response, cause);
      this.setError(error);

   }

   public void setError(VCloudError error) {
      this.error = error;
   }

   public VCloudError getError() {
      return error;
   }

}
