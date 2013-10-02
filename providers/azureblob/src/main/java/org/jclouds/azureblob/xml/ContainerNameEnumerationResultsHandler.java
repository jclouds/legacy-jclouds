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

import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.BlobType;
import org.jclouds.azureblob.domain.LeaseStatus;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.internal.BlobPropertiesImpl;
import org.jclouds.azureblob.domain.internal.HashSetListBlobsResponse;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.io.ContentMetadataCodec;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * EnumerationResults ContainerName="http://myaccount.blob.core.windows.net/mycontainer"
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135734.aspx#samplerequestandresponse" />
 * @author Adrian Cole
 */
public class ContainerNameEnumerationResultsHandler extends ParseSax.HandlerWithResult<ListBlobsResponse> {
   private Set<BlobProperties> blobMetadata = Sets.newLinkedHashSet();
   private String prefix;
   private String marker;
   private int maxResults;
   private String nextMarker;
   private URI currentUrl;
   private URI containerUrl;
   private Date currentLastModified;
   private String currentETag;

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;
   private final ContentMetadataCodec contentMetadataCodec;
   private String delimiter;
   private String currentName;
   private long currentSize;
   private String currentContentType;
   private String currentContentEncoding;
   private String currentContentLanguage;
   private BlobType currentBlobType;
   private Date currentExpires;
   private boolean inBlob;
   private boolean inBlobPrefix;
   private boolean inMetadata;
   private Set<String> blobPrefixes = Sets.newHashSet();
   private byte[] currentContentMD5;
   private Map<String, String> currentMetadata = Maps.newHashMap();
   private LeaseStatus currentLeaseStatus;

   @Inject
   public ContainerNameEnumerationResultsHandler(DateService dateParser, ContentMetadataCodec contentMetadataCodec) {
      this.dateParser = dateParser;
      this.contentMetadataCodec = contentMetadataCodec;
   }

   public ListBlobsResponse getResult() {
      return new HashSetListBlobsResponse(blobMetadata, containerUrl, prefix, marker, maxResults, nextMarker,
               delimiter, blobPrefixes);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Blob")) {
         inBlob = true;
         inBlobPrefix = false;
         inMetadata = false;
      } else if (qName.equals("BlobPrefix")) {
         inBlob = false;
         inBlobPrefix = true;
      } else if (qName.equals("Metadata")) {
         inBlob = true;
         inMetadata = true;
      } else if (qName.equals("EnumerationResults")) {
         containerUrl = URI.create(attributes.getValue("ContainerName").toString().trim());
      }
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
      } else if (qName.equals("Delimiter")) {
         delimiter = currentText.toString().trim();
         delimiter = (delimiter.equals("")) ? null : delimiter;
      } else if (qName.equals("NextMarker")) {
         nextMarker = currentText.toString().trim();
         nextMarker = (nextMarker.equals("")) ? null : nextMarker;
      } else if (qName.equals("BlobType")) {
         currentBlobType = BlobType.fromValue(currentText.toString().trim());
      } else if (qName.equals("LeaseStatus")) {
         currentLeaseStatus = LeaseStatus.fromValue(currentText.toString().trim());
      } else if (qName.equals("Blob")) {
         BlobProperties md = new BlobPropertiesImpl(currentBlobType, currentName, containerUrl.getPath().replace("/",
                  ""), currentUrl, currentLastModified, currentETag, currentSize, currentContentType,
                  currentContentMD5, currentContentEncoding, currentContentLanguage, currentExpires, 
                  currentLeaseStatus, currentMetadata);
         blobMetadata.add(md);
         currentBlobType = null;
         currentName = null;
         currentUrl = null;
         currentLastModified = null;
         currentETag = null;
         currentSize = -1;
         currentContentType = null;
         currentContentEncoding = null;
         currentContentLanguage = null;
         currentContentMD5 = null;
         currentLeaseStatus = null;
         currentExpires = null;
         currentMetadata = Maps.newHashMap();
      } else if (qName.equals("Url")) {
         currentUrl = uriBuilder(currentText.toString().trim()).build();
      } else if (qName.equals("Last-Modified")) {
         currentLastModified = dateParser.rfc822DateParse(currentText.toString().trim());
      } else if (qName.equals("Etag")) {
         currentETag = currentText.toString().trim();
      } else if (qName.equals("Name")) {
         if (inBlob)
            currentName = currentText.toString().trim();
         else if (inBlobPrefix)
            blobPrefixes.add(currentText.toString().trim());
      } else if (qName.equals("Content-Length")) {
         currentSize = Long.parseLong(currentText.toString().trim());
      } else if (qName.equals("Content-MD5")) {
         if (!currentText.toString().trim().equals(""))
            currentContentMD5 = base64().decode(currentText.toString().trim());
      } else if (qName.equals("Content-Type")) {
         currentContentType = currentText.toString().trim();
      } else if (qName.equals("Content-Encoding")) {
         currentContentEncoding = currentText.toString().trim();
         if (currentContentEncoding.equals(""))
            currentContentEncoding = null;
      } else if (qName.equals("Content-Language")) {
         currentContentLanguage = currentText.toString().trim();
         if (currentContentLanguage.equals(""))
            currentContentLanguage = null;
      } else if (qName.equals("Expires")) {
         String trimmedCurrentText = currentText.toString().trim();
         if (trimmedCurrentText.equals("")) {
            currentExpires = null;
         } else {
            currentExpires = contentMetadataCodec.parseExpires(trimmedCurrentText);
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
