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
package org.jclouds.vcloud.terremark.xml;

import org.jclouds.rest.domain.NamedLink;
import org.jclouds.rest.util.Utils;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVDCImpl;
import org.jclouds.vcloud.xml.VDCHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkVDCHandler extends VDCHandler {

   private NamedLink catalog;
   private NamedLink publicIps;
   private NamedLink internetServices;

   public TerremarkVDC getResult() {
      VDC vDC = super.getResult();
      return new TerremarkVDCImpl(vDC.getName(), vDC.getType(), vDC.getLocation(), vDC
               .getResourceEntities(), vDC.getAvailableNetworks(), catalog, publicIps,
               internetServices);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if (qName.equals("Link")) {
         String name = attributes.getValue(attributes.getIndex("name"));
         if (name.equals("Internet Services")) {
            internetServices = Utils.newNamedLink(attributes);
         } else if (name.equals("Public IPs")) {
            publicIps = Utils.newNamedLink(attributes);
         } else {
            String type = attributes.getValue(attributes.getIndex("type"));
            if (type.equals(VCloudMediaType.CATALOG_XML)) {
               catalog = Utils.newNamedLink(attributes);
            }
         }
      }
   }

}
