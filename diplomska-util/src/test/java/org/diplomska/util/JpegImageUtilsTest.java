package org.diplomska.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/29
 * @author         Ales Kunst
 */
public class JpegImageUtilsTest {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    */
   @Test
   public void testCalculateInverseDctMatrix() {
      double[][] y = new double[][] {
         { -128, -128, -128, -128, -128, -128, -128, -128 }, { -128, -128, -128, -128, -128, -128, -128, -128 },
         { -128, -128, -128, -128, -128, -128, -128, -128 }, { -128, -128, -128, -128, -128, -128, -128, -128 },
         { -128, -128, -128, -128, -128, -128, -128, -128 }, { -128, -128, -128, -128, -128, -128, -128, -128 },
         { -128, -128, -128, -128, -128, -128, -128, -128 }, { -128, -128, -128, -128, -128, -128, -128, -128 }
      };
      double[][] dct_01 = new double[][] {
         { 144, -11, 0, 0, 0, 0, 0, 0 }, { 264, 0, 0, 0, 0, 0, 0, 0 }, { -70, 0, 0, 0, 0, 0, 0, 0 },
         { -14, 0, 0, 0, 0, 0, 0, 0 }, { -18, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };
      double[][] idct_01 = JpegImageUtils.calculateInverseDct(dct_01);
      double[][] idct_02 = JpegImageUtils.calculateInverseSlowDct(dct_01);

      for (int row = 0; row < 8; row++) {
         for (int column = 0; column < 8; column++) {
            double value01 = idct_01[row][column];
            double value02 = idct_02[row][column];

            Assert.assertEquals(value01, value02, 0.001d);
         }
      }
   }

   /**
    * Test divide with round up.
    *
    */
   @Test
   public void testDivideWithRoundUp() {
      Assert.assertEquals(2, JpegImageUtils.divideWithRoundUp(4, 3));
   }
}
