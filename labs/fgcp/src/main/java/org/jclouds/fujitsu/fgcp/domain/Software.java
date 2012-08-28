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

import com.google.common.base.Objects;

/**
 * Represents the software installed on a disk.
 * 
 * @author Dies Koper
 */
public class Software {
    private String name;

    private String id;

    private String category;

    private String version;

    private String officialVersion;

    private String patch;

    private String license;

    private String support;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getVersion() {
        return version;
    }

    public String getOfficialVersion() {
        return officialVersion;
    }

    public String getPatch() {
        return patch;
    }

    public String getLicense() {
        return license;
    }

    public String getSupport() {
        return support;
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
        Software that = Software.class.cast(obj);
        return Objects.equal(this.id, that.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues().add("id", id)
                .add("name", name).add("category", category)
                .add("version", version)
                .add("officialVersion", officialVersion)
                .add("support", support).add("patch", patch)
                .add("license", license).toString();
    }
}
