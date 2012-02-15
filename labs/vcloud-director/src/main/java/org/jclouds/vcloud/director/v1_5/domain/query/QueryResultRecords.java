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

package org.jclouds.vcloud.director.v1_5.domain.query;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.query.ContainerType.Builder;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents the results from a vCloud query as records.
 * 
 * <pre>
 * &lt;complexType name="QueryResultRecords" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
public class QueryResultRecords extends ContainerType<QueryResultRecords> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.QUERY_RESULT_RECORDS;

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromQueryResultRecords(this);
   }

   public static class Builder extends ContainerType.Builder<QueryResultRecords> {

      private List<QueryResultRecordType<?>> records = Lists.newArrayList();

      /**
       * @see QueryResultRecords#getRecords()
       */
      public Builder records(List<QueryResultRecordType<?>> records) {
         this.records = records;
         return this;
      }

      /**
       * @see QueryResultRecords#getRecords()
       */
      public Builder record(QueryResultRecordType<?> record) {
         this.records.add(record);
         return this;
      }

      @Override
      public QueryResultRecords build() {
         QueryResultRecords queryResultRecords = new QueryResultRecords(href);
         queryResultRecords.setRecords(records);
         queryResultRecords.setName(name);
         queryResultRecords.setPage(page);
         queryResultRecords.setPageSize(pageSize);
         queryResultRecords.setTotal(total);
         queryResultRecords.setType(type);
         queryResultRecords.setLinks(links);
         return queryResultRecords;
      }

      /**
       * @see Container#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Container#getPage()
       */
      @Override
      public Builder page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see Container#getPageSize()
       */
      @Override
      public Builder pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromContainerType(ContainerType<QueryResultRecords> in) {
         return Builder.class.cast(super.fromContainerType(in));
      }

      public Builder fromQueryResultRecords(QueryResultRecords in) {
         return fromContainerType(in).records(in.getRecords());
      }
   }

   private QueryResultRecords() {
      // For JAXB and builder use
   }

   @XmlElementRef(name = "Record", namespace = VCLOUD_1_5_NS)
   protected List<QueryResultRecordType<?>> records;

   /**
    * Set of records representing query results.
    */
   public List<QueryResultRecordType<?>> getRecords() {
      return records;
   }

   public void setRecords(List<QueryResultRecordType<?>> links) {
      this.records = Lists.newArrayList(checkNotNull(links, "links"));
   }

   public void addRecords(QueryResultRecordType<?> record) {
      this.records.add(checkNotNull(record, "record"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRecords that = QueryResultRecords.class.cast(o);
      return super.equals(that) && equal(this.records, that.records);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(records);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("records", records);
   }

}
