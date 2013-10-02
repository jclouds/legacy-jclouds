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
package org.jclouds.http.options;

import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @see HttpRequestOptions
 * @author Adrian Cole
 * 
 */
public class BaseHttpRequestOptions implements HttpRequestOptions {

   protected final Multimap<String, String> formParameters = LinkedHashMultimap.create();
   protected final Multimap<String, String> queryParameters = LinkedHashMultimap.create();
   protected final Multimap<String, String> headers = LinkedHashMultimap.create();
   protected String payload;
   protected String pathSuffix;

   public String buildStringPayload() {
      return payload;
   }

   protected String getFirstQueryOrNull(String string) {
      Collection<String> values = queryParameters.get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   protected String getFirstFormOrNull(String string) {
      Collection<String> values = formParameters.get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
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
   public Multimap<String, String> buildQueryParameters() {
      return queryParameters;
   }

   public String buildPathSuffix() {
      return pathSuffix;
   }

   public Multimap<String, String> buildFormParameters() {
      return formParameters;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((formParameters == null) ? 0 : formParameters.hashCode());
      result = prime * result + ((headers == null) ? 0 : headers.hashCode());
      result = prime * result + ((pathSuffix == null) ? 0 : pathSuffix.hashCode());
      result = prime * result + ((payload == null) ? 0 : payload.hashCode());
      result = prime * result + ((queryParameters == null) ? 0 : queryParameters.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseHttpRequestOptions other = (BaseHttpRequestOptions) obj;
      if (formParameters == null) {
         if (other.formParameters != null)
            return false;
      } else if (!formParameters.equals(other.formParameters))
         return false;
      if (headers == null) {
         if (other.headers != null)
            return false;
      } else if (!headers.equals(other.headers))
         return false;
      if (pathSuffix == null) {
         if (other.pathSuffix != null)
            return false;
      } else if (!pathSuffix.equals(other.pathSuffix))
         return false;
      if (payload == null) {
         if (other.payload != null)
            return false;
      } else if (!payload.equals(other.payload))
         return false;
      if (queryParameters == null) {
         if (other.queryParameters != null)
            return false;
      } else if (!queryParameters.equals(other.queryParameters))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("formParameters", formParameters)
            .add("headers", headers).add("pathSuffix", pathSuffix).add("payload", payload)
            .add("queryParameters", queryParameters).toString();
   }

}
