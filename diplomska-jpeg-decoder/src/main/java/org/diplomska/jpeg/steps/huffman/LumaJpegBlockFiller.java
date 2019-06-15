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
public class LumaJpegBlockFiller extends JpegBlockFiller {

   /**
    * Constructs ...
    *
    *
    * @param jpegDecoderContext
    */
   public LumaJpegBlockFiller(JpegDecoderContext jpegDecoderContext) {
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
         returnHuffmanTable = jpegDecoderContext.getHuffmanTable(HuffmanTableType.DC_LUMA);
      } else {
         returnHuffmanTable = jpegDecoderContext.getHuffmanTable(HuffmanTableType.AC_LUMA);
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
      return JpegBlock.BlockType.LUMA;
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
      context.chromaCbJpegBlockFiller.initializeJpegBlock();

      return context.chromaCbJpegBlockFiller;
   }
}
