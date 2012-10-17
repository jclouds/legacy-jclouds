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
package org.jclouds.vcloud.director.v1_5.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.io.ByteArrayInputStream;
import javax.inject.Singleton;
import javax.xml.bind.JAXB;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Error;

import com.google.common.base.Throwables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VCloudDirectorErrorHandler implements HttpErrorHandler {

   @Override
   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);

      // Create default exception
      String message = data != null
            ? new String(data)
            : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(), response.getStatusLine());
      Exception exception = new HttpResponseException(command, response, message);
      
      // Try to create a VCloudDirectorException from XML payload, if it exists
      if (response.getPayload() != null && response.getPayload().getContentMetadata().getContentType().startsWith(VCloudDirectorMediaType.ERROR)) {
	      try {
	         Error error = JAXB.unmarshal(new ByteArrayInputStream(data), Error.class);
	         exception = new VCloudDirectorException(error);
	         message = error.getMessage();
	      } catch (Exception e) {
	         Throwables.propagate(e);
	      }
      }

      // Create custom exception for error codes we know about
      if (response.getStatusCode() == 401) {
         exception = new AuthorizationException(message, exception);
      } else if (response.getStatusCode() == 403 || response.getStatusCode() == 404) {
         exception = new ResourceNotFoundException(message, exception);
      }

      command.setException(exception);
   }
}
