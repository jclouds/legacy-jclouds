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
package org.jclouds.compute;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.UtilsImpl;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(UtilsImpl.class)
public interface Utils extends org.jclouds.rest.Utils {
   @Nullable
   SshClient.Factory getSshClientFactory();

   @Nullable
   SshClient.Factory sshFactory();

   /**
    * @return function that gets an ssh client for a node that is available via ssh.
    */
   Function<NodeMetadata, SshClient> sshForNode();

}
