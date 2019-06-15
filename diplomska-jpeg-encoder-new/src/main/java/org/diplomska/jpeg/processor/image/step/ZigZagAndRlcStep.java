package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.util.JpegImageEncoderUtils;

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
public class ZigZagAndRlcStep implements JpegImageProcessorStep {

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
   public void execute(JpegImageContext aJpegImageContext) {
      int rows    = aJpegImageContext.getJpegImageBlocks().length;
      int columns = aJpegImageContext.getJpegImageBlocks()[0].length;

      for (int row = 0; row < rows; row++) {
         for (int column = 0; column < columns; column++) {
            JpegImageBlock currentJpegImageBlock = aJpegImageContext.getJpegImageBlocks()[row][column];

            /*
             * currentJpegImageBlock.getQuantizedYLumaValues(),
             *                      currentJpegImageBlock.getQuantizedCbChromaValues(),
             *                      currentJpegImageBlock.getQuantizedCrChromaValues()
             */
            initializeZigZagArrays(currentJpegImageBlock);
            currentJpegImageBlock.setZigZagRlcEncodedYLumaArray(createRlcArrays(fZigZagLumaYArray));
            currentJpegImageBlock.setZigZagRlcEncodedCrChromaArray(createRlcArrays(fZigZagChromaCrArray));
            currentJpegImageBlock.setZigZagRlcEncodedCbChromaArray(createRlcArrays(fZigZagChromaCbArray));
         }
      }
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

      encodedRlcElement = JpegImageEncoderUtils.encodeToZeroRunLengthCoding(numberOfZeroes, dcElement);
      returnRlcList.add(encodedRlcElement);

      // handle AC elements
      for (int index = 1; index < aZigZagArray.length; index++) {
         int acElement = aZigZagArray[index];

         if (acElement == 0) {
            numberOfZeroes++;
         }

         if (numberOfZeroes == 16) {
            encodedRlcElement = JpegImageEncoderUtils.encodeToZeroRunLengthCoding(15, 0);
            numberOfConsecutiveRlcZeros++;
            numberOfZeroes = 0;
            returnRlcList.add(encodedRlcElement);
         } else if (acElement != 0) {
            encodedRlcElement = JpegImageEncoderUtils.encodeToZeroRunLengthCoding(numberOfZeroes, acElement);
            returnRlcList.add(encodedRlcElement);
            numberOfZeroes              = 0;
            numberOfConsecutiveRlcZeros = 0;
         }
      }

      if (numberOfZeroes > 0) {
         numberOfConsecutiveRlcZeros++;
         encodedRlcElement = JpegImageEncoderUtils.encodeToZeroRunLengthCoding(numberOfZeroes, 0);
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

      return JpegImageEncoderUtils.convertIntegerListToIntArray(returnRlcList);
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
    *
    * @param aCurrentJpegImageBlock
    *
    * int[][] aLumaYJpegBlock, int[][] aChromaCbJpegBlock,
    *                                     int[][] aChromaCrJpegBlock
    */
   protected void initializeZigZagArrays(JpegImageBlock aCurrentJpegImageBlock) {
      fZigZagLumaYArray =
         new int[JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT];
      fZigZagChromaCrArray =
         new int[JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT];
      fZigZagChromaCbArray =
         new int[JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT];

      for (int index = 0; index < JpegImageEncoderUtils.ZIG_ZAG_COORDINATES.length; index++) {
         long coordinates = JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[index];
         int  row         = JpegImageEncoderUtils.getRow(coordinates);
         int  column      = JpegImageEncoderUtils.getColumn(coordinates);

         fZigZagLumaYArray[index]    = aCurrentJpegImageBlock.getQuantizedYLumaValue(row, column);
         fZigZagChromaCbArray[index] = aCurrentJpegImageBlock.getQuantizedCbChromaValue(row, column);
         fZigZagChromaCrArray[index] = aCurrentJpegImageBlock.getQuantizedCrChromaValue(row, column);
      }
   }
}
