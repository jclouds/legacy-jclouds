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

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.jclouds.vcloud.VCloudMediaType.VDC_XML;
import static org.jclouds.vcloud.util.Utils.newNamedResource;
import static org.jclouds.vcloud.util.Utils.putNamedResource;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.internal.OrganizationImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class OrgHandler extends ParseSax.HandlerWithResult<Organization> {
   protected NamedResource org;
   protected Map<String, NamedResource> vdcs = Maps.newLinkedHashMap();
   protected Map<String, NamedResource> tasksLists = Maps.newLinkedHashMap();
   protected Map<String, NamedResource> catalogs = Maps.newLinkedHashMap();

   public Organization getResult() {
      return new OrganizationImpl(org.getId(), org.getName(), org.getLocation(), catalogs, vdcs,
               tasksLists);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Org")) {
         org = newNamedResource(attributes);
      } else if (qName.equals("Link")) {
         int typeIndex = attributes.getIndex("type");
         if (typeIndex != -1) {
            if (attributes.getValue(typeIndex).equals(VDC_XML)) {
               putNamedResource(vdcs, attributes);
            } else if (attributes.getValue(typeIndex).equals(CATALOG_XML)) {
               putNamedResource(catalogs, attributes);
            } else if (attributes.getValue(typeIndex).equals(TASKSLIST_XML)) {
               putNamedResource(tasksLists, attributes);
            }
         }
      }
   }
}
