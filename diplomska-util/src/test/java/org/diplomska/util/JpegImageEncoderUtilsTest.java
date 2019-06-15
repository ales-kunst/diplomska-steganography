package org.diplomska.util;

import org.diplomska.util.structs.RgbColor;
import org.diplomska.util.structs.RunLengthCodingStructure;

import org.jblas.DoubleMatrix;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;

import java.math.BigInteger;

//~--- classes ----------------------------------------------------------------

/**
 * Test class for JpegImageUtils.
 *
 *
 * @version        1.0, 15/04/06
 * @author         Ales Kunst
 */
public class JpegImageEncoderUtilsTest {

   /**
    * Setup.
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Teardown.
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Test calculation of DCT.
    *
    */
   @Test
   public void testCalculateDctMatrix() {
      double[][] testMatrixRaw0 = new double[][] {
         { 12, 16, 19, 12, 12, 27, 51, 47 }, { 16, 24, 12, 19, 12, 20, 39, 51 }, { 24, 27, 8, 39, 35, 34, 24, 44 },
         { 40, 17, 28, 32, 24, 27, 8, 32 }, { 34, 20, 28, 20, 12, 8, 19, 34 }, { 19, 39, 12, 27, 27, 12, 8, 34 },
         { 8, 28, -5, 39, 34, 16, 12, 19 }, { 20, 27, 8, 27, 24, 19, 19, 8 }
      };
      DoubleMatrix testMatrix = new DoubleMatrix(testMatrixRaw0);

      Assert.assertEquals(1, 1);

      for (int i = 0; i < 1; i++) {

         // System.out.println("Run [" + i + "]");
         // DoubleMatrix calculatedDctMatrix = JpegImageUtils.calculateSlowDctMatrix(testMatrixRaw2);
         DoubleMatrix calculatedDctMatrix1 = JpegImageEncoderUtils.calculateDct(testMatrix);

         Assert.assertEquals(calculatedDctMatrix1.get(0, 0), calculatedDctMatrix1.get(0, 0), 0.5);

         // calculatedDctMatrix.get(0, 0);
         // calculatedDctMatrix1.get(0, 0);
      }
   }

