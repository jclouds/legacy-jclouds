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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.cloudstack.domain.LoadBalancerRule.State;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * Tests to see if a LoadBalancerRule is active
 * 
 * @author Adrian Cole
 */
@Singleton
public class LoadBalancerRuleActive implements Predicate<LoadBalancerRule> {

   private final CloudStackClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public LoadBalancerRuleActive(CloudStackClient client) {
      this.client = client;
   }

   public boolean apply(LoadBalancerRule rule) {
      logger.trace("looking for state on rule %s", checkNotNull(rule, "rule"));
      rule = refresh(rule);
      if (rule == null)
         return false;
      logger.trace("%s: looking for rule state %s: currently: %s", rule.getId(), State.ACTIVE, rule.getState());
      return rule.getState() == State.ACTIVE;
   }

   private LoadBalancerRule refresh(LoadBalancerRule rule) {
      return client.getLoadBalancerClient().getLoadBalancerRule(rule.getId());
   }
}
