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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * @author Everett Toews
 */
public class ParseSubdomains implements Function<HttpResponse, PaginatedCollection<Subdomain>>, InvocationContext<ParseSubdomains> {

   private final ParseJson<Subdomains> json;

   @Inject
   ParseSubdomains(ParseJson<Subdomains> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public PaginatedCollection<Subdomain> apply(HttpResponse response) {
      Subdomains subdomains = json.apply(response);

      return subdomains;
   }

   @Override
   public ParseSubdomains setContext(HttpRequest request) {
      return this;
   }

   static class Subdomains extends PaginatedCollection<Subdomain> {

      @ConstructorProperties({ "domains", "links", "totalEntries" })
      protected Subdomains(Iterable<Subdomain> domains, Iterable<Link> links, int totalEntries) {
         super(domains, links, totalEntries);
      }
   }
}
