package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.savvis.vpdc.domain.ResourceAllocation;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ResourceAllocationHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ResourceAllocationHandlerTest")
public class ResourceAllocationHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/resourceallocation.xml");

      ResourceAllocation result = factory.create(injector.getInstance(ResourceAllocationHandler.class)).parse(is);

      ResourceAllocation expects = ResourceAllocation.builder()

      .allocationUnits("Gigabytes").caption("1234568").description("Hard Disk").name("D:\\").hostResource("data").id(6)
            .type(ResourceAllocation.Type.DATA_DISK).virtualQuantity(50)
            .build();
      assertEquals(result, expects);
   }
}
