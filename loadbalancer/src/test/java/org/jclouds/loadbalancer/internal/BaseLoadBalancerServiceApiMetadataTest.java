package org.jclouds.loadbalancer.internal;

import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.Apis;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.jclouds.loadbalancer.LoadBalancerServiceApiMetadata;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseLoadBalancerServiceApiMetadataTest extends BaseApiMetadataTest {

   @SuppressWarnings("rawtypes")
   public BaseLoadBalancerServiceApiMetadataTest(LoadBalancerServiceApiMetadata toTest) {
     super(toTest, ApiType.LOADBALANCER);
   }

   @Test
   public void testContextAssignableFromLoadBalancerServiceContext() {
      Set<ApiMetadata<?, ?, ?, ?>> all = ImmutableSet.copyOf(Apis.contextAssignableFrom(TypeToken.of(LoadBalancerServiceContext.class)));
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}