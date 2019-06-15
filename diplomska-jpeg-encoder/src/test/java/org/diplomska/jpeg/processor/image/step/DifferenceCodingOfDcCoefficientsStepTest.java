package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegEncoderTestUtil;
import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.block.step.ZigZagAndRlcBlockStep;
import org.diplomska.jpeg.processor.image.step.DifferenceCodingOfDcCoefficientsStep;
import org.diplomska.jpeg.util.JpegImageUtils;
import org.diplomska.jpeg.util.RunLengthCodingStructure;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/06/11
 * @author         Ales Kunst
 */
public class DifferenceCodingOfDcCoefficientsStepTest {

   /** Jpeg image context */
   JpegImageContext fJpegImageContext;

   /** Field description */
   private JpegImageContext fTestBlackWhitePngJpegImageContext;

   /** Field description */
   private JpegImageContext fTestPngJpegImageContext;

   //~--- set methods ---------------------------------------------------------

   /**
    * Set up.
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {
      fTestBlackWhitePngJpegImageContext = JpegEncoderTestUtil.createTestBlackWhitePngJpegImageContext();
      fTestPngJpegImageContext           = JpegEncoderTestUtil.createTestPngJpegImageContext();
      fJpegImageContext                  = new JpegImageContext(new int[8][24]);
      createJpegImageBlock_01(fJpegImageContext);
      createJpegImageBlock_02(fJpegImageContext);
      createJpegImageBlock_03(fJpegImageContext);
   }

   //~--- methods -------------------------------------------------------------

   /**
    *    Method description
    *
    */
   @Test
   public void testExecute() {
      DifferenceCodingOfDcCoefficientsStep differenceStep = new DifferenceCodingOfDcCoefficientsStep();

      differenceStep.execute(fJpegImageContext);

      JpegImageBlock jpegImageBlock_01 = fJpegImageContext.getJpegImageBlock(0, 0);

      // --------
      JpegImageBlock jpegImageBlock_02 = fJpegImageContext.getJpegImageBlock(1, 0);

      // --------
      JpegImageBlock jpegImageBlock_03 = fJpegImageContext.getJpegImageBlock(2, 0);

      Assert.assertEquals(44, getRlcStructureForYLuma(jpegImageBlock_01).getCoefficientNumber());
      Assert.assertEquals(-22, getRlcStructureForYLuma(jpegImageBlock_02).getCoefficientNumber());
      Assert.assertEquals(-11, getRlcStructureForYLuma(jpegImageBlock_03).getCoefficientNumber());

      // ---- Cr ----
      Assert.assertEquals(22, getRlcStructureForCrChroma(jpegImageBlock_01).getCoefficientNumber());
      Assert.assertEquals(-11, getRlcStructureForCrChroma(jpegImageBlock_02).getCoefficientNumber());
      Assert.assertEquals(-5, getRlcStructureForCrChroma(jpegImageBlock_03).getCoefficientNumber());

      // ---- Cb ----
      Assert.assertEquals(11, getRlcStructureForCbChroma(jpegImageBlock_01).getCoefficientNumber());
      Assert.assertEquals(-5, getRlcStructureForCbChroma(jpegImageBlock_02).getCoefficientNumber());
      Assert.assertEquals(-3, getRlcStructureForCbChroma(jpegImageBlock_03).getCoefficientNumber());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_01() {
      JpegEncoderTestUtil.runUntilDifferenceCodingOfDcCoefficients(fTestBlackWhitePngJpegImageContext);

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
//    Assert.assertArrayEquals(new int[] { 458879, 0 }, yLumaArray_0_1);
//    Assert.assertArrayEquals(new int[] { 0 }, cbChromaArray_0_1);
//    Assert.assertArrayEquals(new int[] { 0 }, crChromaArray_0_1);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testExecute_02() {
      JpegEncoderTestUtil.runUntilDifferenceCodingOfDcCoefficients(fTestPngJpegImageContext);

      JpegImageBlock jpegImageBlock_0_0 = fTestPngJpegImageContext.getJpegImageBlock(0, 0);
      JpegImageBlock jpegImageBlock_0_1 = fTestPngJpegImageContext.getJpegImageBlock(1, 0);
      int[]          yLumaArray_0_0     = jpegImageBlock_0_0.getZigZagRlcEncodedYLumaArray();
      int[]          cbChromaArray_0_0  = jpegImageBlock_0_0.getZigZagRlcEncodedCbChromaArray();
      int[]          crChromaArray_0_0  = jpegImageBlock_0_0.getZigZagRlcEncodedCrChromaArray();
      int[]          yLumaArray_0_1     = jpegImageBlock_0_1.getZigZagRlcEncodedYLumaArray();
      int[]          cbChromaArray_0_1  = jpegImageBlock_0_1.getZigZagRlcEncodedCbChromaArray();
      int[]          crChromaArray_0_1  = jpegImageBlock_0_1.getZigZagRlcEncodedCrChromaArray();

      // TODO Kunst: Write tests!
//    Assert.assertEquals(458874, yLumaArray_0_0[0]);
//    Assert.assertEquals(393277, cbChromaArray_0_0[0]);
//    Assert.assertEquals(393277, crChromaArray_0_0[0]);
//    Assert.assertEquals(458773, yLumaArray_0_1[0]);
//    Assert.assertEquals(65536, cbChromaArray_0_1[0]);
//    Assert.assertEquals(65536, crChromaArray_0_1[0]);
   }

   /**
    * Create first jpeg image block.
    *
    *
    *
    * @param aJpegImageContext
    *
    */
   private void createJpegImageBlock_01(JpegImageContext aJpegImageContext) {
      JpegImageBlock        jpegImageBlock        = new JpegImageBlock(8, 8);
      ZigZagAndRlcBlockStep zigZagAndRlcBlockStep = new ZigZagAndRlcBlockStep();
      double[][]            lumaYJpegBlock        = {
         { 44, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCrJpegBlock = {
         { 22, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCbJpegBlock = {
         { 11, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      jpegImageBlock.setYCrCbMatrixForCalculation(lumaYJpegBlock, chromaCrJpegBlock, chromaCbJpegBlock);
      zigZagAndRlcBlockStep.execute(jpegImageBlock);
      aJpegImageContext.putJpegImageBlock(0, 0, null);
      aJpegImageContext.putJpegImageBlock(0, 0, jpegImageBlock);
   }

   /**
    * Create second jpeg image block.
    *
    *
    *
    * @param aJpegImageContext
    *
    */
   private void createJpegImageBlock_02(JpegImageContext aJpegImageContext) {
      JpegImageBlock        jpegImageBlock        = new JpegImageBlock(8, 8);
      ZigZagAndRlcBlockStep zigZagAndRlcBlockStep = new ZigZagAndRlcBlockStep();
      double[][]            lumaYJpegBlock        = {
         { 22, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCrJpegBlock = {
         { 11, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCbJpegBlock = {
         { 6, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      jpegImageBlock.setYCrCbMatrixForCalculation(lumaYJpegBlock, chromaCrJpegBlock, chromaCbJpegBlock);
      zigZagAndRlcBlockStep.execute(jpegImageBlock);
      aJpegImageContext.putJpegImageBlock(0, 1, null);
      aJpegImageContext.putJpegImageBlock(0, 1, jpegImageBlock);
   }

   /**
    * Create third jpeg image block.
    *
    *
    *
    * @param aJpegImageContext
    *
    */
   private void createJpegImageBlock_03(JpegImageContext aJpegImageContext) {
      JpegImageBlock        jpegImageBlock        = new JpegImageBlock(8, 8);
      ZigZagAndRlcBlockStep zigZagAndRlcBlockStep = new ZigZagAndRlcBlockStep();
      double[][]            lumaYJpegBlock        = {
         { 11, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCrJpegBlock = {
         { 6, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };
      double[][] chromaCbJpegBlock = {
         { 3, 4, 6, 5, 1, -1, 0, 0 }, { -11, -6, -5, -4, 2, 0, 0, 0 }, { 6, 2, 4, 2, 0, 0, 0, 0 },
         { -3, 1, -1, -2, 1, 0, 0, 0 }, { -9, -2, 0, 0, 0, 0, 0, 0 }, { 4, 0, 1, -1, -1, 0, 0, 0 },
         { -1, -1, 0, 0, 1, 0, 0, 0 }, { 0, -1, 0, 0, 0, 0, 0, 0 }
      };

      jpegImageBlock.setYCrCbMatrixForCalculation(lumaYJpegBlock, chromaCrJpegBlock, chromaCbJpegBlock);
      zigZagAndRlcBlockStep.execute(jpegImageBlock);
      aJpegImageContext.putJpegImageBlock(0, 2, null);
      aJpegImageContext.putJpegImageBlock(0, 2, jpegImageBlock);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Convert Cb Chroma encoded value to rlc structure.
    *
    *
    * @param aJpegImageBlock
    *
    * @return
    */
   private RunLengthCodingStructure getRlcStructureForCbChroma(JpegImageBlock aJpegImageBlock) {
      int dcEncodedValue = aJpegImageBlock.getZigZagRlcEncodedCbChromaArray()[0];

      return JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(dcEncodedValue);
   }

   /**
    * Convert Cr Chroma encoded value to rlc structure.
    *
    *
    * @param aJpegImageBlock
    *
    * @return
    */
   private RunLengthCodingStructure getRlcStructureForCrChroma(JpegImageBlock aJpegImageBlock) {
      int dcEncodedValue = aJpegImageBlock.getZigZagRlcEncodedCrChromaArray()[0];

      return JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(dcEncodedValue);
   }

   /**
    * Convert Y Luma encoded value to rlc structure.
    *
    *
    * @param aJpegImageBlock
    *
    * @return
    */
   private RunLengthCodingStructure getRlcStructureForYLuma(JpegImageBlock aJpegImageBlock) {
      int dcEncodedValue = aJpegImageBlock.getZigZagRlcEncodedYLumaArray()[0];

      return JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(dcEncodedValue);
   }
}
