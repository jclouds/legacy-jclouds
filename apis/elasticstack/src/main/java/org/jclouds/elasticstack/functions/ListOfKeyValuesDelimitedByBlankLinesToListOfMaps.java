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

package org.jclouds.elasticstack.functions;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListOfKeyValuesDelimitedByBlankLinesToListOfMaps implements Function<String, List<Map<String, String>>> {

   @Override
   public List<Map<String, String>> apply(String from) {
      List<Map<String, String>> maps = Lists.newArrayList();
      for (String listOfKeyValues : Splitter.on("\n\n").split(from)) {
         if (!"".equals(listOfKeyValues)) {
            Map<String, String> map = Maps.newLinkedHashMap();
            for (String keyValueLine : Splitter.on('\n').split(listOfKeyValues)) {
               if (!"".equals(keyValueLine)) {
                  int firstIndex = keyValueLine.indexOf(' ');
                  if (firstIndex != -1) {
                     String key = keyValueLine.substring(0, firstIndex);
                     String value = keyValueLine.substring(firstIndex + 1).replace("\\n", "\n");
                     map.put(key, value);
                  }
               }
            }
            if (map.size() != 0)
               maps.add(map);
         }
      }
      return maps;
   }
}