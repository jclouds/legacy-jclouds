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
package org.jclouds.aws;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * Encapsulates an AWS Error from Amazon.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingRESTError.html" />
 * @see AWSError
 * @see org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent
 * @author Adrian Cole
 * 
 */
public class AWSResponseException extends HttpResponseException {

   private AWSError error = new AWSError();

   public AWSResponseException(HttpCommand command, HttpResponse response, AWSError error) {
      super(String.format("request %s failed with code %s, error: %s", command.getCurrentRequest().getRequestLine(), response
               .getStatusCode(), error.toString()), command, response);
      this.setError(error);

   }

   public AWSResponseException(HttpCommand command, HttpResponse response, AWSError error,
            Throwable cause) {
      super(String.format("request %1$s failed with error: %2$s", command.getCurrentRequest().getRequestLine(), error
               .toString()), command, response, cause);
      this.setError(error);

   }

   public AWSResponseException(String message, HttpCommand command, HttpResponse response,
            AWSError error) {
      super(message, command, response);
      this.setError(error);

   }

   public AWSResponseException(String message, HttpCommand command, HttpResponse response,
            AWSError error, Throwable cause) {
      super(message, command, response, cause);
      this.setError(error);

   }

   public void setError(AWSError error) {
      this.error = error;
   }

   public AWSError getError() {
      return error;
   }

}
