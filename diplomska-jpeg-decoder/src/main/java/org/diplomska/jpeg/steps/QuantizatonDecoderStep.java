package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.util.JpegImageUtils;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public class QuantizatonDecoderStep implements JpegDecoderStep {

   /** Field description */
   List<JpegBlock> jpegBlocks;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   public QuantizatonDecoderStep() {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param jpegDecoderContext
    */
   @SuppressWarnings("unchecked")
   @Override
   public void execute(JpegDecoderContext jpegDecoderContext) {
      List<Object> results = jpegDecoderContext.getResult(HuffmanDecodingStep.class);

      jpegBlocks = (List<JpegBlock>) results.get(0);

      int prevDcY  = 0;
      int prevDcCb = 0;
      int prevDcCr = 0;

      // Recalculate Dc coefficients and set them according
      for (int index = 0; index < jpegBlocks.size(); index += 3) {
         if (index > 0) {
            prevDcY                                   = jpegBlocks.get(index - 3).coefficients[0];
            prevDcCb                                  = jpegBlocks.get(index - 2).coefficients[0];
            prevDcCr                                  = jpegBlocks.get(index - 1).coefficients[0];
            jpegBlocks.get(index).coefficients[0]     += prevDcY;
            jpegBlocks.get(index + 1).coefficients[0] += prevDcCb;
            jpegBlocks.get(index + 2).coefficients[0] += prevDcCr;
         }
      }

      // Quantization step
      for (JpegBlock jpegBlock : jpegBlocks) {
         int[][] quantizationMatrix = null;

         if (jpegBlock.getBlockType() == JpegBlock.BlockType.LUMA) {
            quantizationMatrix = jpegDecoderContext.getLumaQuantizationTableSegment().getQuantizationMatrix();
         } else {
            quantizationMatrix = jpegDecoderContext.getChromaQuantizationTableSegment().getQuantizationMatrix();
         }

         int[][] jpegBlockMatrix = jpegBlock.getCoefficientsAsMatrixInt();
         int[][] multiplyMatrix  = JpegImageUtils.getDequantizedMatrix(jpegBlockMatrix, quantizationMatrix);

         jpegBlock.setCoefficientsFromMatrix(multiplyMatrix);
      }

      jpegDecoderContext.addResult(this.getClass(), jpegBlocks);
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
      return "Quantization";
   }
}
