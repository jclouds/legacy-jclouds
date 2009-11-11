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
package org.jclouds.atmosonline.saas.xml;

import java.util.SortedSet;

import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses an object list
 * <p/>
 * 
 * @see <a href="https://community.emc.com/community/labs/atmos_online" />
 * @author Adrian Cole
 */
public class ListDirectoryResponseHandler extends
         ParseSax.HandlerWithResult<SortedSet<DirectoryEntry>> {

   private SortedSet<DirectoryEntry> entries = Sets.newTreeSet();
   private String currentObjectId;
   private FileType currentType;
   private String currentName;

   private StringBuilder currentText = new StringBuilder();

   public SortedSet<DirectoryEntry> getResult() {
      return entries;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ObjectID")) {
         currentObjectId = currentText.toString().trim();
      } else if (qName.equals("FileType")) {
         currentType = FileType.fromValue(currentText.toString().trim());
      } else if (qName.equals("Filename")) {
         currentName = currentText.toString().trim();
         if (currentName.equals(""))
            currentName = null;
      } else if (qName.equals("DirectoryEntry")) {
         entries.add(new DirectoryEntry(currentObjectId, currentType, currentName));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
