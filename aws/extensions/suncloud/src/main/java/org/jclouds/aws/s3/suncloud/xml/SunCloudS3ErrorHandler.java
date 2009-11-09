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
package org.jclouds.aws.s3.suncloud.xml;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.suncloud.domain.SunCloudS3Error;
import org.jclouds.http.functions.ParseSax;

/**
 * Parses the error from the Sun Cloud S3 REST API.
 * 
 * @author Adrian Cole
 */
public class SunCloudS3ErrorHandler extends ParseSax.HandlerWithResult<AWSError> {

   private SunCloudS3Error error = new SunCloudS3Error();
   private StringBuilder currentText = new StringBuilder();

   public AWSError getResult() {
      return error;
   }

   public void endElement(String uri, String name, String qName) {
      // TODO parse the actual error coming back from Sun Cloud
      if (qName.equals("Code")) {
         error.setCode(currentText.toString());
      } else if (qName.equals("Message")) {
         error.setMessage(currentText.toString());
      } else if (qName.equalsIgnoreCase("RequestId")) {
         error.setRequestId(currentText.toString());
      } else if (!qName.equals("Error")) {
         error.getDetails().put(qName, currentText.toString());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
