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

import com.google.common.collect.ImmutableSet;

/**
 * Holds information on the system disk image of a virtual server, including the
 * OS and software pre-installed on it.
 * 
 * @author Dies Koper
 */
public class Image {
    private String id;

    private String serverCategory;

    private String serverApplication;

    private String cpuBit;

    private String sysvolSize;

    private String numOfMaxDisk;

    private String numOfMaxNic;

    @XmlElementWrapper(name = "softwares")
    @XmlElement(name = "software")
    private Set<Software> software;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServerCategory() {
        return serverCategory;
    }

    public void setServerCategory(String serverCategory) {
        this.serverCategory = serverCategory;
    }

    public String getServerApplication() {
        return serverApplication;
    }

    public void setServerApplication(String serverApplication) {
        this.serverApplication = serverApplication;
    }

    public String getCpuBit() {
        return cpuBit;
    }

    public void setCpuBit(String cpuBit) {
        this.cpuBit = cpuBit;
    }

    public String getSysvolSize() {
        return sysvolSize;
    }

    public void setSysvolSize(String sysvolSize) {
        this.sysvolSize = sysvolSize;
    }

    public String getNumOfMaxDisk() {
        return numOfMaxDisk;
    }

    public void setNumOfMaxDisk(String numOfMaxDisk) {
        this.numOfMaxDisk = numOfMaxDisk;
    }

    public String getNumOfMaxNic() {
        return numOfMaxNic;
    }

    public void setNumOfMaxNic(String numOfMaxNic) {
        this.numOfMaxNic = numOfMaxNic;
    }

    public Set<Software> getSoftware() {
        return software == null ? ImmutableSet.<Software> of() : ImmutableSet
                .copyOf(software);
    }

    public void setSoftware(Set<Software> software) {
        this.software = software;
    }
}
