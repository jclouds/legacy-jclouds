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
package org.jclouds.openstack.swift.reference;

/**
 * @author Adrian Cole
 * 
 */
public interface SwiftHeaders {

   public static final String ACCOUNT_TEMPORARY_URL_KEY = "X-Account-Meta-Temp-Url-Key";
   public static final String ACCOUNT_BYTES_USED = "X-Account-Bytes-Used";
   public static final String ACCOUNT_CONTAINER_COUNT = "X-Account-Container-Count";

   public static final String CONTAINER_BYTES_USED = "X-Container-Bytes-Used";
   public static final String CONTAINER_OBJECT_COUNT = "X-Container-Object-Count";
   public static final String CONTAINER_METADATA_PREFIX = "X-Container-Meta-";
   public static final String CONTAINER_DELETE_METADATA_PREFIX = "X-Remove-Container-Meta-";

   public static final String USER_METADATA_PREFIX = "X-Object-Meta-";
   public static final String OBJECT_COPY_FROM = "X-Copy-From";
   
   public static final String CONTAINER_READ = "X-Container-Read";
   public static final String CONTAINER_WRITE = "X-Container-Write";

}
