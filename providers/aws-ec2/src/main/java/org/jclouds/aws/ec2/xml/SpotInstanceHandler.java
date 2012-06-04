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

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest.Builder;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class SpotInstanceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SpotInstanceRequest> {
   private StringBuilder currentText = new StringBuilder();

   protected final DateService dateService;
   protected final Supplier<String> defaultRegion;
   protected final Builder builder;
   protected boolean inLaunchSpecification;
   protected final LaunchSpecificationHandler launchSpecificationHandler;
   protected boolean inTagSet;
   protected final TagSetHandler tagSetHandler;

   @Inject
   public SpotInstanceHandler(DateService dateService, @Region Supplier<String> defaultRegion,
         LaunchSpecificationHandler launchSpecificationHandler, TagSetHandler tagSetHandler,
         SpotInstanceRequest.Builder builder) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
      this.launchSpecificationHandler = launchSpecificationHandler;
      this.tagSetHandler = tagSetHandler;
      this.builder = builder;
   }

   public SpotInstanceRequest getResult() {
      try {
         String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
         if (region == null)
            region = defaultRegion.get();
         return builder.region(region).build();
      } finally {
         builder.clear();
      }
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) throws SAXException {
      if (equalsOrSuffix(qName, "launchSpecification")) {
         inLaunchSpecification = true;
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      }
      if (inLaunchSpecification) {
          launchSpecificationHandler.startElement(uri, name, qName, attrs);
      } else if (inTagSet) {
          tagSetHandler.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
         builder.tags(tagSetHandler.getResult());
      } else if (inTagSet) {
          tagSetHandler.endElement(uri, name, qName);
      }

      if (qName.equals("launchSpecification")) {
         inLaunchSpecification = false;
         builder.launchSpecification(launchSpecificationHandler.getResult());
      } else if (inLaunchSpecification) {
         launchSpecificationHandler.endElement(uri, name, qName);
      }

      if (qName.equals("spotInstanceRequestId")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("instanceId")) {
         builder.instanceId(currentOrNull(currentText));
      } else if (qName.equals("launchedAvailabilityZone")) {
         builder.launchedAvailabilityZone(currentOrNull(currentText));
      } else if (qName.equals("availabilityZoneGroup")) {
         builder.availabilityZoneGroup(currentOrNull(currentText));
      } else if (qName.equals("launchGroup")) {
         builder.launchGroup(currentOrNull(currentText));
      } else if (qName.equals("code")) {
         builder.faultCode(currentOrNull(currentText));
      } else if (qName.equals("message")) {
         builder.faultMessage(currentOrNull(currentText));
      } else if (qName.equals("spotPrice")) {
         String price = currentOrNull(currentText);
         if (price != null)
            builder.spotPrice(Float.parseFloat(price));
      } else if (qName.equals("type")) {
         String type = currentOrNull(currentText);
         if (type != null)
            builder.type(SpotInstanceRequest.Type.fromValue(type));
      } else if (qName.equals("state")) {
         String rawState = currentOrNull(currentText);
         if (rawState != null) {
            builder.rawState(rawState);
            builder.state(SpotInstanceRequest.State.fromValue(rawState));
         }
      } else if (qName.equals("createTime")) {
         String createTime = currentOrNull(currentText);
         if (createTime != null)
            builder.createTime(dateService.iso8601DateParse(createTime));
      } else if (qName.equals("productDescription")) {
         builder.productDescription(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inLaunchSpecification) {
           launchSpecificationHandler.characters(ch, start, length);
      } else if (inTagSet) {
           tagSetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
