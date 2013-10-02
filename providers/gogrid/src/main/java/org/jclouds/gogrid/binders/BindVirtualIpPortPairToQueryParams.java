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
package org.jclouds.gogrid.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.VIRTUAL_IP_KEY;

import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMultimap;

/**
 * Binds a virtual IP to the request.
 * 
 * The {@link IpPortPair} must have a {@link IpPortPair#ip} set with a valid IP address.
 * 
 * @author Oleksiy Yarmula
 */
public class BindVirtualIpPortPairToQueryParams implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input is null") instanceof IpPortPair,
               "this binder is only valid for a IpPortPair argument");

      IpPortPair ipPortPair = (IpPortPair) input;

      checkNotNull(ipPortPair.getIp(), "There must be an IP address defined");
      checkNotNull(ipPortPair.getIp().getIp(), "There must be an IP address defined in Ip object");
      checkState(ipPortPair.getPort() > 0, "The port number must be a positive integer");

      ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.<String, String> builder();

      builder.put(VIRTUAL_IP_KEY + "ip", ipPortPair.getIp().getIp());
      builder.put(VIRTUAL_IP_KEY + "port", String.valueOf(ipPortPair.getPort()));
      
      return (R) request.toBuilder().replaceQueryParams(builder.build()).build();
   }
}
