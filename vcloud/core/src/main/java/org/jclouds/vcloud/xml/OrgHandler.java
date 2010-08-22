/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newNamedResource;
import static org.jclouds.vcloud.util.Utils.putNamedResource;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class OrgHandler extends ParseSax.HandlerWithResult<Org> {
   private StringBuilder currentText = new StringBuilder();

   protected NamedResource org;
   protected Map<String, NamedResource> vdcs = Maps.newLinkedHashMap();
   protected NamedResource tasksList;
   protected Map<String, NamedResource> catalogs = Maps.newLinkedHashMap();
   protected Map<String, NamedResource> networks = Maps.newLinkedHashMap();

   protected String description;

   public Org getResult() {
      return new OrgImpl(org.getName(),  org.getType(), org.getId(), description, catalogs, vdcs, networks, tasksList);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Org")) {
         org = newNamedResource(attributes);
      } else if (qName.equals("Link")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).indexOf("vdc+xml") != -1) {
               putNamedResource(vdcs, attributes);
            } else if (attributes.getValue(typeIndex).indexOf("catalog+xml") != -1) {
               putNamedResource(catalogs, attributes);
            } else if (attributes.getValue(typeIndex).indexOf("tasksList+xml") != -1) {
               tasksList = newNamedResource(attributes);
            } else if (attributes.getValue(typeIndex).indexOf("network+xml") != -1) {
               putNamedResource(networks, attributes);
            }
         }
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
