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
package org.jclouds.aws.ec2.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.jclouds.aws.ec2.domain.TagFilter;
import org.jclouds.aws.ec2.domain.TagFilter.FilterName;
import org.jclouds.aws.ec2.domain.TagFilter.ResourceType;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @author grkvlt@apache.org
 */
public class TagFilters {
    protected final FilterName name;
    protected final Collection<String> values;

    protected TagFilters(FilterName name, Collection<String> values) {
        this.name = name;
        this.values = values != null ? values : Sets.<String>newHashSet();
    }

    public static Multimap<String, String> buildFormParametersForIndex(int index, TagFilter filter) {
        Map<String, String> headers = Maps.newLinkedHashMap();
        headers.put(String.format("Filter.%d.Name", index), filter.getName().value());
        int i = 0;
        for (String value : filter.getValues()) {
            headers.put(String.format("Filter.%d.Value.%d", index, ++i), value);
        }
        return Multimaps.forMap(headers);
    }

    public static StringTagFilter key() {
        return new StringTagFilter(FilterName.KEY);
    }

    public static StringTagFilter resourceId() {
        return new StringTagFilter(FilterName.RESOURCE_ID);
    }

    public static ResourceTypeTagFilter resourceType() {
        return new ResourceTypeTagFilter();
    }

    public static NamedTagFilter value() {
        return new StringTagFilter(FilterName.VALUE);
    }

    public static class NamedTagFilter extends TagFilters {
        public NamedTagFilter(FilterName name) {
            super(name, null);
        }

        public TagFilter filter() {
            return new TagFilter(name, values);
        }
    }

    public static class StringTagFilter extends NamedTagFilter {
        public StringTagFilter(FilterName name) {
            super(name);
        }

        public StringTagFilter exact(String value) {
            return value(value);
        }

        public StringTagFilter contains(String value) {
            return value(String.format("*%s*", value));
        }

        public StringTagFilter value(String value) {
            this.values.add(value);
            return this;
        }

        public StringTagFilter values(String... values) {
            this.values.addAll(Arrays.asList(values));
            return this;
        }
    }

    public static class ResourceTypeTagFilter extends NamedTagFilter {
        public ResourceTypeTagFilter() {
            super(FilterName.RESOURCE_TYPE);
        }

        public ResourceTypeTagFilter resourceType(ResourceType resourceType) {
            values.add(resourceType.value());
            return this;
        }

        public ResourceTypeTagFilter customerGateway() {
            return resourceType(ResourceType.CUSTOMER_GATEWAY);
        }

        public ResourceTypeTagFilter dhcpOptions() {
            return resourceType(ResourceType.DHCP_OPTIONS);
        }

        public ResourceTypeTagFilter image() {
            return resourceType(ResourceType.IMAGE);
        }

        public ResourceTypeTagFilter instance() {
            return resourceType(ResourceType.INSTANCE);
        }

        public ResourceTypeTagFilter internetGateway() {
            return resourceType(ResourceType.INTERNET_GATEWAY);
        }

        public ResourceTypeTagFilter networkAcl() {
            return resourceType(ResourceType.NETWORK_ACL);
        }

        public ResourceTypeTagFilter reservedInstance() {
            return resourceType(ResourceType.RESERVED_INSTANCES);
        }

        public ResourceTypeTagFilter routeTable() {
            return resourceType(ResourceType.ROUTE_TABLE);
        }

        public ResourceTypeTagFilter securityGroup() {
            return resourceType(ResourceType.SECURITY_GROUP);
        }

        public ResourceTypeTagFilter snapshot() {
            return resourceType(ResourceType.SNAPSHOT);
        }

        public ResourceTypeTagFilter instancesRequest() {
            return resourceType(ResourceType.SPOT_INSTANCES_REQUEST);
        }

        public ResourceTypeTagFilter subnet() {
            return resourceType(ResourceType.SUBNET);
        }

        public ResourceTypeTagFilter volume() {
            return resourceType(ResourceType.VOLUME);
        }

        public ResourceTypeTagFilter vpc() {
            return resourceType(ResourceType.VPC);
        }

        public ResourceTypeTagFilter vpnConnection() {
            return resourceType(ResourceType.VPN_CONNECTION);
        }

        public ResourceTypeTagFilter vpnGateway() {
            return resourceType(ResourceType.VPN_GATEWAY);
        }
    }
}
