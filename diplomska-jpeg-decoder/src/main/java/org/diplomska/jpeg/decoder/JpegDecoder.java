package org.diplomska.jpeg.decoder;

import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.JpegDecoderStep;
import org.diplomska.util.JpegImageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 26.nov.2015
 * @author         Ales Kunst
 */
public class JpegDecoder {

   /** Logger */
   public static final Logger LOG = Logger.getLogger(JpegDecoder.class.getName());

   //~--- fields --------------------------------------------------------------

   /** Decoder steps */
   private List<JpegDecoderStep> steps;

   /** Jpeg decoder context */
   private JpegDecoderContext jpegDecoderContext;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructor.
    *
    *
    *
    */
   public JpegDecoder() {
      steps = new ArrayList<JpegDecoderStep>();
   }

   /**
    * Constructor.
    *
    *
    * @param steps
    */
   public JpegDecoder(List<JpegDecoderStep> steps) {
      this.steps = steps;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Add step.
    *
    *
    * @param jpegDecoderStep
    *
    * @return
    */
   public boolean addStep(JpegDecoderStep jpegDecoderStep) {
      return steps.add(jpegDecoderStep);
   }

   /**
    * Decode jpeg from file.
    *
    *
    * @param jpegFile
    */
   public void decode(File jpegFile) {
      try {
         InputStream jpegInStream = null;

         try {
            jpegInStream = new BufferedInputStream(new FileInputStream(jpegFile));
            decode(jpegInStream);
         } finally {
            jpegInStream.close();
         }
      } catch (IOException ioe) {
         JpegImageUtils.throwJpegImageExeption(ioe.getMessage());
      }
   }

   /**
    * Decode jpeg from stream.
    *
    *
    * @param jpegInStream
    */
   public void decode(InputStream jpegInStream) {
      ReadJpegInJfif readJpegInJfif = new ReadJpegInJfif(jpegInStream);

      jpegDecoderContext = new JpegDecoderContext(readJpegInJfif);

      for (JpegDecoderStep step : steps) {
         long startTime = System.currentTimeMillis();

         System.out.println("Executing step: " + step.getName());
         step.execute(jpegDecoderContext);

         long durationTime = (System.currentTimeMillis() - startTime);

         System.out.println("Step " + step.getName() + " finished in: " + (durationTime / 1000d) + " sec");
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   public JpegDecoderContext getJpegDecoderContext() {
      return jpegDecoderContext;
   }
}
