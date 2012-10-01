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

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.ec2.domain.Image.ImageState;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
public abstract class BaseDescribeImagesResponseHandler<T extends Image> extends ParseSax.HandlerForGeneratedRequestWithResult<Set<T>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final Supplier<String> defaultRegion;

   protected Set<T> contents = Sets.newLinkedHashSet();
   protected StringBuilder currentText = new StringBuilder();

   protected Architecture architecture;
   protected String name;
   protected String description;
   protected String imageId;
   protected String imageLocation;
   protected String imageOwnerId;
   protected ImageState imageState;
   protected String rawState;
   protected ImageType imageType;
   protected boolean isPublic;
   protected String kernelId;
   protected String platform;
   protected String ramdiskId;
   protected RootDeviceType rootDeviceType = RootDeviceType.INSTANCE_STORE; // Eucalyptus 1.6 doesn't set rootDeviceType
   protected String rootDeviceName;
   protected VirtualizationType virtualizationType = VirtualizationType.PARAVIRTUAL;
   protected Hypervisor hypervisor = Hypervisor.XEN;

   protected boolean inBlockDeviceMapping;
   protected Map<String, EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();
   protected String deviceName;
   protected String snapshotId;
   protected int volumeSize;
   protected boolean deleteOnTermination = true; // Correct default is true.

   protected boolean inProductCodes;
   protected Set<String> productCodes = Sets.newHashSet();
   protected String productCode;

   @Inject
   public BaseDescribeImagesResponseHandler(@Region Supplier<String> defaultRegion) {
      this.defaultRegion = defaultRegion;
   }

   @Override
   public abstract Set<T> getResult();

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "productCodes")) {
         inProductCodes = true;
      } else if (equalsOrSuffix(qName, "blockDeviceMapping")) {
         inBlockDeviceMapping = true;
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "architecture")) {
         architecture = Architecture.fromValue(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "name") || equalsOrSuffix(qName, "displayName")) {
         // Nova EC2 uses the wrong name for this field
         this.name = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "description")) {
	      // Nova EC2 allows empty values
         description = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "imageId")) {
         imageId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "deviceName")) {
         deviceName = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "imageLocation")) {
         imageLocation = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "imageOwnerId")) {
	      // Nova EC2 allows empty values
         imageOwnerId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "imageState")) {
         rawState = currentOrNull(currentText);
         imageState = ImageState.fromValue(rawState);
      } else if (equalsOrSuffix(qName, "imageType") || equalsOrSuffix(qName, "type")) {
         // Eucalyptus uses the wrong name for this field
         imageType = ImageType.fromValue(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "isPublic")) {
         isPublic = Boolean.parseBoolean(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "kernelId")) {
         kernelId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "platform")) {
         platform = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "productCode")) {
         productCode = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "productCodes")) {
         inProductCodes = false;
      } else if (equalsOrSuffix(qName, "blockDeviceMapping")) {
         inBlockDeviceMapping = false;
      } else if (equalsOrSuffix(qName, "snapshotId")) {
         snapshotId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "volumeSize")) {
         volumeSize = Integer.parseInt(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ramdiskId")) {
         ramdiskId = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "rootDeviceType")) {
         rootDeviceType = RootDeviceType.fromValue(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "rootDeviceName")) {
         rootDeviceName = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "virtualizationType")) {
         virtualizationType = VirtualizationType.fromValue(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "hypervisor")) {
         hypervisor = Hypervisor.fromValue(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "item")) {
         endItem();
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected void endItem() {
      if (inBlockDeviceMapping) {
         ebsBlockDevices.put(deviceName, new Image.EbsBlockDevice(snapshotId, volumeSize, deleteOnTermination));
         deviceName = null;
         snapshotId = null;
         volumeSize = 0;
         deleteOnTermination = true;
      } else if (inProductCodes) {
         if (productCode != null) {
            productCodes.add(productCode);
            productCode = null;
         }
      } else {
         String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
         if (region == null) {
            region = defaultRegion.get();
         }
         contents.add(newImage(region));
         endImage();
      }
   }

   protected abstract T newImage(String region);

   protected void endImage() {
      name = null;
      description = null;
      architecture = null;
      imageId = null;
      imageLocation = null;
      imageOwnerId = null;
      imageState = null;
      rawState = null;
      imageType = null;
      isPublic = false;
      kernelId = null;
      platform = null;
      productCodes = Sets.newHashSet();
      productCode = null;
      ramdiskId = null;
      rootDeviceType = RootDeviceType.INSTANCE_STORE;
      rootDeviceName = null;
      ebsBlockDevices = Maps.newHashMap();
      virtualizationType = VirtualizationType.PARAVIRTUAL;
      hypervisor = Hypervisor.XEN;
   }
}
