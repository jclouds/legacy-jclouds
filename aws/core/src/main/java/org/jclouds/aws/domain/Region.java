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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * Regions used for all aws commands.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AmazonS3/latest/dev/index.html?LocationSelection.html" />
 * 
 */
public class Region {
   /**
    * EU (Ireland)
    * <p/>
    * <h3>S3</h3>
    * <p/>
    * In Amazon S3, the EU (Ireland) Region provides read-after-write consistency for PUTS of new
    * objects in your Amazon S3 bucket and eventual consistency for overwrite PUTS and DELETES.
    */
   public static final String EU_WEST_1 = "eu-west-1";

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
   public static final String US_STANDARD = "us-standard";

   /**
    * 
    */
   public static final String US_EAST_1 = "us-east-1";

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
   public static final String US_WEST_1 = "us-west-1";

   /**
    * Region in Singapore, launched April 28, 2010. This region improves latency for Asia-based
    * users
    */
   public static final String AP_SOUTHEAST_1 = "ap-southeast-1";

   public static Set<String> ALL = ImmutableSet.of(EU_WEST_1, US_STANDARD, US_EAST_1, US_WEST_1,
            AP_SOUTHEAST_1);

}