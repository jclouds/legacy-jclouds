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
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http:
 * 
 * @author Adrian Cole
 * @see <a href="http: />
 */
public class DescribeInstancesResponseHandler extends
      BaseReservationHandler<Set<Reservation<? extends RunningInstance>>> {
   private final TagSetHandler tagSetHandler;
   private Builder<Reservation<? extends RunningInstance>> reservations = ImmutableSet
         .<Reservation<? extends RunningInstance>> builder();
   private boolean inTagSet;

   @Inject
   DescribeInstancesResponseHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion,
         TagSetHandler tagSetHandler) {
      super(dateCodecFactory, defaultRegion);
      this.tagSetHandler = tagSetHandler;
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      super.startElement(uri, name, qName, attrs);
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      if (inTagSet) {
         tagSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inTagSet) {
         tagSetHandler.characters(ch, start, length);
      } else {
         super.characters(ch, start, length);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         builder.tags(tagSetHandler.getResult());
      } else if (inTagSet) {
         tagSetHandler.endElement(uri, name, qName);
      }
      super.endElement(uri, name, qName);
   }

   @Override
   public Set<Reservation<? extends RunningInstance>> getResult() {
      return reservations.build();
   }

   protected boolean endOfReservationItem() {
      return itemDepth == 1;
   }

   @Override
   protected void inItem() {
      if (endOfReservationItem()) {
         reservations.add(super.newReservation());
      } else {
         super.inItem();
      }
   }

   protected boolean endOfInstanceItem() {
      return itemDepth == 2 && inInstancesSet;
   }
}
