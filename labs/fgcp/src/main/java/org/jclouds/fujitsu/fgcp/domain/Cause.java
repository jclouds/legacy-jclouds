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
package org.jclouds.fujitsu.fgcp.domain;


/**
 * Represents a load balancer (SLB) log entry.
 * 
 * @author Dies Koper
 */
public class Cause {
   private String cat;

   private String status;

   private String filePath;

   private String current;

   private String before;

   private String today;

   private String yesterday;

   private String total;

   /**
    * @return category
    */
   public String getCat() {
      return cat;
   }

   /**
    * @return the status
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the filePath
    */
   public String getFilePath() {
      return filePath;
   }

   /**
    * @return the current
    */
   public String getCurrent() {
      return current;
   }

   /**
    * @return the before
    */
   public String getBefore() {
      return before;
   }

   /**
    * @return the today
    */
   public String getToday() {
      return today;
   }

   /**
    * @return the yesterday
    */
   public String getYesterday() {
      return yesterday;
   }

   /**
    * @return the total
    */
   public String getTotal() {
      return total;
   }

}
