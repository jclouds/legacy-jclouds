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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
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
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;

import com.google.common.base.Function;

/**
 * @author Jason King
 */
@Singleton
public class ProductItemToImage implements Function<ProductItem, Image> {

   /**  Pattern to capture the number of bits e.g. "a (32 bit) os" */
   private static final Pattern OS_BITS_PATTERN = Pattern.compile(".*\\((\\d+) ?bit\\).*");

   private static final String CENTOS  = "CentOS";
   private static final String DEBIAN  = "Debian GNU/Linux";
   private static final String FEDORA  = "Fedora Release";
   private static final String RHEL    = "Red Hat Enterprise Linux";
   private static final String UBUNTU  = "Ubuntu Linux";
   private static final String WINDOWS = "Windows Server";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(ProductItem productItem) {
      checkNotNull(productItem,"productItem");

      OsFamily family = osFamily().apply(productItem);
      Integer bits = osBits().apply(productItem);
      OperatingSystem os = OperatingSystem.builder()
                              .description(productItem.getDescription())
                              .family(family)
                              .version(osVersion().apply(productItem))
                              .is64Bit(bits.equals(64))
                              .build();

      return new ImageBuilder()
            .ids(imageId().apply(productItem))
            .description(productItem.getDescription())
            .defaultCredentials(new Credentials("root", null))
            .operatingSystem(os)
            .build();
   }

   /**
    * Parses the item description to determine the OSFamily
    * @return the @see OsFamily or OsFamily.UNRECOGNIZED
    */
    public static Function<ProductItem,OsFamily> osFamily() {
       return new Function<ProductItem,OsFamily>() {
            @Override
            public OsFamily apply(ProductItem productItem) {
               checkNotNull(productItem,"productItem");

               final String description = productItem.getDescription();
               if(description.startsWith(CENTOS)) return OsFamily.CENTOS;
               else if(description.startsWith(DEBIAN)) return OsFamily.DEBIAN;
               else if(description.startsWith(FEDORA)) return OsFamily.FEDORA;
               else if(description.startsWith(RHEL)) return OsFamily.RHEL;
               else if(description.startsWith(UBUNTU)) return OsFamily.UBUNTU;
               else if(description.startsWith(WINDOWS)) return OsFamily.WINDOWS;
               return OsFamily.UNRECOGNIZED;
            }
        };
    }

   /**
    * Parses the item description to determine the os version
    * @return the version
    * @throws java.util.NoSuchElementException if the version cannot be determined
    */
    public static Function<ProductItem,String> osVersion() {
       return new Function<ProductItem,String>() {
            @Override
            public String apply(ProductItem productItem) {
               checkNotNull(productItem,"productItem");

               final String description = productItem.getDescription();
               OsFamily family = osFamily().apply(productItem);
               if (family.equals(OsFamily.CENTOS)) return parseVersion(description, CENTOS);
               else if(family.equals(OsFamily.DEBIAN)) return parseVersion(description, DEBIAN);
               else if(family.equals(OsFamily.FEDORA)) return parseVersion(description, FEDORA);
               else if(family.equals(OsFamily.RHEL)) return parseVersion(description, RHEL);
               else if(family.equals(OsFamily.UBUNTU)) return parseVersion(description, UBUNTU);
               else if(family.equals(OsFamily.WINDOWS)) return parseVersion(description, WINDOWS);
               else throw new NoSuchElementException("No os version for item:"+productItem);
            }
        };
    }

    private static String parseVersion(String description, String os) {
       String noOsName = description.replaceFirst(os,"").trim();
       return noOsName.split(" ")[0];
    }

   /**
    * Parses the item description to determine the number of OS bits
    * Expects the number to be in parenthesis and to contain the word "bit".
    * The following return 64: "A (64 bit) OS", "A (64bit) OS"
    * @return the number of bits
    * @throws java.util.NoSuchElementException if the number of bits cannot be determined
    */
    public static Function<ProductItem,Integer> osBits() {
       return new Function<ProductItem,Integer>() {
            @Override
            public Integer apply(ProductItem productItem) {
               checkNotNull(productItem,"productItem");

               Matcher m = OS_BITS_PATTERN.matcher(productItem.getDescription());
               if (m.matches()) {
                  return Integer.parseInt(m.group(1));
               } else {
                  throw new NoSuchElementException("Cannot determine os-bits for item:"+productItem);
               }
            }
       };
    }

  /**
   * Generates an id for an Image.
   * @return the generated id
   */
   public static Function<ProductItem,String> imageId() {
      return new Function<ProductItem,String>() {
         @Override
         public String apply(ProductItem productItem) {
            checkNotNull(productItem,"productItem");
            ProductItemPrice price = ProductItems.price().apply(productItem);
            return ""+price.getId();
         }
      };
   }

}
