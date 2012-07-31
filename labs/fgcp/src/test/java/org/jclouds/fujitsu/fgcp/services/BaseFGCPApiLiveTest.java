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
package org.jclouds.fujitsu.fgcp.services;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPAsyncApi;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeGroups;

import com.google.inject.Module;

/**
 * @author Dies Koper
 */
public class BaseFGCPApiLiveTest extends BaseComputeServiceContextLiveTest {

    protected RestContext<FGCPApi, FGCPAsyncApi> fgcpContext;

    public BaseFGCPApiLiveTest() {
        provider = "fgcp";
    }

    @Override
    protected Properties setupProperties() {
        Properties overrides = super.setupProperties();

        String proxy = System.getenv("http_proxy");
        if (proxy != null) {

            String[] parts = proxy.split("http://|:|@");

            overrides.setProperty(Constants.PROPERTY_PROXY_HOST,
                    parts[parts.length - 2]);
            overrides.setProperty(Constants.PROPERTY_PROXY_PORT,
                    parts[parts.length - 1]);

            if (parts.length >= 4) {
                overrides.setProperty(Constants.PROPERTY_PROXY_USER,
                        parts[parts.length - 4]);
                overrides.setProperty(Constants.PROPERTY_PROXY_PASSWORD,
                        parts[parts.length - 3]);
            }
        }

        // enables peer verification using the CAs bundled with the JRE (or
        // value of javax.net.ssl.trustStore if set)
        overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "false");

        return overrides;
    }

    @BeforeGroups(groups = { "integration", "live" })
    @Override
    public void setupContext() {
        super.setupContext();
        fgcpContext = view.unwrap();
    }

    @Override
    protected Module getSshModule() {
        return new SshjSshClientModule();
    }
}
