package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.util.JpegImageUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/04/17
 * @author         Ales Kunst
 */
public class QuantizationBlockStepTest {

   /** Jpeg image context of test black white png */
   private JpegImageContext fTestBlackWhitePngJpegImageContext;

   /** Jpeg image context of test black white png */
   private JpegImageContext fDummyJpegImageContext;

   /** Field description */
   private JpegImageContext fTestPngJpegImageContext;

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

      dummyJpegImageBlock.setYCrCbMatrixForCalculation(getYLumaMatrixForCalculation(),
                                                       getCrChromaMatrixForCalculation(),
                                                       getCbChromaMatrixForCalculation());
      dummyJpegImageBlock.setYCrCbMatrixForCalculation(getYLumaMatrixForCalculation(),
                                                       getCrChromaMatrixForCalculation(),
                                                       getCbChromaMatrixForCalculation());

      List<JpegImageBlock> jpegImageBlocks = new ArrayList<JpegImageBlock>();

      jpegImageBlocks.add(dummyJpegImageBlock);
      fDummyJpegImageContext = JpegEncoderTestUtil.createDummyJpegImageContext(8, 8, jpegImageBlocks);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Teardown
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Testing method.
    *
    */
   @Test
   public void testExecute() {
      JpegEncoderTestUtil.runOnlyQuantization(fDummyJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fDummyJpegImageContext.getJpegImageBlock(0, 0);

      for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; row++) {
         for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int expectedYValue = getRefYLumaMatrixForCalculation()[row][column];
            int actualYValue   = jpegImageBlock_0_0.getRoundedMatrixYLumaForCalculation()[row][column];

            Assert.assertEquals(String.format("Difference at Y Luma [%d %d]",
                                              row,
                                              column), expectedYValue, actualYValue);

//          Assert.assertEquals(String.format("Difference at Cb Chroma [%d %d]", x, y),
//                              expectedCbValue, actualCbValue);
         }
      }
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_01() {
      JpegEncoderTestUtil.runUntilQuantization(fTestBlackWhitePngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestBlackWhitePngJpegImageContext.getJpegImageBlock(0, 0);
      JpegImageBlock jpegImageBlock_0_1 = fTestBlackWhitePngJpegImageContext.getJpegImageBlock(1, 0);
      int[][]        yLumaMatrix_0_0    = jpegImageBlock_0_0.getRoundedMatrixYLumaForCalculation();
      int[][]        cbChromaMatrix_0_0 = jpegImageBlock_0_0.getRoundedMatrixCbChromaForCalculation();
      int[][]        crChromaMatrix_0_0 = jpegImageBlock_0_0.getRoundedMatrixCrChromaForCalculation();

      // second jpeg image block
      int[][] yLumaMatrix_0_1    = jpegImageBlock_0_1.getRoundedMatrixYLumaForCalculation();
      int[][] cbChromaMatrix_0_1 = jpegImageBlock_0_1.getRoundedMatrixCbChromaForCalculation();
      int[][] crChromaMatrix_0_1 = jpegImageBlock_0_1.getRoundedMatrixCrChromaForCalculation();

      // first jpeg image block
      Assert.assertArrayEquals(getRefOnlyZeroesMatrixExecute_01(), yLumaMatrix_0_0);
      Assert.assertArrayEquals(getRefChromaMatrixExecute_01(), cbChromaMatrix_0_0);
      Assert.assertArrayEquals(getRefChromaMatrixExecute_01(), crChromaMatrix_0_0);

      // second jpeg image block
      // TODO Kunst: Write tests!
//    Assert.assertArrayEquals(getRefLumaMatrixExecute_01(), yLumaMatrix_0_1);
//    Assert.assertArrayEquals(getRefChromaMatrixExecute_01(), cbChromaMatrix_0_1);
//    Assert.assertArrayEquals(getRefChromaMatrixExecute_01(), crChromaMatrix_0_1);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_02() {
      JpegEncoderTestUtil.runUntilQuantization(fTestPngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestPngJpegImageContext.getJpegImageBlock(0, 0);
      JpegImageBlock jpegImageBlock_0_1 = fTestPngJpegImageContext.getJpegImageBlock(1, 0);
      int[][]        yLumaMatrix_0_0    = jpegImageBlock_0_0.getRoundedMatrixYLumaForCalculation();
      int[][]        cbChromaMatrix_0_0 = jpegImageBlock_0_0.getRoundedMatrixCbChromaForCalculation();
      int[][]        crChromaMatrix_0_0 = jpegImageBlock_0_0.getRoundedMatrixCrChromaForCalculation();
      int[][]        yLumaMatrix_0_1    = jpegImageBlock_0_1.getRoundedMatrixYLumaForCalculation();
      int[][]        cbChromaMatrix_0_1 = jpegImageBlock_0_1.getRoundedMatrixCbChromaForCalculation();
      int[][]        crChromaMatrix_0_1 = jpegImageBlock_0_1.getRoundedMatrixCrChromaForCalculation();

      // TODO Kunst: Write tests!
//    Assert.assertArrayEquals(getExecute02RefYLumaMatrixFromBlock_0_0(), yLumaMatrix_0_0);
//    Assert.assertArrayEquals(getExecute02RefCbChromaMatrixFromBlock_0_0(), cbChromaMatrix_0_0);
//    Assert.assertArrayEquals(getExecute02RefCrChromaMatrixFromBlock_0_0(), crChromaMatrix_0_0);
//
//    // second jpeg image block
//    Assert.assertArrayEquals(getExecute02RefYLumaMatrixFromBlock_0_1(), yLumaMatrix_0_1);
//    Assert.assertArrayEquals(getExecute02RefCbChromaMatrixFromBlock_0_1(), cbChromaMatrix_0_1);
//    Assert.assertArrayEquals(getExecute02RefCrChromaMatrixFromBlock_0_1(), crChromaMatrix_0_1);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    *       Cb chrominance matrix.
    *
    *
    *       @return
    */
   private double[][] getCbChromaMatrixForCalculation() {
      double[][] resultCbChromaMatrixForCalculation = new double[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultCbChromaMatrixForCalculation;
   }

   /**
    * Cr chrominance matrix.
    *
    *
    * @return
    */
   private double[][] getCrChromaMatrixForCalculation() {
      double[][] resultCrChromaMatrixForCalculation = new double[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultCrChromaMatrixForCalculation;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefCbChromaMatrixFromBlock_0_0() {
      int[][] resultRefCbChromaMatrix = new int[][] {
         { 61, 0, -1, -1, 0, 0, 0, 0 }, { 1, 0, -2, -1, 0, 0, 0, 0 }, { 1, 0, -1, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCbChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefCbChromaMatrixFromBlock_0_1() {
      int[][] resultRefCbChromaMatrix = new int[][] {
         { 60, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCbChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefCrChromaMatrixFromBlock_0_0() {
      int[][] resultRefCrChromaMatrix = new int[][] {
         { 61, 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1, 0, 0, 0 },
         { 0, 0, 0, 1, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCrChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefCrChromaMatrixFromBlock_0_1() {
      int[][] resultRefCrChromaMatrix = new int[][] {
         { 60, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefCrChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefYLumaMatrixFromBlock_0_0() {
      int[][] resultRefLumaMatrix = new int[][] {
         { 122, -6, 1, -1, -1, 1, 0, -1 }, { -11, -7, 1, -1, -1, 1, 0, -1 }, { -9, -6, 0, -1, -1, 1, 0, -1 },
         { -8, -4, 0, -1, 0, 1, 0, -1 }, { -5, -3, 0, 0, 0, 0, 0, 0 }, { -3, -1, 0, 0, 0, 0, 0, 0 },
         { -1, -1, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefLumaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getExecute02RefYLumaMatrixFromBlock_0_1() {
      int[][] resultRefLumaMatrix = new int[][] {
         { 16, 32, 33, 19, 11, 5, 3, 1 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefLumaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefChromaMatrixExecute_01() {
      int[][] resultRefChromaMatrix = new int[][] {
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefChromaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefLumaMatrixExecute_01() {
      int[][] resultRefLumaMatrix = new int[][] {
         { 63, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefLumaMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int[][] getRefOnlyZeroesMatrixExecute_01() {
      int[][] resultRefMatrix = new int[][] {
         { -64, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
         { 0, 0, 0, 0, 0, 0, 0, 0 }
      };

      return resultRefMatrix;
   }

   /**
    * Get reference values after quantization.
    *
    *
    * @return
    */
   private int[][] getRefYLumaMatrixForCalculation() {
      int[][] resultYLumaMatrixForCalculation = new int[][] {
         { 44, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      return resultYLumaMatrixForCalculation;
   }

   /**
    * Y luminiscence matrix.
    *
    *
    * @return
    */
   private double[][] getYLumaMatrixForCalculation() {
      double[][] resultYLumaMatrixForCalculation = new double[][] {
         { 699.25, 43.18, 55.25, 72.11, 24.00, -25.51, 11.21, -4.14 },
         { -129.78, -71.50, -70.26, -73.35, 59.43, -24.02, 22.61, -2.05 },
         { 85.71, 30.32, 61.78, 44.87, 14.84, 17.35, 15.51, -13.19 },
         { -40.81, 10.17, -17.53, -55.81, 30.50, -2.28, -21.00, -1.26 },
         { -157.50, -49.39, 13.27, -1.78, -8.75, 22.47, -8.47, -9.23 },
         { 92.49, -9.03, 45.72, -48.13, -58.51, -9.01, -28.54, 10.38 },
         { -53.09, -62.97, -3.49, -19.62, 56.09, -2.25, -3.28, 11.91 },
         { -20.54, -55.90, -20.59, -18.19, -26.58, -27.07, 8.47, 0.31 }
      };

      return resultYLumaMatrixForCalculation;
   }
}
