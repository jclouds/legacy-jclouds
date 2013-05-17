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
import static org.jclouds.trmk.vcloud_0_8.util.Utils.putReferenceType;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogImpl;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogHandler extends ParseSax.HandlerWithResult<Catalog> {

   private StringBuilder currentText = new StringBuilder();

   private ReferenceType catalog;
   private Map<String, ReferenceType> contents = Maps.newLinkedHashMap();
   private String description;

   public Catalog getResult() {
      return new CatalogImpl(catalog.getName(), catalog.getType(), catalog.getHref(), description, contents);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("Catalog")) {
         catalog = newReferenceType(attributes, TerremarkVCloudMediaType.CATALOG_XML);
      } else if (qName.equals("CatalogItem")) {
         putReferenceType(contents, attributes);
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull();
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
