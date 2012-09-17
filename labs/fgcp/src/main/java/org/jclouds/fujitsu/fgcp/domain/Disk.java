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
 * Describes a disk as part of the description of hardware represented by a
 * virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement
public class Disk {
   @XmlElement(name = "diskSize")
   private String size;

   @XmlElement(name = "diskUsage")
   private String usage;

   @XmlElement(name = "diskType")
   private String type;

   public String getSize() {
      return size;
   }

   public String getUsage() {
      return usage;
   }

   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof Disk))
         return false;

      Disk disk = (Disk) o;

      if (size != null ? !size.equals(disk.size) : disk.size != null)
         return false;
      if (type != null ? !type.equals(disk.type) : disk.type != null)
         return false;
      if (usage != null ? !usage.equals(disk.usage) : disk.usage != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = size != null ? size.hashCode() : 0;
      result = 31 * result + (usage != null ? usage.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Disk{" + "size='" + size + '\'' + ", usage='" + usage + '\''
            + ", type='" + type + '\'' + '}';
   }
}
