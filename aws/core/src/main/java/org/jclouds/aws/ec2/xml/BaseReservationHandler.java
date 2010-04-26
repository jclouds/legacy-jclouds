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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RootDeviceType;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseReservationHandler<T> extends HandlerWithResult<T> {

   protected final DateService dateService;

   protected final Region defaultRegion;

   @Inject
   public BaseReservationHandler(DateService dateService, @EC2 Region defaultRegion) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
   }

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();
   private SortedSet<String> groupIds = Sets.newTreeSet();
   private SortedSet<RunningInstance> instances = Sets.newTreeSet();
   private String ownerId;
   private String requesterId;
   private String reservationId;
   private String amiLaunchIndex;
   private String dnsName;
   private String imageId;
   private String instanceId;
   private InstanceState instanceState;
   private InstanceType instanceType;
   private InetAddress ipAddress;
   private String kernelId;
   private String keyName;
   private Date launchTime;
   private boolean monitoring;
   private AvailabilityZone availabilityZone;
   private String platform;
   private String privateDnsName;
   private InetAddress privateIpAddress;
   private Set<String> productCodes = Sets.newHashSet();
   private String ramdiskId;
   private String reason;
   private String subnetId;
   private String vpcId;
   protected boolean inInstances;
   protected boolean inProductCodes;
   protected boolean inGroups;
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
      } else if (qName.equals("imageId")) {
         imageId = currentOrNull();
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("name")) {
         instanceState = InstanceState.fromValue(currentOrNull());
      } else if (qName.equals("instanceType")) {
         instanceType = InstanceType.fromValue(currentOrNull());
      } else if (qName.equals("ipAddress")) {
         ipAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("kernelId")) {
         kernelId = currentOrNull();
      } else if (qName.equals("keyName")) {
         keyName = currentOrNull();
      } else if (qName.equals("launchTime")) {
         launchTime = dateService.iso8601DateParse(currentOrNull());
      } else if (qName.equals("enabled")) {
         monitoring = Boolean.parseBoolean(currentOrNull());
      } else if (qName.equals("availabilityZone")) {
         availabilityZone = AvailabilityZone.fromValue(currentOrNull());
      } else if (qName.equals("platform")) {
         platform = currentOrNull();
      } else if (qName.equals("privateDnsName")) {
         privateDnsName = currentOrNull();
      } else if (qName.equals("privateIpAddress")) {
         privateIpAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("ramdiskId")) {
         ramdiskId = currentOrNull();
      } else if (qName.equals("reason")) {
         reason = currentOrNull();
      } else if (qName.equals("subnetId")) {
         subnetId = currentOrNull();
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
         ebsBlockDevices.put(deviceName, new RunningInstance.EbsBlockDevice(volumeId,
                  attachmentStatus, attachTime, deleteOnTermination));
         this.deviceName = null;
         this.volumeId = null;
         this.attachmentStatus = null;
         this.attachTime = null;
         this.deleteOnTermination = true;
      } else if (inInstances && !inProductCodes && !inBlockDeviceMapping) {
         Region region = EC2Utils.findRegionInArgsOrNull(request);
         if (region == null)
            region = defaultRegion;
         instances.add(new RunningInstance(region, amiLaunchIndex, dnsName, imageId, instanceId,
                  instanceState, instanceType, ipAddress, kernelId, keyName, launchTime,
                  monitoring, availabilityZone, platform, privateDnsName, privateIpAddress,
                  productCodes, ramdiskId, reason, subnetId, vpcId, rootDeviceType, rootDeviceName,
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
         this.monitoring = false;
         this.availabilityZone = null;
         this.platform = null;
         this.privateDnsName = null;
         this.privateIpAddress = null;
         this.productCodes = Sets.newHashSet();
         this.ramdiskId = null;
         this.reason = null;
         this.subnetId = null;
         this.vpcId = null;
         this.rootDeviceType = RootDeviceType.INSTANCE_STORE;
         this.rootDeviceName = null;
         this.ebsBlockDevices = Maps.newHashMap();
      }
   }

   private InetAddress parseInetAddress(String string) {
      String[] byteStrings = string.split("\\.");
      byte[] bytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         bytes[i] = (byte) Integer.parseInt(byteStrings[i]);
      }
      try {
         return InetAddress.getByAddress(bytes);
      } catch (UnknownHostException e) {
         logger.warn(e, "error parsing ipAddress", currentText);
      }
      return null;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected Reservation newReservation() {
      Region region = EC2Utils.findRegionInArgsOrNull(request);
      if (region == null)
         region = defaultRegion;
      Reservation info = new Reservation(region, groupIds, instances, ownerId, requesterId,
               reservationId);
      this.groupIds = Sets.newTreeSet();
      this.instances = Sets.newTreeSet();
      this.ownerId = null;
      this.requesterId = null;
      this.reservationId = null;
      return info;
   }

}