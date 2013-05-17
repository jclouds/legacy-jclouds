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
package org.jclouds.hpcloud.objectstorage.internal;

import java.util.Properties;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageProviderMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.inject.Module;

@Test(groups = "unit", testName = "HPCloudObjectStorageExpectTest")
public class BaseHPCloudObjectStorageBlobStoreExpectTest extends BaseRestClientExpectTest<BlobStore> {


    protected HttpRequest keystoneAuthWithUsernameAndPassword;
    protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
    protected String authToken;
    protected HttpResponse responseWithKeystoneAccess;

    public BaseHPCloudObjectStorageBlobStoreExpectTest() {
        provider = "hpcloud-objectstorage";
        keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
                credential);
        keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKey(identity,
                credential);
        authToken = KeystoneFixture.INSTANCE.getAuthToken();
        responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();

        identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
    }
    
    @Override
    public BlobStore createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
       return createInjector(fn, module, props).getInstance(BlobStore.class);
    }
    
    @Override public ProviderMetadata createProviderMetadata(){
       return new HPCloudObjectStorageProviderMetadata();
    }
}
