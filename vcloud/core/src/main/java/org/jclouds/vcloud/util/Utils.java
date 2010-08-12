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

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.domain.internal.TaskImpl.ErrorImpl;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static NamedResource newNamedResource(Attributes attributes) {
      String uri = attributes.getValue(attributes.getIndex("href"));
      String id = uri.substring(uri.lastIndexOf('/') + 1);
      return new NamedResourceImpl(id, attributes.getValue(attributes
            .getIndex("name")), attributes
            .getValue(attributes.getIndex("type")), URI.create(uri));
   }

   public static Task.Error newError(Attributes attributes) {
      return new ErrorImpl(attrOrNull(attributes, "message"), attrOrNull(
            attributes, "majorErrorCode"), attrOrNull(attributes,
            "minorErrorCode"));
   }

   private static String attrOrNull(Attributes attributes, String attr) {
      return attributes.getIndex(attr) >= 0 ? attributes.getValue(attributes
            .getIndex(attr)) : null;
   }

   public static void putNamedResource(Map<String, NamedResource> map,
         Attributes attributes) {
      map.put(attributes.getValue(attributes.getIndex("name")),
            newNamedResource(attributes));
   }
}
