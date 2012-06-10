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
package org.jclouds.openstack.nova.v2_0.domain.zonescoped;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public class ZoneSecurityGroupNameAndPorts extends ZoneAndName {
   protected final Set<Integer> ports;

   public ZoneSecurityGroupNameAndPorts(String zoneId, String name, Iterable<Integer> ports) {
      super(zoneId, name);
      this.ports = ImmutableSet.<Integer> copyOf(checkNotNull(ports, "ports"));
   }

   public Set<Integer> getPorts() {
      return ports;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ZoneSecurityGroupNameAndPorts that = ZoneSecurityGroupNameAndPorts.class.cast(o);
      return super.equals(that) && equal(this.ports, that.ports);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), ports);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("ports", ports);
   }
}
