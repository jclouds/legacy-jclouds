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

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Holds the statistics of a virtual server.
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "performanceinfo")
public class PerformanceInfo implements Comparable<PerformanceInfo> {
    private long recordTime;
    private double cpuUtilization;
    private long diskReadRequestCount;
    private long diskWriteRequestCount;
    private long diskReadSector;
    private long diskWriteSector;
    private long nicInputByte;
    private long nicOutputByte;
    private long nicInputPacket;
    private long nicOutputPacket;

    /**
     * @return the recordTime
     */
    public long getRecordTime() {
        return recordTime;
    }

    /**
     * @return the cpuUtilization
     */
    public double getCpuUtilization() {
        return cpuUtilization;
    }

    /**
     * @return the diskReadRequestCount
     */
    public long getDiskReadRequestCount() {
        return diskReadRequestCount;
    }

    /**
     * @return the diskWriteRequestCount
     */
    public long getDiskWriteRequestCount() {
        return diskWriteRequestCount;
    }

    /**
     * @return the diskReadSector
     */
    public long getDiskReadSector() {
        return diskReadSector;
    }

    /**
     * @return the diskWriteSector
     */
    public long getDiskWriteSector() {
        return diskWriteSector;
    }

    /**
     * @return the nicInputByte
     */
    public long getNicInputByte() {
        return nicInputByte;
    }

    /**
     * @return the nicOutputByte
     */
    public long getNicOutputByte() {
        return nicOutputByte;
    }

    /**
     * @return the nicInputPacket
     */
    public long getNicInputPacket() {
        return nicInputPacket;
    }

    /**
     * @return the nicOutputPacket
     */
    public long getNicOutputPacket() {
        return nicOutputPacket;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(recordTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PerformanceInfo that = PerformanceInfo.class.cast(obj);
        return Objects.equal(this.recordTime, that.recordTime);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("recordTime", recordTime)
                .add("cpuUtilization", cpuUtilization)
                .add("diskReadRequestCount", diskReadRequestCount)
                .add("diskWriteRequestCount", diskWriteRequestCount)
                .add("diskReadSector", diskReadSector)
                .add("diskWriteSector", diskWriteSector)
                .add("nicInputByte", nicInputByte)
                .add("nicOutputByte", nicOutputByte)
                .add("nicInputPacket", nicInputPacket)
                .add("nicOutputPacket", nicOutputPacket).toString();
    }

    @Override
    public int compareTo(PerformanceInfo o) {
        return (int) (recordTime - o.recordTime);
    }
}
