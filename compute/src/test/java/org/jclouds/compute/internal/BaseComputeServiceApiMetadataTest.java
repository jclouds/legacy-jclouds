package org.jclouds.compute.internal;

import static org.jclouds.reflect.Reflection2.typeToken;

import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadataTest;
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

   public BaseComputeServiceApiMetadataTest(ApiMetadata toTest) {
     super(toTest, ImmutableSet.<TypeToken<? extends View>>of(typeToken(ComputeServiceContext.class)));
   }

}
