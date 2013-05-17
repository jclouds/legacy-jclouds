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
 * Options used to control how a template is created.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/createTemplate.html"
 *      />
 * @author Richard Downer
 */
public class DeleteTemplateOptions extends BaseHttpRequestOptions {

   public static final DeleteTemplateOptions NONE = new DeleteTemplateOptions();

   /**
    * @param zoneId selects the template's zoneId.
    */
   public DeleteTemplateOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see DeleteTemplateOptions#zoneId
       */
      public static DeleteTemplateOptions zoneId(String id) {
         DeleteTemplateOptions options = new DeleteTemplateOptions();
         return options.zoneId(id);
      }

   }

}
