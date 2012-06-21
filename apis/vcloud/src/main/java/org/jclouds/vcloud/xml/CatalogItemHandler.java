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

import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;
import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogItemHandler extends ParseSax.HandlerWithResult<CatalogItem> {
   private StringBuilder currentText = new StringBuilder();

   protected ReferenceType catalogItem;
   protected ReferenceType entity;

   protected String description;
   protected String key;
   protected SortedMap<String, String> properties = Maps.newTreeMap();

   public CatalogItem getResult() {
      return new CatalogItemImpl(catalogItem.getName(), catalogItem.getHref(), description, entity, properties);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (SaxUtils.equalsOrSuffix(qName, "CatalogItem")) {
         catalogItem = newReferenceType(attributes);
      } else if (SaxUtils.equalsOrSuffix(qName, ("Entity"))) {
         entity = newReferenceType(attributes);
      } else if (SaxUtils.equalsOrSuffix(qName, ("Property"))) {
         key = attributes.get("key");
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (SaxUtils.equalsOrSuffix(qName, ("Description"))) {
         description = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, ("Property"))) {
         properties.put(key, currentOrNull());
         key = null;
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
