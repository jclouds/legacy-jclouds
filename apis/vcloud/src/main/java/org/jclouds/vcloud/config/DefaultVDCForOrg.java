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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.suppliers.OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DefaultVDCForOrg implements Function<ReferenceType, ReferenceType> {
   private final OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault selector;
   private final Supplier<Map<String, Org>> nameToOrg;

   @Inject
   public DefaultVDCForOrg(ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull,
            @VDC Predicate<ReferenceType> defaultSelector, Supplier<Map<String, Org>> nameToOrg) {
      this.selector = new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(checkNotNull(
               valueOfConfigurationKeyOrNull, "valueOfConfigurationKeyOrNull"), PROPERTY_VCLOUD_DEFAULT_VDC,
               checkNotNull(defaultSelector, "defaultSelector"));
      this.nameToOrg = checkNotNull(nameToOrg, "nameToOrg");
   }

   @Override
   public ReferenceType apply(ReferenceType defaultOrg) {
      Org org = nameToOrg.get().get(defaultOrg.getName());
      checkState(org != null, "could not retrieve Org at %s", defaultOrg);
      return selector.apply(org.getVDCs().values());
   }

}