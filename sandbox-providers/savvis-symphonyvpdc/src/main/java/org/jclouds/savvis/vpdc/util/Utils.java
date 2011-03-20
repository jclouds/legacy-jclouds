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

package org.jclouds.savvis.vpdc.util;

import java.net.URI;
import java.util.Map;

import org.jclouds.savvis.vpdc.domain.Link;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {
  
   public static Resource newResource(Map<String, String> attributes, String defaultType) {
      String uri = attributes.get("href");
      String type = attributes.get("type");
      // savvis org has null href
      String id = null;
      URI href = null;
      if (uri != null) {
         href = URI.create(uri);
         id = uri.substring(uri.lastIndexOf('/') + 1);
      }
      return (attributes.containsKey("rel")) ? new Link(id, attributes.get("name"), type != null ? type : defaultType,
            href, attributes.get("rel")) : new Resource(id, attributes.get("name"), type != null ? type : defaultType,
            href);
   }

   public static Map<String, String> cleanseAttributes(Attributes in) {
      Builder<String, String> attrs = ImmutableMap.<String, String> builder();
      for (int i = 0; i < in.getLength(); i++) {
         String name = in.getQName(i);
         if (name.indexOf(':') != -1)
            name = name.substring(name.indexOf(':') + 1);
         attrs.put(name, in.getValue(i));
      }
      return attrs.build();
   }

   public static String currentOrNull(StringBuilder currentText) {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public static Resource newResource(Map<String, String> attributes) {
      return newResource(attributes, null);
   }
}
