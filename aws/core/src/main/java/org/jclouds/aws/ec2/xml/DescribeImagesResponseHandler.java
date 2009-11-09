/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.Image.Architecture;
import org.jclouds.aws.ec2.domain.Image.ImageState;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http://ec2.amazonaws.com/doc/2009-08-15/"
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
 *      />
 */
public class DescribeImagesResponseHandler extends ParseSax.HandlerWithResult<SortedSet<Image>> {
   @Resource
   protected Logger logger = Logger.NULL;

   private SortedSet<Image> contents = Sets.newTreeSet();
   private StringBuilder currentText = new StringBuilder();

   private Architecture architecture;
   private String imageId;
   private String imageLocation;
   private String imageOwnerId;
   private ImageState imageState;
   private ImageType imageType;
   private boolean isPublic;
   private @Nullable
   String kernelId;
   private String platform;
   private Set<String> productCodes = Sets.newHashSet();
   private @Nullable
   String ramdiskId;
   private boolean inProductCodes;

   public SortedSet<Image> getResult() {
      return contents;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("productCodesSet")) {
         inProductCodes = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("architecture")) {
         architecture = Architecture.fromValue(currentText.toString().trim());
      } else if (qName.equals("imageId")) {
         imageId = currentText.toString().trim();
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
      } else if (qName.equals("productCodesSet")) {
         inProductCodes = false;
      } else if (qName.equals("ramdiskId")) {
         ramdiskId = currentText.toString().trim();
      } else if (qName.equals("item")) {
         if (!inProductCodes) {
            try {
               contents.add(new Image(architecture, imageId, imageLocation, imageOwnerId,
                        imageState, imageType, isPublic, kernelId, platform, productCodes,
                        ramdiskId));
            } catch (NullPointerException e) {
               logger.warn(e, "malformed image: %s", imageId);
            }
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
         }

      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
