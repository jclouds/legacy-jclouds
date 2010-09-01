/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.filesystem.predicates.validators;

import java.io.File;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Test class for {@link FilesystemContainerNameValidator } class
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Test(groups = "unit", testName = "filesystem.FilesystemContainerNameValidatorTest")
public class FilesystemContainerNameValidatorTest {

    @Test
    public void testNamesValidity() {
        FilesystemBlobKeyValidator validator = new FilesystemBlobKeyValidator();

        validator.validate("all.img");
    }

    @Test
    public void testInvalidNames() {
        FilesystemBlobKeyValidator validator = new FilesystemBlobKeyValidator();

        try {
            validator.validate("");
            fail("Container name value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}

        try {
            validator.validate(null);
            fail("Container name value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}

        try {
            validator.validate(File.separator + "is" + File.separator + "" + "ok");
            fail("Container name value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}

        try {
            validator.validate("all" + File.separator + "is" + File.separator);
            fail("Container name value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}
    }


    //---------------------------------------------------------- Private methods

}
