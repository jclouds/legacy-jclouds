/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.util;

import java.util.Comparator;
import java.util.Map.Entry;

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.features.SubnetApi;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * You can specify filters so that the response includes information for only
 * certain subnets. For example, you can use a filter to specify that you're
 * interested in the subnets in the available state. You can specify multiple
 * values for a filter. The response includes information for a subnet only if
 * it matches at least one of the filter values that you specified.
 *
 * You can specify multiple filters; for example, specify subnets that are in a
 * specific VPC and are in the available state. The response includes
 * information for a subnet only if it matches all the filters that you
 * specified. If there's no match, no special message is returned, the response
 * is simply empty.
 * 
 * <h4>Wildcards</h4> You can use wildcards with the filter values: {@code *}
 * matches zero or more characters, and ? matches exactly one character. You can
 * escape special characters using a backslash before the character. For
 * example, a value of {@code \*amazon\?\\} searches for the literal string
 * {@code *amazon?\}.
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 * @see SubnetApi
 */
public class SubnetFilterBuilder extends ImmutableMultimap.Builder<String, String> {

    private static final String AVAILABILITY_ZONE = "availability-zone";
    private static final String AVAILABLE_IP_ADDRESS_COUNT = "available-ip-address-count";
    private static final String CIDR = "cidr";
    private static final String STATE = "state";
    private static final String SUBNET_ID = "subnet-id";
    private static final String TAG_KEY = "tag-key";
    private static final String TAG_VALUE = "tag-value";
    private static final String TAG_ARBITRARY_BASE = "tag:";
    private static final String VPC_ID = "vpc-id";

    public SubnetFilterBuilder availabilityZone(String availabilityZone) {
        return put(AVAILABILITY_ZONE, availabilityZone);
    }

    public SubnetFilterBuilder availabilityZones(String... availabilityZones) {
        return putAll(AVAILABILITY_ZONE, availabilityZones);
    }

    public SubnetFilterBuilder availabilityZones(Iterable<String> availabilityZones) {
        return putAll(AVAILABILITY_ZONE, availabilityZones);
    }

