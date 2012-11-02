/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Represents a list of RASD items.
 *
 * <pre>
 * &lt;complexType name="RasdItemsList" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "RasdItemsList")
@XmlType(name = "RasdItemsList")
public class RasdItemsList extends Resource implements Set<RasdItem> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromRasdItemsList(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private Set<RasdItem> items = Sets.newLinkedHashSet();

      /**
       * @see RasdItemsList#getItems()
       */
      public B items(Set<RasdItem> items) {
         this.items = checkNotNull(items, "items");
         return self();
      }

      /**
       * @see RasdItemsList#getItems()
       */
      public B item(RasdItem item) {
         this.items.add(checkNotNull(item, "item"));
         return self();
      }

      @Override
      public RasdItemsList build() {
         RasdItemsList rasdItemsList = new RasdItemsList(this);
         return rasdItemsList;
      }

      public B fromRasdItemsList(RasdItemsList in) {
         return fromResource(in).items(in.getItems());
      }
   }

   protected RasdItemsList() {
      // For JAXB and B use
   }

   protected RasdItemsList(Builder<?> builder) {
      super(builder);
      this.items = builder.items;
   }

   @XmlElement(name = "Item")
   protected Set<RasdItem> items = Sets.newLinkedHashSet();

   /**
    * A RASD item content.
    */
   public Set<RasdItem> getItems() {
      return items;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RasdItemsList that = RasdItemsList.class.cast(o);
      return super.equals(that) && equal(this.items, that.items);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), items);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("items", items);
   }

   /**
    * The delegate always returns a {@link Set} even if {@link #items} is {@literal null}.
    * 
    * The delegated {@link Set} is used by the methods implementing its interface.
    * <p>
    * NOTE Annoying lack of multiple inheritance for using ForwardingList!
    */
   private Set<RasdItem> delegate() {
      return getItems();
   }

   @Override
   public boolean add(RasdItem arg0) {
      return delegate().add(arg0);
   }

   @Override
   public boolean addAll(Collection<? extends RasdItem> arg0) {
      return delegate().addAll(arg0);
   }

   @Override
   public void clear() {
      delegate().clear();
   }

   @Override
   public boolean contains(Object arg0) {
      return delegate().contains(arg0);
   }

   @Override
   public boolean containsAll(Collection<?> arg0) {
      return delegate().containsAll(arg0);
   }

   @Override
   public boolean isEmpty() {
      return delegate().isEmpty();
   }

   @Override
   public Iterator<RasdItem> iterator() {
      return delegate().iterator();
   }

   @Override
   public boolean remove(Object arg0) {
      return delegate().remove(arg0);
   }

   @Override
   public boolean removeAll(Collection<?> arg0) {
      return delegate().removeAll(arg0);
   }

   @Override
   public boolean retainAll(Collection<?> arg0) {
      return delegate().retainAll(arg0);
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public Object[] toArray() {
      return delegate().toArray();
   }

   @Override
   public <T> T[] toArray(T[] arg0) {
      return delegate().toArray(arg0);
   }
}
