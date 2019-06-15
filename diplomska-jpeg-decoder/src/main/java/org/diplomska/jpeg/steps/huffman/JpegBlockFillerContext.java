package org.diplomska.jpeg.steps.huffman;

import org.diplomska.jpeg.jfif.struct.HuffmanCode;
import org.diplomska.jpeg.jfif.struct.HuffmanTable;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.util.BitWalker;

//~--- classes ----------------------------------------------------------------

/**
 * Jpeg block filler.
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public class JpegBlockFillerContext {

   /** Field description */
   public final LumaJpegBlockFiller lumaJpegBlockFiller;

   /** Field description */
   public final ChromaCbJpegBlockFiller chromaCbJpegBlockFiller;

   /** Field description */
   public final ChromaCrJpegBlockFiller chromaCrJpegBlockFiller;

   /** Field description */
   private JpegBlockFiller currentBlockFiller;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param jpegDecoderContext
    */
   public JpegBlockFillerContext(JpegDecoderContext jpegDecoderContext) {
      lumaJpegBlockFiller     = new LumaJpegBlockFiller(jpegDecoderContext);
      chromaCbJpegBlockFiller = new ChromaCbJpegBlockFiller(jpegDecoderContext);
      chromaCrJpegBlockFiller = new ChromaCrJpegBlockFiller(jpegDecoderContext);
      currentBlockFiller      = lumaJpegBlockFiller;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Fill the coefficient.
    *
    *
    * @param bitWalker
    * @param huffmanCode
    *
    * @return
    */
   public JpegBlock fill(BitWalker bitWalker, HuffmanCode huffmanCode) {
      return currentBlockFiller.fill(this, bitWalker, huffmanCode);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get huffman table.
    *
    *
    * @return
    */
   public HuffmanTable getHuffmanTable() {
      return currentBlockFiller.getHuffmanTable();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param currentCoefficientRetriever
    */
   void setCurrentCoefficientRetriever(JpegBlockFiller currentCoefficientRetriever) {
      this.currentBlockFiller = currentCoefficientRetriever;
   }
}
