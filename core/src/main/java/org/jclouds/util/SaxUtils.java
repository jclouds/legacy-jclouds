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

package org.jclouds.util;


import java.util.Map;


import org.xml.sax.Attributes;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public class SaxUtils {
   public static Map<String, String> cleanseAttributes(Attributes in) {
      Map<String, String> attrs = Maps.newLinkedHashMap();
      for (int i = 0; i < in.getLength(); i++) {
         String name = in.getQName(i);
         if (name.indexOf(':') != -1)
            name = name.substring(name.indexOf(':') + 1);
         attrs.put(name, in.getValue(i));
      }
      return attrs;
   }

}
