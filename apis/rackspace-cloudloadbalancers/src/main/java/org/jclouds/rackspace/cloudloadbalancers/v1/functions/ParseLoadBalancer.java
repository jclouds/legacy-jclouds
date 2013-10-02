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
package org.jclouds.rackspace.cloudloadbalancers.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ConvertLB.Factory;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class ParseLoadBalancer implements Function<HttpResponse, LoadBalancer>, InvocationContext<ParseLoadBalancer> {

   private final ParseJson<Map<String, LB>> json;
   private final Factory factory;

   private ConvertLB convertLB;

   @Inject
   ParseLoadBalancer(ParseJson<Map<String, LB>> json, ConvertLB.Factory factory) {
      this.json = checkNotNull(json, "json");
      this.factory = checkNotNull(factory, "factory");
   }

   @Override
   public LoadBalancer apply(HttpResponse arg0) {
      checkState(convertLB != null, "convertLB should be set by InvocationContext");
      Map<String, LB> map = json.apply(arg0);
      if (map == null || map.size() == 0)
         return null;
      LB lb = Iterables.get(map.values(), 0);
      return convertLB.apply(lb);
   }

   @Override
   public ParseLoadBalancer setContext(HttpRequest request) {
      return setEndpointAndRegion(request.getEndpoint());
   }

   ParseLoadBalancer setEndpointAndRegion(URI endpoint) {
      String region = endpoint.getHost().substring(0, endpoint.getHost().indexOf('.'));
      
      this.convertLB = factory.createForEndpointAndRegion(endpoint, region);
      
      return this;
   }
}
