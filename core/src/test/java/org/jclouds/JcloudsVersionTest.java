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
package org.jclouds;

import static org.jclouds.JcloudsVersion.VERSION_RESOURCE_FILE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

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

    @Test(expectedExceptions = { NullPointerException.class })
    public void testFailsIfResourceFileMissing() {
        new JcloudsVersion(new ResourceHidingClassLoader(JcloudsVersion.class.getClassLoader(), 
                VERSION_RESOURCE_FILE));
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfInvalidVersion() {
        new JcloudsVersion("${project.version}");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfNonSemverReleaseCandidate() {
        // no longer supported after the 1.3.0 RC cycle
        new JcloudsVersion("1.2.3-rc-4");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfAlphaSnapshot() {
        new JcloudsVersion("1.2.3-alpha.5-SNAPSHOT");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfBetSanapshot() {
        new JcloudsVersion("1.2.3-beta.5-SNAPSHOT");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfReleaseCandidateSnapshot() {
        new JcloudsVersion("1.2.3-rc.4-SNAPSHOT");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfIncubatingSnapshot() {
        new JcloudsVersion("1.2.3-incubating-SNAPSHOT");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testFailsIfNumberedIncubating() {
        new JcloudsVersion("1.2.3-incubating.1");
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
    public void testSupportsReleaseVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3");
        assertFalse(version.alpha, "Expected non-alpha");
        assertFalse(version.beta, "Expected non-beta");
        assertFalse(version.releaseCandidate, "Expected non-release candidate");
        assertNull(version.alphaVersion);
        assertNull(version.betaVersion);
        assertNull(version.releaseCandidateVersion);
    }

    @Test
    public void testSupportsIncubatingReleaseVersion() {
        // *not* a semver-compliant release version!
        JcloudsVersion version = new JcloudsVersion("1.2.3-incubating");
        assertFalse(version.alpha, "Expected non-alpha");
        assertFalse(version.beta, "Expected non-beta");
        assertFalse(version.releaseCandidate, "Expected non-release candidate");
        assertNull(version.alphaVersion);
        assertNull(version.betaVersion);
        assertNull(version.releaseCandidateVersion);
    }

    @Test
    public void testRecognisesAlpha() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-alpha.5");
        assertTrue(version.alpha, "Expected alpha");
    }

    @Test
    public void testExtractsAlphaVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-alpha.5");
        assertEquals(Integer.valueOf(5), version.alphaVersion);
    }

    @Test
    public void testRecognisesBeta() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-beta.5");
        assertTrue(version.beta, "Expected beta");
    }

    @Test
    public void testExtractsBetaVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-beta.5");
        assertEquals(Integer.valueOf(5), version.betaVersion);
    }

    @Test
    public void testRecognisesReleaseCandidate() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc.4");
        assertTrue(version.releaseCandidate, "Expected release candidate");
    }

    @Test
    public void testExtractsReleaseCandidateVersion() {
        JcloudsVersion version = new JcloudsVersion("1.2.3-rc.4");
        assertEquals(Integer.valueOf(4), version.releaseCandidateVersion);
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
