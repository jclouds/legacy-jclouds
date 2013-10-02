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
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.RunningInstance.Builder;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseReservationHandler<T> extends HandlerForGeneratedRequestWithResult<T> {

   protected final DateCodec dateCodec;
   protected final Supplier<String> defaultRegion;

   @Inject
   public BaseReservationHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion) {
      this.dateCodec = dateCodecFactory.iso8601();
      this.defaultRegion = defaultRegion;
   }

   protected Builder<?> builder = newBuilder();

   protected Builder<?> newBuilder() {
      return RunningInstance.builder();
   }

   protected void inItem() {
      if (endOfInstanceItem()) {
         refineBuilderBeforeAddingInstance();
         instances.add(builder.build());
         builder = newBuilder();
      }
   }

   protected StringBuilder currentText = new StringBuilder();
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
   private Set<String> groupNames = Sets.newLinkedHashSet();
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
         groupNames.add(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ownerId")) {
         ownerId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "requesterId")) {
         requesterId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "reservationId")) {
         reservationId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "amiLaunchIndex")) {
         builder.amiLaunchIndex(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "dnsName")) {
         builder.dnsName(currentOrNull(currentText));
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
         builder.launchTime(dateCodec.toDate(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "availabilityZone")) {
         builder.availabilityZone(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "virtualizationType")) {
         builder.virtualizationType(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "platform")) {
         builder.platform(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "privateDnsName")) {
         builder.privateDnsName(currentOrNull(currentText));
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
         attachTime = dateCodec.toDate(currentOrNull(currentText));
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


   protected void refineBuilderBeforeAddingInstance() {
      String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
      builder.region((region == null) ? defaultRegion.get() : region);
      builder.groupNames(groupNames);
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
      Reservation<? extends RunningInstance> info = new Reservation<RunningInstance>(region, groupNames, instances,
            ownerId, requesterId, reservationId);
      this.groupNames = Sets.newLinkedHashSet();
      this.instances = Sets.newLinkedHashSet();
      this.ownerId = null;
      this.requesterId = null;
      this.reservationId = null;
      return info;
   }

}
