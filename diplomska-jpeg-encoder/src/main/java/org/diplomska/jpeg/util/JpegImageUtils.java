package org.diplomska.jpeg.util;

import org.jblas.DoubleMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * This class contains only util methods.
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public class JpegImageUtils {

   /** Field description */
   private static final int ENCODED_BIT_CODED_REPRESENTATION_LENGTH_POSITION = 16;

   /** Field description */
   private static final int ENCODED_NUMBER_OF_ZEROES_POSITION = 20;

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegImageUtils.class);

   /** Maximal columns in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_WIDTH = 8;

   /** Maximal rows in jpeg image block */
   public static final short JPEG_IMAGE_BLOCK_HEIGHT = 8;

   /** Timing logger */
   public static final Logger TIMING_LOGGER = LoggerFactory.getLogger(org.perf4j.StopWatch.DEFAULT_LOGGER_NAME);

   /** 2D discrete cosine transform multiplication matrix */
   private static final DoubleMatrix DCT_MULTIPLICATION_MATRIX = new DoubleMatrix(JPEG_IMAGE_BLOCK_HEIGHT,
                                                                                  JPEG_IMAGE_BLOCK_WIDTH);

   /** Cosine multiplication in DCT */
   private static final double[][][][] DCT_COSINE_MULTIPLICATION_MATRIX =
      new double[JPEG_IMAGE_BLOCK_HEIGHT][JPEG_IMAGE_BLOCK_WIDTH][JPEG_IMAGE_BLOCK_HEIGHT][JPEG_IMAGE_BLOCK_WIDTH];

   /** Luminance quantization matrix. */
   private static final double[][] LUMINANCE_QUANTIZATION_MATRIX_RAW = new double[][] {
      { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 }, { 14, 13, 16, 24, 40, 57, 69, 56 },
      { 14, 17, 22, 29, 51, 87, 80, 62 }, { 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
      { 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 }
   };

   /** Chrominance quantization matrix. */
   private static final double[][] CHROMINANCE_QUANTIZATION_MATRIX_RAW = new double[][] {
      { 17, 18, 24, 47, 99, 99, 99, 99 }, { 18, 21, 26, 66, 99, 99, 99, 99 }, { 24, 26, 56, 99, 99, 99, 99, 99 },
      { 47, 66, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
      { 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }
   };

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

   /** Transposed discrete cosine transform multiplication matrix */
   private static DoubleMatrix fDctMultiplicationMatrixTransposed;

   //~--- static initializers -------------------------------------------------

   static {
      initializeCosineMatrixForSlowDct();
      initializeCosineMatrixForDct();
      initializeZigZagDiagonalMatrix();
      initializeZigZagCoordinates();
   }

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor because this class is a util class.
    *
    */
   private JpegImageUtils() {}

   //~--- methods -------------------------------------------------------------

   /**
    *          Calculate Discrete Cosine Transform.
    *
    *
    *          @param aMatrix color values of one color component (YCrCb)
    *
    *          @return returns calculated DCT matrix.
    */
   public static DoubleMatrix calculateDct(DoubleMatrix aMatrix) {
      DoubleMatrix resultDctMatrix;

      resultDctMatrix = DCT_MULTIPLICATION_MATRIX.mmul(aMatrix);
      resultDctMatrix = resultDctMatrix.mmul(fDctMultiplicationMatrixTransposed);

      return resultDctMatrix;
   }

   /**
    * Calculate DCT according to formula:
    *
    * F(u,v) = 1/4 * C(u) * C(v) * (sum(x: 0 -> 7, sum(y: 0 -> 7, S(u, v, x, y))))
    * where
    * S(u, v, x, y) = f(x, y) * cos((Pi * (2 * x + 1) * u) / 16) * cos((Pi * (2 * y + 1) * v) / 16)
    *
    * @param aColorComponentMatrix
    *
    * @return
    */
   public static DoubleMatrix calculateExtremeSlowDct(DoubleMatrix aColorComponentMatrix) {
      DoubleMatrix resultDctMatrix = new DoubleMatrix(aColorComponentMatrix.getRows(),
                                                      aColorComponentMatrix.getColumns());
      int    pixelRow           = 0;
      int    pixelColumn        = 0;
      int    resultRow          = 0;
      int    resultColumn       = 0;
      double invSquareRootOfTwo = 1 / Math.sqrt(2);
      double invFour            = 0.25;    // 1/4

      for (int u = 0; u < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; u++) {
         resultRow = u;

         for (int v = 0; v < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; v++) {
            resultColumn = v;

            double cu  = (u == 0)
                         ? invSquareRootOfTwo
                         : 1;
            double cv  = (v == 0)
                         ? invSquareRootOfTwo
                         : 1;
            double sum = 0;

            for (int x = 0; x < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; x++) {
               pixelRow = x;

               for (int y = 0; y < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; y++) {
                  pixelColumn = y;

                  double cos_xu              = Math.cos((Math.PI * ((2 * x) + 1) * u) / 16);
                  double cos_yv              = Math.cos((Math.PI * ((2 * y) + 1) * v) / 16);
                  double colorComponentValue = aColorComponentMatrix.get(pixelRow, pixelColumn);

                  sum += colorComponentValue * cos_xu * cos_yv;
               }    // for y
            }       // for x

            sum = invFour * cu * cv * sum;
            resultDctMatrix.put(resultRow, resultColumn, sum);
         }          // for v
      }             // for u

      return resultDctMatrix;
   }

   /**
    * Calculate DCT matrix with slow algorithm.
    *
    *
    * @param aMatrix input matrix of one color component.
    *
    * @return returns DCT matrix.
    */
   public static DoubleMatrix calculateSlowDctMatrix(DoubleMatrix aMatrix) {
      DoubleMatrix resultDctMatrix = new DoubleMatrix(JPEG_IMAGE_BLOCK_WIDTH, JPEG_IMAGE_BLOCK_HEIGHT);
      double       constantCoeff   = 1 / Math.sqrt(2d * JPEG_IMAGE_BLOCK_WIDTH);

      for (int i = 0; i < JPEG_IMAGE_BLOCK_WIDTH; i++) {
         for (int j = 0; j < JPEG_IMAGE_BLOCK_HEIGHT; j++) {
            double sum = 0.0;

            for (int x = 0; x < JPEG_IMAGE_BLOCK_WIDTH; x++) {
               for (int y = 0; y < JPEG_IMAGE_BLOCK_HEIGHT; y++) {

                  // sum = sum + (aMatrix[x][y] * fCosineMultiplicationInDct[x][i][y][j]);
                  sum = sum + (aMatrix.get(x, y) * DCT_COSINE_MULTIPLICATION_MATRIX[x][i][y][j]);
               }
            }

            double cI    = getDctCoefficient(i);
            double cJ    = getDctCoefficient(j);
            double dctIJ = constantCoeff * cI * cJ * sum;

            resultDctMatrix.put(i, j, dctIJ);
         }
      }

      return resultDctMatrix;
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
    * Convert bits to byte.
    *
    * @param aBitsArray
    *
    * @return
    */
   public static byte convertBitsToByte(byte[] aBitsArray) {
      byte returnByte = 0;
      byte position   = 7;

      // It can be only 8 bits in max length
      int bitArrayLength = Math.min(8, aBitsArray.length);

      for (int index = 0; index < bitArrayLength; index++) {
         if ((aBitsArray[index] != 0) && (aBitsArray[index] != 1)) {
            throwJpegImageEncoderExeption("Value ob bits array should be 0 or 1.");
         }

         returnByte = (byte) (returnByte | (aBitsArray[index] << position));
         position--;
      }

      return returnByte;
   }

   /**
    * Convert array of bits to array of bytes.
    *
    *
    * @param bitsArray
    *
    * @return
    */
   public static byte[] convertBitsToBytes(byte[] bitsArray) {
      ByteArrayOutputStream returnOutputStream = new ByteArrayOutputStream();
      byte                  mSBPosition        = 7;
      byte                  position           = mSBPosition;
      byte                  currentByte        = 0;

      for (int index = 0; index < bitsArray.length; index++) {
         if ((bitsArray[index] != 0) && (bitsArray[index] != 1)) {
            throwJpegImageEncoderExeption("Value ob bits array should be 0 or 1.");
         }

         currentByte = (byte) (currentByte | (bitsArray[index] << position));

         if (position == 0) {
            returnOutputStream.write(currentByte);
            position    = mSBPosition;
            currentByte = 0;
         } else {
            position--;
         }
      }

      if (position != mSBPosition) {
         returnOutputStream.write(currentByte);
      }

      return returnOutputStream.toByteArray();
   }

   /**
    * Convert bits to int.
    *
    * @param aBitsArray
    *
    * @return
    */
   public static int convertBitsToInt(byte[] aBitsArray) {
      int returnInt = 0;
      int position  = aBitsArray.length - 1;

      // It can be only 8 bits in max length
      int bitArrayLength = Math.min(32, aBitsArray.length);

      for (int index = 0; index < bitArrayLength; index++) {
         if ((aBitsArray[index] != 0) && (aBitsArray[index] != 1)) {
            throwJpegImageEncoderExeption("Value ob bits array should be 0 or 1.");
         }

         returnInt = returnInt | (aBitsArray[index] << position);
         position--;
      }

      return returnInt;
   }

   /**
    * Convert bits to unsigned byte.
    *
    * @param aBitsArray
    *
    * @return
    */
   public static short convertBitsToUnsignedByte(byte[] aBitsArray) {
      short returnByte = 0;
      byte  position   = 7;

      // It can be only 8 bits in max length
      int bitArrayLength = Math.min(8, aBitsArray.length);

      for (int index = 0; index < bitArrayLength; index++) {
         if ((aBitsArray[index] != 0) && (aBitsArray[index] != 1)) {
            throwJpegImageEncoderExeption("Value ob bits array should be 0 or 1.");
         }

         returnByte = (short) (returnByte | (aBitsArray[index] << position));
         position--;
      }

      return returnByte;
   }

   /**
    * Converts list of object byte to array of primitive byte.
    *
    *
    * @param list
    *
    * @return
    */
   public static byte[] convertByteListToByteArray(List<Byte> list) {
      byte[] returnArray = new byte[list.size()];

      for (int index = 0; index < list.size(); index++) {
         returnArray[index] = list.get(index).byteValue();
      }

      return returnArray;
   }

   /**
    * Convert bytes array to bits array;
    *
    *
    * @param byteArray
    *
    * @return
    */
   public static byte[] convertBytesToBits(byte[] byteArray) {
      byte[] returnBitsArray   = new byte[byteArray.length * 8];
      int    bitsArrayPosition = 0;

      for (int index = 0; index < byteArray.length; index++) {
         byte currentByte = byteArray[index];

         for (byte bitPosition = 7; bitPosition >= 0; bitPosition--) {
            returnBitsArray[bitsArrayPosition] = (byte) ((currentByte >> bitPosition) & 1);
            bitsArrayPosition++;
         }
      }

      return returnBitsArray;
   }

   /**
    * Convert encoded value with minimum size in bits into a normal value used for jpeg image block.
    *
    *
    * @param aNoOfBits
    * @param aEncodedZrlcNumber
    *
    * @return
    */
   public static int convertFromEncodedValueToCoefficientValue(int aNoOfBits, int aEncodedZrlcNumber) {
      int maxNumber           = getMaxNumber(aNoOfBits);
      int returnDecodedNumber = 0;

      if (aNoOfBits > 0) {
         boolean isNegativeNumber = ((aEncodedZrlcNumber >> (aNoOfBits - 1)) | 0) == 0;

         returnDecodedNumber = isNegativeNumber
                               ? (aEncodedZrlcNumber - maxNumber)
                               : aEncodedZrlcNumber;
      } else if (aNoOfBits < 0) {
         throwJpegImageEncoderExeption("Cannot convert a number with negative number of bits.");
      }

      return returnDecodedNumber;
   }

   /**
    * Converts list of object integer to array of primitive int.
    *
    *
    * @param list
    *
    * @return
    */
   public static int[] convertIntegerListToIntArray(List<Integer> list) {
      int[] returnArray = new int[list.size()];

      for (int index = 0; index < list.size(); index++) {
         returnArray[index] = list.get(index).intValue();
      }

      return returnArray;
   }

   /**
    * Method description
    *
    *
    *
    *
    * @param aBitEncodedRepresentation
    *
    * @return
    *
    */
   public static int decodeFromBitCodedRepresentation(byte[] aBitEncodedRepresentation) {
      int returnNumber = 0;

      if (aBitEncodedRepresentation.length == 0) {
         return returnNumber;
      }

      boolean isNegativeNumber = (aBitEncodedRepresentation[0] == 0);

      for (int index = 0; index < aBitEncodedRepresentation.length; index++) {
         int bitPosition = aBitEncodedRepresentation.length - 1 - index;

         returnNumber = returnNumber | (aBitEncodedRepresentation[index] << bitPosition);
      }

      if (isNegativeNumber) {
         returnNumber = returnNumber - getMaxNumber(aBitEncodedRepresentation.length);
      }

      return returnNumber;
   }

   /**
    * Decodes the encoded number to RunLengthCodingStructure and returns it.
    *
    * @param aEncodedZrlcNumber
    *
    * @return
    */
   public static RunLengthCodingStructure decodeFromEncodedZeroRunLengthCoding(int aEncodedZrlcNumber) {
      RunLengthCodingStructure returnStructure = new RunLengthCodingStructure();

      returnStructure.fNumberOfPreceedingZeroes = (aEncodedZrlcNumber & 0xF00000) >> ENCODED_NUMBER_OF_ZEROES_POSITION;
      returnStructure.fNumberOfBits             = ((aEncodedZrlcNumber & 0xF0000)
                                                   >> ENCODED_BIT_CODED_REPRESENTATION_LENGTH_POSITION);
      returnStructure.fEncodedZrlcNumber = (aEncodedZrlcNumber & 0xFFFF);

      return returnStructure;
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

   /**
    * Get minimum size in bits for the input number. Bits are represented
    * as bytes which can have value 0 or 1. If the most significant bit is zero than the
    * number is negative - in this case the first element. In case number is 0 then
    * empty byte array is returned.
    *
    *
    * @param aNumber
    *
    * @return returns byte array which is bit representation of number
    */
   public static byte[] encodeToBitCodedRepresentation(int aNumber) {
      int    bitCodeRepresentationLength = lengthOfBitCodedRepresentation(aNumber);
      byte[] returnBitsArray             = new byte[bitCodeRepresentationLength];

      // in case number is less than 0 the we need to substract the number from max number
      int numberToEncode = (aNumber >= 0)
                           ? aNumber
                           : getMaxNumber(bitCodeRepresentationLength) + aNumber;

      for (int index = 0; index < bitCodeRepresentationLength; index++) {

         // get the bit at a position of number to encode
         byte bitAtPosition = (byte) ((numberToEncode >> index) & 1);
         int  bitPosition   = bitCodeRepresentationLength - 1 - index;

         returnBitsArray[bitPosition] = bitAtPosition;
      }

      return returnBitsArray;
   }

   /**
    * Encode zero length coding into an integer number.
    * Returns a number which is composed of the following elements:
    * 0  - 16 bits  - zero run length value in bits
    * 16 - 20 bits   - length of the bits for encoded number
    * 20 - 24 bits   - number of zeroes
    *
    * @param aNumberOfZeroes
    * @param aNumber
    *
    * @return
    */
   public static int encodeToZeroRunLengthCoding(int aNumberOfZeroes, int aNumber) {
      int    returnNumber                 = 0;
      byte[] bitCodedRepresentationArray  = encodeToBitCodedRepresentation(aNumber);
      int    bitCodedRepresentationNumber = 0;

      returnNumber = (aNumberOfZeroes << ENCODED_NUMBER_OF_ZEROES_POSITION)
                     | (bitCodedRepresentationArray.length << ENCODED_BIT_CODED_REPRESENTATION_LENGTH_POSITION);

      for (int index = 0; index < bitCodedRepresentationArray.length; index++) {
         int bitPosition = bitCodedRepresentationArray.length - 1 - index;

         bitCodedRepresentationNumber = bitCodedRepresentationNumber
                                        | (bitCodedRepresentationArray[index] << bitPosition);
      }

      returnNumber = returnNumber | bitCodedRepresentationNumber;

      return returnNumber;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Default chrominance quantization matrix.
    *
    *
    * @return
    */
   public static double[][] getChrominanceQuantizationMatrix() {
      return CHROMINANCE_QUANTIZATION_MATRIX_RAW;
   }

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
    * Get DCT coefficient.
    *
    *
    * @param x
    *
    * @return returns DCT coefficient.
    */
   public static double getDctCoefficient(int x) {
      double resultCoeficient = 1.0;

      if (x == 0) {
         resultCoeficient = 0.70710678118654752440084436210485;    // 1/sqrt(2)
      }

      return resultCoeficient;
   }

   /**
    * Method returns coordinates of image block in the image. Default dimension of block is 8.
    *
    *
    * @param aCurrentPoint current pixel location.
    *
    * @return coordinates of the jpeg image block.
    */
   public static long getJpegImageBlockCoordinates(long aCurrentPoint) {

      // Here coordinating systems starts at (1,1), because the conversion to jpeg image block
      // coordinates can be easily calculated. At the end 1 is substracted in result to convert
      // it to coordinating system which starts with (0, 0)
      int currentRow        = getRow(aCurrentPoint) + 1;    // X coordinate needs +1 because of the roundingUp
      int currentColumn     = getColumn(aCurrentPoint) + 1;    // Y coordinate needs +1 because of the roundingUp
      int jpegImageBlockRow = divideWithRoundUp(currentRow, JPEG_IMAGE_BLOCK_HEIGHT);    // current row for jpeg image blocks
      int jpegImageBlockColumn = divideWithRoundUp(currentColumn, JPEG_IMAGE_BLOCK_WIDTH);    // current column for jpeg image blocks

      // 1 was substracted because of conversion from coordinate (1, 1) to (N, N) into (0,0) to (N-1, N-1)
      return getPoint(jpegImageBlockRow - 1, jpegImageBlockColumn - 1);
   }

   /**
    * Default luminance quantization matrix.
    *
    *
    * @return
    */
   public static double[][] getLuminanceQuantizationMatrix() {
      return LUMINANCE_QUANTIZATION_MATRIX_RAW;
   }

   /**
    * Get maximum number for number of bits.
    *
    *
    * @param aNoOfBits
    *
    * @return maximum number.
    */
   public static int getMaxNumber(int aNoOfBits) {
      int returnNumber = 0;

      for (int length = 0; length < aNoOfBits; length++) {
         returnNumber = (length > 0)
                        ? (returnNumber << 1) | 1
                        : (returnNumber | 1);
      }

      return returnNumber;
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
    * Method description
    *
    *
    * @param aNumber
    *
    * @return
    */
   public static int lengthOfBitCodedRepresentation(int aNumber) {
      return (aNumber != 0)
             ? Integer.toBinaryString(Math.abs(aNumber)).length()
             : 0;
   }

   /**
    * Throws exception with message.
    *
    *
    * @param message
    */
   public static void throwJpegImageEncoderExeption(String message) {
      throw new RuntimeException(message);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param index
    *
    * @return
    */
   protected static int[] getCoordinates(int index) {

      // long  startTime         = System.nanoTime();
      int[] resultCoordinates = new int[4];
      int   localIndex        = index + 1;
      int   divider0          = JPEG_IMAGE_BLOCK_WIDTH * JPEG_IMAGE_BLOCK_HEIGHT * JPEG_IMAGE_BLOCK_WIDTH;
      int   divider1          = JPEG_IMAGE_BLOCK_WIDTH * JPEG_IMAGE_BLOCK_HEIGHT;
      int   divider2          = JPEG_IMAGE_BLOCK_WIDTH;

      // calculation of first coordinate
      int coordinate0 = divideWithRoundUp(localIndex, divider0);
      int rest0       = (localIndex % divider0 == 0)
                        ? divider0
                        : (localIndex % divider0);

      // calculation of second coordinate
      int coordinate1 = divideWithRoundUp(rest0, divider1);
      int rest1       = (rest0 % divider1 == 0)
                        ? divider1
                        : (rest0 % divider1);

      // calculation of third coordinate
      int coordinate2 = divideWithRoundUp(rest1, divider2);
      int rest2       = (rest1 % divider2 == 0)
                        ? divider2
                        : (rest1 % divider2);

      // set the fourth coordinate
      int coordinate3 = rest2;

      resultCoordinates[0] = coordinate0 - 1;
      resultCoordinates[1] = coordinate1 - 1;
      resultCoordinates[2] = coordinate2 - 1;
      resultCoordinates[3] = coordinate3 - 1;

      // long endTime = System.nanoTime();
      // System.out.println("GetCoordinates [" + (endTime - startTime) + "]");
      return resultCoordinates;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Initialize DCT multiplication matrix.
    *
    */
   private static void initializeCosineMatrixForDct() {
      int k = 0;

      for (int row_i = 0; row_i < JPEG_IMAGE_BLOCK_HEIGHT; row_i++) {
         for (int column_j = 0; column_j < JPEG_IMAGE_BLOCK_WIDTH; column_j++) {
            double dctEntry = Math.sqrt(2d) / 2;

            if (k != 0) {
               int piMultiplier = row_i + (k * 2 * column_j);

               dctEntry = Math.cos((piMultiplier * Math.PI) / 16d);
            }

            DCT_MULTIPLICATION_MATRIX.put(row_i, column_j, dctEntry * 0.5d);
         }

         k++;
      }

      fDctMultiplicationMatrixTransposed = DCT_MULTIPLICATION_MATRIX.transpose();
   }

   /**
    * Initialization of DCT cosine multiplication matrix - cos(((2x+1)*i*pi)/2*N)*cos(((2y+1)*j*pi)/2*N).
    * This is made only once and is used in the straightforward method of calculation of DCT.
    */
   private static final void initializeCosineMatrixForSlowDct() {
      for (int i = 0; i < JPEG_IMAGE_BLOCK_WIDTH; i++) {
         for (int j = 0; j < JPEG_IMAGE_BLOCK_HEIGHT; j++) {
            for (int x = 0; x < JPEG_IMAGE_BLOCK_WIDTH; x++) {
               for (int y = 0; y < JPEG_IMAGE_BLOCK_HEIGHT; y++) {
                  double argumentForFirstCos  = ((2 * x + 1) * i * Math.PI) / (2 * JPEG_IMAGE_BLOCK_WIDTH);
                  double argumentForSecondCos = ((2 * y + 1) * j * Math.PI) / (2 * JPEG_IMAGE_BLOCK_HEIGHT);

                  DCT_COSINE_MULTIPLICATION_MATRIX[x][i][y][j] = Math.cos(argumentForFirstCos)
                                                                 * Math.cos(argumentForSecondCos);
               }
            }
         }
      }
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

      assert row == height : "Row = " + row + " [" + height + "]";
      assert column == width : "Column = " + column + " [" + width + "]";
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
