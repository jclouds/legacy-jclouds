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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a built-in server, also called extended function module (EFM),
 * such as a firewall or load balancer (SLB).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "efm")
public class BuiltinServer {
   @XmlElement(name = "efmId")
   private String id;
   @XmlElement(name = "efmType")
   private BuiltinServerType builtinServerType;
   @XmlElement(name = "efmName")
   private String name;
   private String creator;
   private String slbVip;
   private Firewall firewall;
   private SLB loadbalancer;

   public enum BuiltinServerType {FW, SLB}

   public String getId() {
      return id;
   }

   public BuiltinServerType getType() {
      return builtinServerType;
   }

   public String getName() {
      return name;
   }

   public String getCreator() {
      return creator;
   }

   public String getSlbVip() {
      return slbVip;
   }

   public Firewall getFirewall() {
      return firewall;
   }

   public SLB getLoadbalancer() {
      return loadbalancer;
   }

}
