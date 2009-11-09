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

import static org.jclouds.rest.util.Utils.newLink;
import static org.jclouds.rest.util.Utils.putLink;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;

import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.domain.Link;
import org.jclouds.rest.domain.internal.LinkImpl;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.internal.OrganizationImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class OrgHandler extends ParseSax.HandlerWithResult<Organization> {
   private Link org;
   private Map<String, Link> vdcs = Maps.newHashMap();
   private Map<String, Link> tasksLists = Maps.newHashMap();
   private Link catalog;

   public Organization getResult() {
      return new OrganizationImpl(org.getName(), org.getType(), org.getLocation(), catalog, vdcs,
               tasksLists);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Org")) {
         org = new LinkImpl(attributes.getValue(attributes.getIndex("name")), ORG_XML, URI
                  .create(attributes.getValue(attributes.getIndex("href"))));
      } else if (qName.equals("Link")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).equals(VDC_XML)) {
               putLink(vdcs, attributes);
            } else if (attributes.getValue(typeIndex).equals(CATALOG_XML)) {
               catalog = newLink(attributes);
            } else if (attributes.getValue(typeIndex).equals(TASKSLIST_XML)) {
               putLink(tasksLists, attributes);
            }
         }
      }
   }

}
