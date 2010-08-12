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

package org.jclouds.gogrid.predicates;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertTrue;

import org.jclouds.gogrid.domain.Job;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.options.GetJobListOptions;
import org.jclouds.gogrid.services.GridJobClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Oleksiy Yarmula
 */
public class ServerLatestJobCompletedTest {

    @Test
    public void testPredicate() {
        final String serverName = "SERVER_NAME";
        Server server = createMock(Server.class);
        expect(server.getName()).andStubReturn(serverName);

        GetJobListOptions jobOptions = new GetJobListOptions.Builder().
                latestJobForObjectByName(serverName);

        Job job = createMock(Job.class);
        expect(job.getCurrentState()).andReturn(JobState.SUCCEEDED);

        GridJobClient client = createMock(GridJobClient.class);
        expect(client.getJobList(jobOptions)).
                andReturn(ImmutableSet.<Job>of(job));

        replay(job);
        replay(client);
        replay(server);

        ServerLatestJobCompleted predicate = new ServerLatestJobCompleted(client);
        assertTrue(predicate.apply(server), "The result of the predicate should've been 'true'");

    }

}
