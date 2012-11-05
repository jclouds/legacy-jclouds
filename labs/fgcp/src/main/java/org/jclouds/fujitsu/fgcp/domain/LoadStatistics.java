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
package org.jclouds.fujitsu.fgcp.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Holds statistics of the load on a load balancer (SLB).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "loadstatistics")
public class LoadStatistics {
   private Set<Group> groups = Sets.newLinkedHashSet();

   /**
    * @return the groups
    */
   public Set<Group> getGroups() {
      return groups == null ? ImmutableSet.<Group> of() : ImmutableSet
            .copyOf(groups);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(groups);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LoadStatistics that = LoadStatistics.class.cast(obj);
      return Objects.equal(this.groups, that.groups);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("groups", groups).toString();
   }
}
