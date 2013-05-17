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
import static org.jclouds.gogrid.reference.GoGridQueryParams.IP_STATE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IP_TYPE_KEY;

import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.IpType;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Oleksiy Yarmula
 */
public class GetIpListOptions extends BaseHttpRequestOptions {

   public static final GetIpListOptions NONE = new GetIpListOptions();

   public GetIpListOptions onlyAssigned() {
      checkState(!queryParameters.containsKey(IP_STATE_KEY),
               "Can't have multiple values for whether IP is assigned");
      queryParameters.put(IP_STATE_KEY, IpState.ASSIGNED.toString());
      return this;
   }

   public GetIpListOptions onlyUnassigned() {
      checkState(!queryParameters.containsKey(IP_STATE_KEY),
               "Can't have multiple values for whether IP is assigned");
      queryParameters.put(IP_STATE_KEY, IpState.UNASSIGNED.toString());
      return this;
   }

   public GetIpListOptions onlyWithType(IpType type) {
      checkState(!queryParameters.containsKey(IP_TYPE_KEY),
               "Can't have multiple values for ip type limit");
      queryParameters.put(IP_TYPE_KEY, type.toString());
      return this;
   }

   public GetIpListOptions inDatacenter(String datacenterId) {
      checkState(!queryParameters.containsKey(DATACENTER_KEY), "Can't have duplicate datacenter id");
      queryParameters.put(DATACENTER_KEY, datacenterId);
      return this;
   }

   public static class Builder {

      public GetIpListOptions inDatacenter(String datacenterId) {
         return new GetIpListOptions().inDatacenter(checkNotNull(datacenterId));
      }

      public GetIpListOptions create() {
         return new GetIpListOptions();
      }

      public GetIpListOptions limitToType(IpType type) {
         return new GetIpListOptions().onlyWithType(type);
      }

      public GetIpListOptions unassignedPublicIps() {
         return new GetIpListOptions().onlyWithType(IpType.PUBLIC).onlyUnassigned();
      }
   }

}
