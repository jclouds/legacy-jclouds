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

package org.jclouds.ec2.xml;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.RunningInstance.Builder;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;
import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseReservationHandler<T> extends HandlerForGeneratedRequestWithResult<T> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;
   protected final String defaultRegion;
   protected final Provider<Builder> builderProvider;

   @Inject
   public BaseReservationHandler(DateService dateService, @Region String defaultRegion,
            Provider<RunningInstance.Builder> builderProvider) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
      this.builderProvider = builderProvider;
      this.builder = builderProvider.get();
   }

   protected StringBuilder currentText = new StringBuilder();

   protected Builder builder;

   protected int itemDepth;
   boolean inInstancesSet;
   // attachments
   private String volumeId;
   private Attachment.Status attachmentStatus;
   private Date attachTime;
   private boolean deleteOnTermination;
   private String deviceName;

   // reservation stuff
   private Set<String> groupIds = Sets.newLinkedHashSet();
   private String ownerId;
   private String requesterId;
   private String reservationId;

   private Set<RunningInstance> instances = Sets.newLinkedHashSet();

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("item")) {
         itemDepth++;
      } else if (qName.equals("instancesSet")) {
         inInstancesSet = true;
      }
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("item")) {
         inItem();
         itemDepth--;
      } else if (qName.equals("instancesSet")) {
         inInstancesSet = false;
      } else if (qName.equals("groupId")) {
         groupIds.add(currentOrNull());
      } else if (qName.equals("ownerId")) {
         ownerId = currentOrNull();
      } else if (qName.equals("requesterId")) {
         requesterId = currentOrNull();
      } else if (qName.equals("reservationId")) {
         reservationId = currentOrNull();
      } else if (qName.equals("amiLaunchIndex")) {
         builder.amiLaunchIndex(currentOrNull());
      } else if (qName.equals("dnsName")) {
         String dnsName = currentOrNull();
         // Eucalyptus
         if (!"0.0.0.0".equals(dnsName))
            builder.dnsName(dnsName);
      } else if (qName.equals("imageId")) {
         builder.imageId(currentOrNull());
      } else if (qName.equals("instanceId")) {
         builder.instanceId(currentOrNull());
      } else if (qName.equals("name")) {
         builder.instanceState(InstanceState.fromValue(currentOrNull()));
      } else if (qName.equals("instanceType")) {
         builder.instanceType(currentOrNull());
      } else if (qName.equals("ipAddress")) {
         builder.ipAddress(currentOrNull());
      } else if (qName.equals("kernelId")) {
         builder.kernelId(currentOrNull());
      } else if (qName.equals("keyName")) {
         builder.keyName(currentOrNull());
      } else if (qName.equals("launchTime")) {
         builder.launchTime(parseDate());
      } else if (qName.equals("availabilityZone")) {
         builder.availabilityZone(currentOrNull());
      } else if (qName.equals("virtualizationType")) {
         builder.virtualizationType(currentOrNull());
      } else if (qName.equals("platform")) {
         builder.platform(currentOrNull());
      } else if (qName.equals("privateDnsName")) {
         String privateDnsName = currentOrNull();
         // Eucalyptus
         if (!"0.0.0.0".equals(privateDnsName))
            builder.privateDnsName(privateDnsName);
      } else if (qName.equals("privateIpAddress")) {
         builder.privateIpAddress(currentOrNull());
      } else if (qName.equals("ramdiskId")) {
         builder.ramdiskId(currentOrNull());
      } else if (qName.equals("reason")) {
         builder.reason(currentOrNull());
      } else if (qName.equals("rootDeviceType")) {
         builder.rootDeviceType(RootDeviceType.fromValue(currentOrNull()));
      } else if (qName.equals("rootDeviceName")) {
         builder.rootDeviceName(currentOrNull());
      } else if (qName.equals("deviceName")) {
         deviceName = currentOrNull();
      } else if (qName.equals("volumeId")) {
         volumeId = currentOrNull();
      } else if (qName.equals("status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("ebs")) {
         builder.device(deviceName, new BlockDevice(volumeId, attachmentStatus, attachTime, deleteOnTermination));
         this.deviceName = null;
         this.volumeId = null;
         this.attachmentStatus = null;
         this.attachTime = null;
         this.deleteOnTermination = true;
      } 
      currentText = new StringBuilder();
   }

   protected Date parseDate() {
      try {
         return dateService.iso8601DateParse(currentOrNull());
      } catch (RuntimeException e) {
         // Eucalyptus
         return dateService.iso8601SecondsDateParse(currentOrNull());
      }
   }

   protected void inItem() {
      if (endOfInstanceItem()) {
         refineBuilderBeforeAddingInstance();
         instances.add(builder.build());
         builder = builderProvider.get();
      }
   }

   protected void refineBuilderBeforeAddingInstance() {
      String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;

      // Eucalyptus
      if (builder.getIpAddress() == null && builder.getDnsName() != null && builder.getDnsName().matches(".*[0-9]$")) {
         builder.ipAddress(builder.getDnsName());
         builder.dnsName(null);
      }
      if (builder.getPrivateIpAddress() == null && builder.getPrivateDnsName() != null
               && builder.getPrivateDnsName().matches(".*[0-9]$")) {
         builder.privateIpAddress(builder.getPrivateDnsName());
         builder.privateDnsName(null);
      }

      builder.region((region == null) ? defaultRegion : region);
      builder.groupIds(groupIds);
   }

   protected Builder builder() {
      return builder;
   }

   protected boolean endOfInstanceItem() {
      return itemDepth <= 2 && inInstancesSet;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected Reservation<? extends RunningInstance> newReservation() {
      String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
      if (region == null)
         region = defaultRegion;
      Reservation<? extends RunningInstance> info = new Reservation<RunningInstance>(region, groupIds, instances,
               ownerId, requesterId, reservationId);
      this.groupIds = Sets.newLinkedHashSet();
      this.instances = Sets.newLinkedHashSet();
      this.ownerId = null;
      this.requesterId = null;
      this.reservationId = null;
      return info;
   }

}
