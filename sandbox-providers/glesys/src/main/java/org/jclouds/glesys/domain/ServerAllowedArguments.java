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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.gson.annotations.SerializedName;

/**
 * Lists the allowed arguments for some of the functions in this module such as disksize, cpucores etc.
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_allowedarguments" />
 */
public class ServerAllowedArguments {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Integer> diskSizes;
        private List<Integer> memorySizes;
        private List<Integer> cpuCores;
        private List<String> templates;
        private List<Integer> transfers;
        private List<String> dataCenters;

        public Builder diskSizes(Integer... sizes) {
            return diskSizes(Arrays.<Integer>asList(sizes));
        }

        public Builder diskSizes(List<Integer> sizes) {
            this.diskSizes = sizes;
            return this;
        }

        public Builder memorySizes(Integer... sizes) {
            return memorySizes(Arrays.<Integer>asList(sizes));
        }

        public Builder memorySizes(List<Integer> sizes) {
            this.memorySizes = sizes;
            return this;
        }

        public Builder cpuCores(Integer... cpuCores) {
            this.cpuCores = Arrays.<Integer>asList(cpuCores);
            return this;
        }

        public Builder cpuCores(List<Integer> cpuCores) {
            this.cpuCores = cpuCores;
            return this;
        }

        public Builder templates(String... templates) {
            return templates(Arrays.<String>asList(templates));
        }

        public Builder templates(List<String> templates) {
            this.templates = templates;
            return this;
        }

        public Builder transfers(Integer... transfers) {
            return transfers(Arrays.<Integer>asList(transfers));
        }

        public Builder transfers(List<Integer> transfers) {
            this.transfers = transfers;
            return this;
        }

        public Builder dataCenters(String... dataCenters) {
            return dataCenters(Arrays.<String>asList(dataCenters));
        }

        public Builder dataCenters(List<String> dataCenters) {
            this.dataCenters = dataCenters;
            return this;
        }

        public ServerAllowedArguments build() {
            return new ServerAllowedArguments(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
        }

        public Builder fromAllowedArguments(ServerAllowedArguments in) {
            return diskSizes(in.getDiskSizes())
                    .memorySizes(in.getMemorySizes())
                    .cpuCores(in.getCpuCores())
                    .templates(in.getTemplates())
                    .transfers(in.getTransfers())
                    .dataCenters(in.getDataCenters());
        }
    }

    @SerializedName("disksize")
    private final List<Integer> diskSizes;
    @SerializedName("memorysize")
    private final List<Integer> memorySizes;
    @SerializedName("cpucores")
    private final List<Integer> cpuCores;
    @SerializedName("template")
    private final List<String> templates;
    @SerializedName("transfer")
    private final List<Integer> transfers;
    @SerializedName("datacenter")
    private final List<String> dataCenters;

    public ServerAllowedArguments(List<Integer> diskSizes, List<Integer> memorySizes, List<Integer> cpuCores,
                                  List<String> templates, List<Integer> transfers, List<String> dataCenters) {
        checkNotNull(diskSizes, "diskSizes");
        checkNotNull(memorySizes, "memorySizes");
        checkNotNull(cpuCores, "cpuCores");
        checkNotNull(templates, "templates");
        checkNotNull(transfers, "transfers");
        checkNotNull(dataCenters, "dataCenters");

        this.diskSizes = diskSizes;
        this.memorySizes = memorySizes;
        this.cpuCores = cpuCores;
        this.templates = templates;
        this.transfers = transfers;
        this.dataCenters = dataCenters;
    }

    public List<Integer> getDiskSizes() {
        return diskSizes;
    }

    public List<Integer> getMemorySizes() {
        return memorySizes;
    }

    public List<Integer> getCpuCores() {
        return cpuCores;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public List<Integer> getTransfers() {
        return transfers;
    }

    public List<String> getDataCenters() {
        return dataCenters;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ServerAllowedArguments) {
            final ServerAllowedArguments other = (ServerAllowedArguments) object;
            return Objects.equal(diskSizes, other.diskSizes)
                    && Objects.equal(memorySizes, other.memorySizes)
                    && Objects.equal(cpuCores, other.cpuCores)
                    && Objects.equal(templates, other.templates)
                    && Objects.equal(transfers, other.transfers)
                    && Objects.equal(dataCenters, other.dataCenters);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
    }

    @Override
    public String toString() {
        checkNotNull(diskSizes, "diskSizes");
        checkNotNull(memorySizes, "memorySizes");
        checkNotNull(cpuCores, "cpuCores");
        checkNotNull(templates, "templates");
        checkNotNull(transfers, "transfers");
        checkNotNull(dataCenters, "dataCenters");

        Joiner commaJoiner = Joiner.on(", ");
        return String.format("[disksize=[%s], memorysize=[%s], cpuCores=[%s], templates=[%s], transfers=[%s], datacenters=[%s]]", commaJoiner.join(diskSizes), commaJoiner.join(memorySizes), commaJoiner.join(cpuCores), commaJoiner.join(templates), commaJoiner.join(transfers), commaJoiner.join(dataCenters));
    }

}
