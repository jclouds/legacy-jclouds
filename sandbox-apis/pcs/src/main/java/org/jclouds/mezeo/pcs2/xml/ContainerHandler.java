/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.mezeo.pcs2.xml;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.mezeo.pcs2.domain.ContainerList;
import org.jclouds.mezeo.pcs2.domain.ResourceInfo;
import org.jclouds.mezeo.pcs2.domain.internal.ContainerInfoImpl;
import org.jclouds.mezeo.pcs2.domain.internal.ContainerListImpl;
import org.jclouds.mezeo.pcs2.domain.internal.FileInfoImpl;
import org.jclouds.util.Strings2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class ContainerHandler extends ParseSax.HandlerWithResult<ContainerList> {

   @Resource
   protected Logger logger = Logger.NULL;

   private SortedSet<ResourceInfo> containerMetadata = Sets.newTreeSet();
   protected Map<String, URI> metadataItems = Maps.newHashMap();

   private URI rootUrl;
   private String rootName;
   private Date rootCreated;
   private boolean rootInproject;
   private Date rootModified;
   private String rootOwner;
   private int rootVersion;
   private boolean rootShared;
   private Date rootAccessed;
   private long rootBytes;
   private URI rootParent;
   private URI rootTags;
   private URI rootMetadata;

   private URI currentUrl;
   private String currentName;
   private Date currentCreated;
   private boolean currentInproject;
   private Date currentModified;
   private String currentOwner;
   private int currentVersion;
   private boolean currentShared;
   private Date currentAccessed;
   private long currentBytes;
   private URI currentParent;
   private URI currentTags;
   private URI currentMetadata;
   private URI currentContents;

   private boolean currentPublic;
   private String currentMimeType;
   private URI currentContent;
   private URI currentPermissions;

   private URI currentThumbnail;

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateParser;

   boolean inContainer = false;
   boolean inContents = false;

   private ContainerListImpl rootContainer;

   @Inject
   public ContainerHandler(DateService dateParser) {
      this.dateParser = dateParser;
   }

   public ContainerList getResult() {
      return rootContainer;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("contents") && attributes.getIndex("count") != -1) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            rootUrl = URI.create(attributes.getValue(index).replace("/contents", ""));
         }
         inContents = true;
         return;
      } else if (qName.equals("container")) {
         if (inContents)
            inContainer = true;
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentUrl = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("parent")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentParent = URI.create(attributes.getValue(index));
            if (!inContents)
               rootParent = currentParent;
         }
      } else if (qName.equals("file")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentUrl = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("content")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentContent = URI.create(attributes.getValue(index));
         }
      } else if (qName.equals("contents")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentContents = URI.create(attributes.getValue(index));
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
            if (!inContents)
               rootTags = currentTags;
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
            if (!inContents)
               rootMetadata = currentMetadata;
         }
      } else if (qName.equals("metadata-item")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            String key = Strings2.replaceAll(attributes.getValue(index), METADATA_PATTERN, "");
            metadataItems.put(key.toLowerCase(), URI.create(attributes.getValue(index)));
         }
      }
   }

   public final Pattern METADATA_PATTERN = Pattern.compile(".*/metadata/");

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("contents")) {
         if (!inContainer)
            inContents = false;
         currentText = new StringBuilder();
         return;
      }
      if (qName.equals("container") && !inContents) {
         rootContainer = new ContainerListImpl(rootUrl, rootName, rootCreated, rootInproject,
                  rootModified, rootOwner, rootVersion, rootShared, rootAccessed, rootBytes,
                  containerMetadata, rootTags, rootMetadata, metadataItems, rootParent);
      } else if (qName.equals("container") || qName.equals("file")) {
         if (qName.equals("container")) {
            inContainer = false;
            containerMetadata.add(new ContainerInfoImpl(currentUrl, currentName, currentCreated,
                     currentInproject, currentModified, currentOwner, currentVersion,
                     currentShared, currentAccessed, currentBytes, currentContents, currentTags,
                     currentMetadata, currentParent));
         } else {
            containerMetadata.add(new FileInfoImpl(currentUrl, currentName, currentCreated,
                     currentInproject, currentModified, currentOwner, currentVersion,
                     currentShared, currentAccessed, currentPublic, currentMimeType, currentBytes,
                     currentContent, currentParent, currentPermissions, currentTags,
                     currentMetadata, currentThumbnail));
         }
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
         currentPublic = false;
         currentMimeType = null;
         currentParent = null;
         currentContents = null;
         currentContent = null;
         currentPermissions = null;
         currentTags = null;
         currentMetadata = null;
         currentThumbnail = null;

      } else if (qName.equals("name")) {
         currentName = currentText.toString().trim();
         if (!inContents)
            rootName = currentName;
      } else if (qName.equals("created")) {
         currentCreated = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
         if (!inContents)
            rootCreated = currentCreated;
      } else if (qName.equals("inproject")) {
         currentInproject = Boolean.parseBoolean(currentText.toString().trim());
         if (!inContents)
            rootInproject = currentInproject;
      } else if (qName.equals("modified")) {
         currentModified = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
         if (!inContents)
            rootModified = currentModified;
      } else if (qName.equals("owner")) {
         currentOwner = currentText.toString().trim();
         if (!inContents)
            rootOwner = currentOwner;
      } else if (qName.equals("version")) {
         currentVersion = Integer.parseInt(currentText.toString().trim());
         if (!inContents)
            rootVersion = currentVersion;
      } else if (qName.equals("shared")) {
         currentShared = Boolean.parseBoolean(currentText.toString().trim());
         if (!inContents)
            rootShared = currentShared;
      } else if (qName.equals("accessed")) {
         currentAccessed = dateParser.fromSeconds(Long.parseLong(currentText.toString().trim()));
         if (!inContents)
            rootAccessed = currentAccessed;
      } else if (qName.equals("bytes")) {
         currentBytes = Long.parseLong(currentText.toString().trim());
         if (!inContents)
            rootBytes = currentBytes;
      } else if (qName.equals("public")) {
         currentPublic = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("mime_type")) {
         currentMimeType = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
