package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import org.jblas.DoubleMatrix;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/27
 * @author         Enter your name here...
 */
public class HummingCodesUtil {

   /**
    * Constructs ...
    *
    */
   private HummingCodesUtil() {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aLevel
    *
    * @return
    */
   public static DoubleMatrix createHummingMatrix(int aLevel) {
      DoubleMatrix resultHUmmingMatrix = null;
      int          numOfColumns        = (1 << aLevel) - 1;
      int          numOfRows           = aLevel;

      resultHUmmingMatrix = new DoubleMatrix(numOfRows, numOfColumns);

      for (int columnIndex = 1; columnIndex <= numOfColumns; columnIndex++) {
         byte[] numberRepresentation = BitUtils.convertNumberToBits(columnIndex, BitNumbering.LSB_FIRST, Type.INT);
         int    rowPosition          = aLevel - 1;
         int    columnPosition       = columnIndex - 1;

         for (int bitIndex = 0; bitIndex < aLevel; bitIndex++) {
            resultHUmmingMatrix.put(rowPosition, columnPosition, numberRepresentation[bitIndex]);
            rowPosition--;
         }
      }

      return resultHUmmingMatrix;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aCapacityInBits
    * @param aMessageSizeInBits
    *
    * @return
    */
   public static int getEmbedingRate(int aCapacityInBits, int aMessageSizeInBits) {
      int   resultEmbedingRate = -1;
      float embeddingRatio     = ((float) aMessageSizeInBits / (float) aCapacityInBits);

      for (int k = 1; k <= 9; k++) {
         float embeddingRatioForK = (float) k / ((1 << k) - 1);

         if ((embeddingRatioForK >= embeddingRatio) && ((aMessageSizeInBits % k) == 0)) {
            resultEmbedingRate = k;
         }
      }

      return resultEmbedingRate;
   }

   /**
    * Method description
    *
    *
    * @param aHummingMatrix
    * @param aBits
    *
    * @return
    */
   public static DoubleMatrix getHxVector(DoubleMatrix aHummingMatrix, int[] aBits) {
      DoubleMatrix resultHxVector = null;
      DoubleMatrix hMatrix        = convertToOneColumnMatrix(aBits);

      resultHxVector = aHummingMatrix.mmul(hMatrix);

      for (int rowIndex = 0; rowIndex < aHummingMatrix.getRows(); rowIndex++) {
         int elementValue = (int) resultHxVector.get(rowIndex, 0);

         if ((elementValue % 2) == 0) {
            resultHxVector.put(rowIndex, 0, 0);
         } else {
            resultHxVector.put(rowIndex, 0, 1);
         }
      }

      return resultHxVector;
   }

   /**
    * Method description
    *
    *
    * @param aHummingMatrix
    * @param aVector
    *
    * @return
    */
   public static int getIdenticalColumnNumber(DoubleMatrix aHummingMatrix, double[] aVector) {
      int     resultNumber = -1;
      boolean found        = true;

      for (int column = 0; column < aHummingMatrix.getColumns(); column++) {
         found = true;

         for (int row = 0; row < aHummingMatrix.getRows(); row++) {
            int hummingElement = (int) aHummingMatrix.get(row, column);
            int vectorElement  = (int) aVector[row];

            // if elements differ then go out search another one
            if (hummingElement != vectorElement) {
               found = false;

               break;
            }
         }

         if (found) {
            resultNumber = column;

            break;
         }
      }

      return resultNumber;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aHxVector
    * @param aMessageBits
    *
    * @return
    */
   public static DoubleMatrix subtractMatrices(DoubleMatrix aHxVector, int[] aMessageBits) {
      DoubleMatrix resultSubstractedMatrix = new DoubleMatrix(aHxVector.getRows(), 1);

      for (int rowIndex = 0; rowIndex < aHxVector.getRows(); rowIndex++) {
         int substractedValue = (int) (aHxVector.get(rowIndex, 0) - aMessageBits[rowIndex]);

         if ((substractedValue == 1) || (substractedValue == -1)) {
            resultSubstractedMatrix.put(rowIndex, 0, 1);
         } else if (substractedValue == 0) {
            resultSubstractedMatrix.put(rowIndex, 0, 0);
         } else {
            JpegImageUtils.throwJpegImageExeption("Should be -1, 1 or 0!");
         }
      }

      return resultSubstractedMatrix;
   }

   /**
    * Method description
    *
    *
    * @param aElements
    *
    * @return
    */
   private static DoubleMatrix convertToOneColumnMatrix(int[] aElements) {
      DoubleMatrix resultMatrix = new DoubleMatrix(aElements.length, 1);

      for (int rowIndex = 0; rowIndex < aElements.length; rowIndex++) {
         resultMatrix.put(rowIndex, 0, aElements[rowIndex]);
      }

      return resultMatrix;
   }
}
