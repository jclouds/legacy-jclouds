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

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.AWSImage;
import org.jclouds.ec2.xml.BaseDescribeImagesResponseHandler;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http://ec2.amazonaws.com/doc/2010-06-15/"
 * 
 * @author Andrew Kennedy
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html" />
 */
public class AWSDescribeImagesResponseHandler extends BaseDescribeImagesResponseHandler<AWSImage> {

   private boolean inTagSet;
   private Map<String, String> tagSet = Maps.newHashMap();
   private String tagKey;
   private String tagValue;

   @Inject
   public AWSDescribeImagesResponseHandler(@Region Supplier<String> defaultRegion) {
      super(defaultRegion);
   }

   @Override
   public Set<AWSImage> getResult() {
      return contents;
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      } else super.startElement(uri, name, qName, attrs);
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
      } else if (inTagSet) {
         if (equalsOrSuffix(qName, "key")) {
            tagKey = currentOrNull(currentText);
         } else if (equalsOrSuffix(qName, "value")) {
            tagValue = currentOrNull(currentText);
         }
      }
      super.endElement(uri, name, qName);
   }

   @Override
   protected void endItem() {
      if (inTagSet) {
         // Must convert value to empty String for ImmutableMap
         tagSet.put(this.tagKey, Strings.nullToEmpty(this.tagValue));
         tagKey = null;
         tagValue = null;
      } else super.endItem();
   }

   @Override
   protected AWSImage newImage(String region) {
      AWSImage image = null;
      try {
         image = new AWSImage(region, architecture, this.name, description, imageId, imageLocation, imageOwnerId, imageState,
               rawState, imageType, isPublic, productCodes, kernelId, platform, ramdiskId, rootDeviceType, rootDeviceName,
               ebsBlockDevices, virtualizationType, hypervisor, tagSet);
      } catch (NullPointerException e) {
         logger.warn(e, "malformed image: %s", imageId);
      }
      return image;
   }

   @Override
   protected void endImage() {
      tagSet = Maps.newHashMap();
      tagKey = null;
      tagValue = null;
      super.endImage();
   }
}
