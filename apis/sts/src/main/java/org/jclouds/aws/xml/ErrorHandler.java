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
package org.jclouds.aws.xml;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.http.functions.ParseSax;

/**
 * Parses the error from the Amazon S3 REST API.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingRESTError.html"
 *      />
 * @author Adrian Cole
 */
public class ErrorHandler extends ParseSax.HandlerWithResult<AWSError> {

   private AWSError error = new AWSError();
   private StringBuilder currentText = new StringBuilder();

   public AWSError getResult() {
      return error;
   }

   public void endElement(String uri, String name, String qName) {

      if (qName.equals("Code")) {
         error.setCode(currentText.toString().trim());
      } else if (qName.equals("Message")) {
         error.setMessage(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("RequestId")) {
         error.setRequestId(currentText.toString().trim());
      } else if (!qName.equals("Error")) {
         error.getDetails().put(qName, currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
