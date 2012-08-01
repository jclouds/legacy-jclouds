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
package org.jclouds.azure.servicemanagement.config;

import java.util.Map;

import javax.net.ssl.SSLContext;

import org.jclouds.azure.servicemanagement.AzureServiceManagementAsyncClient;
import org.jclouds.azure.servicemanagement.AzureServiceManagementClient;
import org.jclouds.azure.servicemanagement.features.RoleAsyncClient;
import org.jclouds.azure.servicemanagement.features.RoleClient;
import org.jclouds.azure.servicemanagement.http.SSLContextWithKeysSupplier;
import org.jclouds.azure.storage.config.AzureStorageRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * Configures the Azure Service Management connection.
 * 
 * @author Gerald Pereira
 */
@ConfiguresRestClient
public class AzureServiceManagementRestClientModule
		extends
		AzureStorageRestClientModule<AzureServiceManagementClient, AzureServiceManagementAsyncClient> {
	public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
			.<Class<?>, Class<?>> builder()
			.put(RoleClient.class, RoleAsyncClient.class).build();

	public AzureServiceManagementRestClientModule() {
		super(DELEGATE_MAP);
	}

	@Override
	protected void configure() {
		super.configure();
//		bind(new TypeLiteral<Supplier<SSLContext>>() {
//		}).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
//		});
	}
}
