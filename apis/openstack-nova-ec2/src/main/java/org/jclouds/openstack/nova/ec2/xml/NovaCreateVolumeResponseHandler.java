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
package org.jclouds.openstack.nova.ec2.xml;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;

import com.google.common.base.Supplier;

/**
 *
 * @author Adam lowe
 */
public class NovaCreateVolumeResponseHandler extends CreateVolumeResponseHandler {

   @Inject
   protected NovaCreateVolumeResponseHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion,
            @Zone Supplier<Map<String, Supplier<Set<String>>>> regionToZonesSupplier,
            @Zone Supplier<Set<String>> zonesSupplier) {
      super(dateCodecFactory, defaultRegion, regionToZonesSupplier, zonesSupplier);
   }
   
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("status")) {
         String statusString = currentText.toString().trim();
         if (statusString.contains(" ")) {
            statusString = statusString.substring(0, statusString.indexOf(' '));
         }
         if (inAttachmentSet) {
            attachmentStatus = Attachment.Status.fromValue(statusString);
         } else {
            volumeStatus = Volume.Status.fromValue(statusString);
         }
         currentText = new StringBuilder();
      } else {
         super.endElement(uri, name, qName);
      }
   }

}
