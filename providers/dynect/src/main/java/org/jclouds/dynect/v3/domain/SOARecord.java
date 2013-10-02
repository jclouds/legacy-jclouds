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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.dynect.v3.domain.Zone.SerialStyle;
import org.jclouds.dynect.v3.domain.rdata.SOAData;

import com.google.common.base.Objects.ToStringHelper;

/**
 * Start of Authority per RFC 1035
 * 
 * @author Adrian Cole
 */
public final class SOARecord extends Record<SOAData> {

   @Named("serial_style")
   private final SerialStyle serialStyle;

   @ConstructorProperties({ "zone", "fqdn", "record_type", "record_id", "ttl", "rdata", "serial_style" })
   private SOARecord(String zone, String fqdn, String type, long id, int ttl, SOAData rdata, SerialStyle serialStyle) {
      super(zone, fqdn, type, id, ttl, rdata);
      this.serialStyle = checkNotNull(serialStyle, "serialStyle of %s", id);
   }

   /**
    * @see Zone#getSerialStyle
    */
   public SerialStyle getSerialStyle() {
      return serialStyle;
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("serialStyle", serialStyle);
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder extends Record.Builder<SOAData, Builder> {

      private SerialStyle serialStyle;

      /**
       * @see Zone#getSerialStyle()
       */
      public Builder serialStyle(SerialStyle serialStyle) {
         this.serialStyle = serialStyle;
         return this;
      }

      public SOARecord build() {
         return new SOARecord(zone, fqdn, type, id, ttl, rdata, serialStyle);
      }

      @Override
      public Builder from(RecordId in) {
         if (in instanceof SOARecord) {
            SOARecord record = SOARecord.class.cast(in);
            serialStyle(record.serialStyle);
         }
         return super.from(in);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }   
}
