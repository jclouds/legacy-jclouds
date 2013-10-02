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
package org.jclouds.azureblob.xml;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.domain.internal.BoundedHashSet;
import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.azureblob.domain.internal.ContainerPropertiesImpl;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * EnumerationResults AccountName="http://myaccount.blob.core.windows.net"
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179352.aspx" />
 * @author Adrian Cole
 */
public class AccountNameEnumerationResultsHandler extends
         ParseSax.HandlerWithResult<BoundedSet<ContainerProperties>> {

   private SortedSet<ContainerProperties> containerMetadata = Sets.newTreeSet();
   private String prefix;
   private String marker;
   private int maxResults;
   private String nextMarker;
   private URI currentUrl;
   private Date currentLastModified;
   private String currentETag;
   private boolean inMetadata;

   private Map<String, String> currentMetadata = Maps.newHashMap();

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;
   private URI accountUrl;

   @Inject
   public AccountNameEnumerationResultsHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Container")) {
         inMetadata = false;
      } else if (qName.equals("Metadata")) {
         inMetadata = true;
      } else if (qName.equals("EnumerationResults")) {
         accountUrl = URI.create(attributes.getValue("AccountName").toString().trim());
      }
   }

   public BoundedSet<ContainerProperties> getResult() {
      return new BoundedHashSet<ContainerProperties>(containerMetadata, accountUrl, prefix, marker,
               maxResults, nextMarker);
   }

   public void endElement(String uri, String name, String qName) {
      if (inMetadata && !qName.equals("Metadata")) {
         currentMetadata.put(qName, currentText.toString().trim());
      } else if (qName.equals("Metadata")) {
         inMetadata = false;
      } else if (qName.equals("MaxResults")) {
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
         containerMetadata.add(new ContainerPropertiesImpl(currentUrl, currentLastModified,
                  currentETag, currentMetadata));
         currentUrl = null;
         currentLastModified = null;
         currentETag = null;
         currentMetadata = Maps.newHashMap();
      } else if (qName.equals("Url")) {
         currentUrl = URI.create(currentText.toString().trim());
      } else if (qName.equals("Last-Modified")) {
         currentLastModified = dateParser.rfc822DateParse(currentText.toString().trim());
      } else if (qName.equals("Etag")) {
         currentETag = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
