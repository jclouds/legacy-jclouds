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

package org.jclouds.aws.ec2.xml;

import java.util.Date;
import java.util.Map;

import com.google.inject.Inject;
import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.RunningInstance.EbsBlockDevice;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Maps;

/**
 *
 * @author Adrian Cole
 */
public class BlockDeviceMappingHandler extends
         ParseSax.HandlerWithResult<Map<String, EbsBlockDevice>> {
   private StringBuilder currentText = new StringBuilder();

   private Map<String, EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();
   private String deviceName;
   private String volumeId;
   private boolean deleteOnTermination = true;// correct default is true.
   private Attachment.Status attachmentStatus;
   private Date attachTime;

   protected final DateService dateService;

   @Inject 
   public BlockDeviceMappingHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public Map<String, EbsBlockDevice> getResult() {
      return ebsBlockDevices;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("deviceName")) {
         deviceName = currentText.toString().trim();
      } else if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();
      } else if (qName.equals("deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("item")) {
         ebsBlockDevices.put(deviceName, new EbsBlockDevice(volumeId, attachmentStatus, attachTime, deleteOnTermination));
          this.volumeId = null;
          this.deviceName = null;
          this.deleteOnTermination = true;
          this.attachmentStatus = null;
          this.attachTime = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
