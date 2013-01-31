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
package org.jclouds.route53.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.route53.domain.RecordSet.RecordSubset.Latency;
import org.jclouds.route53.domain.RecordSet.RecordSubset.Weighted;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class RecordSet {

   protected final String name;
   protected final Type type;
   protected final Optional<Integer> ttl;
   protected final Set<String> values;
   protected final Optional<AliasTarget> aliasTarget;

   public enum Type {
      A, AAAA, CNAME, MX, NS, PTR, SOA, SPF, SRV, TXT;
   }

   /**
    * In this case, the rrs is an alias, and it points to another Route53 hosted
    * resource, such as an ELB, S3 bucket, or zone.
    */
   public static class AliasTarget {

      public static AliasTarget dnsNameInZone(String dnsName, String zoneId) {
         return new AliasTarget(dnsName, zoneId);
      }

      private final String dnsName;
      private final String zoneId;

      private AliasTarget(String dnsName, String zoneId) {
         this.dnsName = checkNotNull(dnsName, "dnsName");
         this.zoneId = checkNotNull(zoneId, "zoneId of %s", dnsName);
      }

      public String getDNSName() {
         return dnsName;
      }

      public String getZoneId() {
         return zoneId;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(zoneId, dnsName);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         AliasTarget other = AliasTarget.class.cast(obj);
         return equal(this.dnsName, other.dnsName) && equal(this.zoneId, other.zoneId);
      }

      @Override
      public String toString() {
         return toStringHelper("").omitNullValues().add("dnsName", dnsName).add("zoneId", zoneId).toString();
      }
   }

   /**
    * A portion of a RRs who share the same name and type
    */
   public static abstract class RecordSubset extends RecordSet {
      public static final class Weighted extends RecordSubset {

         private final int weight;

         private Weighted(String id, String name, Type type, int weight, Optional<Integer> ttl, Set<String> values,
               Optional<AliasTarget> aliasTarget) {
            super(id, name, type, ttl, values, aliasTarget);
            this.weight = weight;
         }

         /**
          * determines what portion of traffic for the current resource record
          * set is routed to this subset.
          */
         public int getWeight() {
            return weight;
         }

         @Override
         ToStringHelper differentiate(ToStringHelper in) {
            return in.add("weight", weight);
         }
      }

      public static final class Latency extends RecordSubset {

         private final String region;

         private Latency(String id, String name, Type type, String region, Optional<Integer> ttl, Set<String> values,
               Optional<AliasTarget> aliasTarget) {
            super(id, name, type, ttl, values, aliasTarget);
            this.region = checkNotNull(region, "region of %s", name);
         }

         /**
          * The Amazon EC2 region where the resource that is specified in this
          * resource record set resides.
          */
         public String getRegion() {
            return region;
         }

         @Override
         ToStringHelper differentiate(ToStringHelper in) {
            return in.add("region", region);
         }
      }

      private final String id;

      private RecordSubset(String id, String name, Type type, Optional<Integer> ttl, Set<String> values,
            Optional<AliasTarget> aliasTarget) {
         super(name, type, ttl, values, aliasTarget);
         this.id = checkNotNull(id, "id of %s", name);
      }

      /**
       * The identifier that differentiates beyond {@code name} and {@code type}
       */
      public String getId() {
         return id;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(super.hashCode(), id);
      }

      @Override
      public boolean equals(Object obj) {
         if (super.equals(obj) && obj instanceof RecordSubset) {
            RecordSubset that = RecordSubset.class.cast(obj);
            return equal(this.id, that.id);
         }
         return false;
      }

      abstract ToStringHelper differentiate(ToStringHelper in);

      @Override
      public String toString() {
         return differentiate(toStringHelper("").omitNullValues().add("id", id).add("name", name).add("type", type))
               .add("ttl", ttl.orNull()).add("values", values.isEmpty() ? null : values).add("aliasTarget", aliasTarget.orNull())
               .toString();
      }
   }

   private RecordSet(String name, Type type, Optional<Integer> ttl, Set<String> values, Optional<AliasTarget> aliasTarget) {
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type of %s", name);
      this.ttl = checkNotNull(ttl, "ttl for %s", name);
      this.values = checkNotNull(values, "values for %s", name);
      this.aliasTarget = checkNotNull(aliasTarget, "aliasTarget for %s", aliasTarget);
   }

   /**
    * The name of the domain.
    */
   public String getName() {
      return name;
   }

   /**
    * The resource record set type.
    */
   public Type getType() {
      return type;
   }

   /**
    * Present in all resource record sets except aliases. The resource record
    * cache time to live (TTL), in seconds.
    */
   public Optional<Integer> getTTL() {
      return ttl;
   }

   /**
    * Type-specific values that differentiates the RRs in this set. Empty if
    * {@link #getType()} is {@link Type#A} or {@link Type#AAAA} and
    * {@link #getAliasTarget} is present.
    */
   public Set<String> getValues() {
      return values;
   }

   /**
    * When present, {@link #getType()} is {@link Type#A} or {@link Type#AAAA}.
    * Instead of {@link #getValues()} containing the corresponding IP addresses,
    * the server will follow this link and resolve one on-demand.
    */
   public Optional<AliasTarget> getAliasTarget() {
      return aliasTarget;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      RecordSet other = RecordSet.class.cast(obj);
      return equal(this.name, other.name) && equal(this.type, other.type);
   }

   @Override
   public String toString() {
      return toStringHelper("").omitNullValues().add("name", name).add("type", type).add("ttl", ttl.orNull())
            .add("values", values.isEmpty() ? null : values).add("aliasTarget", aliasTarget.orNull()).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public final static class Builder {
      private String id;
      private String name;
      private Type type;
      private Optional<Integer> ttl = Optional.absent();
      private ImmutableSet.Builder<String> values = ImmutableSet.<String> builder();
      private String dnsName;
      private String zoneId;
      private Integer weight;
      private String region;

      /**
       * @see RecordSubset#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see RecordSet#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see RecordSet#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see RecordSet#getTTL()
       */
      public Builder ttl(Integer ttl) {
         this.ttl = Optional.fromNullable(ttl);
         return this;
      }

      /**
       * @see RecordSet#getAliasTarget()
       */
      public Builder dnsName(String dnsName) {
         this.dnsName = dnsName;
         return this;
      }

      /**
       * @see RecordSet#getAliasTarget()
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @see RecordSet#getAliasTarget()
       */
      public Builder aliasTarget(AliasTarget aliasTarget) {
         if (aliasTarget == null) {
            dnsName = null;
            zoneId = null;
         } else {
            dnsName = aliasTarget.dnsName;
            zoneId = aliasTarget.zoneId;
         }
         return this;
      }

      /**
       * @see RecordSet#getValues()
       */
      public Builder add(String values) {
         this.values.add(values);
         return this;
      }

      /**
       * @see RecordSet#getValues()
       */
      public Builder addAll(Iterable<String> values) {
         this.values.addAll(values);
         return this;
      }

      /**
       * @see RecordSubset.Weighted
       */
      public Builder weight(Integer weight) {
         this.weight = weight;
         return this;
      }

      /**
       * @see RecordSubset.Latency
       */
      public Builder region(String region) {
         this.region = region;
         return this;
      }

      public RecordSet build() {
         Optional<AliasTarget> aliasTarget = dnsName != null ? Optional.fromNullable(AliasTarget.dnsNameInZone(dnsName, zoneId))
               : Optional.<AliasTarget> absent();
         if (weight != null) {
            return new RecordSubset.Weighted(id, name, type, weight, ttl, values.build(), aliasTarget);
         } else if (region != null) {
            return new RecordSubset.Latency(id, name, type, region, ttl, values.build(), aliasTarget);
         }
         return new RecordSet(name, type, ttl, values.build(), aliasTarget);
      }

      public Builder from(RecordSet in) {
         if (in instanceof RecordSubset)
            id(RecordSubset.class.cast(in).id);
         if (in instanceof Weighted) {
            weight(Weighted.class.cast(in).weight);
         } else if (in instanceof Latency) {
            region(Latency.class.cast(in).region);
         }
         return this.name(in.name).type(in.type).ttl(in.ttl.orNull()).addAll(in.values)
               .aliasTarget(in.aliasTarget.orNull());
      }
   }
}
