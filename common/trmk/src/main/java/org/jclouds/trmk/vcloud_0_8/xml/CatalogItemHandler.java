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

import static org.jclouds.trmk.vcloud_0_8.util.Utils.newReferenceType;

import java.util.Map;
import java.util.SortedMap;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogItemImpl;
import org.jclouds.util.SaxUtils;
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
   private ReferenceType customizationOptions;
   private ReferenceType computeOptions;

   public CatalogItem getResult() {
      return new CatalogItemImpl(catalogItem.getName(), catalogItem.getHref(), description, computeOptions,
            customizationOptions, entity, properties);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("CatalogItem")) {
         catalogItem = newReferenceType(attributes);
      } else if (qName.equals("Entity")) {
         entity = newReferenceType(attributes);
      } else if (qName.equals("Property")) {
         key = attributes.get("key");
      } else if (qName.equals("Link")) {
         if (attributes.containsKey("name")) {
            if (attributes.get("name").equals("Customization Options")) {
               customizationOptions = newReferenceType(attributes);
            } else if (attributes.get("name").equals("Compute Options")) {
               computeOptions = newReferenceType(attributes);
            }
         }
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("Property")) {
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
