/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.event.options;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.abiquo.domain.options.search.FilterOptions.BaseFilterOptionsBuilder;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.abiquo.model.enumerator.ComponentType;
import com.abiquo.model.enumerator.EventType;
import com.abiquo.model.enumerator.SeverityType;
import com.google.common.collect.Maps;

/**
 * Available options to query events.
 * 
 * @author Vivien Mahé
 */
public class EventOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      EventOptions options = new EventOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder extends BaseFilterOptionsBuilder<Builder> {
      private Map<String, String> filters = Maps.newHashMap();

      public Builder filters(final Map<String, String> filters) {
         this.filters = filters;
         return this;
      }

      public Builder severity(final SeverityType severity) {
         this.filters.put("severity", severity.name());
         return this;
      }

      public Builder component(final ComponentType component) {
         this.filters.put("component", component.name());
         return this;
      }

      public Builder actionPerformed(final EventType action) {
         this.filters.put("actionperformed", action.name());
         return this;
      }

      public Builder datacenterName(final String dc) {
         this.filters.put("datacenter", dc);
         return this;
      }

      public Builder rackName(final String rack) {
         this.filters.put("rack", rack);
         return this;
      }

      public Builder physicalMachineName(final String pm) {
         this.filters.put("physicalmachine", pm);
         return this;
      }

      public Builder storageSystemName(final String ss) {
         this.filters.put("storagesystem", ss);
         return this;
      }

      public Builder storagePoolName(final String sp) {
         this.filters.put("storagepool", sp);
         return this;
      }

      public Builder volumeName(final String volume) {
         this.filters.put("volume", volume);
         return this;
      }

      public Builder networkName(final String network) {
         this.filters.put("network", network);
         return this;
      }

      public Builder subnetName(final String subnet) {
         this.filters.put("subnet", subnet);
         return this;
      }

      public Builder enterpriseName(final String ent) {
         this.filters.put("enterprise", ent);
         return this;
      }

      public Builder userName(final String user) {
         this.filters.put("user", user);
         return this;
      }

      public Builder virtualDatacenterName(final String vdc) {
         this.filters.put("virtualdatacenter", vdc);
         return this;
      }

      public Builder virtualAppName(final String vapp) {
         this.filters.put("virtualapp", vapp);
         return this;
      }

      public Builder virtualMachineName(final String vm) {
         this.filters.put("virtualMachine", vm);
         return this;
      }

      public Builder performedBy(final String pb) {
         this.filters.put("performedBy", pb);
         return this;
      }

      public Builder description(final String description) {
         this.filters.put("stacktrace", description);
         return this;
      }

      public Builder dateFrom(final Date date) {
         this.filters.put("datefrom", String.valueOf(date.getTime()));
         return this;
      }

      public Builder dateTo(final Date date) {
         this.filters.put("dateTo", String.valueOf(date.getTime()));
         return this;
      }

      public EventOptions build() {
         EventOptions options = new EventOptions();

         for (Entry<String, String> filter : filters.entrySet()) {
            options.queryParameters.put(filter.getKey(), filter.getValue());
         }

         return addFilterOptions(options);
      }
   }
}
