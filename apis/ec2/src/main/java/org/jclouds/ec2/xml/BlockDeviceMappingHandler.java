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
package org.jclouds.ec2.xml;

import java.util.Date;
import java.util.Map;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 *
 * @author Adrian Cole
 */
public class BlockDeviceMappingHandler extends
         ParseSax.HandlerWithResult<Map<String, BlockDevice>> {
   private StringBuilder currentText = new StringBuilder();

   private Map<String, BlockDevice> ebsBlockDevices = Maps.newHashMap();
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

   public Map<String, BlockDevice> getResult() {
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
         ebsBlockDevices.put(deviceName, new BlockDevice(volumeId, attachmentStatus, attachTime, deleteOnTermination));
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
