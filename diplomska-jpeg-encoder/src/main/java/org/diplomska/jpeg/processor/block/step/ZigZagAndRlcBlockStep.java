package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.jpeg.util.JpegImageUtils;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/05/23
 * @author         Ales Kunst
 */
public class ZigZagAndRlcBlockStep implements JpegImageBlockProcessorStep {

   /** Field description */
   private int[] fZigZagLumaYArray;

   /** Field description */
   private int[] fZigZagChromaCrArray;

   /** Field description */
   private int[] fZigZagChromaCbArray;

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aJpegImageBlock
    */
   @Override
   public void execute(JpegImageBlock aJpegImageBlock) {
      initializeZigZagArrays(aJpegImageBlock.getRoundedMatrixYLumaForCalculation(),
                             aJpegImageBlock.getRoundedMatrixCrChromaForCalculation(),
                             aJpegImageBlock.getRoundedMatrixCbChromaForCalculation());
      aJpegImageBlock.setZigZagRlcEncodedYLumaArray(createRlcArrays(fZigZagLumaYArray));
      aJpegImageBlock.setZigZagRlcEncodedCrChromaArray(createRlcArrays(fZigZagChromaCrArray));
      aJpegImageBlock.setZigZagRlcEncodedCbChromaArray(createRlcArrays(fZigZagChromaCbArray));
      aJpegImageBlock.clearCalculationMatrices();
   }

   /**
    * Method description
    *
    *
    * @param aZigZagArray
    *
    * @return
    */
   protected int[] createRlcArrays(int[] aZigZagArray) {
      List<Integer> returnRlcList               = new ArrayList<Integer>();
      int           encodedRlcElement           = 0;
      byte          numberOfZeroes              = 0;
      byte          numberOfConsecutiveRlcZeros = 0;

      // handle DC element
      int dcElement = aZigZagArray[0];

      encodedRlcElement = JpegImageUtils.encodeToZeroRunLengthCoding(numberOfZeroes, dcElement);
      returnRlcList.add(encodedRlcElement);

      // handle AC elements
      for (int index = 1; index < aZigZagArray.length; index++) {
         int acElement = aZigZagArray[index];

         if (acElement == 0) {
            numberOfZeroes++;
         }

         if (numberOfZeroes == 16) {
            encodedRlcElement = JpegImageUtils.encodeToZeroRunLengthCoding(15, 0);
            numberOfConsecutiveRlcZeros++;
            numberOfZeroes = 0;
            returnRlcList.add(encodedRlcElement);
         } else if (acElement != 0) {
            encodedRlcElement = JpegImageUtils.encodeToZeroRunLengthCoding(numberOfZeroes, acElement);
            returnRlcList.add(encodedRlcElement);
            numberOfZeroes              = 0;
            numberOfConsecutiveRlcZeros = 0;
         }
      }

      if (numberOfZeroes > 0) {
         numberOfConsecutiveRlcZeros++;
         encodedRlcElement = JpegImageUtils.encodeToZeroRunLengthCoding(numberOfZeroes, 0);
         returnRlcList.add(encodedRlcElement);
      }

      if (numberOfConsecutiveRlcZeros > 0) {
         while (numberOfConsecutiveRlcZeros > 0) {

            // remove last element
            returnRlcList.remove(returnRlcList.size() - 1);
            numberOfConsecutiveRlcZeros--;
         }
      }

      if (aZigZagArray[aZigZagArray.length - 1] == 0) {
         returnRlcList.add(0);
      }

      return JpegImageUtils.convertIntegerListToIntArray(returnRlcList);
   }

   /**
    *    Method description
    *
    *
    *    @return
    */
   protected int[] getfZigZagChromaCbArray() {
      return fZigZagChromaCbArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected int[] getfZigZagChromaCrArray() {
      return fZigZagChromaCrArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected int[] getfZigZagLumaYArray() {
      return fZigZagLumaYArray;
   }

   /**
    * Method description
    *
    *
    *
    * @param aLumaYJpegBlock
    * @param aChromaCrJpegBlock
    * @param aChromaCbJpegBlock
    */
   protected void initializeZigZagArrays(int[][] aLumaYJpegBlock, int[][] aChromaCrJpegBlock,
                                         int[][] aChromaCbJpegBlock) {
      fZigZagLumaYArray    = new int[JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT];
      fZigZagChromaCrArray = new int[JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT];
      fZigZagChromaCbArray = new int[JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT];

      for (int index = 0; index < JpegImageUtils.ZIG_ZAG_COORDINATES.length; index++) {
         long coordinates = JpegImageUtils.ZIG_ZAG_COORDINATES[index];
         int  row         = JpegImageUtils.getRow(coordinates);
         int  column      = JpegImageUtils.getColumn(coordinates);

         fZigZagLumaYArray[index]    = aLumaYJpegBlock[row][column];
         fZigZagChromaCrArray[index] = aChromaCrJpegBlock[row][column];
         fZigZagChromaCbArray[index] = aChromaCbJpegBlock[row][column];
      }
   }
}
