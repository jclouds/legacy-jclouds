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
package org.jclouds.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class UriTemplates {

   /**
    * 
    * @param template
    *           URI template that can be in level 1 <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a> form.
    * 
    * @param variables
    *           to the URI template
    * @return expanded template, leaving any unresolved parameters literal
    */
   public static String expand(String template, Map<String, ?> variables) {
      if (checkNotNull(template, "template").length() < 3)
         return template.toString(); // skip expansion if there's no valid variables set. ex. {a} is the first valid
      checkNotNull(variables, "variables for %s", template);

      boolean inVar = false;
      StringBuilder var = new StringBuilder();
      StringBuilder builder = new StringBuilder();
      for (char c : Lists.charactersOf(template)) {
         switch (c) {
         case '{':
            inVar = true;
            break;
         case '}':
            inVar = false;
            String key = var.toString();
            Object value = variables.get(var.toString());
            if (value != null)
               builder.append(value);
            else
               builder.append('{').append(key).append('}');
            var = new StringBuilder();
            break;
         default:
            if (inVar)
               var.append(c);
            else
               builder.append(c);
         }
      }
      return builder.toString();
   }
}
