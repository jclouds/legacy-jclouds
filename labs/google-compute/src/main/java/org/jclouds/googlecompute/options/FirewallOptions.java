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

package org.jclouds.googlecompute.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.googlecompute.domain.Firewall;

import java.net.URI;
import java.util.Set;

/**
 * Options to create a firewall.
 *
 * @see Firewall
 * @author David Alves
 */
public class FirewallOptions {

   private String name;
   private URI network;
   private ImmutableSet.Builder<String> sourceRanges = ImmutableSet.builder();
   private ImmutableSet.Builder<String> sourceTags = ImmutableSet.builder();
   private ImmutableSet.Builder<String> targetTags = ImmutableSet.builder();
   private ImmutableSet.Builder<Firewall.Rule> allowed = ImmutableSet.builder();

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getAllowed()
    */
   public Set<Firewall.Rule> getAllowed() {
      return allowed.build();
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getAllowed()
    */
   public FirewallOptions addAllowedRule(Firewall.Rule allowedRule) {
      this.allowed.add(allowedRule);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getAllowed()
    */
   public FirewallOptions allowedRules(Set<Firewall.Rule> allowedRules) {
      this.allowed = ImmutableSet.builder();
      this.allowed.addAll(allowedRules);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getName()
    */
   public FirewallOptions name(String name) {
      this.name = name;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getName()
    */
   public String getName() {
      return name;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getNetwork()
    */
   public FirewallOptions network(URI network) {
      this.network = network;
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getNetwork()
    */
   public URI getNetwork() {
      return network;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceRanges()
    */
   public Set<String> getSourceRanges() {
      return sourceRanges.build();
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceRanges()
    */
   public FirewallOptions addSourceRange(String sourceRange) {
      this.sourceRanges.add(sourceRange);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceRanges()
    */
   public FirewallOptions sourceRanges(Set<String> sourceRanges) {
      this.sourceRanges = ImmutableSet.builder();
      this.sourceRanges.addAll(sourceRanges);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceTags()
    */
   public Set<String> getSourceTags() {
      return sourceTags.build();
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceTags()
    */
   public FirewallOptions addSourceTag(String sourceTag) {
      this.sourceTags.add(sourceTag);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getSourceTags()
    */
   public FirewallOptions sourceTags(Set<String> sourceTags) {
      this.sourceTags = ImmutableSet.builder();
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getTargetTags()
    */
   public Set<String> getTargetTags() {
      return targetTags.build();
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getTargetTags()
    */
   public FirewallOptions addTargetTag(String targetTag) {
      this.targetTags.add(targetTag);
      return this;
   }

   /**
    * @see org.jclouds.googlecompute.domain.Firewall#getTargetTags()
    */
   public FirewallOptions targetTags(Set<String> targetTags) {
      this.targetTags = ImmutableSet.builder();
      this.targetTags.addAll(targetTags);
      return this;
   }

}
