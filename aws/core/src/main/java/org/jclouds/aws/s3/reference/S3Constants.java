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
package org.jclouds.aws.s3.reference;

import org.jclouds.aws.reference.AWSConstants;

/**
 * Configuration properties and constants used in S3 connections.
 * 
 * @author Adrian Cole
 */
public interface S3Constants extends AWSConstants, S3Headers {

   /**
    * S3 service's XML Namespace, as used in XML request and response documents.
    */
   public static final String S3_REST_API_XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";
   public static final String PREFIX = "prefix";
   public static final String MARKER = "marker";
   public static final String MAX_KEYS = "max-keys";
   public static final String DELIMITER = "delimiter";
   public static final String PROPERTY_S3_ENDPOINT = "jclouds.s3.endpoint";
   /**
    * how long do we wait before obtaining a new timestamp for requests.
    */
   public static final String PROPERTY_S3_SESSIONINTERVAL = "jclouds.s3.sessioninterval";

   /**
    * longest time a single synchronous operation can take before throwing an exception.
    */
   public static final String PROPERTY_S3_TIMEOUT = "jclouds.s3.timeout";
   /**
    * time to pause before retrying a transient failure
    */
   public static final String PROPERTY_S3_RETRY = "jclouds.s3.retry";
   /**
    * Any header starting with this prefix is considered user metadata. It will be stored with the
    * object and returned when you retrieve the object/
    */
   public static final String PROPERTY_S3_METADATA_PREFIX = "jclouds.s3.metaprefix";

}
