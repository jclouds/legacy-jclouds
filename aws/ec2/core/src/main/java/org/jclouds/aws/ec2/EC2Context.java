/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

/**
 * Represents an authenticated context to EC2.
 * 
 * <h2>Note</h2> Please issue {@link #close()} when you are finished with this
 * context in order to release resources.
 * 
 * 
 * @see EC2Connection
 * @author Adrian Cole
 * 
 */
public interface EC2Context {

    /**
     * low-level api to EC2. Threadsafe implementations will return a singleton.
     * 
     * @return a connection to EC2
     */
    EC2Connection getConnection();

    /**
     * Closes all connections to EC2.
     */
    void close();

}
