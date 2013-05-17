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
package org.jclouds.gogrid.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.DATACENTER_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IS_SANDBOX_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.SERVER_TYPE_KEY;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Oleksiy Yarmula
 */
public class GetServerListOptions extends BaseHttpRequestOptions {

   public static final GetServerListOptions NONE = new GetServerListOptions();

   public GetServerListOptions limitServerTypeTo(String serverType) {
      checkState(!queryParameters.containsKey(SERVER_TYPE_KEY),
               "Can't have duplicate server type limit");
      queryParameters.put(SERVER_TYPE_KEY, serverType);
      return this;
   }

   public GetServerListOptions inDatacenter(String datacenterId) {
      checkState(!queryParameters.containsKey(DATACENTER_KEY), "Can't have duplicate datacenter id");
      queryParameters.put(DATACENTER_KEY, datacenterId);
      return this;
   }

   public GetServerListOptions onlySandboxServers() {
      checkState(!queryParameters.containsKey(IS_SANDBOX_KEY),
               "Can't have duplicate sandbox type limit");
      queryParameters.put(IS_SANDBOX_KEY, "true");
      return this;
   }

   public GetServerListOptions excludeSandboxServers() {
      checkState(!queryParameters.containsKey(IS_SANDBOX_KEY),
               "Can't have duplicate sandbox type limit");
      queryParameters.put(IS_SANDBOX_KEY, "false");
      return this;
   }

   public static class Builder {
      public GetServerListOptions inDatacenter(String datacenterId) {
         GetServerListOptions getServerListOptions = new GetServerListOptions();
         getServerListOptions.inDatacenter(checkNotNull(datacenterId));
         return getServerListOptions;
      }

      public GetServerListOptions limitServerTypeTo(String serverType) {
         GetServerListOptions getServerListOptions = new GetServerListOptions();
         getServerListOptions.limitServerTypeTo(checkNotNull(serverType));
         return getServerListOptions;
      }

      public GetServerListOptions onlySandboxServers() {
         GetServerListOptions getServerListOptions = new GetServerListOptions();
         getServerListOptions.onlySandboxServers();
         return getServerListOptions;
      }

      public GetServerListOptions excludeSandboxServers() {
         GetServerListOptions getServerListOptions = new GetServerListOptions();
         getServerListOptions.excludeSandboxServers();
         return getServerListOptions;
      }

   }

}
