/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.util;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.internal.TaskImpl.ErrorImpl;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {
   public static ReferenceType newNamedResource(Attributes attributes, String defaultType) {
      String uri = attributes.getValue(attributes.getIndex("href"));
      String type = attributes.getValue(attributes.getIndex("type"));
      return new ReferenceTypeImpl(attributes.getValue(attributes.getIndex("name")), type != null ? type : defaultType,
               URI.create(uri));
   }

   public static ReferenceType newNamedResource(Attributes attributes) {
      return newNamedResource(attributes, null);
   }

   public static Task.Error newError(Attributes attributes) {
      String minorErrorCode = attrOrNull(attributes, "minorErrorCode");
      String vendorSpecificErrorCode = attrOrNull(attributes, "vendorSpecificErrorCode");
      int errorCode;
      // remove this logic when vcloud 0.8 is gone
      try {
         errorCode = Integer.parseInt(attrOrNull(attributes, "majorErrorCode"));
      } catch (NumberFormatException e) {
         errorCode = 500;
         vendorSpecificErrorCode = attrOrNull(attributes, "majorErrorCode");
      }
      return new ErrorImpl(attrOrNull(attributes, "message"), errorCode, minorErrorCode, vendorSpecificErrorCode,
               attrOrNull(attributes, "stackTrace"));
   }

   public static String attrOrNull(Attributes attributes, String attr) {
      return attributes.getIndex(attr) >= 0 ? attributes.getValue(attributes.getIndex(attr)) : null;
   }

   public static void putNamedResource(Map<String, ReferenceType> map, Attributes attributes) {
      map.put(attributes.getValue(attributes.getIndex("name")), newNamedResource(attributes));
   }
}
