/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.reference;

/**
 * Configuration properties and constants used in S3 connections.
 * 
 * @author Adrian Cole
 */
public interface S3Constants {

   /**
    * S3 service's XML Namespace, as used in XML request and response documents.
    */
   public static final String S3_REST_API_XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";
   public static final String PREFIX = "prefix";
   public static final String MARKER = "marker";
   public static final String MAX_KEYS = "max-keys";
   public static final String DELIMITER = "delimiter";
   public static final String PROPERTY_S3_SERVICE_PATH = "jclouds.s3.service-path";
   public static final String PROPERTY_S3_VIRTUAL_HOST_BUCKETS = "jclouds.s3.virtual-host-buckets";

}
