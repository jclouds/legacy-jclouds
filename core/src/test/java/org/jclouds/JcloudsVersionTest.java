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
package org.jclouds;

import static org.jclouds.JcloudsVersion.VERSION_RESOURCE_FILE;
import static org.testng.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Andrew Phillips
 */
@Test(singleThreaded = true)
public class JcloudsVersionTest {

    @Test
    public void testFailsIfResourceFileMissing() {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
                new ResourceHidingClassLoader(original, VERSION_RESOURCE_FILE));
        try {
            new JcloudsVersion();
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfInvalidVersion() {
        new JcloudsVersion("${project.version}");
    }

    @Test
    public void testExtractsVersionFromResourceFile() {
        JcloudsVersion version = new JcloudsVersion();
        assertEquals("0.0.0-SNAPSHOT", version.toString());
    }

    @Test
    public void testExtractsMajorMinorPatchVersions() {
        JcloudsVersion version = new JcloudsVersion("1.2.3");
        assertEquals(1, version.majorVersion);
        assertEquals(2, version.minorVersion);
        assertEquals(3, version.patchVersion);
    }

    @Test
    public void testSupportsNonSnapshot() {
        JcloudsVersion version = new JcloudsVersion("1.2.3");
        assertFalse(version.snapshot, "Expected non-snapshot");
    }

    @Test
    public void testRecognisesSnapshot() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-SNAPSHOT");
        assertTrue(version.snapshot, "Expected snapshot");
    }

    @Test
    public void testSupportsNonReleaseCandidate() {
        JcloudsVersion version = new JcloudsVersion("1.2.3");
        assertFalse(version.releaseCandidate, "Expected non-release candidate");
        assertNull(version.releaseCandidateVersion);
    }

    @Test
    public void testRecognisesReleaseCandidate() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc.4");
        assertTrue(version.releaseCandidate, "Expected release candidate");
    }

    // TODO: remove once x.y.z-rc-n support is dropped after 1.3.0
    @Test
    public void testRecognisesNonSemverReleaseCandidate() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc-4");
        assertTrue(version.releaseCandidate, "Expected release candidate");
    }

    @Test
    public void testExtractsReleaseCandidateVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc.4");
        assertEquals(Integer.valueOf(4), version.releaseCandidateVersion);
    }

    // TODO: remove once x.y.z-rc-n support is dropped after 1.3.0
    @Test
    public void testExtractsNonSemverReleaseCandidateVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc-4");
        assertEquals(Integer.valueOf(4), version.releaseCandidateVersion);
    }

    @Test
    public void testRecognisesReleaseCandidateSnapshot() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc-4-SNAPSHOT");
        assertTrue(version.releaseCandidate, "Expected release candidate");
        assertTrue(version.snapshot, "Expected snapshot");
    }

    private static class ResourceHidingClassLoader extends ClassLoader {
        private final ClassLoader delegate;
        private final List<String> resourcesToHide;

        private ResourceHidingClassLoader(ClassLoader delegate, String... resourcesToHide) {
            this.delegate = delegate;
            this.resourcesToHide = ImmutableList.copyOf(resourcesToHide);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return (Iterables.contains(resourcesToHide, name)
                    ? null
                    : delegate.getResourceAsStream(name));
        }
    }
}