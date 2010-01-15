/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * Regions used for all aws commands.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/latest/dev/index.html?LocationSelection.html" />
 * 
 */
public enum Region {

   /**
    * Allow the default region to be chosen based on the user-configured endpoint.
    */
   DEFAULT,

   /**
    * Region returned is unknown.
    */
   UNKNOWN,

   /**
    * EU (Ireland)
    * <p/>
    * <h3>S3</h3>
    * <p/>
    * In Amazon S3, the EU (Ireland) Region provides read-after-write consistency for PUTS of new
    * objects in your Amazon S3 bucket and eventual consistency for overwrite PUTS and DELETES.
    */
   EU_WEST_1,

   /**
    * 
    * US Standard
    * <p/>
    * <h3>S3</h3>
    * <p/>
    * This is the default Region. All requests sent to s3.amazonaws.com go to this Region unless you
    * specify a LocationConstraint on a bucket. The US Standard Region automatically places your
    * data in either Amazon's east or west coast data centers depending on what will provide you
    * with the lowest latency. To use this region, do not set the LocationConstraint bucket
    * parameter. The US Standard Region provides eventual consistency for all requests.
    */
   US_STANDARD,
   
   /**
    * 
    */
   US_EAST_1,

   /**
    * US-West (Northern California) <h3>S3</h3> Uses Amazon S3 servers in Northern California
    * <p/>
    * Optionally, use the endpoint s3-us-west-1.amazonaws.com on all requests to this bucket to
    * reduce the latency you might experience after the first hour of creating a bucket in this
    * Region.
    * <p/>
    * In Amazon S3, the US-West (Northern California) Region provides read-after-write consistency
    * for PUTS of new objects in your Amazon S3 bucket and eventual consistency for overwrite PUTS
    * and DELETES.
    */
   US_WEST_1;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static Region fromValue(String region) {
      return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(region,
               "region")));
   }
}