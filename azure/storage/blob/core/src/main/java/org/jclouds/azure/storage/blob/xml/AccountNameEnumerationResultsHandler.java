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
package org.jclouds.azure.storage.blob.xml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.domain.ArrayBoundedList;
import org.jclouds.azure.storage.domain.BoundedList;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.inject.Inject;

/**
 * Parses the following XML document:
 * <p/>
 * EnumerationResults AccountName="http://myaccount.blob.core.windows.net"
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179352.aspx" />
 * @author Adrian Cole
 */
public class AccountNameEnumerationResultsHandler extends
         ParseSax.HandlerWithResult<BoundedList<ContainerMetadata>> {

   private List<ContainerMetadata> containerMetadata = new ArrayList<ContainerMetadata>();
   private String prefix;
   private String marker;
   private int maxResults;
   private String nextMarker;
   private URI currentUrl;
   private DateTime currentLastModified;
   private byte[] currentETag;

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;

   @Inject
   public AccountNameEnumerationResultsHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   public BoundedList<ContainerMetadata> getResult() {
      return new ArrayBoundedList<ContainerMetadata>(containerMetadata, prefix, marker, maxResults,
               nextMarker);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("MaxResults")) {
         maxResults = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("Marker")) {
         marker = currentText.toString().trim();
         marker = (marker.equals("")) ? null : marker;
      } else if (qName.equals("Prefix")) {
         prefix = currentText.toString().trim();
         prefix = (prefix.equals("")) ? null : prefix;
      } else if (qName.equals("NextMarker")) {
         nextMarker = currentText.toString().trim();
         nextMarker = (nextMarker.equals("")) ? null : nextMarker;
      } else if (qName.equals("Container")) {
         containerMetadata.add(new ContainerMetadata(currentUrl, currentLastModified, currentETag));
         currentUrl = null;
         currentLastModified = null;
         currentETag = null;
      } else if (qName.equals("Url")) {
         currentUrl = URI.create(currentText.toString().trim());
      } else if (qName.equals("LastModified")) {
         currentLastModified = dateParser.rfc822DateParse(currentText.toString().trim());
      } else if (qName.equals("Etag")) {
         currentETag = HttpUtils.fromHexString(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
