package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.util.JpegImageUtils;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.imageio.ImageIO;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/29
 * @author         Ales Kunst
 */
public class BufferedImageExportDecoderStep implements JpegDecoderStep {

   /** Field description */
   List<JpegBlock> jpegBlocks;

   /** Field description */
   private int imageHeight;

   /** Field description */
   private int imageWidth;

   /** Field description */
   private BufferedImage image;

   /** Field description */
   private File imageFile;

   /** Field description */
   private String imageType;

   /** Field description */
   private int blocksWidth;

   /** Field description */
   private int blocksHeight;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    *
    * @param imageFile
    * @param imageType
    */
   public BufferedImageExportDecoderStep(File imageFile, String imageType) {
      this.imageFile = imageFile;
      this.imageType = imageType;
      blocksWidth    = -1;
      blocksHeight   = -1;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param jpegDecoderContext
    */
   @Override
   public void execute(JpegDecoderContext jpegDecoderContext) {
      List<Object> results = jpegDecoderContext.getResult(InverseDctDecoderStep.class);

      jpegBlocks  = (List<JpegBlock>) results.get(0);
      imageHeight = jpegDecoderContext.getJfifSofSegment().getImageHeight();
      imageWidth  = jpegDecoderContext.getJfifSofSegment().getImageWidth();
      image       = new BufferedImage((getBlocksWidth() * 8), (getBlocksHeight() * 8), BufferedImage.TYPE_INT_RGB);

      int blockNumber = 0;

      for (int index = 0; index < jpegBlocks.size(); index += 3) {
         int     startPositionRow    = getBlockRow(blockNumber) * 8;
         int     startPositionColumn = getBlockColumn(blockNumber) * 8;
         int[][] lumaMatrix          = jpegBlocks.get(index).getCoefficientsAsMatrixInt();
         int[][] chromaCbMatrix      = jpegBlocks.get(index + 1).getCoefficientsAsMatrixInt();
         int[][] chromaCrMatrix      = jpegBlocks.get(index + 2).getCoefficientsAsMatrixInt();

         for (int row = 0; row < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; row++) {
            for (int column = 0; column < JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT; column++) {
               int[] yCbCrColor = new int[3];

               yCbCrColor[0] = lumaMatrix[row][column] + 128;
               yCbCrColor[1] = chromaCbMatrix[row][column] + 128;
               yCbCrColor[2] = chromaCrMatrix[row][column] + 128;

               int[] rgbColorArray = JpegImageUtils.convertYcbcrToRgb(yCbCrColor);
               int   rgbColor      = 0;

               rgbColor |= (rgbColorArray[0] << 16) | (rgbColorArray[1] << 8) | rgbColorArray[2];
               image.setRGB((startPositionColumn + column), (startPositionRow + row), rgbColor);
            }
         }

         blockNumber++;
      }

      try {
         ImageIO.write(image, imageType, imageFile);
      } catch (IOException e) {
         JpegImageUtils.throwJpegImageExeption(e.getMessage());
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   @Override
   public String getName() {
      return "Image export";
   }

   /**
    * Method description
    *
    *
    *
    * @param blockPosition
    *
    * @return
    */
   private int getBlockColumn(int blockPosition) {
      int returnBlockColumn = 0;
      int blocksWidth       = getBlocksWidth();

      if (blockPosition != 0) {
         returnBlockColumn = blockPosition % blocksWidth;
      }

      return returnBlockColumn;
   }

   /**
    * Method description
    *
    *
    * @param blockPosition
    *
    * @return
    */
   private int getBlockRow(int blockPosition) {
      int returnBlockRow = 0;
      int blocksWidth    = getBlocksWidth();

      if (blockPosition >= blocksWidth) {
         returnBlockRow = blockPosition / blocksWidth;
      }

      return returnBlockRow;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int getBlocksHeight() {
      if (blocksHeight == -1) {
         int jpegImageBlocksNumber = jpegBlocks.size() / 3;
         int blockWidth            = getBlocksWidth();

         blocksHeight = jpegImageBlocksNumber / blockWidth;
         blocksHeight = (jpegImageBlocksNumber % blockWidth) != 0
                        ? (blocksHeight + 1)
                        : blocksHeight;
      }

      return blocksHeight;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int getBlocksWidth() {
      if (blocksWidth == -1) {
         blocksWidth = imageWidth / 8;
         blocksWidth = (imageWidth % 8) != 0
                       ? (blocksWidth + 1)
                       : blocksWidth;
      }

      return blocksWidth;
   }
}
