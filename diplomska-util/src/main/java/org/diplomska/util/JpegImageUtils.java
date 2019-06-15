package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import org.jblas.DoubleMatrix;

//~--- classes ----------------------------------------------------------------

/**
 * This class contains only util methods.
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public class JpegImageUtils {

   /** Maximal columns in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_WIDTH = 8;

   /** Maximal rows in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_HEIGHT = 8;

   /** This is the matrix which has diagonal elements numbered. */

   /*
    * This is how 8x8 matrix looks like
    * {  1,  2,  3,  4,  5,  6,  7,  8 }
    * {  2,  3,  4,  5,  6,  7,  8,  9 }
    * {  3,  4,  5,  6,  7,  8,  9, 10 }
    * {  4,  5,  6,  7,  8,  9, 10, 11 }
    * {  5,  6,  7,  8,  9, 10, 11, 12 }
    * {  6,  7,  8,  9, 10, 11, 12, 13 }
    * {  7,  8,  9, 10, 11, 12, 13, 14 }
    * {  8,  9, 10, 11, 12, 13, 14, 15 }
    */
   private static final byte[][] ZIG_ZAG_DIAGONAL_MATRIX = new byte[JPEG_IMAGE_BLOCK_HEIGHT][JPEG_IMAGE_BLOCK_WIDTH];

   /** Coordinates for zig zag obtaining of elements */
   public static final long[] ZIG_ZAG_COORDINATES = new long[JPEG_IMAGE_BLOCK_WIDTH * JPEG_IMAGE_BLOCK_HEIGHT];

   /** DCT matrix */
   public static final double[][] DCT_MATRIX = new double[JPEG_IMAGE_BLOCK_HEIGHT][JPEG_IMAGE_BLOCK_WIDTH];

   /** Inverse DCT matrix */
   public static double[][] TRANS_DCT_MATRIX;

   //~--- static initializers -------------------------------------------------

   static {
      initializeZigZagDiagonalMatrix();
      initializeZigZagCoordinates();
      initializeDctMatrix();
      initializeTransDctMatrix();
   }

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor because this class is a util class.
    *
    */
   private JpegImageUtils() {}

   //~--- methods -------------------------------------------------------------

   /**
    * Add scalar.
    *
    *
    * @param matrix
    * @param value
    *
    * @return
    */
   public static double[][] addNumberToMatrix(double[][] matrix, double value) {
      DoubleMatrix valuesMatrix = new DoubleMatrix(matrix);

      return valuesMatrix.add(value).toArray2();
   }

   /**
    * Calculate Discrete Cosine Transform.
    *
    *
    * @param matrix
    *
    * @return returns calculated DCT matrix.
    */
   public static double[][] calculateDct(double[][] matrix) {
      DoubleMatrix resultDctMatrix;
      DoubleMatrix dctMatrix       = new DoubleMatrix(DCT_MATRIX);
      DoubleMatrix transpDctMatrix = new DoubleMatrix(TRANS_DCT_MATRIX);
      DoubleMatrix valuesMatrix    = new DoubleMatrix(matrix);

      resultDctMatrix = dctMatrix.mmul(valuesMatrix);
      resultDctMatrix = resultDctMatrix.mmul(transpDctMatrix);

      return resultDctMatrix.toArray2();
   }

   /**
    * Calculate inverse Discrete Cosine Transform.
    *
    *
    * @param matrix
    *
    * @return returns calculated DCT matrix.
    */
   public static double[][] calculateInverseDct(double[][] matrix) {
      DoubleMatrix resultDctMatrix;
      DoubleMatrix dctMatrix       = new DoubleMatrix(DCT_MATRIX);
      DoubleMatrix transpDctMatrix = new DoubleMatrix(TRANS_DCT_MATRIX);
      DoubleMatrix valuesMatrix    = new DoubleMatrix(matrix);

      resultDctMatrix = transpDctMatrix.mmul(valuesMatrix);
      resultDctMatrix = resultDctMatrix.mmul(dctMatrix);

      return resultDctMatrix.toArray2();
   }

   /**
    * Method description
    *
    *
    * @param dctMatrix
    *
    * @return
    */
   public static double[][] calculateInverseSlowDct(double[][] dctMatrix) {
      double[][] returnIDct = new double[8][8];

      for (int row = 0; row < 8; row++) {
         for (int column = 0; column < 8; column++) {
            double f_xy = getIDctCoefficient(column, row, dctMatrix);

            returnIDct[row][column] = f_xy;
         }
      }

      return returnIDct;
   }

   /**
    * Clips value to the values at interval start and interval end.
    *
    *
    * @param aValue
    * @param aIntervalStart
    * @param aIntervalEnd
    *
    * @return
    */
   public static double clipValue(double aValue, double aIntervalStart, double aIntervalEnd) {
      double resultClippedValue = aValue;

      if (resultClippedValue < aIntervalStart) {

         // clip to interval start
         resultClippedValue = aIntervalStart;
      } else if (resultClippedValue > aIntervalEnd) {

         // clip to interval end
         resultClippedValue = aIntervalEnd;
      }

      return resultClippedValue;
   }

   /**
    * Method description
    *
    *
    * @param ycbcrColor
    *
    * @return
    */
   public static int[] convertYcbcrToRgb(int[] ycbcrColor) {
      int[]  returnRgbColor = new int[3];
      int    yLuma          = ycbcrColor[0];
      double cRed           = 0.299d;
      double cGreen         = 0.587d;
      double cBlue          = 0.114d;

      /*
       * DoubleMatrix conversionMatrix = new DoubleMatrix(new double[][] {
       *  { 1d, 0d, 1.4d }, { 1d, -0.343d, -0.711d }, { 1d, 1.765d, 0d }
       * });
       * DoubleMatrix yCbCrScalar      = new DoubleMatrix(new double[] { ycbcrColor[0], (ycbcrColor[1] - 128),
       *                                                               (ycbcrColor[2] - 128) });
       * DoubleMatrix rgbColor = conversionMatrix.mmul(yCbCrScalar);
       *
       * for (int index = 0; index < returnRgbColor.length; index++) {
       *  returnRgbColor[index] = (int) clipValue(rgbColor.toArray()[index], 0, 255);
       * }
       */
      double red   = ycbcrColor[0] + (1.40210d * (ycbcrColor[2] - 128));
      double green = ycbcrColor[0] - (0.34414 * (ycbcrColor[1] - 128)) - (0.71414 * (ycbcrColor[2] - 128));
      double blue  = ycbcrColor[0] + (1.77180 * (ycbcrColor[1] - 128));

      returnRgbColor[0] = (int) Math.round(clipValue(red, 0, 255));
      returnRgbColor[1] = (int) Math.round(clipValue(green, 0, 255));;
      returnRgbColor[2] = (int) Math.round(clipValue(blue, 0, 255));;

      /*
       * double red   = (cRed * (2 - (2 * cRed))) + yLuma;
       * double blue  = (cBlue * (2 - (2 * cBlue))) + yLuma;
       * double green = (yLuma - (cBlue * blue) - (cRed * red)) / cGreen;
       *
       * returnRgbColor[0] = (int) Math.round(clipValue(red + 128, 0, 255));
       * returnRgbColor[1] = (int) Math.round(clipValue(green + 128, 0, 255));
       * returnRgbColor[2] = (int) Math.round(clipValue(blue + 128, 0, 255));
       */
      return returnRgbColor;
   }

   /**
    * Divide the number with divisor and return the divided number. In case there is
    * remain doing the modulus then 1 is added to the divided number.
    *
    *
    * @param number
    * @param divisor
    *
    * @return
    */
   public static int divideWithRoundUp(int number, int divisor) {
      int result    = number / divisor;
      int addNumber = (number % divisor == 0)
                      ? 0
                      : 1;    // set to 1 if remainder is greater then zero, 0 otherwise

      result = result + addNumber;

      return result;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get column coordinate from long type point.
    *
    *
    * @param aPoint long representation of point.
    *
    * @return returns column coordinate.
    */
   public static int getColumn(long aPoint) {
      return (int) aPoint;
   }

   /**
    * Method description
    *
    *
    * @param valuesMatrix
    * @param quantizationMatrix
    *
    * @return
    */
   public static int[][] getDequantizedMatrix(int[][] valuesMatrix, int[][] quantizationMatrix) {
      int[][] returnMatrix = new int[valuesMatrix.length][valuesMatrix[0].length];

      for (int row = 0; row < valuesMatrix.length; row++) {
         for (int column = 0; column < valuesMatrix[0].length; column++) {
            returnMatrix[row][column] = valuesMatrix[row][column] * quantizationMatrix[row][column];
         }
      }

      return returnMatrix;
   }

   /**
    * Get maximum number for number of bits.
    *
    *
    * @param noOfBits
    *
    * @return maximum number.
    */
   public static int getMaxNumber(int noOfBits) {
      int returnNumber = 0;

      for (int length = 0; length < noOfBits; length++) {
         returnNumber = (length > 0)
                        ? (returnNumber << 1) | 1
                        : (returnNumber | 1);
      }

      return returnNumber;
   }

   /**
    * Method description
    *
    *
    * @param bits
    *
    * @return
    */
   public static Long getNumberFromHuffmanValueEncoding(byte[] bits) {
      Long returnValue = 0L;

      if ((bits != null) && (bits.length > 0)) {
         int     maxNumber    = getMaxNumber(bits.length);
         boolean isNegative   = bits[0] == 0;
         byte[]  reversedBits = BitUtils.reverseBits(bits);
         long    value        = BitUtils.convertBitsToNumber(reversedBits, BitNumbering.LSB_FIRST, Type.SHORT);

         returnValue = isNegative
                       ? value - maxNumber
                       : value;
      }

      return returnValue;
   }

   /**
    * Code row and column coordinates into long type.
    *
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return returns long representation of point.
    */
   public static long getPoint(int aRow, int aColumn) {
      return (((long) aRow) << 32) | aColumn;
   }

   /**
    * Get row coordinate from long type point.
    *
    *
    * @param aPoint long representation of point.
    *
    * @return returns row coordinate.
    */
   public static int getRow(long aPoint) {
      return (int) (aPoint >> 32);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Throws exception with message.
    *
    *
    * @param message
    */
   public static void throwJpegImageExeption(String message) {
      throw new RuntimeException(message);
   }

   /**
    * Throws exception with message.
    *
    *
    *
    * @param exception
    */
   public static void throwJpegImageExeption(Throwable exception) {
      throw new RuntimeException(exception);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param k
    *
    * @return
    */
   private static double getCk(int k) {
      return (k == 0)
             ? (1d / Math.sqrt(2))
             : 1d;
   }

   /**
    * Method description
    *
    *
    * @param x
    * @param y
    * @param dctMatrix
    *
    * @return
    */
   private static double getIDctCoefficient(int x, int y, double[][] dctMatrix) {
      double returnIDctCoeff = 0;
      double sum             = 0;

      for (int u = 0; u < 8; u++) {
         for (int v = 0; v < 8; v++) {
            double c_u   = getCk(u);
            double c_v   = getCk(v);
            double f_uv  = dctMatrix[v][u];
            double cos_u = Math.cos((((2d * x) + 1) * Math.PI * u) / 16d);
            double cos_v = Math.cos((((2d * y) + 1) * Math.PI * v) / 16d);

            sum += (c_u * c_v * f_uv * cos_u * cos_v);
         }
      }

      returnIDctCoeff = 0.25 * sum;

      return returnIDctCoeff;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Initialize Dct matrix.
    *
    */
   private static void initializeDctMatrix() {
      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            if (row == 0) {
               DCT_MATRIX[row][column] = 1.0d / Math.sqrt(8);
            } else {
               double c1 = Math.sqrt(2.0d / 8.0d);
               double c2 = (((2.0d * column) + 1) * row * Math.PI) / 16.0d;

               DCT_MATRIX[row][column] = c1 * Math.cos(c2);
            }
         }
      }
   }

   /**
    * Initialize transpose.
    *
    */
   private static void initializeTransDctMatrix() {
      DoubleMatrix matrix = new DoubleMatrix(DCT_MATRIX);

      TRANS_DCT_MATRIX = matrix.transpose().toArray2();
   }

   /**
    * Initialize array of coordinates used in zig zag algorithm.
    *
    */
   private static void initializeZigZagCoordinates() {
      int numOfElements = JPEG_IMAGE_BLOCK_WIDTH * JPEG_IMAGE_BLOCK_HEIGHT;
      int width         = JPEG_IMAGE_BLOCK_WIDTH - 1;
      int height        = JPEG_IMAGE_BLOCK_HEIGHT - 1;
      int row           = 0;
      int column        = 0;

      for (int i = 0; i < numOfElements; i++) {
         ZIG_ZAG_COORDINATES[i] = getPoint(row, column);

         if ((row == 0) && (column % 2 == 0) && (column != width)) {                                                // top side of the matrix
            column++;
         } else if ((column == 0) && (row % 2 != 0) && (row != height)) {                                           // left side of the matrix
            row++;
         } else if ((column == width) && (row % 2 != 0) && (row != height)) {                                       // right side of matrix
            row++;
         } else if ((row == height) && (column % 2 == 0) && (column != width)) {                                    // bottom side of matrix
            column++;
         } else if ((ZIG_ZAG_DIAGONAL_MATRIX[row][column] % 2 == 0) && ((row != height) || (column != width))) {    // diagonal down until maximal coordinates
            row++;
            column--;
         } else if ((ZIG_ZAG_DIAGONAL_MATRIX[row][column] % 2 != 0) && ((row != height) || (column != width))) {    // diagonal up until maximal coordinates
            row--;
            column++;
         }
      }
   }

   /**
    * Initialize matrix for later zig zag algorithm.
    * All the diagonals in the matrix are numbered.
    *
    */
   private static void initializeZigZagDiagonalMatrix() {
      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         byte elemValue = (byte) (row + 1);

         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            ZIG_ZAG_DIAGONAL_MATRIX[row][column] = elemValue;
            elemValue++;
         }
      }
   }
}
