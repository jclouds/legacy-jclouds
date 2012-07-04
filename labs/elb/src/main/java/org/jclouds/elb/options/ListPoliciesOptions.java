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
package org.jclouds.elb.options;

import java.util.Set;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Options used to list available policies.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_DescribeLoadBalancerPolicies.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class ListPoliciesOptions extends BaseHttpRequestOptions implements Cloneable {

   private String loadBalancerName;
   private Set<String> names = Sets.newLinkedHashSet();

   /**
    * @see ListPoliciesOptions#getLoadBalancerName()
    */
   public ListPoliciesOptions loadBalancerName(String loadBalancerName) {
      this.loadBalancerName = loadBalancerName;
      return this;
   }

   /**
    * @see ListPoliciesOptions#getNames()
    */
   public ListPoliciesOptions names(Set<String> names) {
      this.names = names;
      return this;
   }

   /**
    * @see ListPoliciesOptions#getNames()
    */
   public ListPoliciesOptions name(String name) {
      this.names.add(name);
      return this;
   }

   /**
    * The mnemonic name associated with the LoadBalancer. If no name is specified, the operation
    * returns the attributes of either all the sample policies pre-defined by Elastic Load Balancing
    * or the specified sample polices.
    */
   public String getLoadBalancerName() {
      return loadBalancerName;
   }

   /**
    * The names of LoadBalancer policies you've created or Elastic Load Balancing sample policy
    * names.
    */
   public Set<String> getNames() {
      return names;
   }

   public static class Builder {

      /**
       * @see ListPoliciesOptions#getLoadBalancerName()
       */
      public static ListPoliciesOptions loadBalancerName(String loadBalancerName) {
         return new ListPoliciesOptions().loadBalancerName(loadBalancerName);
      }

      /**
       * @see ListPoliciesOptions#getNames()
       */
      public static ListPoliciesOptions name(String name) {
         return new ListPoliciesOptions().name(name);
      }

      /**
       * @see ListPoliciesOptions#getNames()
       */
      public static ListPoliciesOptions names(Set<String> names) {
         return new ListPoliciesOptions().names(names);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (loadBalancerName != null)
         params.put("LoadBalancerName", loadBalancerName);
      if (names.size() > 0) {
         int nameIndex = 1;
         for (String name : names) {
            params.put("PolicyNames.member." + nameIndex, name);
            nameIndex++;
         }
      }
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(loadBalancerName, names);
   }

   @Override
   public ListPoliciesOptions clone() {
      return new ListPoliciesOptions().loadBalancerName(loadBalancerName).names(names);
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
      ListPoliciesOptions other = ListPoliciesOptions.class.cast(obj);
      return Objects.equal(this.loadBalancerName, other.loadBalancerName) && Objects.equal(this.names, other.names);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("loadBalancerName", loadBalancerName)
               .add("names", names).toString();
   }
}
