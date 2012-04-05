package org.jclouds.compute.internal;

import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.Apis;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.jclouds.compute.ComputeServiceApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseComputeServiceApiMetadataTest extends BaseApiMetadataTest {

   @SuppressWarnings("rawtypes")
   public BaseComputeServiceApiMetadataTest(ComputeServiceApiMetadata toTest) {
     super(toTest, ApiType.COMPUTE);
   }

   @Test
   public void testContextAssignableFromComputeServiceContext() {
      Set<ApiMetadata<?, ?, ?, ?>> all = ImmutableSet.copyOf(Apis.contextAssignableFrom(TypeToken.of(ComputeServiceContext.class)));
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}