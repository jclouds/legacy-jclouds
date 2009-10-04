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

import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.DateService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class FileListToFileMetadataListHandler extends
BaseFileMetadataHandler<SortedSet<FileMetadata>> {

   private SortedSet<FileMetadata> containerMetadata = Sets.newTreeSet();

   @Inject
   public FileListToFileMetadataListHandler(DateService dateParser) {
     super(dateParser);
   }

   public SortedSet<FileMetadata> getResult() {
      return containerMetadata;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("file")) {
         int index = attributes.getIndex("xlink:href");
         if (index != -1) {
            currentUrl = URI.create(attributes.getValue(index));
         }
      }
   }

   protected void addFileMetadata(FileMetadata metadata) {
      containerMetadata.add(metadata);
   }
}
