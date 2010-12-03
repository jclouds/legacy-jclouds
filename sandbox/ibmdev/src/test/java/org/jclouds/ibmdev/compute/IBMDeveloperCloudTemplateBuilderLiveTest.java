package org.jclouds.ibmdev.compute;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ibmdev.IBMDeveloperCloudTemplateBuilderLiveTest")
public class IBMDeveloperCloudTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public IBMDeveloperCloudTemplateBuilderLiveTest() {
      provider = "ibmdev";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            return input.family != OsFamily.RHEL && //
                  input.family != OsFamily.SUSE && //
                  input.family != OsFamily.WINDOWS;
         }

      };
   }
}