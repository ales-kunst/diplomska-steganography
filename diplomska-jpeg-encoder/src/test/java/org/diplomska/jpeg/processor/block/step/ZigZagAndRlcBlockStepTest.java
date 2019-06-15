package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.block.step.ZigZagAndRlcBlockStep;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/05/24
 * @author         Ales Kunst
 */
public class ZigZagAndRlcBlockStepTest {

   /** Jpeg image context of test black white png */
   private JpegImageContext fTestBlackWhitePngJpegImageContext;

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
   }

   //~--- methods -------------------------------------------------------------

   /**
    *    Method description
    *
    */
   @Test
   public void testCreateRlcArrays() {
      ZigZagAndRlcBlockStep zigZagProcessStep = new ZigZagAndRlcBlockStep();
      int[]                 zigZagArray       = { 9, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
      int[]                 actualRlcList     = zigZagProcessStep.createRlcArrays(zigZagArray);

      Assert.assertEquals(3, actualRlcList.length);
      Assert.assertEquals(262153, actualRlcList[0]);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute() {
      ZigZagAndRlcBlockStep zigZagProcessStep  = new ZigZagAndRlcBlockStep();
      JpegImageBlock        testJpegImageBlock = createJpegImageBlock();

      zigZagProcessStep.execute(testJpegImageBlock);
      Assert.assertEquals(393260, testJpegImageBlock.getZigZagRlcEncodedYLumaArray()[0]);
      Assert.assertEquals(262148, testJpegImageBlock.getZigZagRlcEncodedYLumaArray()[2]);
      Assert.assertEquals(2228225, testJpegImageBlock.getZigZagRlcEncodedYLumaArray()[22]);
      Assert.assertEquals(393260, testJpegImageBlock.getZigZagRlcEncodedCrChromaArray()[0]);
      Assert.assertEquals(262148, testJpegImageBlock.getZigZagRlcEncodedCrChromaArray()[2]);
      Assert.assertEquals(2228225, testJpegImageBlock.getZigZagRlcEncodedCrChromaArray()[22]);
      Assert.assertEquals(393260, testJpegImageBlock.getZigZagRlcEncodedCbChromaArray()[0]);
      Assert.assertEquals(262148, testJpegImageBlock.getZigZagRlcEncodedCbChromaArray()[2]);
      Assert.assertEquals(2228225, testJpegImageBlock.getZigZagRlcEncodedCbChromaArray()[22]);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_01() {
      JpegEncoderTestUtil.runUntilZigZag(fTestBlackWhitePngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestBlackWhitePngJpegImageContext.getJpegImageBlock(0, 0);
      JpegImageBlock jpegImageBlock_0_1 = fTestBlackWhitePngJpegImageContext.getJpegImageBlock(1, 0);
      int[]          yLumaArray_0_0     = jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray();
      int[]          cbChromaArray_0_0  = jpegImageBlock_0_0.getZigZagRlcEncodedCbChromaArray();
      int[]          crChromaArray_0_0  = jpegImageBlock_0_0.getZigZagRlcEncodedCrChromaArray();
      int[]          yLumaArray_0_1     = jpegImageBlock_0_1.getZigZagRlcEncodedYLumaArray();
      int[]          cbChromaArray_0_1  = jpegImageBlock_0_1.getZigZagRlcEncodedCbChromaArray();
      int[]          crChromaArray_0_1  = jpegImageBlock_0_1.getZigZagRlcEncodedCrChromaArray();

      // TODO Kunst: Write tests!
//    Assert.assertArrayEquals(new int[] { 458815, 0 }, yLumaArray_0_0);
//    Assert.assertArrayEquals(new int[] { 0 }, cbChromaArray_0_0);
//    Assert.assertArrayEquals(new int[] { 0 }, crChromaArray_0_0);
//    Assert.assertArrayEquals(new int[] { 393279, 0 }, yLumaArray_0_1);
//    Assert.assertArrayEquals(new int[] { 0 }, cbChromaArray_0_1);
//    Assert.assertArrayEquals(new int[] { 0 }, crChromaArray_0_1);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_02() {
      JpegEncoderTestUtil.runUntilZigZag(fTestPngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestPngJpegImageContext.getJpegImageBlock(0, 0);

      // 122, -6, -11, -9, -7, 1, -1, 1, -6, -8, -5, -4, 0, -1, -1, 1, -1, -1, 0, -3, -3, -1, -1, 0, -1, -1, 1, 0, -1, 0, 1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
      // 1     1   1    1   1  1   1  1   1   1    1   1     1   1  1   1   1      1   1   1   1      1   1  1      1     1            1                 1      1                  1   1
      // TODO Kunst: Write tests!
//    Assert.assertEquals(32, jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray().length);
//    Assert.assertEquals(458874, jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray()[0]);
//    Assert.assertEquals(1114112, jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray()[12]);
//    Assert.assertEquals(1179648, jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray()[17]);
//    Assert.assertEquals(5308417, jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray()[27]);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testInitializeZigZagArrays() {
      ZigZagAndRlcBlockStep zigZagProcessStep        = new ZigZagAndRlcBlockStep();
      int[][]               lumaYJpegBlock           = getLumaYMatrix();
      int[][]               chromaCrJpegBlock        = getChromaCrMatrix();
      int[][]               chromaCbJpegBlock        = getChromaCbMatrix();
      int[]                 expectedLumaYZigZagArray = { 44, 4, -11, 6, -6, 6, 5, -5, 2, -3, -9, 1, 4, -4, 1, -1, 2, 2,
                                                         -1, -2, 4, -1, 0, 0, -2, 0, 0, 0, 0, 0, 0, 1, 0, 1, -1, 0, -1,
                                                         0, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0,
                                                         0, 0, 0, 0, 0, 0, 0 };
      int[] expectedChromaCrZigZagArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                            22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                                            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                            60, 61, 62, 63, 64 };
      int[] expectedChromaCbZigZagArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                            22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                                            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                                            60, 61, 62, 63, 64 };

      zigZagProcessStep.initializeZigZagArrays(lumaYJpegBlock, chromaCrJpegBlock, chromaCbJpegBlock);
      Assert.assertArrayEquals(expectedLumaYZigZagArray, zigZagProcessStep.getfZigZagLumaYArray());
      Assert.assertArrayEquals(expectedChromaCrZigZagArray, zigZagProcessStep.getfZigZagChromaCrArray());
      Assert.assertArrayEquals(expectedChromaCbZigZagArray, zigZagProcessStep.getfZigZagChromaCbArray());
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private JpegImageBlock createJpegImageBlock() {
      JpegImageBlock returnBlock = new JpegImageBlock(8, 8);
      double[][]     matrix      = {
         { 44, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      returnBlock.setYCrCbMatrixForCalculation(matrix, matrix, matrix);

      return returnBlock;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    *    Get Chroma Cb quantized jpeg block.
    *
    *
    *    @return
    */
   private int[][] getChromaCbMatrix() {
      int[][] chromaCbJpegBlock = {
         { 1, 2, 6, 7, 15, 16, 28, 29 }, { 3, 5, 8, 14, 17, 27, 30, 43 }, { 4, 9, 13, 18, 26, 31, 42, 44 },
         { 10, 12, 19, 25, 32, 41, 45, 54 }, { 11, 20, 24, 33, 40, 46, 53, 55 }, { 21, 23, 34, 39, 47, 52, 56, 61 },
         { 22, 35, 38, 48, 51, 57, 60, 62 }, { 36, 37, 49, 50, 58, 59, 63, 64 }
      };

      return chromaCbJpegBlock;
   }

   /**
    * Get Chroma Cr quantized jpeg block.
    *
    *
    * @return
    */
   private int[][] getChromaCrMatrix() {
      int[][] chromaCrJpegBlock = {
         { 1, 2, 6, 7, 15, 16, 28, 29 }, { 3, 5, 8, 14, 17, 27, 30, 43 }, { 4, 9, 13, 18, 26, 31, 42, 44 },
         { 10, 12, 19, 25, 32, 41, 45, 54 }, { 11, 20, 24, 33, 40, 46, 53, 55 }, { 21, 23, 34, 39, 47, 52, 56, 61 },
         { 22, 35, 38, 48, 51, 57, 60, 62 }, { 36, 37, 49, 50, 58, 59, 63, 64 }
      };

      return chromaCrJpegBlock;
   }

   /**
    * Get Luma quantized jpeg block.
    *
    *
    * @return
    */
   private int[][] getLumaYMatrix() {
      int[][] lumaYJpegBlock = {
         { 44, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      return lumaYJpegBlock;
   }
}
