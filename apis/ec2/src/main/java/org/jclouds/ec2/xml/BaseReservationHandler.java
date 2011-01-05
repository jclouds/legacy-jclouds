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
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.MonitoringState;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseReservationHandler<T> extends HandlerForGeneratedRequestWithResult<T> {

   protected final DateService dateService;

   protected final String defaultRegion;

   @Inject
   public BaseReservationHandler(DateService dateService, @Region String defaultRegion) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
   }

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();
   private Set<String> groupIds = Sets.newLinkedHashSet();
   private Set<RunningInstance> instances = Sets.newLinkedHashSet();
   private String ownerId;
   private String requesterId;
   private String reservationId;
   private String amiLaunchIndex;
   private String dnsName;
   private String imageId;
   private String instanceId;
   private InstanceState instanceState;
   private String instanceType;
   private String ipAddress;
   private String kernelId;
   private String keyName;
   private Date launchTime;
   private MonitoringState monitoringState;
   private String availabilityZone;
   private String placementGroup;
   private String virtualizationType = "paravirtual";
   private String platform;
   private String privateDnsName;
   private String privateIpAddress;
   private Set<String> productCodes = Sets.newHashSet();
   private String ramdiskId;
   private String reason;
   private String spotInstanceRequestId;
   private String subnetId;
   private String vpcId;
   protected boolean inInstances;
   protected boolean inProductCodes;
   protected boolean inGroups;
   protected boolean inMonitoring;

   private boolean inBlockDeviceMapping;
   private Map<String, RunningInstance.EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();

   private String volumeId;
   private Attachment.Status attachmentStatus;
   private Date attachTime;
   private boolean deleteOnTermination;
   private RootDeviceType rootDeviceType = RootDeviceType.INSTANCE_STORE;
   private String deviceName;
   private String rootDeviceName;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("instancesSet")) {
         inInstances = true;
      } else if (qName.equals("productCodesSet")) {
         inProductCodes = true;
      } else if (qName.equals("groupSet")) {
         inGroups = true;
      } else if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = true;
      }
      if (qName.equals("monitoring"))
         inMonitoring = true;
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("groupId")) {
         groupIds.add(currentOrNull());
      } else if (qName.equals("ownerId")) {
         ownerId = currentOrNull();
      } else if (qName.equals("requesterId")) {
         requesterId = currentOrNull();
      } else if (qName.equals("reservationId")) {
         reservationId = currentOrNull();
      } else if (qName.equals("amiLaunchIndex")) {
         amiLaunchIndex = currentOrNull();
      } else if (qName.equals("dnsName")) {
         dnsName = currentOrNull();
         // Eucalyptus
         if ("0.0.0.0".equals(dnsName))
            dnsName = null;
      } else if (qName.equals("imageId")) {
         imageId = currentOrNull();
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("name")) {
         String state = currentOrNull();
         if (state != null) {
            // Nova
            if ("shutdown".equalsIgnoreCase(state))
               instanceState = InstanceState.TERMINATED;
            else
               instanceState = InstanceState.fromValue(state);
         }
      } else if (qName.equals("instanceType")) {
         instanceType = currentOrNull();
      } else if (qName.equals("ipAddress")) {
         ipAddress = currentOrNull();
      } else if (qName.equals("kernelId")) {
         kernelId = currentOrNull();
      } else if (qName.equals("keyName")) {
         keyName = currentOrNull();
      } else if (qName.equals("launchTime")) {
         try {
            launchTime = dateService.iso8601DateParse(currentOrNull());
         } catch (RuntimeException e) {
            // Eucalyptus
            launchTime = dateService.iso8601SecondsDateParse(currentOrNull());
         }
      } else if (qName.equals("state") && inMonitoring) {
         monitoringState = MonitoringState.fromValue(currentOrNull());
      } else if (qName.equals("availabilityZone")) {
         availabilityZone = currentOrNull();
      } else if (qName.equals("groupName")) {
         placementGroup = currentOrNull();
      } else if (qName.equals("virtualizationType")) {
         virtualizationType = currentOrNull();
      } else if (qName.equals("platform")) {
         platform = currentOrNull();
      } else if (qName.equals("privateDnsName")) {
         privateDnsName = currentOrNull();
         // Eucalyptus
         if ("0.0.0.0".equals(privateDnsName))
            privateDnsName = null;
      } else if (qName.equals("privateIpAddress")) {
         privateIpAddress = currentOrNull();
      } else if (qName.equals("ramdiskId")) {
         ramdiskId = currentOrNull();
      } else if (qName.equals("reason")) {
         reason = currentOrNull();
      } else if (qName.equals("subnetId")) {
         subnetId = currentOrNull();
      } else if (qName.equals("spotInstanceRequestId")) {
         spotInstanceRequestId = currentOrNull();
      } else if (qName.equals("vpcId")) {
         vpcId = currentOrNull();
      } else if (qName.equals("productCode")) {
         productCodes.add(currentOrNull());
      } else if (qName.equals("productCodesSet")) {
         inProductCodes = false;
      } else if (qName.equals("instancesSet")) {
         inInstances = false;
      } else if (qName.equals("groupSet")) {
         inGroups = false;
      } else if (qName.equals("monitoring")) {
         inMonitoring = false;
      } else if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = false;
      } else if (qName.equals("deviceName")) {
         deviceName = currentOrNull();
      } else if (qName.equals("rootDeviceType")) {
         rootDeviceType = RootDeviceType.fromValue(currentOrNull());
      } else if (qName.equals("volumeId")) {
         volumeId = currentOrNull();
      } else if (qName.equals("status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("rootDeviceName")) {
         rootDeviceName = currentOrNull();
      } else if (qName.equals("item")) {
         inItem();
      }
      currentText = new StringBuilder();
   }

   protected void inItem() {
      if (inBlockDeviceMapping) {
         ebsBlockDevices.put(deviceName, new RunningInstance.EbsBlockDevice(volumeId, attachmentStatus, attachTime,
               deleteOnTermination));
         this.deviceName = null;
         this.volumeId = null;
         this.attachmentStatus = null;
         this.attachTime = null;
         this.deleteOnTermination = true;
      } else if (inInstances && !inProductCodes && !inBlockDeviceMapping) {
         String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;

         // Eucalyptus
         if (ipAddress == null && dnsName != null && dnsName.matches(".*[0-9]$")) {
            ipAddress = dnsName;
            dnsName = null;
         }

         if (privateIpAddress == null && privateDnsName != null && privateDnsName.matches(".*[0-9]$")) {
            privateIpAddress = privateDnsName;
            privateDnsName = null;
         }
         if (region == null)
            region = defaultRegion;
         instances.add(new RunningInstance(region, groupIds, amiLaunchIndex, dnsName, imageId, instanceId,
               instanceState, instanceType, ipAddress, kernelId, keyName, launchTime, monitoringState,
               availabilityZone, placementGroup, virtualizationType, platform, privateDnsName, privateIpAddress,
               productCodes, ramdiskId, reason, subnetId, spotInstanceRequestId, vpcId, rootDeviceType, rootDeviceName,
               ebsBlockDevices));
         this.amiLaunchIndex = null;
         this.dnsName = null;
         this.imageId = null;
         this.instanceId = null;
         this.instanceState = null;
         this.instanceType = null;
         this.ipAddress = null;
         this.kernelId = null;
         this.keyName = null;
         this.launchTime = null;
         this.monitoringState = null;
         this.availabilityZone = null;
         this.placementGroup = null;
         this.virtualizationType = "paravirtual";
         this.platform = null;
         this.privateDnsName = null;
         this.privateIpAddress = null;
         this.productCodes = Sets.newHashSet();
         this.ramdiskId = null;
         this.reason = null;
         this.subnetId = null;
         this.spotInstanceRequestId = null;
         this.vpcId = null;
         this.rootDeviceType = RootDeviceType.INSTANCE_STORE;
         this.rootDeviceName = null;
         this.ebsBlockDevices = Maps.newHashMap();
      }
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
