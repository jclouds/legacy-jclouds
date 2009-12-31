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
package org.jclouds.http.options;

import java.util.Collection;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @see HttpRequestOptions
 * @author Adrian Cole
 * 
 */
public class BaseHttpRequestOptions implements HttpRequestOptions {

   protected Multimap<String, String> matrixParameters = LinkedHashMultimap.create();
   protected Multimap<String, String> formParameters = LinkedHashMultimap.create();
   protected Multimap<String, String> queryParameters = LinkedHashMultimap.create();
   protected Multimap<String, String> headers = LinkedHashMultimap.create();
   protected String payload;
   protected String pathSuffix;

   public String buildStringPayload() {
      return payload;
   }

   protected String getFirstMatrixOrNull(String string) {
      Collection<String> values = matrixParameters.get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
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

   /**
    * {@inheritDoc}
    */
   public Multimap<String, String> buildMatrixParameters() {
      return matrixParameters;
   }

   public String buildPathSuffix() {
      return pathSuffix;
   }

   public Multimap<String, String> buildFormParameters() {
      return formParameters;
   }

}