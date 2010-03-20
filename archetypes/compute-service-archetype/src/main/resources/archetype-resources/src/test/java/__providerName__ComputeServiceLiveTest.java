#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $camelCaseProviderName = "${providerName.substring(0, 1).toLowerCase()}${providerName.substring(1)}" )
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
package ${package};

import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author ${author}
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "${lcaseProviderName}.${providerName}ComputeServiceLiveTest")
public class ${providerName}ComputeServiceLiveTest extends BaseComputeServiceLiveTest {
    
    @BeforeClass
    @Override
    public void setServiceDefaults() {
       service = "${lcaseProviderName}";
    }

    @Override
    public String buildScript() {
        return new StringBuilder()
                .append("TODO: define build script")
                .toString();
    }

    protected Template buildTemplate(TemplateBuilder templateBuilder) {
        return templateBuilder.osFamily(CENTOS).imageDescriptionMatches("TODO: insert image description").smallest()
                .build();
    }

    @Override
    protected JschSshClientModule getSshModule() {
        return new JschSshClientModule();
    }

    public void testAssignability() throws Exception {
        @SuppressWarnings("unused")
        RestContext<${providerName}AsyncClient, ${providerName}Client> ${camelCaseProviderName}Context = 
            new ComputeServiceContextFactory().createContext(service, user, password).getProviderSpecificContext();
    }

    @Test(enabled = true)
    public void endToEndComputeServiceTest() {
        /*
         * TODO: adapt the following sample test for ${providerName}
         */
        ComputeService service = context.getComputeService();
        Template t = service.templateBuilder().minRam(1024).imageId("1532").build();
        
        assertEquals(t.getImage().getId(), "1532");
        service.runNodesWithTag(this.service, 1, t);
        
        Map<String, ? extends ComputeMetadata> nodes = service.getNodes();
        
        ComputeMetadata node = Iterables.find(nodes.values(), new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata computeMetadata) {
                return computeMetadata.getName().startsWith(${providerName}ComputeServiceLiveTest.this.service);
            }
        });
        
        NodeMetadata nodeMetadata = service.getNodeMetadata(node);
        assertEquals(nodeMetadata.getPublicAddresses().size(), 1,
             "There must be 1 public address for the node");
        assertTrue(nodeMetadata.getName().startsWith(this.service));
        service.rebootNode(nodeMetadata); // blocks until finished
        
        assertEquals(service.getNodeMetadata(nodeMetadata).getState(), NodeState.RUNNING);
        service.destroyNode(nodeMetadata);
    }
}
