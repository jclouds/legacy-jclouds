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

import javax.inject.Inject;

import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.DateService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
public class FileMetadataHandler extends BaseFileMetadataHandler<FileMetadata> {
   private final PCSUtil util;
   private FileMetadata fileMetadata = null;
   protected Multimap<String, String> userMetadata = HashMultimap.create();

   @Inject
   public FileMetadataHandler(PCSUtil util, DateService dateParser) {
      super(dateParser);
      this.util = util;
   }

   public FileMetadata getResult() {
      return fileMetadata;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("content")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentUrl = URI.create(attributes.getValue(index).replaceAll("/content", ""));
         }
      } else if (qName.equals("metadata-item")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            String key = attributes.getValue(index).replaceAll(".*/metadata/", "");
            String value = util.get(URI.create(attributes.getValue(index))).trim();
            userMetadata.put(key.toLowerCase(), value);
         }
      }
   }

   protected void addFileMetadata(FileMetadata metadata) {
      this.fileMetadata = metadata;
      this.fileMetadata.setUserMetadata(userMetadata);
   }
}
