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
package org.jclouds.azure.storage.reference;

/**
 * Additional headers specified by Azure Storage REST API.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179357.aspx" />
 * @author Adrian Cole
 * 
 */
public interface AzureStorageHeaders {

   public static final String USER_METADATA_PREFIX = "x-ms-meta-";
   public static final String REQUEST_ID = "x-ms-request-id";
   public static final String VERSION = "x-ms-version";

}
