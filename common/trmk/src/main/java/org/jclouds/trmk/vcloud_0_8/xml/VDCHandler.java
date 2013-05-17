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
import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VDCImpl;
import org.jclouds.trmk.vcloud_0_8.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class VDCHandler extends ParseSax.HandlerWithResult<VDC> {

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType vDC;
   protected String description;

   protected Map<String, ReferenceType> resourceEntities = Maps.newLinkedHashMap();
   protected Map<String, ReferenceType> availableNetworks = Maps.newLinkedHashMap();

   private ReferenceType catalog;
   private ReferenceType publicIps;
   private ReferenceType internetServices;

   public VDC getResult() {
      return new VDCImpl(vDC.getName(), vDC.getType(), vDC.getHref(), description, catalog, publicIps,
            internetServices, resourceEntities, availableNetworks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      super.startElement(uri, localName, qName, attrs);
      if (qName.endsWith("Vdc")) {
         vDC = newReferenceType(attributes);
      } else if (qName.endsWith("Network")) {
         putReferenceType(availableNetworks, attributes);
      } else if (qName.endsWith("ResourceEntity")) {
         putReferenceType(resourceEntities, attributes);
      } else if (equalsOrSuffix(qName, "Link")) {
         String name = attributes.get("name");
         if (name.equals("Internet Services")) {
            internetServices = Utils.newReferenceType(attributes);
         } else if (name.equals("Public IPs")) {
            publicIps = Utils.newReferenceType(attributes);
         } else {
            String type = attributes.get("type");
            if (type.equals(TerremarkVCloudMediaType.CATALOG_XML)) {
               catalog = Utils.newReferenceType(attributes);
            }
         }
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "Description")) {
         description = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
