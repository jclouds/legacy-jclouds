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

import static org.jclouds.vcloud.util.Utils.cleanseAttributes;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.vcloud.VCloudExpressMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVDCImpl;
import org.jclouds.vcloud.util.Utils;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VDCHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkVDCHandler extends VDCHandler {

   @Inject
   public TerremarkVDCHandler(TaskHandler taskHandler) {
      super(taskHandler);
   }

   private ReferenceType catalog;
   private ReferenceType publicIps;
   private ReferenceType internetServices;

   public TerremarkVDC getResult() {
      VDC vDC = super.getResult();
      return new TerremarkVDCImpl(vDC.getName(), vDC.getType(), vDC.getHref(), status, org, description, tasks,
               allocationModel, storageCapacity, cpuCapacity, memoryCapacity, resourceEntities, availableNetworks,
               nicQuota, networkQuota, vmQuota, isEnabled, catalog, publicIps, internetServices);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      super.startElement(uri, localName, qName, attrs);
      if (qName.equals("Link")) {
         String name = attributes.get("name");
         if (name.equals("Internet Services")) {
            internetServices = Utils.newReferenceType(attributes);
         } else if (name.equals("Public IPs")) {
            publicIps = Utils.newReferenceType(attributes);
         } else {
            String type = attributes.get("type");
            if (type.equals(VCloudExpressMediaType.CATALOG_XML)) {
               catalog = Utils.newReferenceType(attributes);
            }
         }
      }
   }

}
