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
package org.jclouds.savvis.vpdc.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.savvis.vpdc.VPDCClient;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.VDC;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class NetworkToLocation implements Function<Network, Location> {
   // rest/api/v0.8/org/1/vdc/22/network/VM-Tier01
   public static final Pattern netPattern = Pattern.compile(".*org/([0-9.]+)/vdc/([0-9.]+)/network/(.*)$");

   private final String providerName;
   private final Supplier<URI> endpoint;
   private final Set<String> isoCodes;
   private VPDCClient client;

   @Inject
   public NetworkToLocation(@Iso3166 Set<String> isoCodes, @Provider String providerName, @Provider Supplier<URI> endpoint,
            VPDCClient client) {
      this.providerName = checkNotNull(providerName, "providerName");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.isoCodes = checkNotNull(isoCodes, "isoCodes");
      this.client = checkNotNull(client, "client");
   }

   @Override
   public Location apply(Network from) {
      Matcher matcher = netPattern.matcher(from.getHref().toASCIIString());
      if (matcher.find()) {
         Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id(providerName).description(
                  endpoint.get().toASCIIString()).iso3166Codes(isoCodes).build();

         Org org = client.getBrowsingClient().getOrg(matcher.group(1));

         Location orgLocation = new LocationBuilder().scope(LocationScope.REGION).id(org.getId()).description(
                  org.getDescription()).parent(provider).build();

         VDC vdc = client.getBrowsingClient().getVDCInOrg(org.getId(), matcher.group(2));

         Location vdcLocation = new LocationBuilder().scope(LocationScope.ZONE).id(vdc.getId()).description(
                  vdc.getDescription()).parent(orgLocation).build();

         return new LocationBuilder().scope(LocationScope.NETWORK).id(from.getId()).description(from.getName()).parent(
                  vdcLocation).build();
      } else {
         throw new IllegalArgumentException("network unparsable: " + from);
      }

   }
}
