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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Andrew Phillips
 */
public class JcloudsVersion {
    @VisibleForTesting
    static final String VERSION_RESOURCE_FILE = "META-INF/maven/org.jclouds/jclouds-core/pom.properties";
    private static final String VERSION_PROPERTY_NAME = "version";

    // x.y.z or x.y.z-rc.n, optionally with -SNAPSHOT suffix - see http://semver.org
    private static final Pattern SEMANTIC_VERSION_PATTERN =
        Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(?:-rc\\.(\\d+))?(?:-SNAPSHOT)?");

    private static final JcloudsVersion INSTANCE = new JcloudsVersion();

    public final int majorVersion;
    public final int minorVersion;
    public final int patchVersion;
    public final boolean releaseCandidate;
    private final String version;

    /**
     * Non-null iff {@link #releaseCandidate} is {@code true}
     */
    public final @Nullable Integer releaseCandidateVersion;
    public final boolean snapshot;

    @VisibleForTesting
    JcloudsVersion() {
        this(readVersionPropertyFromClasspath());
    }

    private static String readVersionPropertyFromClasspath() {
        Properties versionProperties = new Properties();
        try {
            versionProperties.load(checkNotNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(VERSION_RESOURCE_FILE), VERSION_RESOURCE_FILE));
        } catch (IOException exception) {
            throw new IllegalStateException(format("Unable to load version resource file '%s'", VERSION_RESOURCE_FILE), exception);
        }
        return checkNotNull(versionProperties.getProperty(VERSION_PROPERTY_NAME), VERSION_PROPERTY_NAME);
    }

    @VisibleForTesting
    JcloudsVersion(String version) {
        Matcher versionMatcher = SEMANTIC_VERSION_PATTERN.matcher(version);
        checkArgument(versionMatcher.matches(), "Version '%s' did not match expected pattern '%s'", 
                version, SEMANTIC_VERSION_PATTERN);
        this.version = version;
        // a match will produce three or four matching groups (release candidate version optional)
        majorVersion = Integer.valueOf(versionMatcher.group(1));
        minorVersion = Integer.valueOf(versionMatcher.group(2));
        patchVersion = Integer.valueOf(versionMatcher.group(3));
        String releaseCandidateVersionIfPresent = versionMatcher.group(4);
        if (releaseCandidateVersionIfPresent != null) {
            releaseCandidate = true;
            releaseCandidateVersion = Integer.valueOf(releaseCandidateVersionIfPresent);
        } else {
            releaseCandidate = false;
            releaseCandidateVersion = null;
        }
        // endsWith("T") would be cheaper but we only do this once...
        snapshot = version.endsWith("-SNAPSHOT");
    }

    @Override
    public String toString() {
        return version;
    }

    public static JcloudsVersion get() {
        return INSTANCE;
    }
}