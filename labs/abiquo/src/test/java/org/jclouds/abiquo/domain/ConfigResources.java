/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain;

import static org.jclouds.abiquo.domain.DomainUtils.link;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.google.common.io.Resources;

/**
 * Enterprise domain utilities.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class ConfigResources {
   public static LicenseDto licensePost() {
      LicenseDto license = new LicenseDto();
      license.setCode(readLicense("license/expired"));
      license.setCustomerid("3bca6d1d-5fe2-42c5-82ea-a5276ea8c71c");
      return license;
   }

   public static CategoryDto categoryPost() {
      CategoryDto category = new CategoryDto();
      category.setName("category");
      category.setErasable(false);
      category.setDefaultCategory(false);
      return category;
   }

   public static CategoryDto categoryPut() {
      CategoryDto category = categoryPost();
      category.setId(1);
      category.addLink(new RESTLink("edit", "http://localhost/api/config/categories/1"));
      return category;
   }

   public static LicenseDto licensePut() {
      LicenseDto license = licensePost();
      license.setId(1);
      license.addLink(new RESTLink("edit", "http://localhost/api/config/licenses/1"));

      return license;
   }

   public static SystemPropertyDto propertyPut() {
      SystemPropertyDto property = new SystemPropertyDto();
      property.setId(1);
      property.setDescription("Time interval in seconds");
      property.setValue("10");
      property.setName("api.applibrary.ovfpackagesDownloadingProgressUpdateInterval");
      property.addLink(new RESTLink("edit", "http://localhost/api/config/properties/1"));

      return property;
   }

   public static String licensePutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<license>");
      buffer.append(link("/admin/enterprises/config/licenses/1", "edit"));
      buffer.append("<customerid>3bca6d1d-5fe2-42c5-82ea-a5276ea8c71c</customerid>");
      buffer.append("<code>" + readLicense("license/expired") + "</code>");
      buffer.append("<id>1</id>");
      buffer.append("</license>");
      return buffer.toString();
   }

   public static String licensePostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<license>");
      buffer.append("<customerid>3bca6d1d-5fe2-42c5-82ea-a5276ea8c71c</customerid>");
      buffer.append("<code>" + readLicense("license/expired") + "</code>");
      buffer.append("</license>");
      return buffer.toString();
   }

   public static String categoryPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<category>");
      buffer.append("<defaultCategory>false</defaultCategory>");
      buffer.append("<erasable>false</erasable>");
      buffer.append("<name>category</name>");
      buffer.append("</category>");
      return buffer.toString();
   }

   public static String categoryPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<category>");
      buffer.append(link("/config/categories/1", "edit"));
      buffer.append("<defaultCategory>false</defaultCategory>");
      buffer.append("<erasable>false</erasable>");
      buffer.append("<id>1</id>");
      buffer.append("<name>category</name>");
      buffer.append("</category>");
      return buffer.toString();
   }

   public static String iconPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<icon>");
      buffer.append(link("/config/icons/1", "edit"));
      buffer.append("<id>1</id>");
      buffer.append("<name>icon</name>");
      buffer.append("<path>http://www.pixeljoint.com/files/icons/mipreview1.gif</path>");
      buffer.append("</icon>");
      return buffer.toString();
   }

   public static String iconPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<icon>");
      buffer.append("<name>icon</name>");
      buffer.append("<path>http://www.pixeljoint.com/files/icons/mipreview1.gif</path>");
      buffer.append("</icon>");
      return buffer.toString();
   }

   public static String propertyPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<property>");
      buffer.append(link("/config/properties/1", "edit"));
      buffer.append("<description>Time interval in seconds</description>");
      buffer.append("<id>1</id>");
      buffer.append("<name>api.applibrary.ovfpackagesDownloadingProgressUpdateInterval</name>");
      buffer.append("<value>10</value>");
      buffer.append("</property>");
      return buffer.toString();
   }

   private static String readLicense(final String filename) {
      URL url = ConfigResources.class.getResource("/" + filename);
      try {
         return Resources.toString(url, Charset.defaultCharset());
      } catch (IOException e) {
         throw new RuntimeException("Could not read file " + filename);
      }
   }
}
