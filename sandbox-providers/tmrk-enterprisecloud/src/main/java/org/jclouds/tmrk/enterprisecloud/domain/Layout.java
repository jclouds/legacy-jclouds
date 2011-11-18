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

import org.jclouds.javax.annotation.Nullable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Layout is a logical aggregation of virtual machines and physical devices defined by the organization.
 * Layout is an environment concept but visible from both the environment and the compute pool.
 * From the environment, all rows, groups, virtual machines, and physical devices are visible.
 * From the compute pool, only a subset is visible.
 * To appear in a compute pool layout, virtual machines in the rows and groups must be allocated from the compute pool.
 * As physical devices are not allocated from the compute pool, they do not appear on compute pool layouts.
 * <xs:complexType name="LayoutReference">
 * @author Jason King
 */
public class Layout {
    @XmlElement(name = "Group")
    private Group group;

    @XmlElement(name = "Row")
    private Row row;

    public Layout(@Nullable Group group, @Nullable Row row) {
        this.group = group;
        this.row = row;
    }

    public Layout() {
        //For JAXB
    }

    public Group getGroup() {
        return group;
    }

    public Row getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Layout layout = (Layout) o;

        if (group != null ? !group.equals(layout.group) : layout.group != null)
            return false;
        if (row != null ? !row.equals(layout.row) : layout.row != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + (row != null ? row.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "[group="+group+", row="+row+"]";
    }

}
