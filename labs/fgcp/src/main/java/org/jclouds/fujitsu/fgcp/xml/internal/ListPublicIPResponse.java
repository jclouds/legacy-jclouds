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
package org.jclouds.fujitsu.fgcp.xml.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.fujitsu.fgcp.domain.PublicIP;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Wrapper for ListPublicIPResponse.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "ListPublicIPResponse")
public class ListPublicIPResponse extends
      MapWithStatusResponse<PublicIP, String> implements
      SingleElementResponse {
   @XmlElementWrapper(name = "publicips")
   @XmlElement(name = "publicip")
   private Set<PublicIPWithSystemId> ips;

   @Override
   public String toString() {
      return getElement().toString();
   }

   @Override
   protected Map<PublicIP, String> delegate() {
      Builder<PublicIP, String> returnVal = ImmutableMap.builder();
      if (ips != null) {

         for (PublicIPWithSystemId ip : ips) {
            returnVal.put(ip, ip.getVsysId());
         }
      }
      return returnVal.build();
   }

   @Override
   public Set<? extends PublicIP> getElement() {
      return ips == null ? ImmutableSet.<PublicIP> of() : Collections
            .unmodifiableSet(ips);
   }
}
