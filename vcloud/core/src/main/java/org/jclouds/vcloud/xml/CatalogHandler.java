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
package org.jclouds.vcloud.xml;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogHandler extends ParseSax.HandlerWithResult<Catalog> {
   private StringBuilder currentText = new StringBuilder();

   private NamedResource catalog;
   private SortedMap<String, NamedResource> contents = Maps.newTreeMap();
   @Inject
   @CatalogItemRoot
   private String catalogItemRoot;

   private String description;

   public Catalog getResult() {
      return new CatalogImpl(catalog.getId(), catalog.getName(), catalog.getLocation(),
               description, contents);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Catalog")) {
         catalog = Utils.newNamedResource(attributes);
      } else if (qName.equals("CatalogItem")) {
         putNamedResource(contents, attributes);
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

   public NamedResource newNamedResource(Attributes attributes) {
      String href = attributes.getValue(attributes.getIndex("href"));
      String id = href.replace(catalogItemRoot + "/", "");
      assert !id.contains("https://") : String.format(
               "parse of %s should have stripped, but didn't %s", href, id);
      return new NamedResourceImpl(id, attributes.getValue(attributes.getIndex("name")), attributes
               .getValue(attributes.getIndex("type")), URI.create(href));
   }

   public void putNamedResource(Map<String, NamedResource> map, Attributes attributes) {
      map.put(attributes.getValue(attributes.getIndex("name")), newNamedResource(attributes));
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
