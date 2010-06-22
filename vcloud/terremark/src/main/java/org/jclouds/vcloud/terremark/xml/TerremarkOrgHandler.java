/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.terremark.TerremarkVCloudMediaType.KEYSLIST_XML;
import static org.jclouds.vcloud.util.Utils.newNamedResource;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkOrganizationImpl;
import org.jclouds.vcloud.xml.OrgHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkOrgHandler extends OrgHandler {

   private NamedResource keysList;

   public TerremarkOrganization getResult() {
      return new TerremarkOrganizationImpl(org.getId(), org.getName(), org
            .getLocation(), catalogs, vdcs, tasksLists, keysList);
   }

   @Override
   public void startElement(String uri, String localName, String qName,
         Attributes attributes) throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if (qName.equals("Link")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).equals(KEYSLIST_XML)) {
               keysList = newNamedResource(attributes);
            }
         }
      }
   }

}
