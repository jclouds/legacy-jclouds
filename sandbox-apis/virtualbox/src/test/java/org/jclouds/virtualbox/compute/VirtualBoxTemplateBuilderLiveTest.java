package org.jclouds.virtualbox.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;


/**
 * 
 * @author Andrea Turli
 */

@Test(groups = "live", testName = "VirtualBoxTemplateBuilderLiveTest")
public class VirtualBoxTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

	   public VirtualBoxTemplateBuilderLiveTest() {
		      provider = "virtualbox";
		   }

		   @Override
		   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
		      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

		         @Override
		         public boolean apply(OsFamilyVersion64Bit input) {
		            switch (input.family) {
		               case UBUNTU:
		                  return input.version.equals("11.04") && !input.is64Bit;
		               default:
		                  return false;
		            }
		         }

		      });
		   }

		   @Test
		   public void testTemplateBuilder() {
		      System.out.println(this.context.getComputeService().listImages());
		      Template defaultTemplate = this.context.getComputeService().templateBuilder().build();
		      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), false);
		      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "10.04");
		      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
		      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
		   }

		   @Override
		   protected Set<String> getIso3166Codes() {
		      return ImmutableSet.<String> of();
		   }

}
