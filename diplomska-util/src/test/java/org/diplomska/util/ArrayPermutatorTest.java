package org.diplomska.util;

import org.junit.Assert;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/30
 * @author         Enter your name here...
 */
public class ArrayPermutatorTest {

   /**
    * Method description
    *
    */
   @Test
   public void testCreate() {
      ArrayPermutator randomizedArray_01 = new ArrayPermutator(new String("ales").getBytes());
      ArrayPermutator randomizedArray_02 = new ArrayPermutator(new String("ales").getBytes());
      int[]           array_01           = randomizedArray_01.createArray(1000);
      int[]           array_02           = randomizedArray_02.createArray(1000);

      Assert.assertArrayEquals(array_01, array_02);
   }
}
