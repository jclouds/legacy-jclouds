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
package org.jclouds.rackspace.clouddns.v1.functions;

import java.util.Map;
import java.util.Set;

import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Functions for working with Records.
 * 
 * @author Everett Toews
 */
public class RecordFunctions {
   
   private RecordFunctions() {
   }

   /**
    * Take a Set of RecordDetails and return a Map of record id to the Record.
    */
   public static Map<String, Record> toRecordMap(Set<RecordDetail> recordDetails) {
      Map<String, RecordDetail> idsToRecordDetails = Maps.uniqueIndex(recordDetails, RecordFunctions.GET_RECORD_ID);
      return Maps.transformValues(idsToRecordDetails, RecordFunctions.GET_RECORD);
   }
   
   /**
    * Take a RecordDetail and return its id.
    */
   public static final Function<RecordDetail, String> GET_RECORD_ID = new Function<RecordDetail, String>() {
      public String apply(RecordDetail recordDetail) {
         return recordDetail.getId();
      }
   };
   /**
    * Take a RecordDetail and return its Record.
    */
   public static final Function<RecordDetail, Record> GET_RECORD = new Function<RecordDetail, Record>() {
      public Record apply(RecordDetail recordDetail) {
         return recordDetail.getRecord();
      }
   };   
}
