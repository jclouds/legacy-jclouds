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
package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.Spot;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
public class DescribeSpotPriceHistoryResponseHandler extends
         ParseSax.HandlerWithResult<Set<Spot>> {

   private Builder<Spot> spots = ImmutableSet.builder();
   private final SpotHandler spotHandler;

   @Inject
   public DescribeSpotPriceHistoryResponseHandler(SpotHandler spotHandler) {
      this.spotHandler = spotHandler;
   }

   public Set<Spot> getResult() {
      return spots.build();
   }

   @Override
   public HandlerWithResult<Set<Spot>> setContext(HttpRequest request) {
      spotHandler.setContext(request);
      return super.setContext(request);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (!qName.equals("item"))
         spotHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("item")) {
         spots.add(spotHandler.getResult());
      }
      spotHandler.endElement(uri, localName, qName);
   }

   public void characters(char ch[], int start, int length) {
      spotHandler.characters(ch, start, length);
   }

}
