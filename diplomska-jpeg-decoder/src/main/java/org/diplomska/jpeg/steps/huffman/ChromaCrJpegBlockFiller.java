package org.diplomska.jpeg.steps.huffman;

import org.diplomska.jpeg.jfif.struct.HuffmanTable;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.JpegDecoderContext.HuffmanTableType;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.jpeg.steps.struct.JpegBlock.BlockType;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public class ChromaCrJpegBlockFiller extends JpegBlockFiller {

   /**
    * Constructs ...
    *
    *
    * @param jpegDecoderContext
    */
   public ChromaCrJpegBlockFiller(JpegDecoderContext jpegDecoderContext) {
      super(jpegDecoderContext);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   @Override
   public HuffmanTable getHuffmanTable() {
      HuffmanTable returnHuffmanTable = null;

      if (!jpegBlock.isCurrentAc()) {
         returnHuffmanTable = jpegDecoderContext.getHuffmanTable(HuffmanTableType.DC_CHROMA);
      } else {
         returnHuffmanTable = jpegDecoderContext.getHuffmanTable(HuffmanTableType.AC_CHROMA);
      }

      return returnHuffmanTable;
   }

   /**
    * Get block type.
    *
    *
    * @return
    */
   @Override
   protected BlockType getJpegBlockType() {
      return JpegBlock.BlockType.CR;
   }

   /**
    * Get block filler.
    *
    *
    *
    * @param context
    * @return
    */
   @Override
   protected JpegBlockFiller getNextJpegBlockFiller(JpegBlockFillerContext context) {
      context.lumaJpegBlockFiller.initializeJpegBlock();

      return context.lumaJpegBlockFiller;
   }
}
