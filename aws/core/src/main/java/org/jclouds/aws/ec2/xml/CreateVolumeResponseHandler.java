/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class CreateVolumeResponseHandler extends ParseSax.HandlerWithResult<Volume> {
   private StringBuilder currentText = new StringBuilder();

   @Resource
   protected Logger logger = Logger.NULL;
   @Inject
   protected DateService dateService;
   @Inject
   protected Map<AvailabilityZone, Region> availabilityZoneToRegion;

   private String id;
   private int size;
   private String snapshotId;
   private AvailabilityZone availabilityZone;
   private Volume.Status volumeStatus;
   private Date createTime;
   private Set<Attachment> attachments = Sets.newLinkedHashSet();

   private String volumeId;
   private String instanceId;
   private String device;
   private Attachment.Status attachmentStatus;
   private Date attachTime;

   private boolean inAttachmentSet;

   private Region region;

   public Volume getResult() {
      return newVolume();
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("attachmentSet")) {
         inAttachmentSet = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("volumeId")) {
         if (inAttachmentSet) {
            volumeId = currentText.toString().trim();
         } else {
            id = currentText.toString().trim();
         }
      } else if (qName.equals("size")) {
         size = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("availabilityZone")) {
         String availabilityZoneName = currentText.toString().trim();
         try {
            availabilityZone = AvailabilityZone.fromValue(availabilityZoneName);
         } catch (IllegalArgumentException e) {
            logger.warn(e, "unsupported availability zone: %s", availabilityZoneName);
            availabilityZone = AvailabilityZone.UNKNOWN;
         }
      } else if (qName.equals("volumeId")) {
         if (inAttachmentSet) {
            volumeId = currentText.toString().trim();
         } else {
            id = currentText.toString().trim();
         }
      } else if (qName.equals("status")) {
         if (inAttachmentSet) {
            attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
         } else {
            volumeStatus = Volume.Status.fromValue(currentText.toString().trim());
         }
      } else if (qName.equals("createTime")) {
         createTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("attachmentSet")) {
         inAttachmentSet = false;
      } else if (qName.equals("instanceId")) {
         instanceId = currentText.toString().trim();
      } else if (qName.equals("snapshotId")) {
         snapshotId = currentText.toString().trim();
         if (snapshotId.equals(""))
            snapshotId = null;
      } else if (qName.equals("device")) {
         device = currentText.toString().trim();
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("item")) {
         if (inAttachmentSet) {
            attachments.add(new Attachment(EC2Utils.findRegionInArgsOrNull(request), volumeId,
                     instanceId, device, attachmentStatus, attachTime));
            volumeId = null;
            instanceId = null;
            device = null;
            attachmentStatus = null;
            attachTime = null;
         }

      }
      currentText = new StringBuilder();
   }

   private Volume newVolume() {
      Volume volume = new Volume(region, id, size, snapshotId, availabilityZone, volumeStatus,
               createTime, attachments);
      id = null;
      size = 0;
      snapshotId = null;
      availabilityZone = null;
      volumeStatus = null;
      createTime = null;
      attachments = Sets.newLinkedHashSet();
      return volume;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public void setContext(GeneratedHttpRequest<?> request) {
      super.setContext(request);
      region = EC2Utils.findRegionInArgsOrNull(request);
      if (region == null) {
         AvailabilityZone zone = checkNotNull(EC2Utils.findAvailabilityZoneInArgsOrNull(request),
                  "zone not in args: " + Arrays.asList(request.getArgs()));
         region = checkNotNull(availabilityZoneToRegion.get(zone), String.format(
                  "zone %s not in %s", zone, availabilityZoneToRegion));
      }
   }
}