    public SubnetFilterBuilder anyAvailabilityZone() {
        return putAll(AVAILABILITY_ZONE, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder availableIpAddressCount(String availableIpAddressCount) {
        return put(AVAILABLE_IP_ADDRESS_COUNT, availableIpAddressCount);
    }

    public SubnetFilterBuilder availableIpAddressCounts(String... availableIpAddressCounts) {
        return putAll(AVAILABLE_IP_ADDRESS_COUNT, availableIpAddressCounts);
    }

    public SubnetFilterBuilder availableIpAddressCounts(Iterable<String> availableIpAddressCounts) {
        return putAll(AVAILABLE_IP_ADDRESS_COUNT, availableIpAddressCounts);
    }

    public SubnetFilterBuilder anyAvailableIpAddressCount() {
        return putAll(AVAILABLE_IP_ADDRESS_COUNT, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder cidr(String cidr) {
        return put(CIDR, cidr);
    }

    public SubnetFilterBuilder cidrs(String... cidrs) {
        return putAll(CIDR, cidrs);
    }

    public SubnetFilterBuilder cidrs(Iterable<String> cidrs) {
        return putAll(CIDR, cidrs);
    }

    public SubnetFilterBuilder anyCidr() {
        return putAll(CIDR, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder state(String state) {
        return put(STATE, state);
    }

    public SubnetFilterBuilder states(String... states) {
        return putAll(STATE, states);
    }

    public SubnetFilterBuilder states(Iterable<String> states) {
        return putAll(STATE, states);
    }

    public SubnetFilterBuilder anyState() {
        return putAll(STATE, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder available() {
        return put(STATE, Subnet.State.AVAILABLE.value());
    }

    public SubnetFilterBuilder pending() {
        return put(STATE, Subnet.State.PENDING.value());
    }

    public SubnetFilterBuilder subnetId(String subnetId) {
        return put(SUBNET_ID, subnetId);
    }

    public SubnetFilterBuilder subnetIds(String... subnetIds) {
        return putAll(SUBNET_ID, subnetIds);
    }

    public SubnetFilterBuilder subnetIds(Iterable<String> subnetIds) {
        return putAll(SUBNET_ID, subnetIds);
    }

    public SubnetFilterBuilder anySubnetId() {
        return putAll(SUBNET_ID, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder tagKey(String tagKey) {
        return put(TAG_KEY, tagKey);
    }

    public SubnetFilterBuilder tagKeys(String... tagKeys) {
        return putAll(TAG_KEY, tagKeys);
    }

    public SubnetFilterBuilder tagKeys(Iterable<String> tagKeys) {
        return putAll(TAG_KEY, tagKeys);
    }

    public SubnetFilterBuilder anyTagKey() {
        return putAll(TAG_KEY, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder tagValue(String tagValue) {
        return put(TAG_VALUE, tagValue);
    }

    public SubnetFilterBuilder tagValues(String... tagValues) {
        return putAll(TAG_VALUE, tagValues);
    }

    public SubnetFilterBuilder tagValues(Iterable<String> tagValues) {
        return putAll(TAG_VALUE, tagValues);
    }

    public SubnetFilterBuilder anyTagValue() {
        return putAll(TAG_VALUE, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder vpcId(String vpcId) {
        return put(VPC_ID, vpcId);
    }

    public SubnetFilterBuilder vpcIds(String... vpcIds) {
        return putAll(VPC_ID, vpcIds);
    }

    public SubnetFilterBuilder vpcIds(Iterable<String> vpcIds) {
        return putAll(VPC_ID, vpcIds);
    }

    public SubnetFilterBuilder anyVpcId() {
        return putAll(VPC_ID, ImmutableSet.<String> of());
    }

    public SubnetFilterBuilder arbitraryTag(String arbitraryTagKey, String arbitraryTagValue) {
        return put(TAG_ARBITRARY_BASE + arbitraryTagKey, arbitraryTagValue);
    }

    public SubnetFilterBuilder arbitraryTag(String arbitraryTagKey, String... arbitraryTagValues) {
        return putAll(TAG_ARBITRARY_BASE + arbitraryTagKey, arbitraryTagValues);
    }

    public SubnetFilterBuilder arbitraryTag(String arbitraryTagKey, Iterable<String> arbitraryTagValues) {
        return putAll(TAG_ARBITRARY_BASE + arbitraryTagKey, arbitraryTagValues);
    }


    // to set correct return val in chain

    @Override
    public SubnetFilterBuilder put(String key, String value) {
        return SubnetFilterBuilder.class.cast(super.put(key, value));
    }

    @Override
    public SubnetFilterBuilder put(Entry<? extends String, ? extends String> entry) {
        return SubnetFilterBuilder.class.cast(super.put(entry));
    }

    @Override
    public SubnetFilterBuilder putAll(String key, Iterable<? extends String> values) {
        return SubnetFilterBuilder.class.cast(super.putAll(key, values));
    }

    @Override
    public SubnetFilterBuilder putAll(String key, String... values) {
        return SubnetFilterBuilder.class.cast(super.putAll(key, values));
    }

    @Override
    public SubnetFilterBuilder putAll(Multimap<? extends String, ? extends String> multimap) {
        return SubnetFilterBuilder.class.cast(super.putAll(multimap));
    }

    @Override
    @Beta
    public SubnetFilterBuilder orderKeysBy(Comparator<? super String> keyComparator) {
        return SubnetFilterBuilder.class.cast(super.orderKeysBy(keyComparator));
    }

    @Override
    @Beta
    public SubnetFilterBuilder orderValuesBy(Comparator<? super String> valueComparator) {
        return SubnetFilterBuilder.class.cast(super.orderValuesBy(valueComparator));
    }
}
