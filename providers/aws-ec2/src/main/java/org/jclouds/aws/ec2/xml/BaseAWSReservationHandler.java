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
package org.jclouds.aws.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseAWSReservationHandler<T> extends HandlerForGeneratedRequestWithResult<T> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;
   protected final Supplier<String> defaultRegion;
   protected final Provider<AWSRunningInstance.Builder> builderProvider;

   @Inject
   public BaseAWSReservationHandler(DateService dateService, @Region Supplier<String> defaultRegion,
         Provider<AWSRunningInstance.Builder> builderProvider) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
      this.builderProvider = builderProvider;
      this.builder = builderProvider.get();
   }

   protected StringBuilder currentText = new StringBuilder();

   protected AWSRunningInstance.Builder builder;

   protected int itemDepth;
   boolean inInstancesSet;
   // attachments
   private String volumeId;
   private Attachment.Status attachmentStatus;
   private Date attachTime;
   private boolean deleteOnTermination;
   private String deviceName;

   // reservation stuff
   private String groupId;
   private Map<String, String> reservationGroupIdToNames = Maps.newLinkedHashMap();
   private String ownerId;
   private String requesterId;
   private String reservationId;

   private Set<RunningInstance> instances = Sets.newLinkedHashSet();

   protected int depth = 0;

   private boolean inPlacement;

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      if (equalsOrSuffix(qName, "item")) {
         itemDepth++;
      } else if (equalsOrSuffix(qName, "instancesSet")) {
         inInstancesSet = true;
      } else if (equalsOrSuffix(qName, "placement")) {
         inPlacement = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "item")) {
         inItem();
         itemDepth--;
      } else if (equalsOrSuffix(qName, "state")) {
         builder.monitoringState(MonitoringState.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "groupId")) {
         groupId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "groupName") && inPlacement) {
         builder.placementGroup(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "groupName")) {
         switch (itemDepth) {
         case 2:
            reservationGroupIdToNames.put(groupId, currentOrNull(currentText));
            break;
         case 3:
            builder.securityGroupIdToName(groupId, currentOrNull(currentText));
            break;
         }
         groupId = null;
      } else if (equalsOrSuffix(qName, "subnetId")) {
         builder.subnetId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "spotInstanceRequestId")) {
         builder.spotInstanceRequestId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "vpcId")) {
         builder.vpcId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "hypervisor")) {
         builder.hypervisor(Hypervisor.fromValue(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "productCode")) {
         builder.productCode(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "instancesSet")) {
         inInstancesSet = false;
      } else if (equalsOrSuffix(qName, "placement")) {
         inPlacement = false;
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
         builder.rawState(rawState);
         builder.instanceState(InstanceState.fromValue(rawState));
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
      return dateService.iso8601DateParse(currentOrNull(currentText));
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
      builder.region((region == null) ? defaultRegion.get() : region);
   }

   protected abstract boolean endOfInstanceItem();

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected Reservation<? extends RunningInstance> newReservation() {
      String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
      if (region == null)
         region = defaultRegion.get();
      Reservation<? extends RunningInstance> info = new Reservation<RunningInstance>(region,
            reservationGroupIdToNames.values(), instances, ownerId, requesterId, reservationId);
      this.reservationGroupIdToNames = Maps.newLinkedHashMap();
      this.instances = Sets.newLinkedHashSet();
      this.ownerId = null;
      this.requesterId = null;
      this.reservationId = null;
      return info;
   }

}
