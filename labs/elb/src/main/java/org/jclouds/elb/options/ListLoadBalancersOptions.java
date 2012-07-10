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
 * Options used to list available loadBalancers.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_DescribeLoadBalancers.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class ListLoadBalancersOptions extends BaseHttpRequestOptions implements Cloneable {

   private Object marker;
   private Set<String> names = Sets.newLinkedHashSet();

   /**
    * Use this parameter only when paginating results, and only in a subsequent request after you've
    * received a response where the results are truncated. Set it to the value of the Marker element
    * in the response you just received.
    */
   public ListLoadBalancersOptions afterMarker(Object marker) {
      this.marker = marker;
      return this;
   }

   /**
    * list of names associated with the LoadBalancers at creation time.
    */
   public ListLoadBalancersOptions names(Set<String> names) {
      this.names = names;
      return this;
   }

   /**
    * @see #names
    */
   public ListLoadBalancersOptions name(String name) {
      this.names.add(name);
      return this;
   }

   public static class Builder {

      /**
       * @see ListLoadBalancersOptions#getMarker()
       */
      public static ListLoadBalancersOptions afterMarker(Object marker) {
         return new ListLoadBalancersOptions().afterMarker(marker);
      }

      /**
       * @see ListLoadBalancersOptions#getNames()
       */
      public static ListLoadBalancersOptions name(String name) {
         return new ListLoadBalancersOptions().name(name);
      }

      /**
       * @see ListLoadBalancersOptions#getNames()
       */
      public static ListLoadBalancersOptions names(Set<String> names) {
         return new ListLoadBalancersOptions().names(names);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (marker != null)
         params.put("Marker", marker.toString());
      if (names.size() > 0) {
         int nameIndex = 1;
         for (String name : names) {
            params.put("LoadBalancerNames.member." + nameIndex, name);
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
      return Objects.hashCode(marker, names);
   }

   @Override
   public ListLoadBalancersOptions clone() {
      return new ListLoadBalancersOptions().afterMarker(marker).names(names);
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
      ListLoadBalancersOptions other = ListLoadBalancersOptions.class.cast(obj);
      return Objects.equal(this.marker, other.marker) && Objects.equal(this.names, other.names);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("marker", marker).add("names", names).toString();
   }
}
