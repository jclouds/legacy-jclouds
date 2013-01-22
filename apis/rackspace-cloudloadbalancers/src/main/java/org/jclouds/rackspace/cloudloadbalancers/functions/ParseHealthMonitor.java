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
package org.jclouds.rackspace.cloudloadbalancers.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudloadbalancers.domain.HealthMonitor;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
public class ParseHealthMonitor implements 
      Function<HttpResponse, HealthMonitor>, InvocationContext<ParseHealthMonitor> {

   private final ParseJson<Map<String, HealthMonitor>> json;

   @Inject
   ParseHealthMonitor(ParseJson<Map<String, HealthMonitor>> json) {
      this.json = checkNotNull(json, "json");
   }

   @Override
   public HealthMonitor apply(HttpResponse response) {
      Map<String, HealthMonitor> map = json.apply(response);
      
      if (map == null || map.size() == 0)
         return null;
      
      HealthMonitor healthMonitor = Iterables.get(map.values(), 0); 
      
      return healthMonitor.isValid() ? healthMonitor : null;
   }

   @Override
   public ParseHealthMonitor setContext(HttpRequest request) {
      return this;
   }
}
