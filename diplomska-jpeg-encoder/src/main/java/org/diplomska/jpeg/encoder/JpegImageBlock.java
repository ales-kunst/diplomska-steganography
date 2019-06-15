package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.util.JpegImageUtils;
import org.diplomska.jpeg.util.RgbColor;
import org.diplomska.jpeg.util.RunLengthCodingStructure;

import org.jblas.DoubleMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Jpeg image block.
 *
 *
 * @version        1.0, 15/04/03
 * @author         Ales Kunst
 */
public class JpegImageBlock {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegImageBlock.class);

   //~--- fields --------------------------------------------------------------

   /** Field description. */
   private int fJpegBlockColumn;

   /** Field description. */
   private int fJpegBlockRow;

   /** Coordinates of the last element. In the format row, column. */
   private long fLastAddedElementCoordinates;

   /** Y Luma matrix for calculation */
   private DoubleMatrix fMatrixYLumaForCalculation;

   /** Cb chroma matrix for calculation */
   private DoubleMatrix fMatrixCbChromaForCalculation;

   /** Cr chroma matrix for calculation */
   private DoubleMatrix fMatrixCrChromaForCalculation;

   /** Zig zag array for Y luma */
   private int[] fZigZagRlcEncodedYLumaArray;

   /** Zig zag array for Cr chroma */
   private int[] fZigZagRlcEncodedCrChromaArray;

   /** Zig zag array for Cb chroma */
   private int[] fZigZagRlcEncodedCbChromaArray;

   /** Matrix of YCrCb colors */
   private int[][] fColorMatrix;

   //~--- constructors --------------------------------------------------------

   /**
    *    Default constructor.
    *
    *
    *
    *
    * @param aBlockRow
    * @param aBlockColumn
    */
   public JpegImageBlock(int aBlockRow, int aBlockColumn) {
      fJpegBlockRow                  = aBlockRow;
      fJpegBlockColumn               = aBlockColumn;
      fColorMatrix                   =
         new int[JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT][JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH];
      fZigZagRlcEncodedYLumaArray    = new int[0];    // empty array
      fZigZagRlcEncodedCrChromaArray = new int[0];    // empty array
      fZigZagRlcEncodedCbChromaArray = new int[0];    // empty array
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Put color of a pixel into this image block.
    *
    *
    * @param aPixelCoordinates coordinates of the pixel in the image.
    * @param aRgbColor RGB color.
    *
    * @return returns local coordinates in the jpeg image block.
    */
   public long addRgbColor(long aPixelCoordinates, int aRgbColor) {
      int startPixelColumn   = fJpegBlockColumn * JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH;
      int startPixelRow      = fJpegBlockRow * JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT;
      int currentPixelRow    = JpegImageUtils.getRow(aPixelCoordinates);
      int currentpixelColumn = JpegImageUtils.getColumn(aPixelCoordinates);
      int localRow           = currentPixelRow - startPixelRow;
      int localColumn        = currentpixelColumn - startPixelColumn;

      fLastAddedElementCoordinates        = JpegImageUtils.getPoint(localRow, localColumn);
      fColorMatrix[localRow][localColumn] = aRgbColor;

      return fLastAddedElementCoordinates;
   }

   /**
    * Frees memory.
    *
    */
   public void clearCalculationMatrices() {
      fMatrixYLumaForCalculation    = null;
      fMatrixCbChromaForCalculation = null;
      fMatrixCrChromaForCalculation = null;
   }

   /**
    * Method description
    *
    */
   public void clearZigZagArrays() {
      fZigZagRlcEncodedYLumaArray    = null;
      fZigZagRlcEncodedCbChromaArray = null;
      fZigZagRlcEncodedYLumaArray    = null;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get Matrix for Y Cr Cb Color
    *
    *
    * @return returns YCrCb matrix.
    */
   public int[][] getColorMatrix() {
      return fColorMatrix;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getColumn() {
      return fJpegBlockColumn;
   }

   /**
    * Return combined zig zag arrays as array of bits.
    *
    *
    * @return bits of combined zig zag arrays.
    */
   public byte[] getCombinedZigZagArrayAsBits() {
      byte[] bitsOfCombinedZigZagArray = convertZigZagEncodedArrayToBitsArray(getCombinedZigZagArrays());

      return bitsOfCombinedZigZagArray;
   }

   /**
    * Get last added element column [0..7]
    *
    *
    * @return
    */
   public int getLastLocalColumn() {
      return JpegImageUtils.getColumn(fLastAddedElementCoordinates);
   }

   /**
    * Get last added element row [0..7]
    *
    *
    * @return
    */
   public int getLastLocalRow() {
      return JpegImageUtils.getRow(fLastAddedElementCoordinates);
   }

   /**
    * Getter for Dct Cb color.
    *
    *
    * @return
    */
   public DoubleMatrix getMatrixCbChromaForCalculation() {
      return fMatrixCbChromaForCalculation;
   }

   /**
    * Getter for Dct Cr color.
    *
    *
    * @return
    */
   public DoubleMatrix getMatrixCrChromaForCalculation() {
      return fMatrixCrChromaForCalculation;
   }

   /**
    * Getter for Dct Y color.
    *
    *
    * @return
    */
   public DoubleMatrix getMatrixYLumaForCalculation() {
      return fMatrixYLumaForCalculation;
   }

   /**
    * Get rounded Cd matrix.
    *
    *
    * @return
    */
   public int[][] getRoundedMatrixCbChromaForCalculation() {
      return roundMatrix(fMatrixCbChromaForCalculation);
   }

   /**
    * Get rounded Cr matrix.
    *
    *
    * @return
    */
   public int[][] getRoundedMatrixCrChromaForCalculation() {
      return roundMatrix(fMatrixCrChromaForCalculation);
   }

   /**
    * Get rounded Y matrix.
    *
    *
    * @return
    */
   public int[][] getRoundedMatrixYLumaForCalculation() {
      return roundMatrix(fMatrixYLumaForCalculation);
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getRow() {
      return fJpegBlockRow;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int[] getZigZagRlcEncodedCbChromaArray() {
      return fZigZagRlcEncodedCbChromaArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int[] getZigZagRlcEncodedCrChromaArray() {
      return fZigZagRlcEncodedCrChromaArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int[] getZigZagRlcEncodedYLumaArray() {
      return fZigZagRlcEncodedYLumaArray;
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Sets Dc coefficient for Cb component.
    *
    *
    * @param aValue
    *
    * @return
    */
   public int setDcCoefficientInZigZagRlcCbChromaArray(int aValue) {
      fZigZagRlcEncodedCbChromaArray[0] = aValue;

      return fZigZagRlcEncodedCbChromaArray[0];
   }

   /**
    * Sets Dc coefficient for Cr component.
    *
    *
    * @param aValue
    *
    * @return
    */
   public int setDcCoefficientInZigZagRlcCrChromaArray(int aValue) {
      fZigZagRlcEncodedCrChromaArray[0] = aValue;

      return fZigZagRlcEncodedCrChromaArray[0];
   }

   /**
    * Sets Dc coefficient for Luma component.
    *
    *
    * @param aValue
    *
    * @return
    */
   public int setDcCoefficientInZigZagRlcYLumaArray(int aValue) {
      fZigZagRlcEncodedYLumaArray[0] = aValue;

      return fZigZagRlcEncodedYLumaArray[0];
   }

   /**
    * Setter method.
    *
    *
    *
    * @param aMatrixCbChromaForCalculation
    */
   public void setMatrixCbChromaForCalculation(DoubleMatrix aMatrixCbChromaForCalculation) {
      fMatrixCbChromaForCalculation = aMatrixCbChromaForCalculation;
   }

   /**
    * Method description
    *
    *
    *
    * @param aMatrixCrChromaForCalculation
    */
   public void setMatrixCrChromaForCalculation(DoubleMatrix aMatrixCrChromaForCalculation) {
      fMatrixCrChromaForCalculation = aMatrixCrChromaForCalculation;
   }

   /**
    * Method description
    *
    *
    *
    * @param aMatrixYLumaForCalculation
    */
   public void setMatrixYLumaForCalculation(DoubleMatrix aMatrixYLumaForCalculation) {
      fMatrixYLumaForCalculation = aMatrixYLumaForCalculation;
   }

   /**
    * Set matrices for calculation.
    *
    *
    * @param aYLumaMatrixForCalculation
    * @param aCrChromaMatrixForCalculation
    * @param aCbChromaMatrixForCalculation
    */
   public void setYCrCbMatrixForCalculation(double[][] aYLumaMatrixForCalculation,
                                            double[][] aCrChromaMatrixForCalculation,
                                            double[][] aCbChromaMatrixForCalculation) {

      // initialize matrices for calculation
      setMatrixYLumaForCalculation(new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT,
                                                    JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH));
      setMatrixCrChromaForCalculation(new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT,
                                                       JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH));
      setMatrixCbChromaForCalculation(new DoubleMatrix(JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT,
                                                       JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH));

      for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            fMatrixYLumaForCalculation.put(row, column, aYLumaMatrixForCalculation[row][column]);
            fMatrixCrChromaForCalculation.put(row, column, aCrChromaMatrixForCalculation[row][column]);
            fMatrixCbChromaForCalculation.put(row, column, aCbChromaMatrixForCalculation[row][column]);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param fZigZagRlcEncodedCbChromaArray
    */
   public void setZigZagRlcEncodedCbChromaArray(int[] fZigZagRlcEncodedCbChromaArray) {
      this.fZigZagRlcEncodedCbChromaArray = fZigZagRlcEncodedCbChromaArray;
   }

   /**
    * Method description
    *
    *
    * @param fZigZagRlcEncodedCrChromaArray
    */
   public void setZigZagRlcEncodedCrChromaArray(int[] fZigZagRlcEncodedCrChromaArray) {
      this.fZigZagRlcEncodedCrChromaArray = fZigZagRlcEncodedCrChromaArray;
   }

   /**
    * Method description
    *
    *
    * @param fZigZagRlcEncodedYLumaArray
    */
   public void setZigZagRlcEncodedYLumaArray(int[] fZigZagRlcEncodedYLumaArray) {
      this.fZigZagRlcEncodedYLumaArray = fZigZagRlcEncodedYLumaArray;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public String toString() {
      String resultString = "";

      resultString += "Block row: " + fJpegBlockRow + " Block column: " + fJpegBlockColumn + "\n";
      resultString += "Matrix rows: " + JpegImageUtils.getRow(fLastAddedElementCoordinates) + " Matrix columns: "
                      + JpegImageUtils.getColumn(fLastAddedElementCoordinates) + "\n";
      resultString += "Color matrix: \n";

      for (int row = 0; row < fColorMatrix.length; row++) {
         resultString += "[";

         for (int column = 0; column < fColorMatrix[0].length; column++) {
            int rgbColor = fColorMatrix[row][column];

            resultString += "(" + RgbColor.getRed(rgbColor) + ", " + RgbColor.getGreen(rgbColor) + ","
                            + RgbColor.getBlue(rgbColor) + ")";

            if (column < fColorMatrix[0].length - 1) {
               resultString += "; ";
            }
         }

         resultString += "]\n";
      }

      resultString += getZigZagPrintoutString("Y Luma: [ ", " ]\n", fZigZagRlcEncodedYLumaArray);
      resultString += getZigZagPrintoutString("Cb Luma: [ ", " ]\n", fZigZagRlcEncodedCbChromaArray);
      resultString += getZigZagPrintoutString("Cr Luma: [ ", " ]\n", fZigZagRlcEncodedCrChromaArray);

      return resultString;
   }

   /**
    * Convert to bit array size.
    *
    *
    * @param aZigZagEncodedArray
    *
    * @return
    */
   protected byte[] convertZigZagEncodedArrayToBitsArray(int[] aZigZagEncodedArray) {
      ByteArrayOutputStream resultStreamArray = new ByteArrayOutputStream();

      for (int rlcEncodedElement : aZigZagEncodedArray) {
         RunLengthCodingStructure rlcStructure = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(rlcEncodedElement);

         try {
            resultStreamArray.write(rlcStructure.toBits());
         } catch (IOException ioe) {
            JpegImageUtils.throwJpegImageEncoderExeption(ioe.getMessage());
         }
      }

      return resultStreamArray.toByteArray();
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get combined array of all zig zag arrays.
    *
    *
    * @return combined zig zag array.
    */
   protected int[] getCombinedZigZagArrays() {
      List<Integer> resultCombinedZigZagArray = new ArrayList<Integer>();

      for (int index = 0; index < fZigZagRlcEncodedYLumaArray.length; index++) {
         resultCombinedZigZagArray.add(fZigZagRlcEncodedYLumaArray[index]);
      }

      for (int index = 0; index < fZigZagRlcEncodedCbChromaArray.length; index++) {
         resultCombinedZigZagArray.add(fZigZagRlcEncodedCbChromaArray[index]);
      }

      for (int index = 0; index < fZigZagRlcEncodedCrChromaArray.length; index++) {
         resultCombinedZigZagArray.add(fZigZagRlcEncodedCrChromaArray[index]);
      }

      return JpegImageUtils.convertIntegerListToIntArray(resultCombinedZigZagArray);
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected final long getLastAddedElementCoordinates() {
      return fLastAddedElementCoordinates;
   }

   /**
    * Get minimum size in which the zigzag arrays can be encoded
    *
    *
    *
    * @param aZigZagEncodedArray
    * @return
    */
   protected int getMinimumByteArraySize(int[] aZigZagEncodedArray) {
      int returnBitsSizeSum = 0;

      for (int encodedRlc : aZigZagEncodedArray) {
         RunLengthCodingStructure rlcStructure = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(encodedRlc);

         // the first part has 8 bits and the second part has size fNumberOfBits
         returnBitsSizeSum = returnBitsSizeSum + 8 + rlcStructure.fNumberOfBits;
      }

      return returnBitsSizeSum;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aMatrix
    *
    * @return
    */
   protected int[][] roundMatrix(DoubleMatrix aMatrix) {
      int[][] resultRoundedMatrix =
         new int[JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT][JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH];

      for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            resultRoundedMatrix[row][column] = (int) Math.round(aMatrix.get(row, column));
         }
      }

      return resultRoundedMatrix;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aPreText
    * @param aPostText
    * @param aZigZagArray
    *
    * @return
    */
   private String getZigZagPrintoutString(String aPreText, String aPostText, int[] aZigZagArray) {
      String resultString = aPreText;
      int    zigzagIndex  = 0;

      for (int rlcEncodedElement : aZigZagArray) {
         RunLengthCodingStructure rlcStructure = JpegImageUtils.decodeFromEncodedZeroRunLengthCoding(rlcEncodedElement);

         resultString += Integer.toHexString(rlcStructure.fNumberOfPreceedingZeroes).toUpperCase();
         resultString += Integer.toHexString(rlcStructure.fNumberOfBits).toUpperCase();
         resultString += " " + rlcStructure.getCoefficientNumber();

         if (zigzagIndex < (aZigZagArray.length - 1)) {
            resultString += ", ";
         }

         zigzagIndex++;
      }

      resultString += aPostText;

      return resultString;
   }
}
