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
import org.jclouds.trmk.vcloud_0_8.binders.BindAddNodeServiceToXmlPayload;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class AddNodeOptions extends BindAddNodeServiceToXmlPayload {

   @VisibleForTesting
   String description = null;
   @VisibleForTesting
   String enabled = "true";
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      Map<String, Object> copy = Maps.newHashMap();
      copy.putAll(postParams);
      copy.put("description", description);
      copy.put("enabled", enabled);
      return super.bindToRequest(request, copy);
   }

   public AddNodeOptions disabled() {
      this.enabled = "false";
      return this;
   }

   public AddNodeOptions withDescription(String description) {
      this.description = description;
      return this;
   }

   public static class Builder {

      /**
       * @see AddNodeOptions#withDescription(String)
       */
      public static AddNodeOptions withDescription(String description) {
         AddNodeOptions options = new AddNodeOptions();
         return options.withDescription(description);
      }

      /**
       * @see AddNodeOptions#disabled()
       */
      public static AddNodeOptions disabled() {
         AddNodeOptions options = new AddNodeOptions();
         return options.disabled();
      }
   }
}
