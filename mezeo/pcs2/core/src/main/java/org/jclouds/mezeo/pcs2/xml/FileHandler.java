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
package org.jclouds.mezeo.pcs2.xml;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs2.domain.internal.FileInfoWithMetadataImpl;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class FileHandler extends ParseSax.HandlerWithResult<FileInfoWithMetadata> {
   protected Map<String, URI> metadataItems = Maps.newHashMap();

   protected URI currentUrl;
   private String currentName;
   private DateTime currentCreated;
   private boolean currentInproject;
   private DateTime currentModified;
   private String currentOwner;
   private int currentVersion;
   private boolean currentShared;
   private DateTime currentAccessed;
   private long currentBytes;
   private String currentMimeType;
   private boolean currentPublic;

   protected StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;

   private FileInfoWithMetadata fileMetadataList;

   private URI currentPermissions;

   private URI currentTags;

   private URI currentThumbnail;

   private URI currentMetadata;

   private URI currentContent;

   private URI currentParent;

   @Inject
   public FileHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("content")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentContent = URI.create(attributes.getValue(index));
            currentUrl = URI.create(attributes.getValue(index).replaceAll("/content", ""));
         }
      } else if (qName.equals("permissions")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentPermissions = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("tags")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentTags = URI.create(attributes.getValue(index));

         }
      } else if (qName.equals("parent")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentParent = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("thumbnail")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentThumbnail = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("metadata")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentMetadata = URI.create(attributes.getValue(index));

         }
      } else if (qName.equals("metadata-item")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            String key = attributes.getValue(index).replaceAll(".*/metadata/", "");
            metadataItems.put(key.toLowerCase(), URI.create(attributes.getValue(index)));
         }
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("name")) {
         currentName = currentText.toString().trim();
      } else if (qName.equals("created")) {
         currentCreated = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
      } else if (qName.equals("inproject")) {
         currentInproject = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("modified")) {
         currentModified = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
      } else if (qName.equals("owner")) {
         currentOwner = currentText.toString().trim();
      } else if (qName.equals("version")) {
         currentVersion = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("shared")) {
         currentShared = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("accessed")) {
         currentAccessed = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
      } else if (qName.equals("bytes")) {
         currentBytes = Long.parseLong(currentText.toString().trim());
      } else if (qName.equals("mime_type")) {
         currentMimeType = currentText.toString().trim();
      } else if (qName.equals("public")) {
         currentPublic = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("file")) {
         fileMetadataList = new FileInfoWithMetadataImpl(currentUrl, currentName, currentCreated,
                  currentInproject, currentModified, currentOwner, currentVersion, currentShared,
                  currentAccessed, currentPublic, currentMimeType, currentBytes, currentContent,
                  currentParent, currentPermissions, currentTags, currentMetadata, metadataItems,
                  currentThumbnail);
         currentUrl = null;
         currentName = null;
         currentCreated = null;
         currentInproject = false;
         currentModified = null;
         currentOwner = null;
         currentVersion = 0;
         currentShared = false;
         currentAccessed = null;
         currentBytes = 0;
         currentMimeType = null;
         currentPublic = false;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public FileInfoWithMetadata getResult() {
      return fileMetadataList;
   }
}
