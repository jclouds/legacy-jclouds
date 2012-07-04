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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.util.Suppliers2;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.suppliers.OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DefaultOrgForUser implements Function<String, Supplier<ReferenceType>> {

   private final OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault selector;
   private final Supplier<VCloudSession> sessionSupplier;

   @Inject
   public DefaultOrgForUser(ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull,
         @Org Predicate<ReferenceType> defaultSelector, Supplier<VCloudSession> sessionSupplier) {
      this.selector = new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(checkNotNull(
            valueOfConfigurationKeyOrNull, "valueOfConfigurationKeyOrNull"), PROPERTY_VCLOUD_DEFAULT_ORG, checkNotNull(
            defaultSelector, "defaultSelector"));
      this.sessionSupplier = checkNotNull(sessionSupplier, "sessionSupplier");
   }

   @Override
   public Supplier<ReferenceType> apply(final String user) {
      return Suppliers2.compose(new Function<VCloudSession, ReferenceType>() {

         @Override
         public ReferenceType apply(VCloudSession session) {
            checkState(session != null, "could not retrieve Session at %s", user);
            return selector.apply(session.getOrgs().values());
         }

      }, sessionSupplier);
   }

}