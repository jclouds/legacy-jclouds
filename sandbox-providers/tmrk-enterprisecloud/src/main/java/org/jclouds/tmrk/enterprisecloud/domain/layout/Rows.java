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
package org.jclouds.tmrk.enterprisecloud.domain.layout;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual LayoutRow elements.
 * <xs:complexType name="RowsType">
 * @author Jason King
 */
public class Rows {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromRows(this);
   }

   public static class Builder {

       private Set<LayoutRow> rows = Sets.newLinkedHashSet();

       /**
        * @see Rows#getRows
        */
       public Builder rows(Set<LayoutRow> rows) {
          this.rows = Sets.newLinkedHashSet(checkNotNull(rows, "rows"));
          return this;
       }

       public Builder addRow(LayoutRow row) {
          rows.add(checkNotNull(row,"row"));
          return this;
       }

       public Rows build() {
           return new Rows(rows);
       }

       public Builder fromRows(Rows in) {
         return rows(in.getRows());
       }
   }

   private Rows() {
      //For JAXB and builder use
   }

   private Rows(Set<LayoutRow> entries) {
      this.rows = Sets.newLinkedHashSet(entries);
   }

   @XmlElement(name = "Row", required=false)
   private Set<LayoutRow> rows = Sets.newLinkedHashSet();

   public Set<LayoutRow> getRows() {
      return Collections.unmodifiableSet(rows);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Rows tasks1 = (Rows) o;

      if (!rows.equals(tasks1.rows)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return rows.hashCode();
   }

   public String toString() {
      return "["+ rows.toString()+"]";
   }
}
