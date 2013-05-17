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
package org.jclouds.trmk.ecloud.xml;

import static org.jclouds.trmk.vcloud_0_8.util.Utils.newReferenceType;
import static org.jclouds.util.SaxUtils.cleanseAttributes;

import java.util.Map;

import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.ecloud.domain.internal.ECloudOrgImpl;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.xml.OrgHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ECloudOrgHandler extends OrgHandler {

   private ReferenceType dataCentersList;
   private ReferenceType deviceTags;
   private ReferenceType vAppCatalog;

   public ECloudOrg getResult() {
      return new ECloudOrgImpl(org.getName(), org.getType(), org.getHref(), description, catalogs, vdcs, tasksLists,
            keys, dataCentersList, deviceTags, vAppCatalog);
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
