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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.jclouds.vcloud.director.v1_5.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Base type for query result Records. Subtypes define more specific elements.
 *
 * <pre>
 * &lt;complexType name="QueryResultRecordType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlSeeAlso({
      QueryResultVAppTemplateRecord.class,
      QueryResultVAppRecord.class,
      QueryResultVMRecord.class,
      QueryResultDatastoreRecord.class,
      QueryResultCatalogRecord.class,
      QueryResultNetworkRecord.class,
      QueryResultRoleRecord.class,
      QueryResultAdminGroupRecord.class,
      QueryResultAdminVdcRecord.class,
      QueryResultAdminUserRecord.class,
      QueryResultStrandedUserRecord.class,
      QueryResultMediaRecord.class}
)
public class QueryResultRecordType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromQueryResultRecordType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> {

      private URI href;
      private String id;
      private String type;
      private Set<Link> links = Sets.newLinkedHashSet();

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see QueryResultRecordType#getHref()
       */
      public B href(URI href) {
         this.href = href;
         return self();
      }

      /**
       * @see QueryResultRecordType#getId()
       */
      public B id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see QueryResultRecordType#getType()
       */
      public B type(String type) {
         this.type = type;
         return self();
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      public B links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return self();
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      public B link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return self();
      }

      public QueryResultRecordType build() {
         return new QueryResultRecordType(this);
      }

      public B fromQueryResultRecordType(QueryResultRecordType in) {
         return href(in.getHref()).id(in.getId()).type(in.getType());
      }
   }

   @XmlElement(name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   private URI href;
   @XmlAttribute
   private String id;
   @XmlAttribute
   private String type;

   protected QueryResultRecordType(Builder<?> builder) {
      this.links = builder.links;
      this.href = builder.href;
      this.id = builder.id;
      this.type = builder.type;
   }

   public QueryResultRecordType(Set<Link> links, URI href, String id, String type) {
      this.links = links;
      this.href = href;
      this.id = id;
      this.type = type;
   }

   public QueryResultRecordType(URI href) {
      this.href = href;
   }

   protected QueryResultRecordType() {
      // For JAXB
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    */
   public Set<Link> getLinks() {
      return links;
   }

   /**
    * Contains the URI to the entity. An object reference, expressed in URL format. Because this URL includes the object identifier
    * portion of the id attribute value, it uniquely identifies the object, persists for the life of the object, and is never
    * reused. The value of the href attribute is a reference to a view of the object, and can be used to access a representation of
    * the object that is valid in a particular context. Although URLs have a well-known syntax and a well-understood interpretation,
    * a client should treat each href as an opaque string. The rules that govern how the server constructs href strings might change
    * in future releases.
    *
    * @return an opaque reference and should never be parsed
    */
   public URI getHref() {
      return href;
   }

   /**
    * The resource identifier, expressed in URN format. The value of this attribute uniquely identifies the resource, persists for
    * the life of the resource, and is never reused.
    */
   public String getId() {
      return id;
   }
   /**
    * Contains the type of the the entity. The object type, specified as a MIME content type, of the object that the link
    * references. This attribute is present only for links to objects. It is not present for links to actions.
    *
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getType() {
      return type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRecordType that = QueryResultRecordType.class.cast(o);
      return equal(this.href, that.href) && equal(this.id, that.id) &&
            equal(this.type, that.type) && equal(this.links, that.links);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(href, id, type, links);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("href", href).add("id", id)
            .add("type", type).add("links", links);
   }
}
