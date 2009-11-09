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

import static org.jclouds.rest.util.Utils.putLink;

import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.domain.Link;
import org.jclouds.rest.domain.internal.LinkImpl;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogHandler extends ParseSax.HandlerWithResult<Catalog> {
   private Link Catalog;
   private Map<String, Link> contents = Maps.newHashMap();

   public Catalog getResult() {
      return new CatalogImpl(Catalog.getName(), Catalog.getType(), Catalog.getLocation(), contents);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Catalog")) {
         Catalog = new LinkImpl(attributes.getValue(attributes.getIndex("name")), attributes
                  .getValue(attributes.getIndex("type")), URI.create(attributes.getValue(attributes
                  .getIndex("href"))));
      } else if (qName.equals("CatalogItem")) {
         putLink(contents, attributes);
      }
   }

}
