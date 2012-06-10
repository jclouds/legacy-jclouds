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
package org.jclouds.openstack.nova.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces;
import org.jclouds.openstack.nova.v2_0.predicates.ExtensionPredicates;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * We use the annotation {@link org.jclouds.openstack.services.Extension} to
 * bind a class that is an extension to an extension found in the
 * {@link org.jclouds.openstack.nova.v2_0.features.ExtensionClient#listExtensions} call.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet implements
      ImplicitOptionalConverter {
   private final LoadingCache<String, Set<Extension>> extensions;

   @com.google.inject.Inject(optional=true)
   @Named("openstack.nova.extensions")
   Multimap<URI, URI> aliases = ImmutableMultimap.<URI, URI>builder()
      .put(URI.create(ExtensionNamespaces.SECURITY_GROUPS),
           URI.create("http://docs.openstack.org/compute/ext/securitygroups/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.FLOATING_IPS),
           URI.create("http://docs.openstack.org/compute/ext/floating_ips/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.KEYPAIRS),
           URI.create("http://docs.openstack.org/compute/ext/keypairs/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.SIMPLE_TENANT_USAGE),
           URI.create("http://docs.openstack.org/compute/ext/os-simple-tenant-usage/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.HOSTS),
           URI.create("http://docs.openstack.org/compute/ext/hosts/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.VOLUMES),
           URI.create("http://docs.openstack.org/compute/ext/volumes/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.VIRTUAL_INTERFACES),
           URI.create("http://docs.openstack.org/compute/ext/virtual_interfaces/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.CREATESERVEREXT),
           URI.create("http://docs.openstack.org/compute/ext/createserverext/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.ADMIN_ACTIONS),
           URI.create("http://docs.openstack.org/compute/ext/admin-actions/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.AGGREGATES),
           URI.create("http://docs.openstack.org/compute/ext/aggregates/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.FLAVOR_EXTRA_SPECS),
           URI.create("http://docs.openstack.org/compute/ext/flavor_extra_specs/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.QUOTAS),
           URI.create("http://docs.openstack.org/compute/ext/quotas-sets/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.QUOTA_CLASSES),
           URI.create("http://docs.openstack.org/compute/ext/quota-classes-sets/api/v1.1"))
      .put(URI.create(ExtensionNamespaces.VOLUME_TYPES),
           URI.create("http://docs.openstack.org/compute/ext/volume_types/api/v1.1"))
      .build();
   
   @Inject
   public PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet(
         LoadingCache<String, Set<Extension>> extensions) {
      this.extensions = checkNotNull(extensions, "extensions");
   }

   @Override
   public Optional<Object> apply(ClassMethodArgsAndReturnVal input) {
      Optional<org.jclouds.openstack.v2_0.services.Extension> ext = Optional.fromNullable(input.getClazz().getAnnotation(
            org.jclouds.openstack.v2_0.services.Extension.class));
      if (ext.isPresent()) {
         checkState(input.getArgs() != null && input.getArgs().length == 1, "expecting an arg %s", input);
         URI namespace = URI.create(ext.get().namespace());
         if (Iterables.any(extensions.getUnchecked(checkNotNull(input.getArgs()[0], "arg[0] in %s", input).toString()),
               ExtensionPredicates.namespaceOrAliasEquals(namespace, aliases.get(namespace))))
            return Optional.of(input.getReturnVal());
      }
      return Optional.absent();
   }

   public String toString() {
      return "presentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet()";
   }

}