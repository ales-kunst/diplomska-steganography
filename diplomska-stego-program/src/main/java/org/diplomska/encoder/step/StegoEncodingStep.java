package org.diplomska.encoder.step;

import org.apache.commons.cli.CommandLine;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoAlgorithmFactory;
import org.diplomska.stego.StegoEncodingAlgorithm;
import org.diplomska.stego.context.JpegStegoEncoderContext;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.StegoProgramParameters;

import java.io.FileInputStream;
import java.io.InputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/14
 * @author         Enter your name here...
 */
public class StegoEncodingStep implements JpegImageProcessorStep, StegoProgramParameters {

   /** Field description */
   private String inputStegoFilename;

   /** Field description */
   private CommandLine commandLine;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aCommandLine
    */
   public StegoEncodingStep(CommandLine aCommandLine) {
      inputStegoFilename = aCommandLine.getOptionValue(IN_STEGO_FILE_SHORT);
      commandLine        = aCommandLine;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void execute(JpegImageContext aJpegImageContext) {
      StegoEncodingAlgorithm stegoEncodingAlgorithm = StegoAlgorithmFactory.getEncoder(commandLine, aJpegImageContext);
      DataContext            jpegStegoContext       = new JpegStegoEncoderContext(aJpegImageContext);

      try {
         InputStream stegoInputStream = new FileInputStream(inputStegoFilename);

         stegoEncodingAlgorithm.encode(jpegStegoContext, stegoInputStream);
      } catch (Exception e) {
         JpegImageUtils.throwJpegImageExeption(e);
      }

      System.out.println(stegoEncodingAlgorithm.getStatistics().toString());
   }
}
