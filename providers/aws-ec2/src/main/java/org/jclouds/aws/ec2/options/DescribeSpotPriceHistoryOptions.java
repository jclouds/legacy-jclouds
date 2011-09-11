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

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

/**
 * Contains options supported in the Form API for the DescribeSpotPriceHistory operation. <h2>
 * Usage</h2> The recommended way to instantiate a DescribeSpotPriceHistoryOptions object is to
 * statically import DescribeSpotPriceHistoryOptions.Builder.* and invoke a static creation method
 * followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions.Builder.*
 * <p/>
 * AWSEC2Client client = // get connection
 * history = client.getSpotInstanceServices().describeSpotPriceHistoryInRegion(from(yesterday).instanceType("m1.small"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-form-DescribeSpotPriceHistory.html"
 *      />
 */
public class DescribeSpotPriceHistoryOptions extends BaseEC2RequestOptions {
   public static final DescribeSpotPriceHistoryOptions NONE = new DescribeSpotPriceHistoryOptions();
   private static final DateService service = new SimpleDateFormatDateService();

   /**
    * Start date and time of the Spot Instance price history data.
    */
   public DescribeSpotPriceHistoryOptions from(Date start) {
      formParameters.put("StartTime", service.iso8601DateFormat(checkNotNull(start, "start")));
      return this;
   }

   /**
    * End date and time of the Spot Instance price history data.
    */
   public DescribeSpotPriceHistoryOptions to(Date end) {
      formParameters.put("EndTime", service.iso8601DateFormat(checkNotNull(end, "end")));
      return this;
   }

   /**
    * Specifies the instance type to return.
    */
   public DescribeSpotPriceHistoryOptions instanceType(String type) {
      formParameters.put("InstanceType.1", checkNotNull(type, "type"));
      return this;
   }

   /**
    * The description of the AMI.
    */
   public DescribeSpotPriceHistoryOptions productDescription(String description) {
      formParameters.put("ProductDescription", checkNotNull(description, "description"));
      return this;
   }

   public static class Builder {
      /**
       * @see DescribeSpotPriceHistoryOptions#from
       */
      public static DescribeSpotPriceHistoryOptions from(Date start) {
         DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
         return options.from(start);
      }

      /**
       * @see DescribeSpotPriceHistoryOptions#to
       */
      public static DescribeSpotPriceHistoryOptions to(Date end) {
         DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
         return options.to(end);
      }

      /**
       * @see DescribeSpotPriceHistoryOptions#instanceType(InstanceType)
       */
      public static DescribeSpotPriceHistoryOptions instanceType(String instanceType) {
         DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
         return options.instanceType(instanceType);
      }

      /**
       * @see DescribeSpotPriceHistoryOptions#productDescription(String)
       */
      public static DescribeSpotPriceHistoryOptions productDescription(String description) {
         DescribeSpotPriceHistoryOptions options = new DescribeSpotPriceHistoryOptions();
         return options.productDescription(description);
      }

   }
}
