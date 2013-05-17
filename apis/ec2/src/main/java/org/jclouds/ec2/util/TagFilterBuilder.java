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

import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.features.TagApi;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * You can use filters to limit the results when describing tags. For example,
 * you could get only the tags for a particular resource type. You can specify
 * multiple values for a filter. A tag must match at least one of the specified
 * values for it to be included in the results.
 * 
 * You can specify multiple filters (for example, limit the results to a
 * specific resource type, and get only tags with values that contain the string
 * database). The result includes information for a particular tag only if it
 * matches all your filters. If there's no match, no special message is
 * returned; the response is simply empty.
 * 
 * <h4>Wildcards</h4> You can use wildcards with the filter values: {@code *}
 * matches zero or more characters, and ? matches exactly one character. You can
 * escape special characters using a backslash before the character. For
 * example, a value of {@code \*amazon\?\\} searches for the literal string
 * {@code *amazon?\}.
 * 
 * @author Adrian Cole
 * @see TagApi
 */
public class TagFilterBuilder extends ImmutableMultimap.Builder<String, String> {

   private static final String KEY = "key";
   private static final String VALUE = "value";
   private static final String RESOURCE_ID = "resource-id";
   private static final String RESOURCE_TYPE = "resource-type";

   public TagFilterBuilder key(String key) {
      return put(KEY, key);
   }

   public TagFilterBuilder keys(String... keys) {
      return putAll(KEY, keys);
   }

   public TagFilterBuilder keys(Iterable<String> keys) {
      return putAll(KEY, keys);
   }

   public TagFilterBuilder anyKey() {
      return putAll(KEY, ImmutableSet.<String> of());
   }

   public TagFilterBuilder value(String value) {
      return put(VALUE, value);
   }

   public TagFilterBuilder values(String... values) {
      return putAll(VALUE, ImmutableSet.<String> copyOf(values));
   }

   public TagFilterBuilder values(Iterable<String> values) {
      return putAll(VALUE, ImmutableSet.<String> copyOf(values));
   }

   public TagFilterBuilder anyValue() {
      return putAll(VALUE, ImmutableSet.<String> of());
   }

   public TagFilterBuilder resourceId(String resourceId) {
      return put(RESOURCE_ID, resourceId);
   }

   public TagFilterBuilder resourceIds(String... resourceIds) {
      return putAll(RESOURCE_ID, resourceIds);
   }

   public TagFilterBuilder resourceIds(Iterable<String> resourceIds) {
      return putAll(RESOURCE_ID, resourceIds);
   }

   public TagFilterBuilder anyResourceId() {
      return putAll(RESOURCE_ID, ImmutableSet.<String> of());
   }

   public TagFilterBuilder resourceType(String resourceType) {
      return put(RESOURCE_TYPE, resourceType);
   }

   public TagFilterBuilder resourceTypes(String... resourceTypes) {
      return putAll(RESOURCE_TYPE, resourceTypes);
   }

   public TagFilterBuilder resourceTypes(Iterable<String> resourceTypes) {
      return putAll(RESOURCE_TYPE, resourceTypes);
   }

   public TagFilterBuilder anyResourceType() {
      return putAll(RESOURCE_TYPE, ImmutableSet.<String> of());
   }

   public TagFilterBuilder customerGateway() {
      return put(RESOURCE_TYPE, Tag.ResourceType.CUSTOMER_GATEWAY);
   }

   public TagFilterBuilder dhcpOptions() {
      return put(RESOURCE_TYPE, Tag.ResourceType.DHCP_OPTIONS);
   }

   public TagFilterBuilder image() {
      return put(RESOURCE_TYPE, Tag.ResourceType.IMAGE);
   }

   public TagFilterBuilder instance() {
      return put(RESOURCE_TYPE, Tag.ResourceType.INSTANCE);
   }

   public TagFilterBuilder internetGateway() {
      return put(RESOURCE_TYPE, Tag.ResourceType.INTERNET_GATEWAY);
   }

   public TagFilterBuilder networkAcl() {
      return put(RESOURCE_TYPE, Tag.ResourceType.NETWORK_ACL);
   }

   public TagFilterBuilder reservedInstance() {
      return put(RESOURCE_TYPE, Tag.ResourceType.RESERVED_INSTANCES);
   }

   public TagFilterBuilder routeTable() {
      return put(RESOURCE_TYPE, Tag.ResourceType.ROUTE_TABLE);
   }

   public TagFilterBuilder securityGroup() {
      return put(RESOURCE_TYPE, Tag.ResourceType.SECURITY_GROUP);
   }

   public TagFilterBuilder snapshot() {
      return put(RESOURCE_TYPE, Tag.ResourceType.SNAPSHOT);
   }

   public TagFilterBuilder instancesRequest() {
      return put(RESOURCE_TYPE, Tag.ResourceType.SPOT_INSTANCES_REQUEST);
   }

   public TagFilterBuilder subnet() {
      return put(RESOURCE_TYPE, Tag.ResourceType.SUBNET);
   }

   public TagFilterBuilder volume() {
      return put(RESOURCE_TYPE, Tag.ResourceType.VOLUME);
   }

   public TagFilterBuilder vpc() {
      return put(RESOURCE_TYPE, Tag.ResourceType.VPC);
   }

   public TagFilterBuilder vpnConnection() {
      return put(RESOURCE_TYPE, Tag.ResourceType.VPN_CONNECTION);
   }

   public TagFilterBuilder vpnGateway() {
      return put(RESOURCE_TYPE, Tag.ResourceType.VPN_GATEWAY);
   }

   // to set correct return val in chain

   @Override
   public TagFilterBuilder put(String key, String value) {
      return TagFilterBuilder.class.cast(super.put(key, value));
   }

   @Override
   public TagFilterBuilder put(Entry<? extends String, ? extends String> entry) {
      return TagFilterBuilder.class.cast(super.put(entry));
   }

   @Override
   public TagFilterBuilder putAll(String key, Iterable<? extends String> values) {
      return TagFilterBuilder.class.cast(super.putAll(key, values));
   }

   @Override
   public TagFilterBuilder putAll(String key, String... values) {
      return TagFilterBuilder.class.cast(super.putAll(key, values));
   }

   @Override
   public TagFilterBuilder putAll(Multimap<? extends String, ? extends String> multimap) {
      return TagFilterBuilder.class.cast(super.putAll(multimap));
   }

   @Override
   @Beta
   public TagFilterBuilder orderKeysBy(Comparator<? super String> keyComparator) {
      return TagFilterBuilder.class.cast(super.orderKeysBy(keyComparator));
   }

   @Override
   @Beta
   public TagFilterBuilder orderValuesBy(Comparator<? super String> valueComparator) {
      return TagFilterBuilder.class.cast(super.orderValuesBy(valueComparator));
   }
}
