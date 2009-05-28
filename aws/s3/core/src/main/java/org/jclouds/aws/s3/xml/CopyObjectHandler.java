/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.xml;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;

/**
 * Parses the response from Amazon S3 COPY Object command.
 * <p/>
 * CopyObjectResult is the document we expect to parse.
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTObjectCOPY.html" />
 * @author Adrian Cole
 */
public class CopyObjectHandler extends ParseSax.HandlerWithResult<S3Object.Metadata> {

   private S3Object.Metadata metadata;
   private StringBuilder currentText = new StringBuilder();
   @Inject
   private DateService dateParser;

   public void setKey(String key) {
      metadata = new S3Object.Metadata(key);
   }

   public S3Object.Metadata getResult() {
      return metadata;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ETag")) {
         metadata.setMd5(S3Utils.fromHexString(currentText.toString().replaceAll("\"", "")));
      } else if (qName.equals("LastModified")) {
         metadata.setLastModified(dateParser.iso8601DateParse(currentText.toString()));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
