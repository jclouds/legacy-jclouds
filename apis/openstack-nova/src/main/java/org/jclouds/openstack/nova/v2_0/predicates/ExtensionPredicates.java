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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collection;

import org.jclouds.openstack.nova.v2_0.domain.Extension;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with Extensions
 * 
 * @author Adrian Cole
 */

public class ExtensionPredicates {

   /**
    * matches namespace of the given extension
    * 
    * @param namespace
    *           ex {@code http://docs.openstack.org/ext/keypairs/api/v1.1}
    * @return predicate that will match namespace of the given extension
    */
   public static Predicate<Extension> namespaceEquals(final URI namespace) {
      checkNotNull(namespace, "namespace must be defined");

      return new Predicate<Extension>() {
         @Override
         public boolean apply(Extension ext) {
            return namespace.toASCIIString().equals(ext.getNamespace().toASCIIString().replace("https", "http"));
         }

         @Override
         public String toString() {
            return "namespaceEquals(" + namespace + ")";
         }
      };
   }

   /**
    * matches alias of the given extension
    * 
    * @param alias
    *           ex. {@code os-keypairs}
    * @return predicate that will alias of the given extension
    */
   public static Predicate<Extension> aliasEquals(final String alias) {
      checkNotNull(alias, "alias must be defined");

      return new Predicate<Extension>() {
         @Override
         public boolean apply(Extension ext) {
            return alias.equals(ext.getAlias());
         }

         @Override
         public String toString() {
            return "aliasEquals(" + alias + ")";
         }
      };
   }   
   /**
    * matches namespace of the given extension
    * 
    * @param namespace
    *           ex {@code http://docs.openstack.org/ext/keypairs/api/v1.1}
    * @param namespacesAliases
    *           Collection of ex {@code http://docs.openstack.org/compute/ext/keypairs/api/v1.1}
    * @return predicate that will match namespace of the given extension
    */
   public static Predicate<Extension> namespaceOrAliasEquals(final URI namespace, final Collection<URI> namespaceAliases) {
      checkNotNull(namespace, "namespace must be defined");
      checkNotNull(namespaceAliases, "namespace aliases must be defined");

      return new Predicate<Extension>() {
         @Override
         public boolean apply(Extension ext) {
            return namespace.toASCIIString().equals(ext.getNamespace().toASCIIString().replace("https", "http")) ||
            		namespaceAliases.contains(ext.getNamespace());
         }

         @Override
         public String toString() {
            return "namespaceOrAliasEquals(" + namespace + ")";
         }
      };
   }
}
