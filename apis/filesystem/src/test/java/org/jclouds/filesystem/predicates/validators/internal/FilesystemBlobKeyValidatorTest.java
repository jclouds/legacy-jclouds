/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.filesystem.predicates.validators.internal;

import java.io.File;
import org.jclouds.filesystem.predicates.validators.FilesystemBlobKeyValidator;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * Test class for {@link FilesystemBlobKeyValidator } class
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Test(groups = "unit", testName = "filesystem.FilesystemBlobKeyValidatorTest")
public class FilesystemBlobKeyValidatorTest {

    @Test
    public void testNamesValidity() {
        FilesystemBlobKeyValidator validator = new FilesystemBlobKeyValidatorImpl();

        validator.validate("all.img");
        validator.validate("all" + File.separator + "is" + File.separator + "" + "ok");
    }

    @Test
    public void testInvalidNames() {
        FilesystemBlobKeyValidator validator = new FilesystemBlobKeyValidatorImpl();

        try {
            validator.validate("");
            fail("Blob key value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}

        try {
            validator.validate(File.separator + "is" + File.separator + "" + "ok");
            fail("Blob key value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}

        try {
            validator.validate("all" + File.separator + "is" + File.separator);
            fail("Blob key value incorrect, but was not recognized");
        } catch(IllegalArgumentException e) {}
    }


    //---------------------------------------------------------- Private methods

}
