package org.diplomska.jpeg.encoder;

import org.diplomska.util.JpegImageEncoderUtils;
import org.diplomska.util.structs.JpegImageConstants;
import org.diplomska.util.structs.RunLengthCodingStructure;

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
public class JpegImageBlock implements JpegImageConstants {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegImageBlock.class);

   //~--- fields --------------------------------------------------------------

   /** Field description. */
   private int fJpegBlockColumn;

   /** Field description. */
   private int fJpegBlockRow;

   /** Coordinates of the last element. In the format row, column. */
   private long fLastAddedElementCoordinates;

   /** Zig zag array for Y luma */
   private int[] fZigZagRlcEncodedYLumaArray;

   /** Zig zag array for Cr chroma */
   private int[] fZigZagRlcEncodedCrChromaArray;

   /** Zig zag array for Cb chroma */
   private int[] fZigZagRlcEncodedCbChromaArray;

   /** Field description */
   private int fStartPixelColumn;

   /** Field description */
   private int fStartPixelRow;

   /** Field description */
   private JpegImageContext fJpegImageContext;

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
      fStartPixelRow                 = fJpegBlockRow * JPEG_IMAGE_BLOCK_HEIGHT;
      fStartPixelColumn              = fJpegBlockColumn * JPEG_IMAGE_BLOCK_WIDTH;
      fZigZagRlcEncodedYLumaArray    = new int[0];    // empty array
      fZigZagRlcEncodedCrChromaArray = new int[0];    // empty array
      fZigZagRlcEncodedCbChromaArray = new int[0];    // empty array
   }

   /**
    * Constructs ...
    *
    *
    * @param aBlockRow
    * @param aBlockColumn
    * @param aJpegImageContext
    */
   public JpegImageBlock(int aBlockRow, int aBlockColumn, JpegImageContext aJpegImageContext) {
      this(aBlockRow, aBlockColumn);
      fJpegImageContext = aJpegImageContext;
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
      int currentPixelRow    = JpegImageEncoderUtils.getRow(aPixelCoordinates);
      int currentpixelColumn = JpegImageEncoderUtils.getColumn(aPixelCoordinates);
      int localRow           = currentPixelRow - fStartPixelRow;
      int localColumn        = currentpixelColumn - fStartPixelColumn;

      fLastAddedElementCoordinates = JpegImageEncoderUtils.getPoint(localRow, localColumn);

      return fLastAddedElementCoordinates;
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
    * Method description
    *
    *
    * @param aLocalRow
    * @param aLocalColumn
    *
    * @return
    */
   public int getImageValue(int aLocalRow, int aLocalColumn) {
      int imageRow         = fStartPixelRow + aLocalRow;
      int imageColumn      = fStartPixelColumn + aLocalColumn;
      int resultImageValue = 0;

      resultImageValue = fJpegImageContext.getImageValue(imageRow, imageColumn);

      return resultImageValue;
   }

   /**
    * Get last added element column [0..7]
    *
    *
    * @return
    */
   public int getLastLocalColumn() {
      return JpegImageEncoderUtils.getColumn(fLastAddedElementCoordinates);
   }

   /**
    * Get last added element row [0..7]
    *
    *
    * @return
    */
   public int getLastLocalRow() {
      return JpegImageEncoderUtils.getRow(fLastAddedElementCoordinates);
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public int getQuantizedCbChromaValue(int aRow, int aColumn) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;
      int imageValue  = fJpegImageContext.getImageValue(pixelRow, pixelColumn);

      return JpegImageEncoderUtils.getQuantizedCbChromaValue(imageValue);
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public int getQuantizedCrChromaValue(int aRow, int aColumn) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;
      int imageValue  = fJpegImageContext.getImageValue(pixelRow, pixelColumn);

      return JpegImageEncoderUtils.getQuantizedCrChromaValue(imageValue);
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public int getQuantizedYLumaValue(int aRow, int aColumn) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;
      int imageValue  = fJpegImageContext.getImageValue(pixelRow, pixelColumn);

      return JpegImageEncoderUtils.getQuantizedYLumaValue(imageValue);
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
   public int getStartPixelColumn() {
      return fStartPixelColumn;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getStartPixelRow() {
      return fStartPixelRow;
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
    * Method description
    *
    *
    * @param aQuantizedYLumaMatrix
    * @param aQuantizedCbChromaMatrix
    * @param aQuantizedCrChromaMatrix
    */
   public void setQuantizationMatrices(DoubleMatrix aQuantizedYLumaMatrix, DoubleMatrix aQuantizedCbChromaMatrix,
                                       DoubleMatrix aQuantizedCrChromaMatrix) {
      int imageValue = 0;

      for (int row = 0; row < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         int pixelRow = fStartPixelRow + row;

         for (int column = 0; column < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int  pixelColumn   = fStartPixelColumn + column;
            byte yLumaValue    = (byte) Math.round(aQuantizedYLumaMatrix.get(row, column));
            byte cbChromaValue = (byte) Math.round(aQuantizedCbChromaMatrix.get(row, column));
            byte crChromaValue = (byte) Math.round(aQuantizedCrChromaMatrix.get(row, column));

            imageValue = (yLumaValue << 16) & 0xFF0000;
            imageValue = imageValue | ((cbChromaValue << 8) & 0x00FF00);
            imageValue = imageValue | (crChromaValue & 0x0000FF);
            fJpegImageContext.setImageValue(pixelRow, pixelColumn, imageValue);
         }
      }
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedCbChromaValue(int aRow, int aColumn, byte aValue) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;

      fJpegImageContext.setQuantizedCbChromaValue(pixelRow, pixelColumn, aValue);
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedCrChromaValue(int aRow, int aColumn, byte aValue) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;

      fJpegImageContext.setQuantizedCrChromaValue(pixelRow, pixelColumn, aValue);
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedYLumaValue(int aRow, int aColumn, byte aValue) {
      int pixelRow    = fStartPixelRow + aRow;
      int pixelColumn = fStartPixelColumn + aColumn;

      fJpegImageContext.setQuantizedYLumaValue(pixelRow, pixelColumn, aValue);
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

      // Y luma values
      resultString += "Y Luma\n";

      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         resultString += "[";

         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            if (column < JPEG_IMAGE_BLOCK_WIDTH - 1) {
               resultString += getQuantizedYLumaValue(row, column) + ",";
            } else {
               resultString += getQuantizedYLumaValue(row, column);
            }
         }

         resultString += "]\n";
      }

      // Cb Chroma values
      resultString += "Cb Chroma\n";

      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         resultString += "[";

         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            if (column < JPEG_IMAGE_BLOCK_WIDTH - 1) {
               resultString += getQuantizedCbChromaValue(row, column) + ",";
            } else {
               resultString += getQuantizedCbChromaValue(row, column);
            }
         }

         resultString += "]\n";
      }

      // Cr Chroma values
      resultString += "Cr Chroma\n";

      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         resultString += "[";

         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            if (column < JPEG_IMAGE_BLOCK_WIDTH - 1) {
               resultString += getQuantizedCrChromaValue(row, column) + ",";
            } else {
               resultString += getQuantizedCrChromaValue(row, column);
            }
         }

         resultString += "]\n";
      }

      // resultString += getZigZagPrintoutString("Y Luma: [ ", " ]\n", fZigZagRlcEncodedYLumaArray);
      // resultString += getZigZagPrintoutString("Cb Luma: [ ", " ]\n", fZigZagRlcEncodedCbChromaArray);
      // resultString += getZigZagPrintoutString("Cr Luma: [ ", " ]\n", fZigZagRlcEncodedCrChromaArray);
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
         RunLengthCodingStructure rlcStructure =
            JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(rlcEncodedElement);

         try {
            resultStreamArray.write(rlcStructure.toBits());
         } catch (IOException ioe) {
            JpegImageEncoderUtils.throwJpegImageEncoderExeption(ioe.getMessage());
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

      return JpegImageEncoderUtils.convertIntegerListToIntArray(resultCombinedZigZagArray);
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
         RunLengthCodingStructure rlcStructure = JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(encodedRlc);

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
      int[][] resultRoundedMatrix = new int[JPEG_IMAGE_BLOCK_HEIGHT][JPEG_IMAGE_BLOCK_WIDTH];

      for (int row = 0; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
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
         RunLengthCodingStructure rlcStructure =
            JpegImageEncoderUtils.decodeFromEncodedZeroRunLengthCoding(rlcEncodedElement);

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
