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
package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.util.Utils.newReferenceType;
import static org.jclouds.vcloud.util.Utils.putReferenceType;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.domain.TerremarkOrg;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkOrgImpl;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class TerremarkOrgHandler extends OrgHandler {
   @Inject
   public TerremarkOrgHandler(TaskHandler taskHandler) {
      super(taskHandler);
   }

   protected ReferenceType keysList;
   protected Map<String, ReferenceType> tasksLists = Maps.newLinkedHashMap();

   public TerremarkOrg getResult() {
      return new TerremarkOrgImpl(org.getName(), org.getType(), org.getHref(), description, catalogs, vdcs, networks,
            tasksLists, keysList);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("Org")) {
         org = newReferenceType(attributes);
      } else if (qName.endsWith("Link")) {
         String type = attributes.get("type");
         if (type != null) {
            if (type.indexOf("vdc+xml") != -1) {
               putReferenceType(vdcs, attributes);
            } else if (type.indexOf("catalog+xml") != -1) {
               putReferenceType(catalogs, attributes);
            } else if (type.indexOf("tasksList+xml") != -1) {
               putReferenceType(tasksLists, attributes);
            } else if (type.indexOf("network+xml") != -1) {
               putReferenceType(networks, attributes);
            } else if (type != null && type.endsWith("keysList+xml")) {
               keysList = newReferenceType(attributes);
            }
         }
      } else {
         taskHandler.startElement(uri, localName, qName, attrs);
      }

   }
}
