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
 * Options to the GlobalUsageClient.generateUsageOptions() API call
 *
 * @author Richard Downer
 */
public class GenerateUsageRecordsOptions extends BaseHttpRequestOptions {

   public static final GenerateUsageRecordsOptions NONE = new GenerateUsageRecordsOptions();

   public static class Builder {
      public static GenerateUsageRecordsOptions domainId(String domainId) {
         GenerateUsageRecordsOptions options = new GenerateUsageRecordsOptions();
         return options.domainId(domainId);
      }
   }

   public GenerateUsageRecordsOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;
   }
}
