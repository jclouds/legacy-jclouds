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
package org.jclouds.aws.sqs.reference;

import org.jclouds.aws.reference.AWSConstants;

/**
 * Configuration properties and constants used in SQS connections.
 * 
 * @author Adrian Cole
 */
public interface SQSConstants extends AWSConstants {
   public static final String PROPERTY_SQS_ENDPOINT = "jclouds.sqs.endpoint";

   public static final String PROPERTY_SQS_ENDPOINT_EU_WEST_1 = "jclouds.sqs.endpoint.eu_west_1";
   public static final String PROPERTY_SQS_ENDPOINT_US_EAST_1 = "jclouds.sqs.endpoint.us_east_1";
   public static final String PROPERTY_SQS_ENDPOINT_US_WEST_1 = "jclouds.sqs.endpoint.us_west_1";
}
