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

package org.jclouds.savvis.vpdc.xml;

import javax.inject.Inject;

import org.jclouds.savvis.vpdc.domain.SymphonyVPDCVDC;
import org.jclouds.savvis.vpdc.domain.internal.SymphonyVPDCVDCImpl;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.VDCHandler;

/**
 * @author Adrian Cole
 */
public class SymphonyVPDCVDCHandler extends VDCHandler {

   @Inject
   public SymphonyVPDCVDCHandler(TaskHandler taskHandler) {
      super(taskHandler);
   }

   private String offeringTag;

   public SymphonyVPDCVDC getResult() {
      VDC vDC = super.getResult();
      return new SymphonyVPDCVDCImpl(vDC.getName(), vDC.getType(), vDC.getHref(), status, org, description, tasks,
            allocationModel, storageCapacity, cpuCapacity, memoryCapacity, resourceEntities, availableNetworks,
            nicQuota, networkQuota, vmQuota, isEnabled, offeringTag);
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.endsWith("OfferingTag")) {
         this.offeringTag = currentOrNull();
      }
      super.endElement(uri, name, qName);

   }

}
