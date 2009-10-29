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
package org.jclouds.vcloudx.xml;

import static org.jclouds.vcloudx.VCloudXMediaType.CATALOG_XML;
import static org.jclouds.vcloudx.VCloudXMediaType.TASKSLIST_XML;
import static org.jclouds.vcloudx.VCloudXMediaType.VDC_XML;

import java.net.URI;
import java.util.Set;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloudx.domain.OrgLinks;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class OrgLinksHandler extends ParseSax.HandlerWithResult<OrgLinks> {
   private String name;
   private URI org;
   private Set<URI> vdcs = Sets.newHashSet();
   private Set<URI> tasksLists = Sets.newHashSet();
   private URI catalog;

   public OrgLinks getResult() {
      return new OrgLinks(name, org, catalog, vdcs, tasksLists);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Org")) {
         int index = attributes.getIndex("href");
         if (index != -1) {
            org = URI.create(attributes.getValue(index));
         }
         index = attributes.getIndex("name");
         if (index != -1) {
            name = attributes.getValue(index);
         }
      } else if (qName.equals("Link")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).equals(VDC_XML)) {
               int index = attributes.getIndex("href");
               if (index != -1) {
                  vdcs.add(URI.create(attributes.getValue(index)));
               }
            } else if (attributes.getValue(typeIndex).equals(CATALOG_XML)) {
               int index = attributes.getIndex("href");
               if (index != -1) {
                  catalog = URI.create(attributes.getValue(index));
               }
            } else if (attributes.getValue(typeIndex).equals(TASKSLIST_XML)) {
               int index = attributes.getIndex("href");
               if (index != -1) {
                  tasksLists.add(URI.create(attributes.getValue(index)));
               }
            }
         }
      }
   }
}
