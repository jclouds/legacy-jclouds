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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Represents a network firewall
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
 * @see <a href="https://developers.google.com/compute/docs/networking#firewalls"/>
 */
public class Firewall extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromFirewall(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private String network;
      private ImmutableSet.Builder<String> sourceRanges = ImmutableSet.builder();
      private ImmutableSet.Builder<String> sourceTags = ImmutableSet.builder();
      private ImmutableSet.Builder<String> targetTags = ImmutableSet.builder();
      private ImmutableSet.Builder<FirewallRule> allowed = ImmutableSet.builder();

      /**
       * @see Firewall#getNetwork()
       */
      public T network(String network) {
         this.network = network;
         return self();
      }

      /**
       * @see Firewall#getSourceRanges()
       */
      public T addSourceRange(String sourceRange) {
         this.sourceRanges.add(checkNotNull(sourceRange));
         return self();
      }

      /**
       * @see Firewall#getSourceRanges()
       */
      public T sourceRanges(Set<String> sourceRanges) {
         this.sourceRanges.addAll(checkNotNull(sourceRanges));
         return self();
      }

      /**
       * @see Firewall#getSourceTags()
       */
      public T addSourceTag(String sourceTag) {
         this.sourceTags.add(checkNotNull(sourceTag));
         return self();
      }

      /**
       * @see Firewall#getSourceTags()
       */
      public T sourceTags(Set<String> sourceTags) {
         this.sourceTags.addAll(checkNotNull(sourceTags));
         return self();
      }

      /**
       * @see Firewall#getTargetTags()
       */
      public T addTargetTag(String targetTag) {
         this.targetTags.add(checkNotNull(targetTag));
         return self();
      }

      /**
       * @see Firewall#getTargetTags()
       */
      public T targetTags(Set<String> targetTags) {
         this.targetTags.addAll(checkNotNull(targetTags));
         return self();
      }

      /**
       * @see Firewall#getAllowed()
       */
      public T addAllowed(FirewallRule firewallRule) {
         this.allowed.add(checkNotNull(firewallRule));
         return self();
      }

      /**
       * @see Firewall#getAllowed()
       */
      public T allowed(Set<FirewallRule> firewallRules) {
         this.allowed.addAll(checkNotNull(firewallRules));
         return self();
      }

      public Firewall build() {
         return new Firewall(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, network, sourceRanges.build(), sourceTags.build(), targetTags.build(),
                 allowed.build());
      }

      public T fromFirewall(Firewall in) {
         return super.fromResource(in).network(in.getNetwork()).sourceRanges(in.getSourceRanges()).sourceTags(in
                 .getSourceTags()).targetTags(in.getTargetTags()).allowed(in.getAllowed());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String network;
   private final Set<String> sourceRanges;
   private final Set<String> sourceTags;
   private final Set<String> targetTags;
   private final Set<FirewallRule> allowed;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "network", "sourceRanges",
           "sourceTags", "targetTags", "allowed"
   })
   protected Firewall(String id, Date creationTimestamp, String selfLink, String name, String description,
                      String network, Set<String> sourceRanges, Set<String> sourceTags, Set<String> targetTags,
                      Set<FirewallRule> allowed) {
      super(Kind.FIREWALL, id, creationTimestamp, selfLink, name, description);
      this.network = network;
      this.sourceRanges = nullCollectionOnNullOrEmpty(sourceRanges);
      this.sourceTags = nullCollectionOnNullOrEmpty(sourceTags);
      this.targetTags = nullCollectionOnNullOrEmpty(targetTags);
      this.allowed = checkNotNull(allowed);
      checkState(!allowed.isEmpty());
   }

   /**
    * @return URL of the network to which this firewall is applied; provided by the client when the firewall is created.
    */
   public String getNetwork() {
      return network;
   }

   /**
    * @return a list of IP address blocks expressed in CIDR format which this rule applies to. One or both of
    *         sourceRanges and sourceTags may be set; an inbound connection is allowed if either the range or the tag
    *         of the source matches.
    */
   public Set<String> getSourceRanges() {
      return sourceRanges;
   }

   /**
    * @return a list of instance tags which this rule applies to. One or both of sourceRanges and sourceTags may be
    *         set; an inbound connection is allowed if either the range or the tag of the source matches.
    */
   public Set<String> getSourceTags() {
      return sourceTags;
   }

   /**
    * @return a list of instance tags indicating sets of instances located on network which may make network
    *         connections as specified in allowed. If no targetTags are specified,
    *         the firewall rule applies to all instances
    *         on the specified network.
    */
   public Set<String> getTargetTags() {
      return targetTags;
   }

   /**
    * @return the list of rules specified by this firewall. Each rule specifies a protocol and port-range tuple that
    *         describes a permitted connection.
    */
   public Set<FirewallRule> getAllowed() {
      return allowed;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, network, sourceRanges,
              sourceTags, targetTags, allowed);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Firewall that = Firewall.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.network, that.network)
              && Objects.equal(this.sourceRanges, that.sourceRanges)
              && Objects.equal(this.sourceTags, that.sourceTags)
              && Objects.equal(this.targetTags, that.targetTags)
              && Objects.equal(this.allowed, that.allowed);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("network", network).add("sourceRanges", sourceRanges).add("sourceTags",
                      sourceTags).add("targetTags", targetTags).add("allowed", allowed);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
