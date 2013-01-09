/*
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
package org.jclouds.vcloud.director.v1_5.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

/**
 * Resolves URN to its Admin HREF via the entity Resolver
 * 
 * @author Adrian Cole
 */
@Singleton
public final class URNToAdminHref implements Function<Object, URI> {
   private final LoadingCache<String, Entity> resolveEntityCache;

   @Inject
   public URNToAdminHref(LoadingCache<String, Entity> resolveEntityCache) {
      this.resolveEntityCache = checkNotNull(resolveEntityCache, "resolveEntityCache");
   }

   @Override
   public URI apply(@Nullable Object from) {
      checkArgument(checkNotNull(from, "urn") instanceof String, "urn is a String argument");
      Entity entity = resolveEntityCache.getUnchecked(from.toString());
      Optional<Link> link = Iterables.tryFind(entity.getLinks(), typeContainsAdmin);
      checkArgument(link.isPresent(), "no admin link found for entity %s", entity);
      return link.get().getHref();
   }

   private static final Predicate<Link> typeContainsAdmin = new Predicate<Link>() {
      @Override
      public boolean apply(Link in) {
         return in.getType().indexOf(".admin.") != -1;
      }
   };
}
