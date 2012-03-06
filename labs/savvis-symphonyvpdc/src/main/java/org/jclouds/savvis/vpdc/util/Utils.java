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
package org.jclouds.savvis.vpdc.util;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.savvis.vpdc.domain.Link;
import org.jclouds.savvis.vpdc.domain.NetworkConfigSection;
import org.jclouds.savvis.vpdc.domain.NetworkConnectionSection;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.ResourceImpl;
import org.jclouds.savvis.vpdc.domain.VM;
import org.xml.sax.Attributes;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static Resource newResource(Map<String, String> attributes, String defaultType) {
      String uri = attributes.get("href");
      String type = attributes.get("type");
      // savvis org has null href
      String id = null;
      URI href = null;
      if (uri != null) {
         href = URI.create(uri);
         id = uri.substring(uri.lastIndexOf('/') + 1);
      }
      return (attributes.containsKey("rel")) ? new Link(id, attributes.get("name"), type != null ? type : defaultType,
               href, attributes.get("rel")) : new ResourceImpl(id, attributes.get("name"), type != null ? type
               : defaultType, href);
   }

   public static Set<String> getIpsFromVM(VM vm) {
      Iterable<String> ipFromConnections = transform(vm.getNetworkConnectionSections(),
               new Function<NetworkConnectionSection, String>() {
                  @Override
                  public String apply(NetworkConnectionSection input) {
                     return input.getIpAddress();
                  };
               });
      Iterable<String> ipsFromNat = concat(transform(vm.getNetworkConfigSections(),
               new Function<NetworkConfigSection, Iterable<String>>() {
                  @Override
                  public Iterable<String> apply(NetworkConfigSection input) {
                     return concat(input.getInternalToExternalNATRules().keySet(), input
                              .getInternalToExternalNATRules().values());
                  };
               }));
      return ImmutableSet.copyOf(filter(concat(ipFromConnections, ipsFromNat), notNull()));
   }

   public static Map<String, String> cleanseAttributes(Attributes in) {
      Builder<String, String> attrs = ImmutableMap.<String, String> builder();
      for (int i = 0; i < in.getLength(); i++) {
         String name = in.getQName(i);
         if (name.indexOf(':') != -1)
            name = name.substring(name.indexOf(':') + 1);
         attrs.put(name, in.getValue(i));
      }
      return attrs.build();
   }

   public static String currentOrNull(StringBuilder currentText) {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public static Resource newResource(Map<String, String> attributes) {
      return newResource(attributes, null);
   }
}
