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

import static com.google.common.collect.Iterables.find;
import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;
import static org.jclouds.demo.tweetstore.integration.util.Zips.zipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jboss.as.controller.client.helpers.standalone.DeploymentAction;
import org.jboss.as.controller.client.helpers.standalone.DeploymentPlan;
import org.jboss.as.controller.client.helpers.standalone.ServerDeploymentActionResult;
import org.jboss.as.controller.client.helpers.standalone.ServerDeploymentManager;
import org.jboss.as.controller.client.helpers.standalone.ServerDeploymentPlanResult;
import org.jboss.as.embedded.EmbeddedServerFactory2;
import org.jboss.as.embedded.ServerStartException;
import org.jboss.as.embedded.StandaloneServer;

import com.google.common.base.Predicate;
import com.google.common.io.Files;

/**
 * Basic functionality to start a local JBoss AS 7 instance.
 * 
 * @author Andrew Phillips
 */
public class RhcloudServer {
    private static final byte[] CONTEXT_ROOT_XML_BYTES = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><jboss-web><context-root>/</context-root></jboss-web>".getBytes();
    
    protected StandaloneServer server;
    protected ServerDeploymentManager manager;

    public void writePropertiesAndStartServer(String warfile,
            String serverHome, Properties props) throws IOException,
            ServerStartException, InterruptedException, ExecutionException {
        String propsfile = String.format("%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + propsfile);
        storeProperties(propsfile, props);
        assert new File(propsfile).exists();
        
        // in OpenShift, TweetStore runs at the server root
        String ctxrootfile = String.format("%1$s/WEB-INF/jboss-web.xml", warfile);
        System.err.println("file: " + ctxrootfile);
        Files.write(CONTEXT_ROOT_XML_BYTES, new File(ctxrootfile));
        assert new File(ctxrootfile).exists();
        
        server = EmbeddedServerFactory2.create(new File(serverHome), System.getProperties(), System.getenv());
        server.start();
        TimeUnit.SECONDS.sleep(30);
        manager = ServerDeploymentManager.Factory.create(server.getModelControllerClient());
        ServerDeploymentActionResult deploymentResult = deploy(warfile);
        System.err.println("deployment result: " + deploymentResult.getResult());
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

    protected ServerDeploymentActionResult deploy(String explodedWar)
            throws IOException, InterruptedException, ExecutionException {
        File war = zipFile(explodedWar, format("%s-rhcloud.war", explodedWar), true);
        final String deploymentName = war.getName();
        
        DeploymentPlan plan = 
            manager.newDeploymentPlan().add(deploymentName, war).andDeploy().build();
        ServerDeploymentPlanResult deploymentResult = manager.execute(plan).get();
        return deploymentResult.getDeploymentActionResult(find(
                plan.getDeploymentActions(), new Predicate<DeploymentAction>() {
                    @Override
                    public boolean apply(DeploymentAction input) {
                        return input.getDeploymentUnitUniqueName().equals(deploymentName);
                    }
                }).getId());
    }

    public void stop() throws Exception {
        server.stop();
    }
}