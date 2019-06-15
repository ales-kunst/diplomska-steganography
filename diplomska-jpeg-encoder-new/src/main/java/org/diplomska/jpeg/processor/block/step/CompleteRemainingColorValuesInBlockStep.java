package org.diplomska.jpeg.processor.block.step;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.processor.block.JpegImageBlockProcessorStep;
import org.diplomska.util.JpegImageEncoderUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/07/27
 * @author         Ales Kunst
 */
public class CompleteRemainingColorValuesInBlockStep implements JpegImageBlockProcessorStep {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(CompleteRemainingColorValuesInBlockStep.class);

   //~--- methods -------------------------------------------------------------

   /**
    * Execute this step.
    *
    *
    * @param aJpegImageBlock
    */
   @Override
   public void execute(JpegImageBlock aJpegImageBlock) {

      // Check if MCU has less than 8 column or rows filled
      if ((aJpegImageBlock.getLastLocalColumn() < 7) || (aJpegImageBlock.getLastLocalRow() < 7)) {
         if ((aJpegImageBlock.getLastLocalColumn() < 7) && (aJpegImageBlock.getLastLocalRow() < 7)) {
            completeRemainingRowsAndColumns(aJpegImageBlock);
         } else if (aJpegImageBlock.getLastLocalColumn() < 7) {
            completeRemainingColumns(aJpegImageBlock);
         } else if (aJpegImageBlock.getLastLocalRow() < 7) {
            completeRemainingRows(aJpegImageBlock);
         }
      }
   }

   /**
    * Complete columns.
    *
    *
    * @param aJpegImageBlock
    */
   private void completeRemainingColumns(JpegImageBlock aJpegImageBlock) {
      int localColumn      = aJpegImageBlock.getLastLocalColumn();
      int startPixelColumn = aJpegImageBlock.getColumn() * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH;
      int startPixelRow    = aJpegImageBlock.getRow() * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT;
      int startLocalColumn = localColumn + 1;

      for (int row = 0; row < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         int colorValue = aJpegImageBlock.getImageValue(row, startLocalColumn - 1);

         for (int column = startLocalColumn; column < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int  currentPixelRow         = startPixelRow + row;
            int  currentPixelColumn      = startPixelColumn + column;
            long currentPixelCoordinates = JpegImageEncoderUtils.getPoint(currentPixelRow, currentPixelColumn);

            aJpegImageBlock.addRgbColor(currentPixelCoordinates, colorValue);
         }
      }
   }

   /**
    * Complete rows.
    *
    *
    * @param aJpegImageBlock
    */
   private void completeRemainingRows(JpegImageBlock aJpegImageBlock) {
      int localRow         = aJpegImageBlock.getLastLocalRow();
      int startPixelColumn = aJpegImageBlock.getColumn() * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH;
      int startPixelRow    = aJpegImageBlock.getRow() * JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT;
      int startLocalRow    = localRow + 1;

      for (int row = startLocalRow; row < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = 0; column < JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int  colorValue              = aJpegImageBlock.getImageValue(startLocalRow - 1, column);
            int  currentPixelRow         = startPixelRow + row;
            int  currentPixelColumn      = startPixelColumn + column;
            long currentPixelCoordinates = JpegImageEncoderUtils.getPoint(currentPixelRow, currentPixelColumn);

            aJpegImageBlock.addRgbColor(currentPixelCoordinates, colorValue);
         }
      }
   }

   /**
    * Complete rows and columns.
    *
    *
    * @param aJpegImageBlock
    */
   private void completeRemainingRowsAndColumns(JpegImageBlock aJpegImageBlock) {
      int localColumn      = aJpegImageBlock.getLastLocalColumn();
      int localRow         = aJpegImageBlock.getLastLocalRow();
      int startPixelColumn = aJpegImageBlock.getColumn() * JPEG_IMAGE_BLOCK_WIDTH;
      int startPixelRow    = aJpegImageBlock.getRow() * JPEG_IMAGE_BLOCK_HEIGHT;
      int startLocalColumn = localColumn + 1;
      int startRow         = localRow + 1;
      int colorValue       = aJpegImageBlock.getImageValue(startRow - 1, startLocalColumn - 1);

      for (int row = startRow; row < JPEG_IMAGE_BLOCK_HEIGHT; row++) {
         for (int column = startLocalColumn; column < JPEG_IMAGE_BLOCK_WIDTH; column++) {
            int  currentPixelRow         = startPixelRow + row;
            int  currentPixelColumn      = startPixelColumn + column;
            long currentPixelCoordinates = JpegImageEncoderUtils.getPoint(currentPixelRow, currentPixelColumn);

            aJpegImageBlock.addRgbColor(currentPixelCoordinates, colorValue);
         }
      }
   }
}
