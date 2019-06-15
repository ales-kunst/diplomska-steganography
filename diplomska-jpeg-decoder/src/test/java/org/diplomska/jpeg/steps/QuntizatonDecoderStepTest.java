package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/28
 * @author         Ales Kunst
 */
public class QuntizatonDecoderStepTest extends JpegDecodingStep {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    */
   @Test
   public void testExecute() {
      checkSimpleImage();
      checkComplexImage();
      checkAlpeNormalImage();
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkAlpeNormalImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("slovenske_alpe_triglav.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(HuffmanDecodingStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);

      // Assert.assertEquals(3, list.size());
      // Assert.assertEquals(-13, list.get(0).coefficients[0]);
   }

   /**
    * Method description
    *
    */
   private void checkComplexImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("complex_color_8_8.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(QuantizatonDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock          = list.get(0);

      Assert.assertEquals(-208, jpegBlock.coefficients[0]);
   }

   /**
    * Method description
    *
    */
   private void checkSimpleImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());
      steps.add(new QuantizatonDecoderStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("simple_black_white.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(QuantizatonDecoderStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
      JpegBlock          jpegBlock          = list.get(0);

      Assert.assertEquals(1024, jpegBlock.coefficients[0]);
   }
}
