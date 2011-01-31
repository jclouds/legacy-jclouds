/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.location.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class JustProvider implements Supplier<Set<? extends Location>> {
   private final String providerName;
   private final URI endpoint;
   private final Set<String> isoCodes;

   @Inject
   public JustProvider(@Iso3166 Set<String> isoCodes, @Provider String providerName, @Provider URI endpoint) {
      this.providerName = checkNotNull(providerName, "providerName");
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.isoCodes = checkNotNull(isoCodes, "isoCodes");
   }

   @Override
   public Set<? extends Location> get() {
      return ImmutableSet.of(new LocationBuilder().scope(LocationScope.PROVIDER).id(providerName).description(
               endpoint.toASCIIString()).iso3166Codes(isoCodes).build());
   }

}