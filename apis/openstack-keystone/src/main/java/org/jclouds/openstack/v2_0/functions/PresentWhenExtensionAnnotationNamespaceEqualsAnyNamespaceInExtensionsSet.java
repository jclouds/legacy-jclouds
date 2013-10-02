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
package org.jclouds.openstack.v2_0.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static org.jclouds.openstack.v2_0.predicates.ExtensionPredicates.namespaceOrAliasEquals;
import static org.jclouds.util.Optionals2.unwrapIfOptional;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Optional;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * We use the annotation {@link org.jclouds.openstack.services.Extension} to bind a class that implements an extension
 * API to an {@link Extension}.
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
         LoadingCache<String, Set<? extends Extension>> extensions, Multimap<URI, URI> aliases) {
      this.extensions = checkNotNull(extensions, "extensions");
      this.aliases = aliases == null ? ImmutableMultimap.<URI, URI> of() : ImmutableMultimap.copyOf(aliases);
   }

   @Override
   public Optional<Object> apply(InvocationSuccess input) {
      Class<?> target = unwrapIfOptional(input.getInvocation().getInvokable().getReturnType());
      Optional<org.jclouds.openstack.v2_0.services.Extension> ext = Optional.fromNullable(target
            .getAnnotation(org.jclouds.openstack.v2_0.services.Extension.class));
      if (ext.isPresent()) {
         URI namespace = URI.create(ext.get().namespace());
         List<Object> args = input.getInvocation().getArgs();
         if (args.isEmpty()) {
            if (any(extensions.getUnchecked(""), namespaceOrAliasEquals(namespace, aliases.get(namespace))))
               return input.getResult();
         } else if (args.size() == 1) {
            String arg0 = checkNotNull(args.get(0), "arg[0] in %s", input).toString();
            if (any(extensions.getUnchecked(arg0), namespaceOrAliasEquals(namespace, aliases.get(namespace))))
               return input.getResult();
         } else {
            throw new RuntimeException(String.format("expecting zero or one args %s", input));
         }
         return Optional.absent();
      } else {
         // No extension annotation, should check whether to return absent
         return input.getResult();
      }
   }

   @Override
   public String toString() {
      return "presentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet()";
   }

}
