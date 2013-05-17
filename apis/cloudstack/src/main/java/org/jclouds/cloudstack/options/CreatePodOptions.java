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

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options to the GlobalPodClient.createPod API call.
 *
 * @author Richard Downer
 */
public class CreatePodOptions extends BaseHttpRequestOptions {

   public static final CreatePodOptions NONE = new CreatePodOptions();
   
   public static class Builder {

      public static CreatePodOptions allocationState(AllocationState allocationState) {
         return new CreatePodOptions().allocationState(allocationState);
      }

   }

   public CreatePodOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

}
