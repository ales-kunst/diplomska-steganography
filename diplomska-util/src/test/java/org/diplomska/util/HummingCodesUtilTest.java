package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import org.jblas.DoubleMatrix;

import org.junit.Assert;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/27
 * @author         Enter your name here...
 */
public class HummingCodesUtilTest {

   /**
    * Method description
    *
    */
   @Test
   public void testCreateHummingMatrix() {
      double[][]   expectedMatrix        = new double[][] {
         { 0, 0, 0, 1, 1, 1, 1 }, { 0, 1, 1, 0, 0, 1, 1 }, { 1, 0, 1, 0, 1, 0, 1 }
      };
      DoubleMatrix expectedHummingMatrix = new DoubleMatrix(expectedMatrix);
      DoubleMatrix actualHummingMatrix   = HummingCodesUtil.createHummingMatrix(3);

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 7; column++) {
            int expectedElem = (int) expectedHummingMatrix.get(row, column);
            int actualElem   = (int) actualHummingMatrix.get(row, column);

            Assert.assertEquals(expectedElem, actualElem);
         }
      }
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetEmbedingRate() {
      int embeddingRate = HummingCodesUtil.getEmbedingRate(50000, 1000);

      Assert.assertEquals(8, embeddingRate);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetHxVector() {
      int[]        coverBits      = new int[] { 1, 0, 1, 0, 1, 0, 1 };
      DoubleMatrix hummingMatrix  = HummingCodesUtil.createHummingMatrix(3);
      DoubleMatrix resultHxMatrix = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);

      Assert.assertArrayEquals(new double[][] {
         { 0 }, { 0 }, { 0 }
      }, resultHxMatrix.toArray2());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetIdenticalColumnNumber() {
      int[]        coverBits         = new int[] { 1, 0, 0, 0, 1, 1, 1 };
      DoubleMatrix hummingMatrix     = HummingCodesUtil.createHummingMatrix(3);
      DoubleMatrix resultHxMatrix    = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);
      int[]        messageBits       = new int[] { 0, 1, 0 };
      DoubleMatrix substractedMatrix = HummingCodesUtil.subtractMatrices(resultHxMatrix, messageBits);
      int          columnPosition    = HummingCodesUtil.getIdenticalColumnNumber(hummingMatrix,
                                                                                 substractedMatrix.toArray());

      Assert.assertEquals(6, columnPosition);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testSubtractMatrices() {
      int[]        coverBits         = new int[] { 1, 0, 0, 0, 1, 1, 1 };
      DoubleMatrix hummingMatrix     = HummingCodesUtil.createHummingMatrix(3);
      DoubleMatrix resultHxMatrix    = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);
      int[]        messageBits       = new int[] { 0, 1, 0 };
      DoubleMatrix substractedMatrix = HummingCodesUtil.subtractMatrices(resultHxMatrix, messageBits);

      Assert.assertArrayEquals(new double[] { 1, 1, 1 }, substractedMatrix.toArray(), 0.01);
   }

   /**
    * Method description
    *
    */
   @Test
   public void testTestAllCombinationsFor_3_2_Humming() {
      DoubleMatrix hummingMatrix = HummingCodesUtil.createHummingMatrix(2);

      for (int messageNumber = 0; messageNumber <= 3; messageNumber++) {
         int[] messageBits = getMessageBitArray(messageNumber, 2);

         for (int coverNumber = 0; coverNumber <= 7; coverNumber++) {
            int[]        coverBits    = getCoverBitArray(coverNumber, 3);
            DoubleMatrix hxMatrix     = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);
            DoubleMatrix substrMatrix = HummingCodesUtil.subtractMatrices(hxMatrix, messageBits);

            if (!isOkMatrix(substrMatrix)) {
               int columnNumber = HummingCodesUtil.getIdenticalColumnNumber(hummingMatrix, substrMatrix.toArray());

               if (coverBits[columnNumber] == 0) {
                  coverBits[columnNumber] = 1;
               } else {
                  coverBits[columnNumber] = 0;
               }
            }

            DoubleMatrix hxResultMatrix = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);

            checkIfVectorsAreEqual(messageBits, hxResultMatrix);
         }
      }
   }

   /**
    * Method description
    *
    */
   @Test
   public void testTestAllCombinationsFor_7_3_Humming() {
      DoubleMatrix hummingMatrix = HummingCodesUtil.createHummingMatrix(3);

      for (int messageNumber = 0; messageNumber <= 7; messageNumber++) {
         int[] messageBits = getMessageBitArray(messageNumber, 3);

         for (int coverNumber = 0; coverNumber <= 127; coverNumber++) {
            int[]        coverBits    = getCoverBitArray(coverNumber, 7);
            DoubleMatrix hxMatrix     = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);
            DoubleMatrix substrMatrix = HummingCodesUtil.subtractMatrices(hxMatrix, messageBits);

            if (!isOkMatrix(substrMatrix)) {
               int columnNumber = HummingCodesUtil.getIdenticalColumnNumber(hummingMatrix, substrMatrix.toArray());

               if (coverBits[columnNumber] == 0) {
                  coverBits[columnNumber] = 1;
               } else {
                  coverBits[columnNumber] = 0;
               }
            }

            DoubleMatrix hxResultMatrix = HummingCodesUtil.getHxVector(hummingMatrix, coverBits);

            checkIfVectorsAreEqual(messageBits, hxResultMatrix);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param aExpectedMessageBits
    * @param aActualMessageBits
    */
   private void checkIfVectorsAreEqual(int[] aExpectedMessageBits, DoubleMatrix aActualMessageBits) {
      for (int index = 0; index < aActualMessageBits.length; index++) {
         int actualValue = (int) aActualMessageBits.get(index, 0);

         Assert.assertEquals(aExpectedMessageBits[index], actualValue);
      }
   }

   /**
    * Method description
    *
    *
    * @param aBitArray
    * @param aNewSize
    *
    * @return
    */
   private int[] convertToDiffSizeArray(byte[] aBitArray, int aNewSize) {
      int   resultArrayIndex = 0;
      int[] resultArray      = new int[aNewSize];
      int   startingBit      = aBitArray.length - aNewSize;

      for (int index = startingBit; index < aBitArray.length; index++) {
         resultArray[resultArrayIndex] = aBitArray[index];
         resultArrayIndex++;
      }

      return resultArray;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param value
    * @param aSize
    *
    * @return
    */
   private int[] getCoverBitArray(int value, int aSize) {
      byte[] messageBits       = BitUtils.convertNumberToBits(value, BitNumbering.MSB_FIRST, Type.INT);
      int[]  resultMessageBits = convertToDiffSizeArray(messageBits, aSize);

      return resultMessageBits;
   }

   /**
    * Method description
    *
    *
    * @param value
    * @param aSize
    *
    * @return
    */
   private int[] getMessageBitArray(int value, int aSize) {
      byte[] messageBits       = BitUtils.convertNumberToBits(value, BitNumbering.MSB_FIRST, Type.BYTE);
      int[]  resultMessageBits = convertToDiffSizeArray(messageBits, aSize);

      return resultMessageBits;
   }

   /**
    * Method description
    *
    *
    * @param aMatrix
    *
    * @return
    */
   private boolean isOkMatrix(DoubleMatrix aMatrix) {
      boolean resultIsOk = true;

      for (int row = 0; row < aMatrix.getRows(); row++) {
         int element = (int) aMatrix.get(row, 0);

         if (element != 0) {
            resultIsOk = false;

            break;
         }
      }

      return resultIsOk;
   }
}
