/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.Date;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest.Type;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;

/**
 * 
 * @author Adrian Cole
 */
// TODO finish
public class SpotInstanceRequestHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SpotInstanceRequest> {
   private StringBuilder currentText = new StringBuilder();

   @Inject
   protected DateService dateService;
   @Inject
   @Region
   private String defaultRegion;
   private String availabilityZoneGroup;
   private Date createTime;
   private String fault;
   private String instanceId;
   private String launchGroup;
   private String launchSpecification;
   private String productDescription;
   private String id;
   private float spotPrice;
   private String state;
   private Type type;
   private Date validFrom;
   private Date validUntil;

   public SpotInstanceRequest getResult() {
      String region = AWSUtils.findRegionInArgsOrNull(getRequest());
      if (region == null)
         region = defaultRegion;
      SpotInstanceRequest returnVal = new SpotInstanceRequest(region, availabilityZoneGroup, createTime, fault,
            instanceId, launchGroup, launchSpecification, productDescription, id, spotPrice, state, type, validFrom,
            validUntil);
      this.availabilityZoneGroup = null;
      this.createTime = null;
      this.fault = null;
      this.instanceId = null;
      this.launchGroup = null;
      this.launchSpecification = null;
      this.productDescription = null;
      this.id = null;
      this.spotPrice = -1;
      this.state = null;
      this.type = null;
      this.validFrom = null;
      this.validUntil = null;
      return returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("availabilityZoneGroup")) {
         this.availabilityZoneGroup = currentText.toString().trim();
      } else if (qName.equals("createTime")) {
         createTime = this.dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("type")) {
         type = SpotInstanceRequest.Type.fromValue(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
