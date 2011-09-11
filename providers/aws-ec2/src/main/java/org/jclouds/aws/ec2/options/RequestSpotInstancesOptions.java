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
package org.jclouds.aws.ec2.options;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the RequestSpotInstances operation. <h2>
 * Usage</h2> The recommended way validUntil instantiate a RequestSpotInstancesOptions object is
 * validUntil statically import RequestSpotInstancesOptions.Builder.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.RequestSpotInstancesOptions.Builder.*
 * <p/>
 * AWSEC2Client client = // get connection
 * history = client.getSpotInstanceServices().requestSpotInstancesInRegion("us-east-1",validFrom(yesterday).type("m1.small"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-RequestSpotInstances.html"
 *      />
 */
public class RequestSpotInstancesOptions extends BaseEC2RequestOptions {
   public static final RequestSpotInstancesOptions NONE = new RequestSpotInstancesOptions();
   private static final DateService service = new SimpleDateFormatDateService();

   /**
    * Start date of the request. If this is a one-time request, the request becomes active at this
    * date and time and remains active until all instances launch, the request expires, or the
    * request is canceled. If the request is persistent, the request becomes active at this date and
    * time and remains active until it expires or is canceled.
    */
   public RequestSpotInstancesOptions validFrom(Date start) {
      formParameters.put("ValidFrom", service.iso8601SecondsDateFormat(checkNotNull(start, "start")));
      return this;
   }

   /**
    * End date of the request. If this is a one-time request, the request remains active until all
    * instances launch, the request is canceled, or this date is reached. If the request is
    * persistent, it remains active until it is canceled or this date and time is reached.
    */
   public RequestSpotInstancesOptions validUntil(Date end) {
      formParameters.put("ValidUntil", service.iso8601SecondsDateFormat(checkNotNull(end, "end")));
      return this;
   }

   /**
    * Specifies the Spot Instance type.
    */
   public RequestSpotInstancesOptions type(SpotInstanceRequest.Type type) {
      formParameters.put("Type", checkNotNull(type, "type").toString());
      return this;
   }

   /**
    * Specifies the instance launch group. Launch groups are Spot Instances that launch together and
    * terminate together.
    */
   public RequestSpotInstancesOptions launchGroup(String launchGroup) {
      formParameters.put("LaunchGroup", checkNotNull(launchGroup, "launchGroup"));
      return this;
   }

   /**
    * Specifies the Availability Zone group. If you specify the same Availability Zone group for all
    * Spot Instance requests, all Spot Instances are launched in the same Availability Zone.
    */
   public RequestSpotInstancesOptions availabilityZoneGroup(String availabilityZoneGroup) {
      formParameters.put("AvailabilityZoneGroup", checkNotNull(availabilityZoneGroup, "availabilityZoneGroup"));
      return this;
   }

   public static class Builder {
      /**
       * @see RequestSpotInstancesOptions#validFrom
       */
      public static RequestSpotInstancesOptions validFrom(Date start) {
         RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
         return options.validFrom(start);
      }

      /**
       * @see RequestSpotInstancesOptions#validUntil
       */
      public static RequestSpotInstancesOptions validUntil(Date end) {
         RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
         return options.validUntil(end);
      }

      /**
       * @see RequestSpotInstancesOptions#type
       */
      public static RequestSpotInstancesOptions type(SpotInstanceRequest.Type type) {
         RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
         return options.type(type);
      }

      /**
       * @see RequestSpotInstancesOptions#launchGroup(String)
       */
      public static RequestSpotInstancesOptions launchGroup(String launchGroup) {
         RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
         return options.launchGroup(launchGroup);
      }

      /**
       * @see RequestSpotInstancesOptions#availabilityZoneGroup
       */
      public static RequestSpotInstancesOptions availabilityZoneGroup(String availabilityZoneGroup) {
         RequestSpotInstancesOptions options = new RequestSpotInstancesOptions();
         return options.availabilityZoneGroup(availabilityZoneGroup);
      }

   }
}
