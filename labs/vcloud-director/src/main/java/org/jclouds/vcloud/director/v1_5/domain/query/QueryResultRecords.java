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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;

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
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "QueryResultRecords")
public class QueryResultRecords<T extends QueryResultRecordType<T>> extends ContainerType<QueryResultRecords<T>> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.QUERY_RESULT_RECORDS;

   public static <T extends QueryResultRecordType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromQueryResultRecords(this);
   }

   public static class Builder<T extends QueryResultRecordType<T>> extends ContainerType.Builder<QueryResultRecords<T>> {

      private List<T> records = Lists.newArrayList();

      /**
       * @see QueryResultRecords#getRecords()
       */
      public Builder<T> records(List<T> records) {
         this.records = records;
         return this;
      }

      /**
       * @see QueryResultRecords#getRecords()
       */
      public Builder<T> record(T record) {
         this.records.add(record);
         return this;
      }

      @Override
      public QueryResultRecords<T> build() {
         QueryResultRecords<T> queryResultRecords = new QueryResultRecords<T>(href);
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
       * @see ContainerType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ContainerType#getPage()
       */
      @Override
      public Builder<T> page(Integer page) {
         this.page = page;
         return this;
      }

      /**
       * @see ContainerType#getPageSize()
       */
      @Override
      public Builder<T> pageSize(Integer pageSize) {
         this.pageSize = pageSize;
         return this;
      }

      /**
       * @see ContainerType#getTotal()
       */
      @Override
      public Builder<T> total(Long total) {
         this.total = total;
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         super.links(Sets.newLinkedHashSet(checkNotNull(links, "links")));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder<T> fromContainerType(ContainerType<QueryResultRecords<T>> in) {
         return Builder.class.cast(super.fromContainerType(in));
      }

      public Builder<T> fromQueryResultRecords(QueryResultRecords<T> in) {
         return fromContainerType(in).records(in.getRecords());
      }
   }

   protected QueryResultRecords() {
      // For JAXB and builder use
   }

   protected QueryResultRecords(URI href) {
      super(href);
   }

   @XmlElementRef
   protected List<T> records;

   /**
    * Set of records representing query results.
    */
   public List<T> getRecords() {
      return records;
   }

   public void setRecords(List<T> records) {
      this.records = Lists.newArrayList(checkNotNull(records, "records"));
   }

   public void addRecords(T record) {
      this.records.add(checkNotNull(record, "record"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRecords<T> that = QueryResultRecords.class.cast(o);
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
