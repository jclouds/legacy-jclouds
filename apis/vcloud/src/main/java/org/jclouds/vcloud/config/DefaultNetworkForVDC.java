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
package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.suppliers.OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DefaultNetworkForVDC implements Function<ReferenceType, ReferenceType> {

   private final OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault selector;
   private final Supplier<Map<URI, VDC>> uriToVDC;

   @Inject
   public DefaultNetworkForVDC(ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull,
            @Network Predicate<ReferenceType> defaultSelector, Supplier<Map<URI, VDC>> uriToVDC) {
      this.selector = new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(checkNotNull(
               valueOfConfigurationKeyOrNull, "valueOfConfigurationKeyOrNull"), PROPERTY_VCLOUD_DEFAULT_NETWORK,
               checkNotNull(defaultSelector, "defaultSelector"));
      this.uriToVDC = checkNotNull(uriToVDC, "uriToVDC");
   }

   @Override
   public ReferenceType apply(ReferenceType defaultVDC) {
      org.jclouds.vcloud.domain.VDC vDC = uriToVDC.get().get(defaultVDC.getHref());
      checkState(vDC != null, "could not retrieve VDC at %s", defaultVDC);
      return selector.apply(vDC.getAvailableNetworks().values());
   }

}