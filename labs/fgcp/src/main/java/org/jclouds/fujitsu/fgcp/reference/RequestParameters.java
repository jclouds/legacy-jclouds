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
package org.jclouds.fujitsu.fgcp.reference;

/**
 * Configuration parameters and constants used in HTTP requests.
 * 
 * @author Dies Koper
 */
public interface RequestParameters {

   /**
    * Indicates the action to perform. Example: ListVSYS
    */
   public static final String ACTION = "Action";

   /**
    * The API version to use. Example: 2011-01-31
    */
   public static final String VERSION = "Version";

   /**
    * The locale to use. Example: en
    */
   public static final String LOCALE = "Locale";

   /**
    * The Access Key ID for the request sender. This identifies the account
    * which will be charged for usage of the service. The account with which
    * the Access Key ID is associated must be signed up for FGCP, or requests
    * will not be accepted. AKIADQKE4SARGYLE
    */
   public static final String ACCESS_KEY_ID = "AccessKeyId";

   /**
    * The date and time at which the request is signed, in the format
    * YYYY-MM-DDThh:mm:ssZ. For more information, go to ISO 8601. Example:
    * 2006-07-07T15:04:56Z
    */
   public static final String TIMESTAMP = "Timestamp";

   /**
    * The date and time at which the signer included in the request expires, in
    * the format YYYY-MM-DDThh:mm:ssZ. Example: 2006-07-07T15:04:56Z
    */
   public static final String EXPIRES = "Expires";

   /**
    * The request signer. For more information, go to the Amazon Elastic
    * Compute Cloud Developer Guide. Example: Qnpl4Qk/7tINHzfXCiT7VbBatDA=
    */
   public static final String SIGNATURE = "Signature";

   /**
    * The hash algorithm you use to create the request signer. Valid value:
    * SHA1withRSA.
    */
   public static final String SIGNATURE_METHOD = "SignatureMethod";

   /**
    * The signer version you use to sign the request. Set this value to 1.0.
    * 
    */
   public static final String SIGNATURE_VERSION = "SignatureVersion";
}
