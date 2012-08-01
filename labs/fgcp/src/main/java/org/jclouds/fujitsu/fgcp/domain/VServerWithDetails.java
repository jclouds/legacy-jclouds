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

import java.util.Arrays;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableSet;

/**
 * Represents a virtual server with virtual storage and NICs.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vserver")
public class VServerWithDetails extends VServer implements
        Comparable<VServerWithDetails> {
    @XmlElementWrapper(name = "vdisks")
    @XmlElement(name = "vdisk")
    protected Set<VDisk> vdisks;
    @XmlElementWrapper(name = "vnics")
    @XmlElement(name = "vnic")
    protected Set<VNIC> vnics;
    protected Image image;

    public Set<VDisk> getVdisks() {
        return vdisks == null ? ImmutableSet.<VDisk> of() : ImmutableSet
                .copyOf(vdisks);
    }

    public void setVdisks(Set<VDisk> vdisks) {
        this.vdisks = vdisks;
    }

    public Set<VNIC> getVnics() {
        return vnics == null ? ImmutableSet.<VNIC> of() : ImmutableSet
                .copyOf(vnics);
    }

    public void setVnics(Set<VNIC> vnics) {
        this.vnics = vnics;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public int compareTo(VServerWithDetails o) {
        return id == null ? -1 : id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VServerWithDetails))
            return false;

        VServerWithDetails vServerWithDetails = (VServerWithDetails) o;

        if (!id.equals(vServerWithDetails.id))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "VServerWithDetails{" + "id='" + id + '\'' + ", name='" + name
                + '\'' + ", type='" + type + '\'' + ", diskimageId='"
                + diskimageId + '\'' + ", creator='" + creator + '\''
                + ", vdisks=" + (vdisks == null ? null : Arrays.asList(vdisks))
                + ", vnics=" + (vnics == null ? null : Arrays.asList(vnics))
                + ", image=" + image + '}';
    }
}
