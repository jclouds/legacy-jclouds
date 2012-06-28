/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.xml;

import java.net.URI;
import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;

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
      if (SaxUtils.equalsOrSuffix(qName, "Version")) {
         version = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "LoginUrl")) {
         location = URI.create(currentOrNull());
      } else if (SaxUtils.equalsOrSuffix(qName, "VersionInfo")) {
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
