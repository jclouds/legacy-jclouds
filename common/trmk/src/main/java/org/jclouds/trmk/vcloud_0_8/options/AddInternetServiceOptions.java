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
package org.jclouds.trmk.vcloud_0_8.options;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.trmk.vcloud_0_8.binders.BindAddInternetServiceToXmlPayload;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class AddInternetServiceOptions extends BindAddInternetServiceToXmlPayload {

   @VisibleForTesting
   String description = null;
   @VisibleForTesting
   String enabled = "true";
   @VisibleForTesting
   Boolean monitorEnabled = null;

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      ImmutableMap.Builder<String, Object> copy = ImmutableMap.builder();
      copy.putAll(postParams);
      if (description != null)
         copy.put("description", description);
      copy.put("enabled", enabled);
      if (monitorEnabled != null)
         copy.put("monitor", monitorEnabled.toString());
      return super.bindToRequest(request, copy.build());
   }

   public AddInternetServiceOptions disabled() {
      this.enabled = "false";
      return this;
   }

   public AddInternetServiceOptions monitorDisabled() {
      this.monitorEnabled = false;
      return this;
   }

   public AddInternetServiceOptions withDescription(String description) {
      this.description = description;
      return this;
   }

   public static class Builder {

      /**
       * @see AddInternetServiceOptions#withDescription(String)
       */
      public static AddInternetServiceOptions withDescription(String description) {
         AddInternetServiceOptions options = new AddInternetServiceOptions();
         return options.withDescription(description);
      }

      /**
       * @see AddInternetServiceOptions#monitorDisabled()
       */
      public static AddInternetServiceOptions monitorDisabled() {
         AddInternetServiceOptions options = new AddInternetServiceOptions();
         return options.monitorDisabled();
      }

      /**
       * @see AddInternetServiceOptions#disabled()
       */
      public static AddInternetServiceOptions disabled() {
         AddInternetServiceOptions options = new AddInternetServiceOptions();
         return options.disabled();
      }
   }
}
