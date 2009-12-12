/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.vcloud.terremark.domain.ComputeOption;

/**
 * @author Adrian Cole
 */
public class ComputeOptionHandler extends HandlerWithResult<ComputeOption> {

   private StringBuilder currentText = new StringBuilder();

   int processorCount;
   int memory;
   float costPerHour;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public ComputeOption getResult() {
      return new ComputeOption(processorCount, memory, costPerHour);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("ProcessorCount")) {
         processorCount = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Memory")) {
         memory = Integer.parseInt(currentOrNull());
      } else if (qName.equals("CostPerHour")) {
         costPerHour = Float.parseFloat(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}