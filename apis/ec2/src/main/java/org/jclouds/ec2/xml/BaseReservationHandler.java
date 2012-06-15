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

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

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

import com.google.common.base.Supplier;
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
   protected final Supplier<String> defaultRegion;
   protected final Provider<Builder> builderProvider;

   @Inject
   public BaseReservationHandler(DateService dateService, @Region Supplier<String> defaultRegion,
         Provider<RunningInstance.Builder> builderProvider) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
      this.builderProvider = builderProvider;
      this.builder = builderProvider.get();
   }

   protected StringBuilder currentText = new StringBuilder();

   protected Builder builder;

   protected int itemDepth;
   protected boolean inInstancesSet;
   protected boolean inProductCodes;
   protected boolean inGroupSet;

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
      if (equalsOrSuffix(qName, "item")) {
         itemDepth++;
      } else if (equalsOrSuffix(qName, "instancesSet")) {
         inInstancesSet = true;
      } else if (equalsOrSuffix(qName, "productCodes")) {
         inProductCodes = true;
      } else if (equalsOrSuffix(qName, "groupSet")) {
         inGroupSet = true;
      } 
   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "item")) {
         inItem();
         itemDepth--;
      } else if (equalsOrSuffix(qName, "instancesSet")) {
         inInstancesSet = false;
      } else if (equalsOrSuffix(qName, "productCodes")) {
         inProductCodes = false;
      } else if (equalsOrSuffix(qName, "groupSet")) {
         inGroupSet = false;
      } else if (equalsOrSuffix(qName, "groupId")) {
         groupIds.add(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ownerId")) {
         ownerId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "requesterId")) {
         requesterId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "reservationId")) {
         reservationId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "amiLaunchIndex")) {
         builder.amiLaunchIndex(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "dnsName")) {
         String dnsName = currentOrNull(currentText);
         // Eucalyptus
         if (!"0.0.0.0".equals(dnsName))
            builder.dnsName(dnsName);
      } else if (equalsOrSuffix(qName, "imageId")) {
         builder.imageId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "instanceId")) {
         builder.instanceId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "name")) {
         String rawState = currentOrNull(currentText);
         if (rawState != null) {
            builder.rawState(rawState);
            builder.instanceState(InstanceState.fromValue(rawState));
         }
      } else if (equalsOrSuffix(qName, "instanceType")) {
         builder.instanceType(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ipAddress")) {
         builder.ipAddress(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "kernelId")) {
         builder.kernelId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "keyName")) {
         builder.keyName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "launchTime")) {
         builder.launchTime(parseDate());
      } else if (equalsOrSuffix(qName, "availabilityZone")) {
         builder.availabilityZone(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "virtualizationType")) {
         builder.virtualizationType(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "platform")) {
         builder.platform(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "privateDnsName")) {
         String privateDnsName = currentOrNull(currentText);
         // Eucalyptus
         if (!"0.0.0.0".equals(privateDnsName))
            builder.privateDnsName(privateDnsName);
      } else if (equalsOrSuffix(qName, "privateIpAddress")) {
         builder.privateIpAddress(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ramdiskId")) {
         builder.ramdiskId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "reason")) {
         builder.reason(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "rootDeviceType")) {
         builder.rootDeviceType(RootDeviceType.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "rootDeviceName")) {
         builder.rootDeviceName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "deviceName")) {
         deviceName = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "volumeId")) {
         volumeId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (equalsOrSuffix(qName, "attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (equalsOrSuffix(qName, "deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentText.toString().trim());
      } else if (equalsOrSuffix(qName, "ebs")) {
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
         return dateService.iso8601DateParse(currentOrNull(currentText));
      } catch (RuntimeException e) {
         // Eucalyptus
         return dateService.iso8601SecondsDateParse(currentOrNull(currentText));
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

      builder.region((region == null) ? defaultRegion.get() : region);
      builder.groupIds(groupIds);
   }

   protected Builder builder() {
      return builder;
   }

   protected boolean endOfInstanceItem() {
      return itemDepth <= 2 && inInstancesSet && !inProductCodes && !inGroupSet;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected Reservation<? extends RunningInstance> newReservation() {
      String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
      if (region == null)
         region = defaultRegion.get();
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
