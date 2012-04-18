/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.ec2.xml;

import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;

/**
 *
 * @author Adam lowe
 */
public class NovaCreateVolumeResponseHandler extends CreateVolumeResponseHandler {
   
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
