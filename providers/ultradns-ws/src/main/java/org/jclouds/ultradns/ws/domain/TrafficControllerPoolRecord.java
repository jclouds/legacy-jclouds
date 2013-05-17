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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class TrafficControllerPoolRecord {

   public static TrafficControllerPoolRecord createA(String rdata) {
      return new TrafficControllerPoolRecord("A", rdata);
   }

   public static TrafficControllerPoolRecord createCNAME(String rdata) {
      return new TrafficControllerPoolRecord("CNAME", rdata);
   }
   public static TrafficControllerPoolRecord create(String type, String rdata) {
      return new TrafficControllerPoolRecord(type, rdata);
   }

   private final String type;
   private final String rdata;

   private TrafficControllerPoolRecord(String type, String rdata) {
      this.type = checkNotNull(type, "type");
      this.rdata = checkNotNull(rdata, "rdata");
   }

   /**
    * the type of the record, either {@code A} or {@code CNAME}
    */
   public String getType() {
      return type;
   }

   /**
    * address or cname this points to. ex. {@code jclouds.org.} or
    * {@code 1.2.3.4}
    */
   public String getRData() {
      return rdata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(type, rdata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      TrafficControllerPoolRecord that = TrafficControllerPoolRecord.class.cast(obj);
      return equal(this.type, that.type) && equal(this.rdata, that.rdata);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("type", type).add("rdata", rdata).toString();
   }
}
