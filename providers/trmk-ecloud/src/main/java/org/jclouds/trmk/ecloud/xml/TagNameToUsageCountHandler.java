/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.ecloud.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author Adrian Cole
 */
public class TagNameToUsageCountHandler extends ParseSax.HandlerWithResult<Map<String, Integer>> {
   protected StringBuilder currentText = new StringBuilder();

   private Builder<String, Integer> builder = ImmutableMap.<String, Integer> builder();
   private String name;

   public Map<String, Integer> getResult() {
      try {
         return builder.build();
      } finally {
         builder = ImmutableMap.<String, Integer> builder();
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Name")) {
         name = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "UsageCount")) {
         builder.put(name, new Integer(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
