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
package org.jclouds.cloudwatch.xml;

import org.jclouds.cloudwatch.domain.Datapoint;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import javax.inject.Inject;
import java.util.Date;

/**
 * 
 * @author Adrian Cole
 */
public class DatapointHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Datapoint> {
   private StringBuilder currentText = new StringBuilder();

   protected final DateService dateService;
   private Double average;
   private Double maximum;
   private Double minimum;
   private Date timestamp;
   private Double samples;
   private Double sum;
   private Unit unit;
   private String customUnit;

   @Inject
   public DatapointHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public Datapoint getResult() {
      Datapoint datapoint = new Datapoint(average, maximum, minimum, timestamp, samples, sum, unit, customUnit);
      this.average = null;
      this.maximum = null;
      this.minimum = null;
      this.timestamp = null;
      this.samples = null;
      this.sum = null;
      this.unit = null;
      this.customUnit = null;
      return datapoint;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Average")) {
         average = doubleOrNull();
      } else if (qName.equals("Maximum")) {
         maximum = doubleOrNull();
      } else if (qName.equals("Minimum")) {
         minimum = doubleOrNull();
      } else if (qName.equals("Timestamp")) {
         timestamp = dateService.iso8601SecondsDateParse(currentText.toString().trim());
      } else if (qName.equals("SampleCount")) {
         samples = doubleOrNull();
      } else if (qName.equals("Sum")) {
         sum = doubleOrNull();
      } else if (qName.equals("Unit")) {
         unit = Unit.fromValue(currentText.toString().trim());
      } else if (qName.equals("CustomUnit")) {
         customUnit = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   private Double doubleOrNull() {
      String string = currentText.toString().trim();
      if (!string.equals("")) {
         return new Double(string);
      }
      return null;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
