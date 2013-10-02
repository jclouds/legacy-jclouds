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
package org.jclouds.cloudstack.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * @author Richard Downer
 */
public class ExtractTemplateOptions extends BaseHttpRequestOptions {

   public static final ExtractTemplateOptions NONE = new ExtractTemplateOptions();

   /**
    * the url to which the ISO would be extracted
    */
   public ExtractTemplateOptions url(String url) {
      this.queryParameters.replaceValues("url", ImmutableSet.of(url));
      return this;
   }

   public static class Builder {

      public static ExtractTemplateOptions url(String url) {
         ExtractTemplateOptions options = new ExtractTemplateOptions();
         return options.url(url);
      }

   }

}
