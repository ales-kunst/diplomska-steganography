package org.diplomska.jpeg.steps.huffman;

import org.diplomska.jpeg.steps.InverseDctDecoderStep;
import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.JpegDecoderStep;
import org.diplomska.jpeg.steps.JpegDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/27
 * @author         Ales Kunst
 */
public class HuffmanDecodingStepTest extends JpegDecodingStep {

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
    * Testing method.
    *
    * @throws IOException
    *
    */
   @Test
   public void testExecute() throws IOException {
      checkSimpleJpgImage();
      checkComplexJpegImage();
      checkAlpeNormalImage();
      checkGodsEyeImage();
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkAlpeNormalImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("slovenske_alpe_triglav.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(HuffmanDecodingStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);

      Assert.assertEquals(25425, list.size());
      Assert.assertEquals(21, list.get(0).coefficients[0]);
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkComplexJpegImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("complex_color_8_8.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(HuffmanDecodingStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);

      Assert.assertEquals(3, list.size());
      Assert.assertEquals(-13, list.get(0).coefficients[0]);
   }

   /**
    * Method description
    *
    */
   private void checkGodsEyeImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("gods_eye.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(InverseDctDecoderStep.class);

      // List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);
   }

   /**
    * Method description
    *
    */
   @SuppressWarnings("unchecked")
   private void checkSimpleJpgImage() {
      List<JpegDecoderStep> steps = new ArrayList<JpegDecoderStep>();

      steps.add(new HuffmanDecodingStep());

      JpegDecoderContext jpegDecoderContext = executeDecoding("simple_black_white.jpg", steps);
      List<Object>       map                = jpegDecoderContext.getResult(HuffmanDecodingStep.class);
      List<JpegBlock>    list               = (List<JpegBlock>) map.get(0);

      Assert.assertEquals(6, list.size());
      Assert.assertEquals(64, list.get(0).coefficients[0]);
   }
}
