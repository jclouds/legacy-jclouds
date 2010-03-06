/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;


import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridComputeServiceLiveTest {

    private ComputeServiceContext context;

    @BeforeTest
    public void setupClient() throws IOException {
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

        context = new ComputeServiceContextFactory().createContext("gogrid", user, password,
                ImmutableSet.of(new Log4JLoggingModule(), new JschSshClientModule()));

    }

    @Test(enabled=false)
    public void endToEndComputeServiceTest() {
        ComputeService service = context.getComputeService();
        Template t = service.templateBuilder().minRam(1024).imageId("GSI-6890f8b6-c8fb-4ac1-bc33-2563eb4e29d2").build();

        assertEquals(t.getImage().getId(), "GSI-6890f8b6-c8fb-4ac1-bc33-2563eb4e29d2");
        service.runNodesWithTag("testTag", 1, t);

        Map<String, ? extends ComputeMetadata> nodes = service.getNodes();
        assertEquals(nodes.size(), 1);

        NodeMetadata nodeMetadata = service.getNodeMetadata(Iterables.getOnlyElement(nodes.values()));
        assertEquals(nodeMetadata.getPublicAddresses().size(), 1, "There must be 1 public address for the node");
        assertTrue(nodeMetadata.getName().startsWith("testTag"));
        service.rebootNode(nodeMetadata); // blocks until finished

        assertEquals(service.getNodeMetadata(nodeMetadata).getState(), NodeState.RUNNING);
        service.destroyNode(nodeMetadata);
    }
}
