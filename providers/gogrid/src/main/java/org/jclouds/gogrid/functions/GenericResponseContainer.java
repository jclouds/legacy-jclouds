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
package org.jclouds.gogrid.functions;

import java.util.SortedSet;

/**
 * General format of GoGrid's response.
 * 
 * This is the view for most responses, and the actual result (or error) will
 * be set to {@link #list}. Note that even the single returned item will be set
 * to {@link #list} per GoGrid's design.
 * 
 * This class is not intended to be used by customers directly, it is here to
 * assist in deserialization.
 * 
 * @author Oleksiy Yarmula
 */
public class GenericResponseContainer<T> {

   private Summary summary;
   private String status;
   private String method;
   private SortedSet<T> list;

   public Summary getSummary() {
      return summary;
   }

   public String getStatus() {
      return status;
   }

   public String getMethod() {
      return method;
   }

   public SortedSet<T> getList() {
      return list;
   }

   static class Summary {
      private int total;
      private int start;
      private int numPages;
      private int returned;

      public int getTotal() {
         return total;
      }

      public int getStart() {
         return start;
      }

      public int getNumPages() {
         return numPages;
      }

      public int getReturned() {
         return returned;
      }
   }

}
