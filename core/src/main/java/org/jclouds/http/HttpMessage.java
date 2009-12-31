/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Provides base functionality of HTTP requests and responses.
 * 
 * @author Adrian Cole
 * 
 */
public class HttpMessage {

   /**
    * synchronized as there is no concurrent version. Headers may change in flight due to redirects.
    */
   protected Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap
            .<String, String> create());

   public Multimap<String, String> getHeaders() {
      return headers;
   }

   /**
    * try to get the value, then try as lowercase.
    */
   public String getFirstHeaderOrNull(String string) {
      Collection<String> values = headers.get(string);
      if (values.size() == 0)
         values = headers.get(string.toLowerCase());
      return (values.size() >= 1) ? values.iterator().next() : null;
   }

}