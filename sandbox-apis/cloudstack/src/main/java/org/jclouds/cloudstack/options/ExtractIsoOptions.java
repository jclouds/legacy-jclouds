/**
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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the Iso extractIso method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#extractIso
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#extractIso
 * @author Richard Downer
 */
public class ExtractIsoOptions extends BaseHttpRequestOptions {

   public static final ExtractIsoOptions NONE = new ExtractIsoOptions(); 

   /**
    * @param url the url to which the ISO would be extracted
    */
   public ExtractIsoOptions url(String url) {
      this.queryParameters.replaceValues("url", ImmutableSet.of(url + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param url the url to which the ISO would be extracted
       */
      public static ExtractIsoOptions url(String url) {
         return new ExtractIsoOptions().url(url);
      }
   }

}