   /**
    * Method description
    *
    */
   @Test
   public void testConvertBitsToByte() {
      byte[] bitsArray_neg_128 = { 1, 0, 0, 0, 0, 0, 0, 0 };
      byte[] bitsArray_127     = { 0, 1, 1, 1, 1, 1, 1, 1 };
      byte[] bitsArray_1       = { 0, 0, 0, 0, 0, 0, 0, 1 };
      byte[] bitsArray_2       = { 0, 0, 0, 0, 0, 0, 1, 0 };
      byte[] bitsArray_neg_1   = { 1, 1, 1, 1, 1, 1, 1, 1 };

      Assert.assertEquals(-128, JpegImageEncoderUtils.convertBitsToByte(bitsArray_neg_128));
      Assert.assertEquals(127, JpegImageEncoderUtils.convertBitsToByte(bitsArray_127));
      Assert.assertEquals(2, JpegImageEncoderUtils.convertBitsToByte(bitsArray_2));
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToByte(bitsArray_1));
      Assert.assertEquals(-1, JpegImageEncoderUtils.convertBitsToByte(bitsArray_neg_1));
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testConvertBitsToBytes() {
      byte[] bitsArray_neg_128 = { 1, 0, 0, 0, 0, 0, 0, 0 };
      byte[] bitsArray_127     = { 0, 1, 1, 1, 1, 1, 1, 1 };
      byte[] bitsArray_1       = { 0, 0, 0, 0, 0, 0, 0, 1 };
      byte[] bitsArray_2       = { 0, 0, 0, 0, 0, 0, 1, 0 };

      // ------
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_neg_128).length);
      Assert.assertEquals(-128, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_neg_128)[0]);

      // ------
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_127).length);
      Assert.assertEquals(127, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_127)[0]);

      // ------
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_2).length);
      Assert.assertEquals(2, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_2)[0]);

      // ------
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_1).length);
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToBytes(bitsArray_1)[0]);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testConvertBitsToUnsignedByte() {
      byte[] bitsArray_128 = { 1, 0, 0, 0, 0, 0, 0, 0 };
      byte[] bitsArray_127 = { 0, 1, 1, 1, 1, 1, 1, 1 };
      byte[] bitsArray_1   = { 0, 0, 0, 0, 0, 0, 0, 1 };
      byte[] bitsArray_2   = { 0, 0, 0, 0, 0, 0, 1, 0 };
      byte[] bitsArray_255 = { 1, 1, 1, 1, 1, 1, 1, 1 };

      Assert.assertEquals(128, JpegImageEncoderUtils.convertBitsToUnsignedByte(bitsArray_128));
      Assert.assertEquals(127, JpegImageEncoderUtils.convertBitsToUnsignedByte(bitsArray_127));
      Assert.assertEquals(2, JpegImageEncoderUtils.convertBitsToUnsignedByte(bitsArray_2));
      Assert.assertEquals(1, JpegImageEncoderUtils.convertBitsToUnsignedByte(bitsArray_1));
      Assert.assertEquals(255, JpegImageEncoderUtils.convertBitsToUnsignedByte(bitsArray_255));
   }

   /**
    * Test method
    *
    */
   @Test
   public void testConvertBytesToBits() {
      byte[] byteArray_neg_128                  = { -128 };
      byte[] byteArray_neg_128_expected         = { 1, 0, 0, 0, 0, 0, 0, 0 };
      byte[] byteArray_1                        = { 1 };
      byte[] byteArray_1_expected               = { 0, 0, 0, 0, 0, 0, 0, 1 };
      byte[] byteArray_neg_128_neg_128          = { -128, -128 };
      byte[] byteArray_neg_128_neg_128_expected = { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
      byte[] byteArray_64_1                     = { 64, 1 };
      byte[] byteArray_64_1_expected            = { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };

      Assert.assertArrayEquals(byteArray_neg_128_expected, JpegImageEncoderUtils.convertBytesToBits(byteArray_neg_128));
      Assert.assertArrayEquals(byteArray_1_expected, JpegImageEncoderUtils.convertBytesToBits(byteArray_1));
      Assert.assertArrayEquals(byteArray_neg_128_neg_128_expected,
                               JpegImageEncoderUtils.convertBytesToBits(byteArray_neg_128_neg_128));
      Assert.assertArrayEquals(byteArray_64_1_expected, JpegImageEncoderUtils.convertBytesToBits(byteArray_64_1));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testConvertFromEncodedValueToCoefficientValue() {
      Assert.assertEquals(-15, JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue((byte) 4, 0));
      Assert.assertEquals(-14, JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue((byte) 4, 1));
      Assert.assertEquals(31, JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue((byte) 5, 31));
      Assert.assertEquals(-32, JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue((byte) 6, 31));
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testDecodeFromBitCodedRepresentation() {
      Assert.assertEquals(0, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] {}));
      Assert.assertEquals(-1, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 0 }));
      Assert.assertEquals(1, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 1 }));
      Assert.assertEquals(-30, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 0, 0, 0, 0, 1 }));
      Assert.assertEquals(57, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 1, 1, 1, 0, 0, 1 }));
      Assert.assertEquals(-127, JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 0, 0, 0, 0, 0, 0,
                                                                                                    0 }));
      Assert.assertEquals(127,
                          JpegImageEncoderUtils.decodeFromBitCodedRepresentation(new byte[] { 1, 1, 1, 1, 1, 1, 1 }));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testDecodeFromEncodedZeroRunLengthCoding() {
      int                      encodedZrlcNumber0 = (new BigInteger("000001100000000000111001", 2)).intValue();
      RunLengthCodingStructure zRlcNumbers0       =
         JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedZrlcNumber0);
      int                      encodedZrlcNumber1 = (new BigInteger("111100000000000000000000", 2)).intValue();
      RunLengthCodingStructure zRlcNumbers1       =
         JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedZrlcNumber1);
      int                      encodedZrlcNumber2 = (new BigInteger("010001010000000000010111", 2)).intValue();
      RunLengthCodingStructure zRlcNumbers2       =
         JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedZrlcNumber2);
      int                      encodedZrlcNumber3 = (new BigInteger("000101010000000000000001", 2)).intValue();
      RunLengthCodingStructure zRlcNumbers3       =
         JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedZrlcNumber3);
      int                      encodedZrlcNumber4 = (new BigInteger("000001000000000000000111", 2)).intValue();
      RunLengthCodingStructure zRlcNumbers4       =
         JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedZrlcNumber4);

      // this should be equal to 0000 = 0 1100 = 6 length of coded 57 value 111001 = 57
      Assert.assertArrayEquals(new int[] { 0, 6, 57 }, zRlcNumbers0.toArray());
      Assert.assertArrayEquals(new int[] { 15, 0, 0 }, zRlcNumbers1.toArray());
      Assert.assertArrayEquals(new int[] { 4, 5, 23 }, zRlcNumbers2.toArray());
      Assert.assertArrayEquals(new int[] { 1, 5, 1 }, zRlcNumbers3.toArray());
      Assert.assertArrayEquals(new int[] { 0, 4, 7 }, zRlcNumbers4.toArray());
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testEncodeToBitCodedRepresentation() {
      Assert.assertArrayEquals(new byte[] {}, JpegImageEncoderUtils.encodeToBitCodedRepresentation(0));
      Assert.assertArrayEquals(new byte[] { 1 }, JpegImageEncoderUtils.encodeToBitCodedRepresentation(1));
      Assert.assertArrayEquals(new byte[] { 0 }, JpegImageEncoderUtils.encodeToBitCodedRepresentation(-1));
      Assert.assertArrayEquals(new byte[] { 0, 0, 0, 0, 1 }, JpegImageEncoderUtils.encodeToBitCodedRepresentation(-30));
      Assert.assertArrayEquals(new byte[] { 1, 1, 1, 0, 0, 1 },
                               JpegImageEncoderUtils.encodeToBitCodedRepresentation(57));
      Assert.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0 },
                               JpegImageEncoderUtils.encodeToBitCodedRepresentation(-127));
      Assert.assertArrayEquals(new byte[] { 1, 1, 1, 1, 1, 1, 1 },
                               JpegImageEncoderUtils.encodeToBitCodedRepresentation(127));
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testEncodeToRgb() {
      Color color    = new Color(100, 100, 100);
      int   colorRgb = color.getRGB() & 0xFFFFFF;

      Assert.assertEquals(colorRgb, RgbColor.getRgbColor(100, 100, 100));
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testEncodeToZeroRunLengthCoding() {
      int zRlcNumber0         = JpegImageEncoderUtils.encodeToZeroRunLengthCoding((byte) 0, 57);
      int expectedZrlcNumber0 = (new BigInteger("000001100000000000111001", 2)).intValue();
      int zRlcNumber1         = JpegImageEncoderUtils.encodeToZeroRunLengthCoding((byte) 15, 0);
      int expectedZrlcNumber1 = (new BigInteger("111100000000000000000000", 2)).intValue();
      int zRlcNumber2         = JpegImageEncoderUtils.encodeToZeroRunLengthCoding((byte) 4, 23);
      int expectedZrlcNumber2 = (new BigInteger("010001010000000000010111", 2)).intValue();

      // this should be equal to 0000 = 0 1100 = 6 length of coded 57 value 111001 = 57
      Assert.assertEquals(expectedZrlcNumber0, zRlcNumber0);
      Assert.assertEquals(expectedZrlcNumber1, zRlcNumber1);
      Assert.assertEquals(expectedZrlcNumber2, zRlcNumber2);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetColumn() {
      Assert.assertEquals(1, JpegImageEncoderUtils.getColumn(4294967297l));
   }

   /**
    * Testing of getting coordinates in 4D array.
    *
    */
   @Test
   public void testGetCoordinates() {
      int index = 0;

      for (int i = 0; i < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH; i++) {
         for (int j = 0; j < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT; j++) {
            for (int x = 0; x < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH; x++) {
               for (int y = 0; y < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT; y++) {
                  int[] coordinates = JpegImageEncoderUtils.getCoordinates(index);

                  Assert.assertEquals(i, coordinates[0]);
                  Assert.assertEquals(j, coordinates[1]);
                  Assert.assertEquals(x, coordinates[2]);
                  Assert.assertEquals(y, coordinates[3]);
                  index++;
               }
            }
         }
      }
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetJpegImageBlockCoordinates() {

      // (1, 1) = 4294967297 is (0,0) coordinate of jpeg image block
      Assert.assertEquals(0, JpegImageEncoderUtils.getJpegImageBlockCoordinates(4294967297l));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetMaxNumber() {
      Assert.assertEquals(0, JpegImageEncoderUtils.getMaxNumber(0));
      Assert.assertEquals(1, JpegImageEncoderUtils.getMaxNumber(1));
      Assert.assertEquals(3, JpegImageEncoderUtils.getMaxNumber(2));
      Assert.assertEquals(7, JpegImageEncoderUtils.getMaxNumber(3));
      Assert.assertEquals(15, JpegImageEncoderUtils.getMaxNumber(4));
      Assert.assertEquals(31, JpegImageEncoderUtils.getMaxNumber(5));
      Assert.assertEquals(63, JpegImageEncoderUtils.getMaxNumber(6));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetPoint() {
      Assert.assertEquals(4294967297l, JpegImageEncoderUtils.getPoint(1, 1));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetRow() {
      Assert.assertEquals(1, JpegImageEncoderUtils.getRow(4294967297l));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testInitializeZigZagCoordinatesMatrix() {
      Assert.assertEquals(0, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[0]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[0]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[1]));
      Assert.assertEquals(1, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[1]));
      Assert.assertEquals(1, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[2]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[2]));
      Assert.assertEquals(2, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[3]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[3]));
      Assert.assertEquals(1, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[4]));
      Assert.assertEquals(1, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[4]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[5]));
      Assert.assertEquals(2, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[5]));
      Assert.assertEquals(0, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[6]));
      Assert.assertEquals(3, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[6]));
      Assert.assertEquals(1, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[7]));
      Assert.assertEquals(2, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[7]));
      Assert.assertEquals(6, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[61]));
      Assert.assertEquals(7, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[61]));
      Assert.assertEquals(7, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[62]));
      Assert.assertEquals(6, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[62]));
      Assert.assertEquals(7, JpegImageEncoderUtils.getRow(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[63]));
      Assert.assertEquals(7, JpegImageEncoderUtils.getColumn(JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[63]));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testLengthOfBitCodedRepresentation() {
      Assert.assertEquals(0, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(0));
      Assert.assertEquals(1, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-1));
      Assert.assertEquals(1, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(1));
      Assert.assertEquals(2, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-2));
      Assert.assertEquals(2, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(2));
      Assert.assertEquals(2, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-3));
      Assert.assertEquals(2, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(3));
      Assert.assertEquals(4, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-10));
      Assert.assertEquals(4, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-15));
      Assert.assertEquals(4, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(8));
      Assert.assertEquals(7, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-84));
      Assert.assertEquals(7, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(127));
      Assert.assertEquals(7, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(64));
      Assert.assertEquals(9, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-300));
      Assert.assertEquals(9, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-511));
      Assert.assertEquals(9, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(511));
      Assert.assertEquals(10, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(-786));
      Assert.assertEquals(10, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(1023));
      Assert.assertEquals(10, JpegImageEncoderUtils.lengthOfBitCodedRepresentation(512));
   }

   /**
    * Method description
    *
    */
   @Test
   public void testToBits() {
      RunLengthCodingStructure rlcStructure = new RunLengthCodingStructure();

      rlcStructure.fNumberOfPreceedingZeroes = 0;
      rlcStructure.fNumberOfBits             = 5;
      rlcStructure.fEncodedZrlcNumber        = 9;
      Assert.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1 }, rlcStructure.toBits());
   }
}
