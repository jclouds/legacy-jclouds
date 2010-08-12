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

package org.jclouds.gogrid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oleksiy Yarmula
 */
public class GoGridUtils {

    /**
     * Matches nth group or returns null.
     *  
     * @param stringToParse string that the pattern will be applied to
     * @param pattern regular expression {@link java.util.regex.Pattern pattern}
     * @param nthGroup number of the group to extract / return
     * @return matched group or null
     */
    public static String parseStringByPatternAndGetNthMatchGroup(String stringToParse, Pattern pattern, int nthGroup) {
        Matcher osVersionMatcher = pattern.matcher(stringToParse);
        if (osVersionMatcher.find()) {
            return osVersionMatcher.group(nthGroup);
        }
        return null;
    }

}
