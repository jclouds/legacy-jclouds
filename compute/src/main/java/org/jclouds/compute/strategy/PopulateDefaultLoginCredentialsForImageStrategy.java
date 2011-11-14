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
package org.jclouds.compute.strategy;

import org.jclouds.compute.strategy.impl.ReturnCredentialsBoundToImage;
import org.jclouds.domain.Credentials;

import com.google.inject.ImplementedBy;

/**
 * @author Oleksiy Yarmula
 */
@ImplementedBy(ReturnCredentialsBoundToImage.class)
public interface PopulateDefaultLoginCredentialsForImageStrategy {

    /**
     * Processes the resource to determine credentials.
     *
     * @param resourceToAuthenticate
     *                  this can be any resource, such as an image,
     *                  running server instance or other. It's the
     *                  responsibility of an implementation to apply
     *                  the cloud-specific logic.
     * @return credentials object. Note: the key
     *                  may not be set, but the identity must be set
     */
    Credentials execute(Object resourceToAuthenticate);

}
