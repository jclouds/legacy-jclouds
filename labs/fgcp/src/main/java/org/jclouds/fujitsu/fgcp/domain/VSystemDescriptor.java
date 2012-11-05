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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Describes a virtual system template.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vsysdescriptor")
public class VSystemDescriptor {
   @XmlElement(name = "vsysdescriptorId")
   private String id;

   @XmlElement(name = "vsysdescriptorName")
   private String name;

   private String creatorName;

   private String registrant;

   private String description;

   private String keyword;

   @XmlElementWrapper(name = "vservers")
   @XmlElement(name = "vserver")
   private Set<VServerWithDetails> servers = Sets.newLinkedHashSet();

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the creatorName
    */
   public String getCreatorName() {
      return creatorName;
   }

   /**
    * @return the registrant
    */
   public String getRegistrant() {
      return registrant;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return the keyword
    */
   public String getKeyword() {
      return keyword;
   }

   /**
    * @return the servers
    */
   public Set<VServerWithDetails> getServers() {
      return servers == null ? ImmutableSet.<VServerWithDetails> of()
            : ImmutableSet.copyOf(servers);
   }
}
