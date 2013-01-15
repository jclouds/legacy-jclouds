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

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Range.closed;
import static com.google.common.collect.Range.singleton;

/**
 * Represents a network firewall
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
 * @see <a href="https://developers.google.com/compute/docs/networking#firewalls"/>
 */
@Beta
public final class Firewall extends Resource {

   private final URI network;
   private final Set<String> sourceRanges;
   private final Set<String> sourceTags;
   private final Set<String> targetTags;
   private final Set<Rule> allowed;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "network", "sourceRanges",
           "sourceTags", "targetTags", "allowed"
   })
   protected Firewall(String id, Date creationTimestamp, URI selfLink, String name, String description,
                      URI network, Set<String> sourceRanges, Set<String> sourceTags, Set<String> targetTags,
                      Set<Rule> allowed) {
      super(Kind.FIREWALL, id, fromNullable(creationTimestamp), selfLink, checkNotNull(name, "name"),
              fromNullable(description));
      this.network = checkNotNull(network, "network of %s", name);
      this.sourceRanges = sourceRanges == null ? ImmutableSet.<String>of() : sourceRanges;
      this.sourceTags = sourceTags == null ? ImmutableSet.<String>of() : sourceTags;
      this.targetTags = targetTags == null ? ImmutableSet.<String>of() : targetTags;
      this.allowed = allowed == null ? ImmutableSet.<Rule>of() : allowed;
   }

   /**
    * @return URI of the network to which this firewall is applied; provided by the client when the firewall is created.
    */
   public URI getNetwork() {
      return network;
   }

   /**
    * One or both of sourceRanges and sourceTags may be set; an inbound connection is allowed if either the range or
    * the tag of the source matches.
    *
    * @return a list of IP address blocks expressed in CIDR format which this rule applies to.
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
    * If no targetTags are specified, the firewall rule applies to all instances on the specified network.
    *
    * @return a list of instance tags indicating sets of instances located on network which may make network
    *         connections as specified in allowed.
    */
   public Set<String> getTargetTags() {
      return targetTags;
   }

   /**
    * Each rule specifies a protocol and port-range tuple that describes a permitted connection.
    *
    * @return the list of rules specified by this firewall.
    */
   public Set<Rule> getAllowed() {
      return allowed;
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

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFirewall(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private URI network;
      private ImmutableSet.Builder<String> sourceRanges = ImmutableSet.builder();
      private ImmutableSet.Builder<String> sourceTags = ImmutableSet.builder();
      private ImmutableSet.Builder<String> targetTags = ImmutableSet.builder();
      private ImmutableSet.Builder<Rule> allowed = ImmutableSet.builder();

      /**
       * @see Firewall#getNetwork()
       */
      public Builder network(URI network) {
         this.network = network;
         return this;
      }

      /**
       * @see Firewall#getSourceRanges()
       */
      public Builder addSourceRange(String sourceRange) {
         this.sourceRanges.add(checkNotNull(sourceRange));
         return this;
      }

      /**
       * @see Firewall#getSourceRanges()
       */
      public Builder sourceRanges(Set<String> sourceRanges) {
         this.sourceRanges.addAll(checkNotNull(sourceRanges));
         return this;
      }

      /**
       * @see Firewall#getSourceTags()
       */
      public Builder addSourceTag(String sourceTag) {
         this.sourceTags.add(checkNotNull(sourceTag));
         return this;
      }

      /**
       * @see Firewall#getSourceTags()
       */
      public Builder sourceTags(Set<String> sourceTags) {
         this.sourceTags.addAll(checkNotNull(sourceTags));
         return this;
      }

      /**
       * @see Firewall#getTargetTags()
       */
      public Builder addTargetTag(String targetTag) {
         this.targetTags.add(checkNotNull(targetTag));
         return this;
      }

      /**
       * @see Firewall#getTargetTags()
       */
      public Builder targetTags(Set<String> targetTags) {
         this.targetTags.addAll(checkNotNull(targetTags));
         return this;
      }

      /**
       * @see Firewall#getAllowed()
       */
      public Builder addAllowed(Rule firewallRule) {
         this.allowed.add(checkNotNull(firewallRule));
         return this;
      }

      /**
       * @see Firewall#getAllowed()
       */
      public Builder allowed(Set<Rule> firewallRules) {
         this.allowed = ImmutableSet.builder();
         this.allowed.addAll(firewallRules);
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Firewall build() {
         return new Firewall(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, network, sourceRanges.build(), sourceTags.build(), targetTags.build(),
                 allowed.build());
      }

      public Builder fromFirewall(Firewall in) {
         return super.fromResource(in).network(in.getNetwork()).sourceRanges(in.getSourceRanges()).sourceTags(in
                 .getSourceTags()).targetTags(in.getTargetTags()).allowed(in.getAllowed());
      }

   }

   /**
    * A Firewall rule. Rule specifies a protocol and port-range tuple that describes a
    * permitted connection.
    *
    * @author David Alves
    * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
    */
   public static final class Rule {

      public enum IPProtocol {

         TCP, UDP, ICMP, UNKNOWN;

         public String value() {
            return name().toLowerCase();
         }

         @Override
         public String toString() {
            return value();
         }

         public static IPProtocol fromValue(String protocol) {
            return valueOf(protocol.toUpperCase());
         }
      }

      private final IPProtocol ipProtocol;
      private final RangeSet<Integer> ports;

      @ConstructorProperties({
              "IPProtocol", "ports"
      })
      private Rule(IPProtocol IPProtocol, RangeSet<Integer> ports) {
         this.ipProtocol = checkNotNull(IPProtocol);
         this.ports = ports == null ? TreeRangeSet.<Integer>create() : ports;
      }

      /**
       * This can either be a well known protocol string (tcp, udp or icmp) or the IP protocol number.
       *
       * @return this is the IP protocol that is allowed for this rule.
       */
      public IPProtocol getIPProtocol() {
         return ipProtocol;
      }

      /**
       * Each entry must be either an integer or a range. If not specified, connections through any port are allowed.
       * Example inputs include: ["22"], ["80,"443"], and ["12345-12349"].
       * <p/>
       * It is an error to specify this for any protocol that isn't UDP or TCP.
       *
       * @return An optional list of ports which are allowed.
       */
      public RangeSet<Integer> getPorts() {
         return ports;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(ipProtocol, ports);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         Rule that = Rule.class.cast(obj);
         return equal(this.ipProtocol, that.ipProtocol)
                 && equal(this.ports, that.ports);
      }

      /**
       * {@inheritDoc}
       */
      public Objects.ToStringHelper string() {
         return toStringHelper(this)
                 .add("IPProtocol", ipProtocol).add("ports", ports);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromFirewallRule(this);
      }

      public static final class Builder {

         private IPProtocol ipProtocol;
         private RangeSet<Integer> ports = TreeRangeSet.create();

         /**
          * @see org.jclouds.googlecompute.domain.Firewall.Rule#getIPProtocol()
          */
         public Builder IPProtocol(IPProtocol IPProtocol) {
            this.ipProtocol = checkNotNull(IPProtocol);
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Firewall.Rule#getPorts()
          */
         public Builder addPort(Integer port) {
            this.ports.add(singleton(checkNotNull(port, "port")));
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Firewall.Rule#getPorts()
          */
         public Builder addPortRange(Integer start, Integer end) {
            checkState(checkNotNull(start, "start") < checkNotNull(end, "end"),
                    "start of range must be lower than end of range");
            this.ports.add(closed(start, end));
            return this;
         }

         /**
          * @see org.jclouds.googlecompute.domain.Firewall.Rule#getPorts()
          */
         public Builder ports(RangeSet<Integer> ports) {
            this.ports = TreeRangeSet.create();
            this.ports.addAll(ports);
            return this;
         }

         public Rule build() {
            return new Rule(ipProtocol, ports);
         }

         public Builder fromFirewallRule(Rule firewallRule) {
            return new Builder().IPProtocol(firewallRule.getIPProtocol()).ports(firewallRule.getPorts());
         }
      }

   }
}
