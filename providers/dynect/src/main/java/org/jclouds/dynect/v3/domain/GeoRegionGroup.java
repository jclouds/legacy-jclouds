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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
public class GeoRegionGroup {

   private final Optional<String> serviceName;
   private final String name;
   private final List<String> countries;
   private final List<RecordSet> recordSets;

   private GeoRegionGroup(Optional<String> serviceName, String name, List<String> countries, List<RecordSet> recordSets) {
      this.serviceName = checkNotNull(serviceName, "serviceName");
      this.name = checkNotNull(name, "name");
      this.countries = checkNotNull(countries, "countries of %s/%s", serviceName, name);
      this.recordSets = checkNotNull(recordSets, "recordSets of %s", name);
   }

   /**
    * Name of the Geo Service. Absent, if a member of {@link GeoService}
    */
   public Optional<String> getServiceName() {
      return serviceName;
   }

   /**
    * Name of the Region Group
    */
   public String getName() {
      return name;
   }

   /**
    * A list of ISO-3166 two letter codes to represent the names of countries
    * and their subdivisions or one of the predefined groups.
    */
   public List<String> getCountries() {
      return countries;
   }

   /**
    * record sets assigned to this region group.
    */
   public List<RecordSet> getRecordSets() {
      return recordSets;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(serviceName, name, countries, recordSets);
   }

   /**
    * permits equals comparisons with subtypes
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || !(obj instanceof GeoRegionGroup))
         return false;
      GeoRegionGroup that = GeoRegionGroup.class.cast(obj);
      return equal(this.serviceName, that.serviceName) && equal(this.name, that.name)
            && equal(this.countries, that.countries) && equal(this.recordSets, that.recordSets);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("serviceName", serviceName.orNull()).add("name", name)
            .add("countries", countries).add("recordSets", recordSets).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {

      private Optional<String> serviceName = Optional.absent();
      private String name;
      private ImmutableList.Builder<String> countries = ImmutableList.builder();
      private ImmutableList.Builder<RecordSet> recordSets = ImmutableList.builder();

      /**
       * @see GeoRegionGroup#getServiceName()
       */
      public Builder serviceName(String serviceName) {
         this.serviceName = Optional.fromNullable(serviceName);
         return this;
      }

      /**
       * @see GeoRegionGroup#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see GeoRegionGroup#getCountries()
       */
      public Builder addCountry(String country) {
         this.countries.add(country);
         return this;
      }

      /**
       * replaces current record sets
       * 
       * @see GeoRegionGroup#getCountries()
       */
      public Builder countries(Iterable<String> countries) {
         this.countries = ImmutableList.<String> builder().addAll(countries);
         return this;
      }

      /**
       * @see GeoRegionGroup#getCountries()
       */
      public Builder addAllCountries(Iterable<String> countries) {
         this.countries.addAll(countries);
         return this;
      }

      /**
       * @see GeoRegionGroup#getRecordSets()
       */
      public Builder addRecordSet(RecordSet recordSet) {
         this.recordSets.add(recordSet);
         return this;
      }

      /**
       * replaces current record sets
       * 
       * @see GeoRegionGroup#getRecordSets()
       */
      public Builder recordSets(Iterable<RecordSet> recordSets) {
         this.recordSets = ImmutableList.<RecordSet> builder().addAll(recordSets);
         return this;
      }

      /**
       * @see GeoRegionGroup#getRecordSets()
       */
      public Builder addAllRecordSets(Iterable<RecordSet> recordSets) {
         this.recordSets.addAll(recordSets);
         return this;
      }

      public GeoRegionGroup build() {
         return new GeoRegionGroup(serviceName, name, countries.build(), recordSets.build());
      }

      public Builder from(GeoRegionGroup in) {
         return serviceName(in.serviceName.orNull()).name(in.name).countries(in.countries).recordSets(in.recordSets);
      }
   }
}
