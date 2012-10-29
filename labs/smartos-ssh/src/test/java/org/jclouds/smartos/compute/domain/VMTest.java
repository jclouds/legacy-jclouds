package org.jclouds.smartos.compute.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "VMTest")
public class VMTest {

   @Test
   public void testParse() throws IOException {
      // Response from console from a 'vmadm list -p'
      InputStream is = getClass().getResourceAsStream("vmadm-list-response.txt");

      BufferedReader r = new BufferedReader(new InputStreamReader(is));
      String line = null;
      ImmutableList.Builder<VM> resultBuilder = ImmutableList.builder();
      while ((line = r.readLine()) != null) {
         VM vm = VM.builder().fromVmadmString(line).build();

         resultBuilder.add(vm);
      }
      List<VM> vmList = resultBuilder.build();

      Assert.assertEquals(2, vmList.size());

      Assert.assertEquals(UUID.fromString("60bd2ae5-4e4d-4952-88f9-1b850259d914"), vmList.get(0).getUuid());
      Assert.assertEquals(VM.State.STOPPED, vmList.get(0).getState());

   }
}
