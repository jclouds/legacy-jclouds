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
package org.jclouds.tmrk.enterprisecloud.domain;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wraps individual VirtualMachine elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * @author Jason King
 */
@XmlRootElement(name="VirtualMachines")
public class VirtualMachines {

    private LinkedHashSet<VirtualMachine> virtualMachines = Sets.newLinkedHashSet();

    @XmlElement(name = "VirtualMachine")
    void setVirtualMachine(VirtualMachine nic) {
        this.virtualMachines.add(nic);
    }

    public Set<VirtualMachine> getVirtualMachines() {
        return Collections.unmodifiableSet(virtualMachines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualMachines vms1 = (VirtualMachines) o;

        if (!virtualMachines.equals(vms1.virtualMachines)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return virtualMachines.hashCode();
    }

    public String toString() {
        return "["+ virtualMachines.toString()+"]";
    }
}
