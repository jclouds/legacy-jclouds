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
package org.jclouds.compute.util;

import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.net.HostAndPort;
import com.google.inject.ImplementedBy;

/**
 * For finding an open/reachable ip:port for a node.
 * 
 * @author aled
 */
@ImplementedBy(ConcurrentOpenSocketFinder.class)
public interface OpenSocketFinder {

   /**
    * 
    * @param node         The node (checking its public and private addresses)
    * @param port         The port to try to connect to
    * @param timeoutValue Max time to try to connect to the ip:port
    * @param timeUnits
    * 
    * @return The reachable ip:port
    * @throws NoSuchElementException If no ports accessible within the given time
    * @throws IllegalStateException  If the given node has no public or private addresses
    */
   HostAndPort findOpenSocketOnNode(final NodeMetadata node, final int port, long timeoutValue, TimeUnit timeUnits);
}
