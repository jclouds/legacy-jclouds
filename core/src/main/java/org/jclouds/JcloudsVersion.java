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
 * @author Adrian Cole
 */
public class JcloudsVersion {
    @VisibleForTesting
    static final String VERSION_RESOURCE_FILE = "META-INF/maven/org.apache.jclouds/jclouds-core/pom.properties";
    private static final String VERSION_PROPERTY_NAME = "version";

    /*
     * x.y.z or x.y.z-incubating or x.y.z-alpha.n or x.y.z-beta.n or x.y.z-rc.n or x.y.z-SNAPSHOT -
     * see http://semver.org. Note that x.y.z-incubating does *not* meet the 
     * semver criteria for a *release* version.
     */
    private static final Pattern SEMANTIC_VERSION_PATTERN =
        Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(?:-(alpha|beta|rc)\\.(\\d+)|-incubating|-SNAPSHOT)?");
    private static final String ALPHA_VERSION_IDENTIFIER = "alpha";
    private static final String BETA_VERSION_IDENTIFIER = "beta";

    private static final JcloudsVersion INSTANCE = new JcloudsVersion();

    public final int majorVersion;
    public final int minorVersion;
    public final int patchVersion;
    public final boolean alpha;
    public final boolean beta;

    /**
     * Non-null iff {@link #alpha} is {@code true}
     */
    @Nullable public final Integer alphaVersion;

    /**
     * Non-null iff {@link #beta} is {@code true}
     */
    @Nullable public final Integer betaVersion;

    public final boolean releaseCandidate;

    /**
     * Non-null iff {@link #releaseCandidate} is {@code true}
     */
    @Nullable public final Integer releaseCandidateVersion;
    public final boolean snapshot;
    private final String version;

    @VisibleForTesting
    JcloudsVersion() {
        this(JcloudsVersion.class.getClassLoader());
    }

    @VisibleForTesting
    JcloudsVersion(ClassLoader resourceLoader) {
        this(readVersionPropertyFromClasspath(resourceLoader));
    }

    private static String readVersionPropertyFromClasspath(ClassLoader resourceLoader) {
        Properties versionProperties = new Properties();
        try {
            versionProperties.load(checkNotNull(resourceLoader.getResourceAsStream(VERSION_RESOURCE_FILE), VERSION_RESOURCE_FILE));
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
        // a match will produce three or five matching groups (alpha/beta/release candidate identifier and version optional)
        majorVersion = Integer.valueOf(versionMatcher.group(1));
        minorVersion = Integer.valueOf(versionMatcher.group(2));
        patchVersion = Integer.valueOf(versionMatcher.group(3));

        String alphaOrBetaOrReleaseCandidateVersionIfPresent = versionMatcher.group(4);
        if (alphaOrBetaOrReleaseCandidateVersionIfPresent != null) {
            Integer alphaOrBetaOrReleaseCandidateVersion = Integer.valueOf(versionMatcher.group(5));
            if (alphaOrBetaOrReleaseCandidateVersionIfPresent.equals(ALPHA_VERSION_IDENTIFIER)) {
                alpha = true;
                alphaVersion = alphaOrBetaOrReleaseCandidateVersion;
                beta = false;
                betaVersion = null;
                releaseCandidate = false;
                releaseCandidateVersion = null;
            } else if (alphaOrBetaOrReleaseCandidateVersionIfPresent.equals(BETA_VERSION_IDENTIFIER)) {
                alpha = false;
                alphaVersion = null;
                beta = true;
                betaVersion = alphaOrBetaOrReleaseCandidateVersion;
                releaseCandidate = false;
                releaseCandidateVersion = null;
            } else {
                alpha = false;
                alphaVersion = null;
                beta = false;
                betaVersion = null;
                releaseCandidate = true;
                releaseCandidateVersion = alphaOrBetaOrReleaseCandidateVersion;
            }
        } else {
            alpha = false;
            alphaVersion = null;
            beta = false;
            betaVersion = null;
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
