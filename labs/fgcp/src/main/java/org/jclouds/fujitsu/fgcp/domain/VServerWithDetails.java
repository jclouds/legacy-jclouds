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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a virtual server with virtual storage and NICs.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vserver")
public class VServerWithDetails extends VServerWithVNICs {
   @XmlElementWrapper(name = "vdisks")
   @XmlElement(name = "vdisk")
   protected Set<VDisk> vdisks = Sets.newLinkedHashSet();
   protected Image image;

   public Set<VDisk> getVdisks() {
      return vdisks == null ? ImmutableSet.<VDisk> of() : ImmutableSet
            .copyOf(vdisks);
   }

   public Image getImage() {
      return image;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("type", type).add("creator", creator)
            .add("diskimageId", diskimageId).add("vdisks", vdisks)
            .add("vnics", vnics).add("image", image).toString();
   }
}
