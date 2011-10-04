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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;

/**
 * tag filter.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html" />
 * @author grkvlt@apache.org
 */
public class TagFilter implements Comparable<TagFilter> {
    public static enum FilterName {
        KEY,
        RESOURCE_ID,
        RESOURCE_TYPE,
        VALUE;

        public String value() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
        }

        @Override
        public String toString() {
            return value();
        }

        public static FilterName fromValue(String name) {
            try {
                return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(name, "name")));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static enum ResourceType {
        CUSTOMER_GATEWAY,
        DHCP_OPTIONS,
        IMAGE,
        INSTANCE,
        INTERNET_GATEWAY,
        NETWORK_ACL,
        RESERVED_INSTANCES,
        ROUTE_TABLE,
        SECURITY_GROUP,
        SNAPSHOT,
        SPOT_INSTANCES_REQUEST,
        SUBNET,
        VOLUME,
        VPC,
        VPN_CONNECTION,
        VPN_GATEWAY;

        public String value() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
        }

        @Override
        public String toString() {
            return value();
        }

        public static ResourceType fromValue(String name) {
            try {
                return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(name, "name")));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private final FilterName name;
    private final Collection<String> values;

    public TagFilter(FilterName name, Collection<String> values) {
        this.name = checkNotNull(name, "name");
        this.values = checkNotNull(values, "values");
    }

    @Override
    public int compareTo(TagFilter o) {
        return name.compareTo(o.name);
    }

    /**
     * @return Name of the filter type.
     */
    public FilterName getName() {
        return name;
    }

    /**
     * @return Values to filter on.
     */
    public Collection<String> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagFilter other = (TagFilter) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", values=" + values + "]";
    }
}
