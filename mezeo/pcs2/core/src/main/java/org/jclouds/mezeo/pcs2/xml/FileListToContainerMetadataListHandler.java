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
package org.jclouds.mezeo.pcs2.xml;

import java.net.URI;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class FileListToContainerMetadataListHandler extends
         ParseSax.HandlerWithResult<SortedSet<ContainerMetadata>> {

   private SortedSet<ContainerMetadata> containerMetadata = Sets.newTreeSet();
   private URI currentUrl;
   private String currentName;
   private DateTime currentCreated;
   private boolean currentInproject;
   private DateTime currentModified;
   private String currentOwner;
   private int currentVersion;
   private boolean currentShared;
   private DateTime currentAccessed;
   private long currentBytes;

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;

   @Inject
   public FileListToContainerMetadataListHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   public SortedSet<ContainerMetadata> getResult() {
      return containerMetadata;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("container")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentUrl = URI.create(attributes.getValue(index));
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
      } else if (qName.equals("container")) {
         containerMetadata.add(new ContainerMetadata(currentName, currentUrl, currentCreated,
                  currentModified, currentAccessed, currentOwner, currentShared, currentInproject,
                  currentVersion, currentBytes));
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

      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
