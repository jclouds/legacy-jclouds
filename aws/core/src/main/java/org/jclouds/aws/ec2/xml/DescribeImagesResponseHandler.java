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

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.RootDeviceType;
import org.jclouds.aws.ec2.domain.Image.Architecture;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.domain.Image.ImageState;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http://ec2.amazonaws.com/doc/2009-11-30/"
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
 *      />
 */
public class DescribeImagesResponseHandler extends ParseSax.HandlerWithResult<Set<Image>> {
   @Resource
   protected Logger logger = Logger.NULL;
   @Inject
   @EC2
   String defaultRegion;
   private Set<Image> contents = Sets.newLinkedHashSet();
   private StringBuilder currentText = new StringBuilder();

   private Architecture architecture;
   private String name;
   private String description;
   private String imageId;
   private String imageLocation;
   private String imageOwnerId;
   private ImageState imageState;
   private ImageType imageType;
   private boolean isPublic;
   private String kernelId;
   private String platform;
   private Set<String> productCodes = Sets.newHashSet();
   private String ramdiskId;
   private boolean inProductCodes;
   private boolean inBlockDeviceMapping;
   private RootDeviceType rootDeviceType;
   private Map<String, EbsBlockDevice> ebsBlockDevices = Maps.newHashMap();
   private String deviceName;
   private String snapshotId;
   private int volumeSize;
   private boolean deleteOnTermination = true;// correct default is true.

   private String rootDeviceName;

   public Set<Image> getResult() {
      return contents;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("productCodes")) {
         inProductCodes = true;
      } else if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("architecture")) {
         architecture = Architecture.fromValue(currentText.toString().trim());
      } else if (qName.equals("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equals("description")) {
         description = currentText.toString().trim();
      } else if (qName.equals("imageId")) {
         imageId = currentText.toString().trim();
      } else if (qName.equals("deviceName")) {
         deviceName = currentText.toString().trim();
      } else if (qName.equals("imageLocation")) {
         imageLocation = currentText.toString().trim();
      } else if (qName.equals("imageOwnerId")) {
         imageOwnerId = currentText.toString().trim();
      } else if (qName.equals("imageState")) {
         imageState = ImageState.fromValue(currentText.toString().trim());
      } else if (qName.equals("imageType")) {
         imageType = ImageType.fromValue(currentText.toString().trim());
      } else if (qName.equals("isPublic")) {
         isPublic = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("kernelId")) {
         kernelId = currentText.toString().trim();
      } else if (qName.equals("platform")) {
         platform = currentText.toString().trim();
      } else if (qName.equals("productCode")) {
         productCodes.add(currentText.toString().trim());
      } else if (qName.equals("productCodes")) {
         inProductCodes = false;
      } else if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = false;
      } else if (qName.equals("snapshotId")) {
         snapshotId = currentText.toString().trim();
      } else if (qName.equals("volumeSize")) {
         volumeSize = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("deleteOnTermination")) {
         deleteOnTermination = Boolean.parseBoolean(currentText.toString().trim());
      } else if (qName.equals("ramdiskId")) {
         ramdiskId = currentText.toString().trim();
      } else if (qName.equals("rootDeviceType")) {
         rootDeviceType = RootDeviceType.fromValue(currentText.toString().trim());
      } else if (qName.equals("rootDeviceName")) {
         rootDeviceName = currentText.toString().trim();
      } else if (qName.equals("item")) {
         if (inBlockDeviceMapping) {
            ebsBlockDevices.put(deviceName, new Image.EbsBlockDevice(snapshotId, volumeSize,
                     deleteOnTermination));
            this.deviceName = null;
            this.snapshotId = null;
            this.volumeSize = 0;
            this.deleteOnTermination = true;
         } else if (!inProductCodes) {
            try {
               String region = EC2Utils.findRegionInArgsOrNull(request);
               if (region == null)
                  region = defaultRegion;
               contents.add(new Image(region, architecture,
                        this.name, description, imageId, imageLocation, imageOwnerId, imageState,
                        imageType, isPublic, productCodes, kernelId, platform, ramdiskId,
                        rootDeviceType, rootDeviceName, ebsBlockDevices));
            } catch (NullPointerException e) {
               logger.warn(e, "malformed image: %s", imageId);
            }
            this.name = null;
            this.description = null;
            this.architecture = null;
            this.imageId = null;
            this.imageLocation = null;
            this.imageOwnerId = null;
            this.imageState = null;
            this.imageType = null;
            this.isPublic = false;
            this.kernelId = null;
            this.platform = null;
            this.productCodes = Sets.newHashSet();
            this.ramdiskId = null;
            this.rootDeviceType = null;
            this.rootDeviceName = null;
            this.ebsBlockDevices = Maps.newHashMap();
         }

      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
