package org.diplomska.stego.context;

import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.stego.DataContext;
import org.diplomska.util.structs.JpegImageConstants;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/15
 * @author         Enter your name here...
 */
public class JpegStegoDecoderContext implements DataContext, JpegImageConstants {

   /** Field description */
   private JpegDecoderContext jpegDecoderContext;

   /** Field description */
   private List<JpegBlock> jpegBlocks;

   /** Field description */
   private int blocksHeight;

   /** Field description */
   private int blocksWidth;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aJpegDecoderContext
    */
   @SuppressWarnings("unchecked")
   public JpegStegoDecoderContext(JpegDecoderContext aJpegDecoderContext) {
      jpegDecoderContext = aJpegDecoderContext;

      List<Object> results = jpegDecoderContext.getResult(HuffmanDecodingStep.class);

      jpegBlocks   = (List<JpegBlock>) results.get(0);
      blocksHeight = getBlocksHeight();
      blocksWidth  = getBlocksWidth();
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public int getImageData(int aRow, int aColumn, int aColorComponentType) {
      int returnValue      = 0;
      int blockRow         = aRow / JPEG_IMAGE_BLOCK_HEIGHT;
      int blockColumn      = aColumn / JPEG_IMAGE_BLOCK_WIDTH;
      int blockStartRow    = blockRow * JPEG_IMAGE_BLOCK_HEIGHT;
      int blockStartColumn = blockColumn * JPEG_IMAGE_BLOCK_WIDTH;
      int localRow         = aRow - blockStartRow;
      int localColumn      = aColumn - blockStartColumn;
      int blockIndex       = ((blockRow * blocksWidth) + blockColumn) * 3;

      if (Y_LUMA == aColorComponentType) {
         returnValue = jpegBlocks.get(blockIndex).getCoefficientsAsMatrixInt()[localRow][localColumn];

         // System.out.println(jpegBlocks.get(blockIndex).toString());
      } else if (CB_CHROMA == aColorComponentType) {
         returnValue = jpegBlocks.get(blockIndex + 1).getCoefficientsAsMatrixInt()[localRow][localColumn];

         // System.out.println(jpegBlocks.get(blockIndex + 1).toString());
      } else if (CR_CHROMA == aColorComponentType) {
         returnValue = jpegBlocks.get(blockIndex + 2).getCoefficientsAsMatrixInt()[localRow][localColumn];

         // System.out.println(jpegBlocks.get(blockIndex + 2).toString());
      }

      return returnValue;
   }

   @Override
   public int getImageHeight() {
      return jpegDecoderContext.getJfifSofSegment().getImageHeight();
   }

   @Override
   public int getImageWidth() {
      return jpegDecoderContext.getJfifSofSegment().getImageWidth();
   }

   @Override
   public int getNumberOfColorComponents() {
      return 3;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setImageData(int aRow, int aColumn, int colorComponentValue, int aColorComponentType) {}

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   private int getBlocksHeight() {
      int jpegImageBlocksNumber = jpegBlocks.size() / 3;
      int blockWidth            = getBlocksWidth();

      blocksHeight = jpegImageBlocksNumber / blockWidth;
      blocksHeight = (jpegImageBlocksNumber % blockWidth) != 0
                     ? (blocksHeight + 1)
                     : blocksHeight;

      return blocksHeight;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int getBlocksWidth() {
      blocksWidth = getImageWidth() / 8;
      blocksWidth = (getImageWidth() % 8) != 0
                    ? (blocksWidth + 1)
                    : blocksWidth;

      return blocksWidth;
   }
}
