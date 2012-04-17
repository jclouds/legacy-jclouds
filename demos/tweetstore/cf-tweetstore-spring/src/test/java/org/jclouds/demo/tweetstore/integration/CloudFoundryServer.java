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
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;
import static org.jclouds.demo.tweetstore.integration.util.Zips.zipDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.cloudfoundry.client.lib.CloudApplication.AppState;
import org.cloudfoundry.client.lib.CloudFoundryClient;

/**
 * Basic &quot;server facade&quot; functionality to deploy a WAR to Cloud Foundry.
 * 
 * @author Andrew Phillips
 */
public class CloudFoundryServer {
    private static final String CLOUD_FOUNDRY_APPLICATION_URL_SUFFIX = ".cloudfoundry.com";
    
    protected CloudFoundryClient client;
    protected String appName;
    
    public void writePropertiesAndStartServer(final String address, final String warfile, 
            String target, String username, String password, Properties props) throws IOException, InterruptedException, ExecutionException {
        String propsfile = String.format("%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + propsfile);
        storeProperties(propsfile, props);
        assert new File(propsfile).exists();

        client = new CloudFoundryClient(username, password, target);
        client.login();
        appName = getAppName(address);
        deploy(warfile);
        client.logout();
        TimeUnit.SECONDS.sleep(10);
    }

    private void deploy(String explodedWar) throws IOException {
        File war = zipDir(explodedWar, format("%s-cloudfoundry.war", explodedWar));
        client.uploadApplication(appName, war); 
        
        // adapted from https://github.com/cloudfoundry/vcap-java-client/blob/master/cloudfoundry-maven-plugin/src/main/java/org/cloudfoundry/maven/Update.java
        AppState appState = client.getApplication(appName).getState();
        switch (appState) {
        case STOPPED:
            client.startApplication(appName);
            break;
        case STARTED:
            client.restartApplication(appName);
            break;
        default:
            throw new IllegalStateException(format("Unexpected application state '%s'", appState));
        }
    }

    private static void storeProperties(String filename, Properties props)
            throws IOException {
        FileOutputStream targetFile = new FileOutputStream(filename);
        try {
            props.store(targetFile, "test");
        } finally {
            closeQuietly(targetFile);
        }
    }

    private static String getAppName(String applicationUrl) {
        checkArgument(applicationUrl.endsWith(CLOUD_FOUNDRY_APPLICATION_URL_SUFFIX), 
                "Application URL '%s' does not end in '%s'", applicationUrl, 
                CLOUD_FOUNDRY_APPLICATION_URL_SUFFIX);
        
        return applicationUrl.substring(0, 
                applicationUrl.length() - CLOUD_FOUNDRY_APPLICATION_URL_SUFFIX.length());
    }

    public void stop() throws Exception {
        checkState(client != null, "'stop' called before 'writePropertiesAndStartServer'");
        client.login();
        client.stopApplication(appName);
        client.logout();
    }
}