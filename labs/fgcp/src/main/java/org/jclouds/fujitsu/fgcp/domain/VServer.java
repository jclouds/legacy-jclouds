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
 * Represents a virtual server.
 * 
 * @author Dies Koper
 */
public class VServer {
   @XmlElement(name = "vserverId")
   protected String id;
   @XmlElement(name = "vserverName")
   protected String name;
   @XmlElement(name = "vserverType")
   protected String type;
   protected String diskimageId;
   protected String creator;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public String getDiskimageId() {
      return diskimageId;
   }

   public String getCreator() {
      return creator;
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
      VServer that = VServer.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("type", type).add("creator", creator)
            .add("diskimageId", diskimageId).toString();
   }

}
