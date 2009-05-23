/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http.options;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @see HttpRequestOptions
 * @author Adrian Cole
 * 
 */
public class BaseHttpRequestOptions implements HttpRequestOptions {

   protected SortedMap<String, String> parameters = new TreeMap<String, String>();
   protected Multimap<String, String> headers = HashMultimap.create();
   protected String payload;

   public String buildPayload() {
      return payload;
   }

   protected String getFirstHeaderOrNull(String string) {
      Collection<String> values = headers.get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   protected void replaceHeader(String key, String value) {
      headers.removeAll(key);
      headers.put(key, value);
   }

   /**
    * {@inheritDoc}
    */
   public Multimap<String, String> buildRequestHeaders() {
      return headers;
   }

   /**
    * {@inheritDoc}
    */
   public String buildQueryString() {
      StringBuilder builder = new StringBuilder("");
      if (parameters.size() > 0) {
         builder.append("?");
         for (Iterator<Entry<String, String>> i = parameters.entrySet().iterator(); i.hasNext();) {
            Entry<String, String> entry = i.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            if (i.hasNext())
               builder.append("&");
         }
      }
      String returnVal = builder.toString();
      return returnVal;
   }

}