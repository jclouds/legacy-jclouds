/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */

package org.jclouds.googlecompute.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows to optionally specify a filter, max results and a page token for <code>listFirstPage()</code> REST methods.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations/listFirstPage"/>
 */
public class ListOptions extends BaseHttpRequestOptions {

   /**
    * Optional. Filter expression for filtering listed resources, in the form filter={expression}. Your {expression}
    * must contain the following:
    * <p/>
    * {@code <field_name> <comparison_string> <literal_string>}
    * <ul>
    * <li>{@code <field_name>}:  The name of the field you want to compare. The field name must be valid for the
    * type of resource being filtered. Only atomic field types are supported (string, number,
    * boolean). Array and object fields are not currently supported.</li>
    * <li>{@code <comparison_string>}: The comparison string, either eq (equals) or ne (not equals).</li>
    * <li>{@code <literal_string>}: The literal string value to filter to. The literal value must be valid
    * for the type of field (string, number, boolean). For string fields, the literal value is interpreted as a
    * regular expression using RE2 syntax. The literal value must match the entire field. For example,
    * when filtering instances, name eq my_instance won't work, but name eq .*my_instance will work.</li>
    * </ul>
    * <p/>
    * For example:
    * <p/>
    * {@code filter=status ne RUNNING}
    * <p/>
    * The above filter returns only results whose status field does not equal RUNNING. You can also enclose your
    * literal string in single, double, or no quotes. For example, all three of the following would be valid
    * expressions:
    * <p/>
    * {@code filter=status ne "RUNNING"}<br/>
    * {@code filter=status ne 'RUNNING'}<br/>
    * {@code filter=status ne RUNNING}<br/>
    * <p/>
    * Complex regular expressions can also be used, like the following:
    * {@code name eq '."my_instance_[0-9]+'}
    */
   public ListOptions filter(String filter) {
      this.queryParameters.put("filter", checkNotNull(filter, "filter"));
      return this;
   }

   /**
    * Sets Maximum count of results to be returned. Maximum and default value is 100. Acceptable values are 0 to
    * 100, inclusive. (Default: 100)
    */
   public ListOptions maxResults(Integer maxResults) {
      this.queryParameters.put("maxResults", checkNotNull(maxResults, "maxResults") + "");
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#filter(String)
       */
      public ListOptions filter(String filter) {
         return new ListOptions().filter(filter);
      }

      /**
       * @see ListOptions#maxResults(Integer)
       */
      public ListOptions maxResults(Integer maxResults) {
         return new ListOptions().maxResults(maxResults);
      }
   }
}
