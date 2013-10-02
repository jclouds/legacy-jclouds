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
package org.jclouds.aws.ec2.xml;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class SpotHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Spot> {
   private StringBuilder currentText = new StringBuilder();

   protected final DateCodec dateCodec;
   protected final Supplier<String> defaultRegion;

   @Inject
   public SpotHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion) {
      this.dateCodec = dateCodecFactory.iso8601();
      this.defaultRegion = defaultRegion;
   }

   private Spot.Builder builder = Spot.builder();

   public Spot getResult() {
      try {
         String region = getRequest() == null ? null : AWSUtils.findRegionInArgsOrNull(getRequest());
         if (region == null)
            region = defaultRegion.get();
         return builder.region(region).build();
      } finally {
         builder.clear();
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("instanceType")) {
         builder.instanceType(currentText.toString().trim());
      } else if (qName.equals("productDescription")) {
         builder.productDescription(currentText.toString().trim());
      } else if (qName.equals("spotPrice")) {
         builder.spotPrice(Float.parseFloat(currentText.toString().trim()));
      } else if (qName.equals("timestamp")) {
         builder.timestamp(dateCodec.toDate(currentText.toString().trim()));
      } else if (qName.equals("availabilityZone")) {
         builder.availabilityZone(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
