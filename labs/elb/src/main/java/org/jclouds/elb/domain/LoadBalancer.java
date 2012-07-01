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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * A load balancer is represented by a DNS name and a set of ports. The load balancer is the
 * destination to which all requests intended for your application should be directed. Each load
 * balancer can distribute requests to multiple EC2 instances. Load Balancers can span multiple
 * Availability Zones within an EC2 region, but they cannot span multiple regions.
 * 
 * <h3>note</h3>
 * 
 * Elastic Load Balancing automatically generates a DNS name for each load balancer. You can map any
 * other domain name (such as www.example.com) to the automatically generated DNS name using CNAME.
 * Or you can use an Amazon Route 53 alias for the load balancer's DNS name. Amazon Route 53
 * provides secure and reliable routing to the infrastructure that uses AWS products, such as Amazon
 * EC2, Amazon Simple Storage Service (Amazon S3), or Elastic Load Balancing. For more information
 * on using Amazon Route 53 for your load balancer, see Using Domain Names with Elastic Load
 * Balancing. For information about CNAME records, see the CNAME Record Wikipedia article.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/arch-loadbalancing.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class LoadBalancer {
   /**
    * Specifies the type of LoadBalancer. This option is only available for LoadBalancers attached
    * to an Amazon VPC.
    */
   public static enum Scheme {

      /**
       * the LoadBalancer has a publicly resolvable DNS name that resolves to public IP addresses
       */
      INTERNET_FACING,
      /**
       * the LoadBalancer has a publicly resolvable DNS name that resolves to private IP addresses.
       */
      INTERNAL,
      /**
       * The scheme was returned unrecognized.
       */
      UNRECOGNIZED;

      public String value() {
         return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name()));
      }

      @Override
      public String toString() {
         return value();
      }

      public static Scheme fromValue(String scheme) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(scheme, "scheme")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromLoadBalancer(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      private String name;
      private Date createdTime;
      private String dnsName;
      private ImmutableSet.Builder<String> instanceIds = ImmutableSet.<String> builder();
      private ImmutableSet.Builder<ListenerWithPolicies> listeners = ImmutableSet.<ListenerWithPolicies> builder();
      private Optional<Scheme> scheme = Optional.absent();
      private Optional<String> VPCId = Optional.absent();

      /**
       * @see LoadBalancer#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see LoadBalancer#getCreatedTime()
       */
      public T createdTime(Date createdTime) {
         this.createdTime = createdTime;
         return self();
      }

      /**
       * @see LoadBalancer#getDnsName()
       */
      public T dnsName(String dnsName) {
         this.dnsName = dnsName;
         return self();
      }

      /**
       * @see LoadBalancer#getInstanceIds()
       */
      public T instanceIds(Iterable<String> instanceId) {
         this.instanceIds.addAll(instanceId);
         return self();
      }

      /**
       * @see LoadBalancer#getInstanceIds()
       */
      public T instanceId(String instanceId) {
         this.instanceIds.add(instanceId);
         return self();
      }

      /**
       * @see LoadBalancer#getListeners()
       */
      public T listeners(Iterable<ListenerWithPolicies> listener) {
         this.listeners.addAll(listener);
         return self();
      }

      /**
       * @see LoadBalancer#getListeners()
       */
      public T listener(ListenerWithPolicies listener) {
         this.listeners.add(listener);
         return self();
      }

      /**
       * @see LoadBalancer#getScheme()
       */
      public T scheme(Scheme scheme) {
         this.scheme = Optional.fromNullable(scheme);
         return self();
      }

      /**
       * @see LoadBalancer#getVPCId()
       */
      public T VPCId(String VPCId) {
         this.VPCId = Optional.fromNullable(VPCId);
         return self();
      }

      public LoadBalancer build() {
         return new LoadBalancer(name, createdTime, dnsName, instanceIds.build(), listeners.build(), scheme, VPCId);
      }

      public T fromLoadBalancer(LoadBalancer in) {
         return this.name(in.getName()).createdTime(in.getCreatedTime()).dnsName(in.getDnsName()).instanceIds(
                  in.getInstanceIds()).scheme(in.getScheme().orNull()).VPCId(in.getVPCId().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final Date createdTime;
   private final String dnsName;
   private final Set<String> instanceIds;
   private final Set<ListenerWithPolicies> listeners;
   private final Optional<Scheme> scheme;
   private final Optional<String> VPCId;

   protected LoadBalancer(String name, Date createdTime, String dnsName, Iterable<String> instanceIds,
            Iterable<ListenerWithPolicies> listeners, Optional<Scheme> scheme, Optional<String> VPCId) {
      this.name = name;
      this.createdTime = createdTime;
      this.dnsName = dnsName;
      this.instanceIds = ImmutableSet.copyOf(instanceIds);
      this.listeners = ImmutableSet.copyOf(listeners);
      this.scheme = scheme;
      this.VPCId = VPCId;
   }

   /**
    * The name associated with the LoadBalancer. The name must be unique within your set of
    * LoadBalancers.
    */
   public String getName() {
      return name;
   }

   /**
    * Provides the date and time the LoadBalancer was created.
    */
   public Date getCreatedTime() {
      return createdTime;
   }

   /**
    * Specifies the external DNS name associated with the LoadBalancer.
    */
   public String getDnsName() {
      return dnsName;
   }

   /**
    * Provides a list of EC2 instance IDs for the LoadBalancer.
    */
   public Set<String> getInstanceIds() {
      return instanceIds;
   }

   /**
    * Provides a list of listeners for the LoadBalancer.
    */
   public Set<ListenerWithPolicies> getListeners() {
      return listeners;
   }

   /**
    * Type of the loadbalancer; This option is only available for LoadBalancers attached to an
    * Amazon VPC.
    */
   public Optional<Scheme> getScheme() {
      return scheme;
   }

   /**
    * Provides the ID of the VPC attached to the LoadBalancer.
    */
   public Optional<String> getVPCId() {
      return VPCId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name, createdTime);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadBalancer other = (LoadBalancer) obj;
      return Objects.equal(this.name, other.name) && Objects.equal(this.createdTime, other.createdTime);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("createdTime", createdTime).add(
               "dnsName", dnsName).add("instanceIds", instanceIds).add("listeners", listeners).add("scheme",
               scheme.orNull()).add("VPCId", VPCId.orNull()).toString();
   }

}
