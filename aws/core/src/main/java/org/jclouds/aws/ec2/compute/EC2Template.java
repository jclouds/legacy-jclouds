/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;

import com.google.common.annotations.VisibleForTesting;

/**
 * 
 * @author Adrian Cole
 */
public class EC2Template implements Template {
   private final EC2Client client;
   private final Map<Architecture, Map<OperatingSystem, Map<Region, String>>> imageAmiIdMap;
   private EC2Size size;
   private OperatingSystem operatingSystem;
   private Region region;
   private transient Image image;

   public EC2Template(EC2Client client,
            Map<Architecture, Map<OperatingSystem, Map<Region, String>>> imageAmiIdMap,
            EC2Size size, OperatingSystem operatingSystem, Region region, @Nullable Image image) {
      this.client = client;
      this.size = size;
      this.operatingSystem = operatingSystem;
      this.region = region;
      this.imageAmiIdMap = imageAmiIdMap;
      this.image = image != null ? image : resolveImage();
   }

   EC2Template(EC2Client client,
            Map<Architecture, Map<OperatingSystem, Map<Region, String>>> imageAmiIdMap,
            String location) {
      this(client, imageAmiIdMap, EC2Size.M1_SMALL, OperatingSystem.UBUNTU, Region
               .fromValue(location), null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EC2Size getSize() {
      return size;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Image getImage() {
      return image;
   }

   @VisibleForTesting
   Image resolveImage() {
      Architecture architecture = size.supportsArchitecture(Architecture.X86_64) ? Architecture.X86_64
               : Architecture.X86_32;
      String ami = checkNotNull(
               checkNotNull(
                        checkNotNull(
                                 imageAmiIdMap.get(architecture),
                                 String.format(
                                          "architecture %s not supported.  Valid choices %s: ",
                                          architecture, imageAmiIdMap.keySet())).get(
                                 operatingSystem),
                        String
                                 .format(
                                          "operatingSystem %s not supported for architecture %s.  Valid choices %s: ",
                                          operatingSystem, architecture, imageAmiIdMap.get(
                                                   architecture).keySet())).get(region),
               String
                        .format(
                                 "region %s not supported for operatingSystem %s, architecture %s.  Valid choices %s: ",
                                 region, operatingSystem, architecture, imageAmiIdMap.get(
                                          architecture).get(operatingSystem).keySet()));
      return EC2Utils.newImage(client, region, operatingSystem, architecture, ami);
   }

   public Template asSize(EC2Size size) {
      this.size = size;
      this.image = resolveImage();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template smallest() {
      return asSize(EC2Size.M1_SMALL);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template biggest() {
      return asSize(EC2Size.M2_4XLARGE);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template fastest() {
      return asSize(EC2Size.C1_XLARGE);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template inLocation(String location) {
      this.region = Region.fromValue(location);
      this.image = resolveImage();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template os(OperatingSystem os) {
      this.operatingSystem = os;
      this.image = resolveImage();
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      EC2Template other = (EC2Template) obj;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "EC2Template [image=" + image + ", operatingSystem=" + operatingSystem + ", region="
               + region + ", size=" + size + "]";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Object clone() throws CloneNotSupportedException {
      return new EC2Template(client, imageAmiIdMap, size, operatingSystem, region, image);
   }

   public Region getRegion() {
      return region;
   }

}
