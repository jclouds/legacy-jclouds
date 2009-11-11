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

import static org.jclouds.rest.util.Utils.putNamedLink;

import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.domain.NamedLink;
import org.jclouds.rest.domain.internal.NamedLinkImpl;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class VDCHandler extends ParseSax.HandlerWithResult<VDC> {
   private NamedLink vDC;
   private Map<String, NamedLink> resourceEntities = Maps.newHashMap();
   private Map<String, NamedLink> availableNetworks = Maps.newHashMap();

   public VDC getResult() {
      return new VDCImpl(vDC.getName(), vDC.getType(), vDC.getLocation(), resourceEntities,
               availableNetworks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Vdc")) {
         vDC = new NamedLinkImpl(attributes.getValue(attributes.getIndex("name")), attributes
                  .getValue(attributes.getIndex("type")), URI.create(attributes.getValue(attributes
                  .getIndex("href"))));
      } else if (qName.equals("Network")) {
         putNamedLink(availableNetworks, attributes);
      } else if (qName.equals("ResourceEntity")) {
         putNamedLink(resourceEntities, attributes);
      }
   }

}
