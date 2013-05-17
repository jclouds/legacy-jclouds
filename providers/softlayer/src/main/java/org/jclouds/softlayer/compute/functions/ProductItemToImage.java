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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * @author Jason King
 */
@Singleton
public class ProductItemToImage implements Function<ProductItem, Image> {

   /**
    * Pattern to capture the number of bits e.g. "a (32 bit) os"
    */
   private static final Pattern OS_BITS_PATTERN = Pattern.compile(".*\\((\\d+) ?bit\\).*");

   private static final String CENTOS = "CentOS";
   private static final String DEBIAN = "Debian GNU/Linux";
   private static final String FEDORA = "Fedora Release";
   private static final String RHEL = "Red Hat Enterprise Linux";
   private static final String UBUNTU = "Ubuntu Linux";
   private static final String WINDOWS = "Windows Server";
   private static final String CLOUD_LINUX = "CloudLinux";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(ProductItem productItem) {
      checkNotNull(productItem, "productItem");
      String description = checkNotNull(productItem.getDescription(), "productItem.description");

      OsFamily osFamily = osFamily().apply(description);
      if (osFamily == OsFamily.UNRECOGNIZED) {
         logger.debug("Cannot determine os family for item: %s", productItem);
      }
      Integer bits = osBits().apply(description);
      if (bits == null) {
         logger.debug("Cannot determine os bits for item: %s", productItem);
      }
      String osVersion = osVersion().apply(description);
      if (osVersion == null) {
         logger.debug("Cannot determine os version for item: %s", productItem);
      }
      OperatingSystem os = OperatingSystem.builder()
            .description(description)
            .family(osFamily)
            .version(osVersion)
            .is64Bit(Objects.equal(bits, 64))
            .build();

      return new ImageBuilder()
            .ids(imageId().apply(productItem))
            .description(description)
            .operatingSystem(os)
            .status(Image.Status.AVAILABLE)
            .build();
   }

   /**
    * Parses the item description to determine the OSFamily
    *
    * @return the @see OsFamily or OsFamily.UNRECOGNIZED
    */
   public static Function<String, OsFamily> osFamily() {
      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(final String description) {
            if (description != null) {
               if (description.startsWith(CENTOS)) return OsFamily.CENTOS;
               else if (description.startsWith(DEBIAN)) return OsFamily.DEBIAN;
               else if (description.startsWith(FEDORA)) return OsFamily.FEDORA;
               else if (description.startsWith(RHEL)) return OsFamily.RHEL;
               else if (description.startsWith(UBUNTU)) return OsFamily.UBUNTU;
               else if (description.startsWith(WINDOWS)) return OsFamily.WINDOWS;
               else if (description.startsWith(CLOUD_LINUX)) return OsFamily.CLOUD_LINUX;
            }

            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   /**
    * Parses the item description to determine the os version
    *
    * @return the version or null if the version cannot be determined
    */
   public static Function<String, String> osVersion() {
      return new Function<String, String>() {
         @Override
         public String apply(final String description) {
            OsFamily family = osFamily().apply(description);

            if (Objects.equal(family, OsFamily.CENTOS)) return parseVersion(description, CENTOS);
            else if (Objects.equal(family, OsFamily.DEBIAN)) return parseVersion(description, DEBIAN);
            else if (Objects.equal(family, OsFamily.FEDORA)) return parseVersion(description, FEDORA);
            else if (Objects.equal(family, OsFamily.RHEL)) return parseVersion(description, RHEL);
            else if (Objects.equal(family, OsFamily.UBUNTU)) return parseVersion(description, UBUNTU);
            else if (Objects.equal(family, OsFamily.WINDOWS)) return parseVersion(description, WINDOWS);
            else if (Objects.equal(family, OsFamily.CLOUD_LINUX)) return parseVersion(description, CLOUD_LINUX);

            return null;
         }
      };
   }

   private static String parseVersion(String description, String os) {
      String noOsName = description.replaceFirst(os, "").trim();
      return noOsName.split(" ")[0];
   }

   /**
    * Parses the item description to determine the number of OS bits
    * Expects the number to be in parenthesis and to contain the word "bit".
    * The following return 64: "A (64 bit) OS", "A (64bit) OS"
    *
    * @return the number of bits or null if the number of bits cannot be determined
    */
   public static Function<String, Integer> osBits() {
      return new Function<String, Integer>() {
         @Override
         public Integer apply(String description) {
            if (description != null) {
               Matcher m = OS_BITS_PATTERN.matcher(description);
               if (m.matches()) {
                  return Integer.parseInt(m.group(1));
               }
            }

            return null;
         }
      };
   }

   /**
    * Generates an id for an Image.
    *
    * @return the generated id
    */
   public static Function<ProductItem, String> imageId() {
      return new Function<ProductItem, String>() {
         @Override
         public String apply(ProductItem productItem) {
            checkNotNull(productItem, "productItem");
            ProductItemPrice price = ProductItems.price().apply(productItem);
            return "" + price.getId();
         }
      };
   }

}
