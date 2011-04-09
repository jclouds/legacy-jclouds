/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest.Builder;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class SpotInstanceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SpotInstanceRequest> {
   private StringBuilder currentText = new StringBuilder();

   protected final DateService dateService;
   protected final String defaultRegion;
   protected final Builder builder;
   protected boolean inLaunchSpecification;
   protected final LaunchSpecificationHandler launchSpecificationHandler;

   @Inject
   public SpotInstanceHandler(DateService dateService, @Region String defaultRegion,
         LaunchSpecificationHandler launchSpecificationHandler, SpotInstanceRequest.Builder builder) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
      this.launchSpecificationHandler = launchSpecificationHandler;
      this.builder = builder;
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public SpotInstanceRequest getResult() {
      try {
         String region = getRequest() != null ? AWSUtils.findRegionInArgsOrNull(getRequest()) : null;
         if (region == null)
            region = defaultRegion;
         return builder.region(region).build();
      } finally {
         builder.clear();
      }
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("launchSpecification")) {
         inLaunchSpecification = true;
      }
      if (inLaunchSpecification)
         launchSpecificationHandler.startElement(uri, name, qName, attrs);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("launchSpecification")) {
         inLaunchSpecification = false;
         builder.launchSpecification(launchSpecificationHandler.getResult());
      }
      if (inLaunchSpecification) {
         launchSpecificationHandler.endElement(uri, name, qName);
      } else if (qName.equals("spotInstanceRequestId")) {
         builder.id(currentOrNull());
      } else if (qName.equals("instanceId")) {
         builder.instanceId(currentOrNull());
      } else if (qName.equals("availabilityZoneGroup")) {
         builder.availabilityZoneGroup(currentOrNull());
      } else if (qName.equals("launchGroup")) {
         builder.launchGroup(currentOrNull());
      } else if (qName.equals("code")) {
         builder.faultCode(currentOrNull());
      } else if (qName.equals("message")) {
         builder.faultMessage(currentOrNull());
      } else if (qName.equals("spotPrice")) {
         String price = currentOrNull();
         if (price != null)
            builder.spotPrice(Float.parseFloat(price));
      } else if (qName.equals("type")) {
         String type = currentOrNull();
         if (type != null)
            builder.type(SpotInstanceRequest.Type.fromValue(type));
      } else if (qName.equals("state")) {
         String state = currentOrNull();
         if (state != null)
            builder.state(SpotInstanceRequest.State.fromValue(state));
      } else if (qName.equals("createTime")) {
         String createTime = currentOrNull();
         if (createTime != null)
            builder.createTime(dateService.iso8601DateParse(createTime));
      } else if (qName.equals("productDescription")) {
         builder.productDescription(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inLaunchSpecification)
         launchSpecificationHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);
   }
}
