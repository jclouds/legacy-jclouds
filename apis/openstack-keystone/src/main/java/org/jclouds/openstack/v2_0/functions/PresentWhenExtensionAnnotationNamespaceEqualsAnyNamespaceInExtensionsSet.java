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
package org.jclouds.openstack.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.predicates.ExtensionPredicates;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * We use the annotation {@link org.jclouds.openstack.services.Extension} to
 * bind a class that implements an extension API to an {@link Extension}.
 * 
 * @author Adrian Cole
 * 
 */
public class PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet implements
      ImplicitOptionalConverter {
   private final LoadingCache<String, Set<? extends Extension>> extensions;
   private final Multimap<URI, URI> aliases;
   
   @Inject
   public PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet(
         LoadingCache<String, Set<? extends Extension>> extensions,
         Multimap<URI, URI> aliases) {
      this.extensions = checkNotNull(extensions, "extensions");
      this.aliases = aliases == null ? ImmutableMultimap.<URI, URI>of() : ImmutableMultimap.copyOf(aliases);
   }

   @Override
   public Optional<Object> apply(ClassMethodArgsAndReturnVal input) {
      Optional<org.jclouds.openstack.v2_0.services.Extension> ext = Optional.fromNullable(input.getClazz().getAnnotation(
            org.jclouds.openstack.v2_0.services.Extension.class));
      if (ext.isPresent()) {
         URI namespace = URI.create(ext.get().namespace());
         if (input.getArgs().length == 0) {
	        if (Iterables.any(extensions.getUnchecked(""),
	              ExtensionPredicates.namespaceOrAliasEquals(namespace, aliases.get(namespace))))
	           return Optional.of(input.getReturnVal());
	     } else if (input.getArgs().length == 1) {
	        if (Iterables.any(extensions.getUnchecked(checkNotNull(input.getArgs()[0], "arg[0] in %s", input).toString()),
	              ExtensionPredicates.namespaceOrAliasEquals(namespace, aliases.get(namespace))))
	           return Optional.of(input.getReturnVal());
         } else {
            throw new RuntimeException(String.format("expecting zero or one args %s", input));
         }
         return Optional.absent();
      } else {
         // No extension annotation, should check whether to return absent
	     return Optional.of(input.getReturnVal());
      }
   }

   @Override
   public String toString() {
      return "presentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet()";
   }

}
