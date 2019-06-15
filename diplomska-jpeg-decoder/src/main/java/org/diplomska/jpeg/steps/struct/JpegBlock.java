package org.diplomska.jpeg.steps.struct;

import org.diplomska.util.JpegImageUtils;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public class JpegBlock {

   /** Field description */
   private static final int EOB_POSITION = 64;

   //~--- fields --------------------------------------------------------------

   /** Field description */
   public short[] coefficients;

   /** Position */
   private int position;

   /** Block type */
   private BlockType blockType;

   //~--- constant enums ------------------------------------------------------

   /**
    * Enum description
    *
    */
   public static enum BlockType { LUMA, CB, CR; }

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private JpegBlock() {
      coefficients = new short[64];
      position     = 0;
   }

   /**
    * Constructs ...
    *
    *
    * @param blockType
    */
   public JpegBlock(BlockType blockType) {
      this();
      this.blockType = blockType;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get block type.
    *
    *
    * @return
    */
   public BlockType getBlockType() {
      return blockType;
   }

   /**
    * Get as matrix of doubles.
    *
    *
    * @return
    */
   public double[][] getCoefficientsAsMatrixDouble() {
      double[][] returnMatrix =
         new double[JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT][JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH];

      for (int index = 0; index < coefficients.length; index++) {
         long point  = JpegImageUtils.ZIG_ZAG_COORDINATES[index];
         int  column = JpegImageUtils.getColumn(point);
         int  row    = JpegImageUtils.getRow(point);

         returnMatrix[row][column] = coefficients[index];
      }

      return returnMatrix;
   }

   /**
    * Get as matrix of integers.
    *
    *
    * @return
    */
   public int[][] getCoefficientsAsMatrixInt() {
      int[][] returnMatrix = new int[JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT][JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH];

      for (int index = 0; index < coefficients.length; index++) {
         long point  = JpegImageUtils.ZIG_ZAG_COORDINATES[index];
         int  column = JpegImageUtils.getColumn(point);
         int  row    = JpegImageUtils.getRow(point);

         returnMatrix[row][column] = coefficients[index];
      }

      return returnMatrix;
   }

   /**
    * Is ac
    *
    *
    * @return
    */
   public boolean isCurrentAc() {
      return (position > 0);
   }

   /**
    * Is dc filled
    *
    *
    * @return
    */
   public boolean isCurrentDc() {
      return (position == 0);
   }

   /**
    * Is array full
    *
    *
    * @return
    */
   public boolean isEob() {
      return position == EOB_POSITION;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Put coefficient with number of leading zeroes.
    *
    *
    * @param numberOfZeroes
    * @param coefficientValue
    */
   public void put(int numberOfZeroes, int coefficientValue) {
      if ((numberOfZeroes == 0) && (coefficientValue == 0) && isCurrentAc()) {
         position = EOB_POSITION;
      } else {
         for (int index = 0; index < numberOfZeroes; index++) {
            put(0);
         }

         put(coefficientValue);
      }
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Set coefficients from matrix
    *
    *
    * @param matrix
    */
   public void setCoefficientsFromMatrix(int[][] matrix) {
      for (int index = 0; index < coefficients.length; index++) {
         long point  = JpegImageUtils.ZIG_ZAG_COORDINATES[index];
         int  column = JpegImageUtils.getColumn(point);
         int  row    = JpegImageUtils.getRow(point);

         coefficients[index] = (short) matrix[row][column];
      }
   }

   /**
    * Set coefficients from matrix
    *
    *
    * @param matrix
    */
   public void setCoefficientsFromMatrix(double[][] matrix) {
      for (int index = 0; index < coefficients.length; index++) {
         long point  = JpegImageUtils.ZIG_ZAG_COORDINATES[index];
         int  column = JpegImageUtils.getColumn(point);
         int  row    = JpegImageUtils.getRow(point);

         if (matrix[row][column] > 255d) {
            coefficients[index] = 255;
         } else {
            coefficients[index] = (short) Math.round(matrix[row][column]);
         }
      }
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
      StringBuffer returnString = new StringBuffer();
      int[][]      blockInt     = getCoefficientsAsMatrixInt();

      returnString.append("\n");

      for (int r = 0; r < 8; r++) {
         returnString.append("[");

         for (int c = 0; c < 8; c++) {
            if (c < 7) {
               returnString.append(String.format("%5d, ", blockInt[r][c]));
            } else {
               returnString.append(String.format("%5d", blockInt[r][c]));
            }
         }

         returnString.append("]\n");
      }

      return returnString.toString();
   }

   /**
    * Put coefficient value into the array.
    *
    *
    * @param value
    */
   private void put(int value) {
      if (!isEob()) {
         coefficients[position] = (short) value;
         position++;
      } else {
         JpegImageUtils.throwJpegImageExeption("Trying to write beyond block!");
      }
   }
}
