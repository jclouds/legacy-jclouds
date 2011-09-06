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
package org.jclouds.vcloud.util;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudError;
import org.jclouds.vcloud.domain.VCloudError.MinorCode;
import org.jclouds.vcloud.domain.internal.ErrorImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static ReferenceType newReferenceType(Map<String, String> attributes, String defaultType) {
      String uri = attributes.get("href");
      String type = attributes.get("type");
      // savvis org has null href
      URI href = (uri != null) ? URI.create(uri) : null;
      return new ReferenceTypeImpl(attributes.get("name"), type != null ? type : defaultType, href);
   }

   public static ReferenceType newReferenceType(Map<String, String> attributes) {
      return newReferenceType(attributes, null);
   }

   public static VCloudError newError(Map<String, String> attributes) {

      String vendorSpecificErrorCode = attributes.get("vendorSpecificErrorCode");
      int errorCode;
      // remove this logic when vcloud 0.8 is gone
      try {
         errorCode = Integer.parseInt(attributes.get("majorErrorCode"));
      } catch (NumberFormatException e) {
         errorCode = 500;
         vendorSpecificErrorCode = attributes.get("majorErrorCode");
      }
      MinorCode minorErrorCode = attributes.containsKey("minorErrorCode") ? MinorCode.fromValue(attributes
               .get("minorErrorCode")) : null;
      if (minorErrorCode == null || minorErrorCode == MinorCode.UNRECOGNIZED) {
         vendorSpecificErrorCode = attributes.get("minorErrorCode");
      }

      return new ErrorImpl(attributes.get("message"), errorCode, minorErrorCode, vendorSpecificErrorCode, attributes
               .get("stackTrace"));
   }

   public static void putReferenceType(Map<String, ReferenceType> map, Map<String, String> attributes) {
      map.put(attributes.get("name"), newReferenceType(attributes));
   }
}
