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
package org.jclouds.trmk.vcloud_0_8.xml;

import java.net.URI;
import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class SupportedVersionsHandler extends ParseSax.HandlerWithResult<SortedMap<String, URI>> {
   private StringBuilder currentText = new StringBuilder();

   private SortedMap<String, URI> contents = Maps.newTreeMap();
   private String version;
   private URI location;

   public SortedMap<String, URI> getResult() {
      return contents;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Version")) {
         version = currentOrNull();
      } else if (qName.equals("LoginUrl")) {
         location = URI.create(currentOrNull());
      } else if (qName.equals("VersionInfo")) {
         contents.put(version, location);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
