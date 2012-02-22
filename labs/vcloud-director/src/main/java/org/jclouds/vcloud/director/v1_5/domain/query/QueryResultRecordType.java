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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ QueryResultCatalogRecord.class, QueryResultNetworkRecord.class })
public class QueryResultRecordType<T extends QueryResultRecordType<T>> {

   public static <T extends QueryResultRecordType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromQueryResultRecordType(this);
   }

   public static class Builder<T extends QueryResultRecordType<T>> {

      protected URI href;
      protected String id;
      protected String type;
      protected Set<Link> links = Sets.newLinkedHashSet();

      /**
       * @see QueryResultRecordType#getHref()
       */
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see QueryResultRecordType#getId()
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see QueryResultRecordType#getType()
       */
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      public Builder<T> links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see QueryResultRecordType#getLinks()
       */
      public Builder<T> link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      public QueryResultRecordType<T> build() {
         QueryResultRecordType<T> record = new QueryResultRecordType<T>(href);
         record.setId(id);
         record.setType(type);
         record.setLinks(links);
         return record;
      }

      public Builder<T> fromQueryResultRecordType(QueryResultRecordType<T> in) {
         return href(in.getHref()).id(in.getId()).type(in.getType());
      }
   }

   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Link")
   private Set<Link> links = Sets.newLinkedHashSet();
   @XmlAttribute
   @XmlSchemaType(name = "anyURI")
   private URI href;
   @XmlAttribute
   private String id;
   @XmlAttribute
   private String type;
   @XmlAnyAttribute
   // XXX not sure about this
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   public QueryResultRecordType(URI href) {
      this.href = href;
   }

   public QueryResultRecordType() {
      // For JAXB
   }

   /**
    * Set of optional links to an entity or operation associated with this object.
    */
   public Set<Link> getLinks() {
      return links;
   }

   public void setLinks(Set<Link> links) {
      this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
   }

   public void addLink(Link link) {
      this.links.add(checkNotNull(link, "link"));
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

   public void setHref(URI href) {
      this.href = href;
   }

   /**
    * The resource identifier, expressed in URN format. The value of this attribute uniquely identifies the resource, persists for
    * the life of the resource, and is never reused.
    */
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
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

   public void setType(String type) {
      this.type = type;
   }

   // XXX not sure about this

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    */
   public Map<QName, String> getOtherAttributes() {
      return otherAttributes;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      QueryResultRecordType<?> that = QueryResultRecordType.class.cast(o);
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
