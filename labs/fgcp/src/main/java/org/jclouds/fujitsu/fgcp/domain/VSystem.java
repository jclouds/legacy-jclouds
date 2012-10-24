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

import com.google.common.base.Objects;

/**
 * Represents a virtual system.
 * 
 * @author Dies Koper
 */
public class VSystem {
   @XmlElement(name = "vsysId")
   protected String id;
   @XmlElement(name = "vsysName")
   protected String name;
   protected String creator;
   @XmlElement(name = "baseDescriptor")
   protected String template;
   protected String description;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getCreator() {
      return creator;
   }

   public String getTemplate() {
      return template;
   }

   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VSystem that = VSystem.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("creator", creator)
            .add("template", template).add("description", description)
            .toString();
   }
}
