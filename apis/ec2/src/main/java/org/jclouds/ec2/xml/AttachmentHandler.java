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

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class AttachmentHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Attachment> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;
   protected final Supplier<String> defaultRegion;

   @Inject
   AttachmentHandler(DateService dateService, @Region Supplier<String> defaultRegion) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
   }

   private StringBuilder currentText = new StringBuilder();
   private String volumeId;
   private String instanceId;
   private String device;
   private Attachment.Status attachmentStatus;
   private Date attachTime;

   public Attachment getResult() {
      String region = AWSUtils.findRegionInArgsOrNull(getRequest());
      if (region == null)
         region = defaultRegion.get();
      return new Attachment(region, volumeId, instanceId, device, attachmentStatus, attachTime);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();
      } else if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();
      } else if (qName.equals("status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("instanceId")) {
         instanceId = currentText.toString().trim();
      } else if (qName.equals("device")) {
         device = currentText.toString().trim();
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
