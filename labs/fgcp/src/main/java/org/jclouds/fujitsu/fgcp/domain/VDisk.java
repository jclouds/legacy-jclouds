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

import com.google.common.base.Objects;

/**
 * Represents attachable storage in the form of a virtual disk.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "vdisk")
public class VDisk {
    @XmlElement(name = "diskId")
    private String id;
    @XmlElement(name = "diskName")
    private String name;
    @XmlElement(name = "attachedTo")
    private String attachedServer;
    private String creator;
    private double size;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAttachedServer() {
        return attachedServer;
    }

    public String getCreator() {
        return creator;
    }

    public double getSize() {
        return size;
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
        VDisk that = VDisk.class.cast(obj);
        return Objects.equal(this.id, that.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("id", id)
                .add("name", name).add("attachedServer", attachedServer)
                .add("creator", creator).add("size", size).toString();
    }

}
