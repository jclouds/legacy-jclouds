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
package org.jclouds.rackspace.clouddns.v1.functions;

import java.util.Map;
import java.util.Set;

import org.jclouds.rackspace.clouddns.v1.domain.Domain;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Functions for working with Domains.
 * 
 * @author Everett Toews
 */
public class DomainFunctions {

   /**
    * Take a Set of Domains and return a Map of domain name to the Domain.
    */
   public static Map<String, Domain> toDomainMap(Set<Domain> domains) {
      return Maps.uniqueIndex(domains, DomainFunctions.GET_DOMAIN_NAME);
   }

   /**
    * Take a Domain and return its name.
    */
   public static final Function<Domain, String> GET_DOMAIN_NAME = new Function<Domain, String>() {
      public String apply(Domain domain) {
         return domain.getName();
      }
   };
}
