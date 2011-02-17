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

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.RunningInstance.Builder;
import org.jclouds.ec2.xml.BaseReservationHandler;

import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseAWSReservationHandler<T> extends BaseReservationHandler<T> {

   public BaseAWSReservationHandler(DateService dateService, String defaultRegion, Provider<Builder> builderProvider) {
      super(dateService, defaultRegion, builderProvider);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("state")) {
         builder().monitoringState(MonitoringState.fromValue(currentOrNull()));
      } else if (qName.equals("groupName")) {
         builder().placementGroup(currentOrNull());
      } else if (qName.equals("subnetId")) {
         builder().subnetId(currentOrNull());
      } else if (qName.equals("spotInstanceRequestId")) {
         builder().spotInstanceRequestId(currentOrNull());
      } else if (qName.equals("vpcId")) {
         builder().vpcId(currentOrNull());
      } else if (qName.equals("productCode")) {
         builder().productCode(currentOrNull());
      }
      super.endElement(uri, name, qName);
   }

   @Override
   protected AWSRunningInstance.Builder builder() {
      return AWSRunningInstance.Builder.class.cast(builder);
   }

   public void characters(char ch[], int start, int length) {
      super.characters(ch, start, length);
   }

}
