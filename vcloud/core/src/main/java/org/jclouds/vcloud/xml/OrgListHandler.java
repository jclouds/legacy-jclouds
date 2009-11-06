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

import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;
import static org.jclouds.vcloud.util.Utils.putLink;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.Link;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class OrgListHandler extends ParseSax.HandlerWithResult<Map<String, Link>> {

   private Map<String, Link> org = Maps.newHashMap();

   public Map<String, Link> getResult() {
      return org;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Org")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).equals(ORG_XML)) {
               putLink(org, attributes);
            }
         }
      }
   }
}
