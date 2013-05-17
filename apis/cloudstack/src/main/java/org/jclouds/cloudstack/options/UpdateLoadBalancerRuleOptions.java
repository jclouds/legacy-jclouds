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
package org.jclouds.cloudstack.options;

import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a load balancer rule is updated
 *
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/user/updateLoadBalancerRule.html"
 *      />
 */
public class UpdateLoadBalancerRuleOptions extends BaseHttpRequestOptions {

   public static final UpdateLoadBalancerRuleOptions NONE = new UpdateLoadBalancerRuleOptions();

   /**
    * @param algorithm load balancer algorithm (source, roundrobin, leastconn)
    */
   public UpdateLoadBalancerRuleOptions algorithm(LoadBalancerRule.Algorithm algorithm) {
      this.queryParameters.replaceValues("algorithm", ImmutableSet.of(algorithm.toString()));
      return this;
   }

   /**
    * @param description the description of the load balancer rule
    */
   public UpdateLoadBalancerRuleOptions description(String description) {
      this.queryParameters.replaceValues("description", ImmutableSet.of(description));
      return this;
   }

   /**
    * @param name the name of the load balancer rule
    */
   public UpdateLoadBalancerRuleOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateLoadBalancerRuleOptions#algorithm
       */
      public static UpdateLoadBalancerRuleOptions algorithm(LoadBalancerRule.Algorithm algorithm) {
         UpdateLoadBalancerRuleOptions options = new UpdateLoadBalancerRuleOptions();
         return options.algorithm(algorithm);
      }

      /**
       * @see UpdateLoadBalancerRuleOptions#description
       */
      public static UpdateLoadBalancerRuleOptions description(String description) {
         UpdateLoadBalancerRuleOptions options = new UpdateLoadBalancerRuleOptions();
         return options.description(description);
      }

      /**
       * @see UpdateLoadBalancerRuleOptions#name
       */
      public static UpdateLoadBalancerRuleOptions name(String name) {
         UpdateLoadBalancerRuleOptions options = new UpdateLoadBalancerRuleOptions();
         return options.name(name);
      }
   }
}
