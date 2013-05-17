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
package org.jclouds.openstack.nova.ec2;

import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.openstack.nova.ec2.services.NovaEC2KeyPairAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to EC2 services.
 * 
 * @author Adam Lowe
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(NovaEC2Client.class)} as
 *             {@link NovaEC2AsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface NovaEC2AsyncClient extends EC2AsyncClient {
   /**
    * {@inheritDoc}
    */
   @Delegate
   @Override
   NovaEC2KeyPairAsyncClient getKeyPairServices();
}
