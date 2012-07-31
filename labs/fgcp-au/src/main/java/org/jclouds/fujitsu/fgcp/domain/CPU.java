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
 * Describes the virtual CPU of a server.
 * 
 * @author Dies Koper
 */
public class CPU {
    @XmlElement(name = "cpuArch")
    private String arch;
    @XmlElement(name = "cpuPerf")
    private double speedPerCore;
    @XmlElement(name = "numOfCpu")
    private double cores;

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public double getSpeedPerCore() {
        return speedPerCore;
    }

    public void setSpeedPerCore(double coreSpeed) {
        this.speedPerCore = coreSpeed;
    }

    public double getCores() {
        return cores;
    }

    public void setCores(double cores) {
        this.cores = cores;
    }

    @Override
    public String toString() {
        return "CPU{" + "arch='" + arch + '\'' + ", speedPerCore='"
                + speedPerCore + '\'' + ", cores='" + cores + '\'' + '}';
    }
}
