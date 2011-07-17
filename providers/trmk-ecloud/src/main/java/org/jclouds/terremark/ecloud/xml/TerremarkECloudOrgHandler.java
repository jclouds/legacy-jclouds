/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.terremark.ecloud.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.terremark.ecloud.domain.TerremarkECloudOrg;
import org.jclouds.terremark.ecloud.domain.internal.TerremarkECloudOrgImpl;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.xml.TerremarkOrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkECloudOrgHandler extends TerremarkOrgHandler {
   @Inject
   public TerremarkECloudOrgHandler(TaskHandler taskHandler) {
      super(taskHandler);
   }

   private ReferenceType dataCentersList;
   private ReferenceType deviceTags;
   private ReferenceType vAppCatalog;

   public TerremarkECloudOrg getResult() {
      return new TerremarkECloudOrgImpl(org.getName(), org.getType(), org.getHref(), description, catalogs, vdcs,
            networks, tasksLists, keysList, deviceTags, vAppCatalog, dataCentersList);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      super.startElement(uri, localName, qName, attrs);
      if (qName.equals("Link")) {
         if (attributes.containsKey("type")) {
            String type = attributes.get("type");
            if (type != null && type.endsWith("dataCentersList+xml")) {
               dataCentersList = newReferenceType(attributes);
            } else if (type != null && type.endsWith("tagsList+xml")) {
               deviceTags = newReferenceType(attributes);
            } else if (type != null && type.endsWith("VAppCatalogList+xml")) {
               vAppCatalog = newReferenceType(attributes);
            }
         }
      }
   }
}
