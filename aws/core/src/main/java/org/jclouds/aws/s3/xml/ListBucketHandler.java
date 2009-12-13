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
package org.jclouds.aws.s3.xml;

import java.util.Date;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.aws.s3.domain.CanonicalUser;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.aws.s3.domain.internal.BucketListObjectMetadata;
import org.jclouds.aws.s3.domain.internal.TreeSetListBucketResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.DateService;
import org.jclouds.util.EncryptionService;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * ListBucketResult xmlns="http://s3.amazonaws.com/doc/2006-03-01"
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html"
 *      />
 */
public class ListBucketHandler extends ParseSax.HandlerWithResult<ListBucketResponse> {
   private SortedSet<ObjectMetadata> contents;
   private SortedSet<String> commonPrefixes;
   private CanonicalUser currentOwner;
   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;
   private final EncryptionService encryptionService;

   private String bucketName;
   private String prefix;
   private String marker;
   private int maxResults;
   private String delimiter;
   private boolean isTruncated;

   @Inject
   public ListBucketHandler(DateService dateParser, EncryptionService encryptionService) {
      this.dateParser = dateParser;
      this.encryptionService = encryptionService;
      this.contents = Sets.newTreeSet();
      this.commonPrefixes = Sets.newTreeSet();
   }

   public ListBucketResponse getResult() {
      return new TreeSetListBucketResponse(bucketName, contents, prefix, marker, maxResults,
               delimiter, isTruncated, commonPrefixes);
   }

   private boolean inCommonPrefixes;
   private String currentKey;
   private Date currentLastModified;
   private String currentETag;
   private byte[] currentMD5;
   private long currentSize;
   private StorageClass currentStorageClass;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("CommonPrefixes")) {
         inCommonPrefixes = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ID")) {
         currentOwner = new CanonicalUser(currentText.toString().trim());
      } else if (qName.equals("DisplayName")) {
         currentOwner.setDisplayName(currentText.toString().trim());
      } else if (qName.equals("Key")) { // content stuff
         currentKey = currentText.toString().trim();
      } else if (qName.equals("LastModified")) {
         currentLastModified = dateParser.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("ETag")) {
         currentETag = currentText.toString().trim();
         currentMD5 = encryptionService.fromHexString(currentETag.replaceAll("\"", ""));
      } else if (qName.equals("Size")) {
         currentSize = new Long(currentText.toString().trim());
      } else if (qName.equals("Owner")) {
      } else if (qName.equals("StorageClass")) {
         currentStorageClass = ObjectMetadata.StorageClass.valueOf(currentText.toString().trim());
      } else if (qName.equals("Contents")) {
         contents.add(new BucketListObjectMetadata(currentKey, currentLastModified, currentETag,
                  currentMD5, currentSize, currentOwner, currentStorageClass));
      } else if (qName.equals("Name")) {
         this.bucketName = currentText.toString().trim();
      } else if (qName.equals("Prefix")) {
         String prefix = currentText.toString().trim();
         if (inCommonPrefixes)
            commonPrefixes.add(prefix);
         else
            this.prefix = prefix;
      } else if (qName.equals("Delimiter")) {
         if (!currentText.toString().equals(""))
            this.delimiter = currentText.toString().trim();
      } else if (qName.equals("Marker")) {
         if (!currentText.toString().equals(""))
            this.marker = currentText.toString().trim();
      } else if (qName.equals("MaxKeys")) {
         this.maxResults = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("IsTruncated")) {
         this.isTruncated = Boolean.parseBoolean(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
