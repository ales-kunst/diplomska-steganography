package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Discrete cosine transform test.
 *
 *
 * @version        1.0, 15/04/17
 * @author         Ales Kunst
 */
public class DiscreteCosineTransformBlockStepTest {

   /** Jpeg image context of test black white png */
   private JpegImageContext fTestBlackWhitePngJpegImageContext;

   /** Jpeg image context of test png */
   private JpegImageContext fTestPngJpegImageContext;

   /** Dummy jpeg image context */
   private JpegImageContext fDummyJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    * Setup.
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      fTestBlackWhitePngJpegImageContext = JpegEncoderTestUtil.createTestBlackWhitePngJpegImageContext();
      fTestPngJpegImageContext           = JpegEncoderTestUtil.createTestPngJpegImageContext();

      JpegImageBlock dummyJpegImageBlock = new JpegImageBlock(0, 0);

      // dummyJpegImageBlock.setYCrCbMatrix(getYLumaMatrix(), getCrChromaMatrix(), getCbChromaMatrix());
      List<JpegImageBlock> jpegImageBlocks = new ArrayList<JpegImageBlock>();

      jpegImageBlocks.add(dummyJpegImageBlock);
      fDummyJpegImageContext = JpegEncoderTestUtil.createDummyJpegImageContext(8, 8, jpegImageBlocks);
   }

   //~--- methods -------------------------------------------------------------

   /**
    *    Tear down.
    *
    *
    *    @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Test discrete cosine transform process.
    *
    */
   @Test
   public void testExecute() {
      JpegEncoderTestUtil.runUntilDct(fDummyJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fDummyJpegImageContext.getJpegImageBlock(0, 0);
      int[][]        actualYMatrix      = jpegImageBlock_0_0.getRoundedMatrixYLumaForCalculation();
      int[][]        actualCbMatrix     = jpegImageBlock_0_0.getRoundedMatrixCbChromaForCalculation();

      // TODO Kunst: Write tests!
//    Assert.assertArrayEquals(getRefYDctJpegImageBlock0(), actualYMatrix);
//    Assert.assertArrayEquals(getRefCbDctJpegImageBlock0(), actualCbMatrix);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_01() {
      JpegEncoderTestUtil.runUntilDct(fTestBlackWhitePngJpegImageContext);
   }

   /**
    *    Method description
    *
    */
   @Test
   public void testExecute_02() {
      JpegEncoderTestUtil.runUntilDct(fTestPngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestPngJpegImageContext.getJpegImageBlock(0, 0);
      JpegImageBlock jpegImageBlock_0_1 = fTestPngJpegImageContext.getJpegImageBlock(1, 0);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getCbChromaMatrix() {
      int[][] resultCbChromaMatrix = new int[][] {
         { 100, 100, 100, 100, 100, 100, 100, 100 }, { 100, 100, 100, 100, 100, 100, 100, 100 },
         { 100, 100, 100, 100, 100, 100, 100, 100 }, { 100, 100, 100, 100, 100, 100, 100, 100 },
         { 100, 100, 100, 100, 100, 100, 100, 100 }, { 100, 100, 100, 100, 100, 100, 100, 100 },
         { 100, 100, 100, 100, 100, 100, 100, 100 }, { 100, 100, 100, 100, 100, 100, 100, 100 }
      };

      return resultCbChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getCrChromaMatrix() {
      int[][] resultCrChromaMatrix = new int[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultCrChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefCbDctJpegImageBlock0() {
      int[][] resultRefCbJpegImageBlock0 = new int[][] {
         { 800, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCbJpegImageBlock0;
   }

   /**
    * Reference values for execute_01 test.
    *
    *
    * @return
    */
   private int[][] getRefCbDctValuesForExecute_0_0_01() {
      int[][] resultRefCbDctValues = new int[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCbDctValues;
   }

   /**
    * Reference values for execute_01 test.
    *
    *
    * @return
    */
   private int[][] getRefCrDctValuesForExecute_0_0_01() {
      int[][] resultRefCrDctValues = new int[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCrDctValues;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefYDctJpegImageBlock0() {
      int[][] resultRefJpegImageBlock0 = new int[][] {
         { -325, 43, 55, 72, 24, -26, 11, -4 }, { -130, -71, -70, -73, 59, -24, 23, -2 },
         { 86, 30, 62, 45, 15, 17, 16, -13 }, { -41, 10, -18, -56, 31, -2, -21, -1 },
         { -157, -49, 13, -2, -9, 22, -8, -9 }, { 92, -9, 46, -48, -59, -9, -29, 10 },
         { -53, -63, -3, -20, 56, -2, -3, 12 }, { -21, -56, -21, -18, -27, -27, 8, 0 }
      };

      return resultRefJpegImageBlock0;
   }

   /**
    * Reference values for execute_01 test.
    *
    *
    * @return
    */
   private int[][] getRefYDctValuesForExecute_0_0_01() {
      int[][] resultRefYDctValues = new int[][] {
         { -1024, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefYDctValues;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefYDctValuesForExecute_0_1_01() {
      int[][] resultRefYDctValues = new int[][] {
         { 1016, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefYDctValues;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getYLumaMatrix() {
      int[][] resultYLumaMatrix = new int[][] {
         { 48, 39, 40, 68, 60, 38, 50, 121 }, { 149, 82, 79, 101, 113, 106, 27, 62 },
         { 58, 63, 77, 69, 124, 107, 74, 125 }, { 80, 97, 74, 54, 59, 71, 91, 66 }, { 18, 34, 33, 46, 64, 61, 32, 37 },
         { 149, 108, 80, 106, 116, 61, 73, 92 }, { 211, 233, 159, 88, 107, 158, 161, 109 },
         { 212, 104, 40, 44, 71, 136, 113, 66 }
      };

      for (int row = 0; row < 8; row++) {
         for (int column = 0; column < 8; column++) {
            resultYLumaMatrix[row][column] -= 128;
         }
      }

      return resultYLumaMatrix;
   }
}
