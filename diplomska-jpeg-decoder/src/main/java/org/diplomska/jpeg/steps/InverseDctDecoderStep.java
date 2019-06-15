package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.util.JpegImageUtils;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/29
 * @author         Ales Kunst
 */
public class InverseDctDecoderStep implements JpegDecoderStep {

   /** Field description */
   List<JpegBlock> jpegBlocks;

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
      List<Object> results = jpegDecoderContext.getResult(QuantizatonDecoderStep.class);

      jpegBlocks = (List<JpegBlock>) results.get(0);

      for (JpegBlock jpegBlock : jpegBlocks) {
         double[][] newValuesMatrix = JpegImageUtils.calculateInverseDct(jpegBlock.getCoefficientsAsMatrixDouble());

         // if (jpegBlock.getBlockType() == JpegBlock.BlockType.LUMA) {
         // newValuesMatrix = JpegImageUtils.addNumberToMatrix(newValuesMatrix, 128);
         // }
         jpegBlock.setCoefficientsFromMatrix(newValuesMatrix);
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
      return "IDCT";
   }
}
