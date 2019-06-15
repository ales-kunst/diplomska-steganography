package org.diplomska.decoder.step;

import org.apache.commons.cli.CommandLine;

import org.diplomska.jpeg.steps.JpegDecoderContext;
import org.diplomska.jpeg.steps.JpegDecoderStep;
import org.diplomska.jpeg.steps.huffman.HuffmanDecodingStep;
import org.diplomska.jpeg.steps.struct.JpegBlock;
import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoAlgorithmFactory;
import org.diplomska.stego.StegoDecodingAlgorithm;
import org.diplomska.stego.context.JpegStegoDecoderContext;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.StegoProgramParameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/17
 * @author         Enter your name here...
 */
public class StegoDecodingStep implements JpegDecoderStep, StegoProgramParameters {

   /** Field description */
   private String outputStegoFilename;

   /** Field description */
   private CommandLine commandLine;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aCommandLine
    */
   public StegoDecodingStep(CommandLine aCommandLine) {
      outputStegoFilename = aCommandLine.getOptionValue(OUT_STEGO_FILE_SHORT);
      commandLine         = aCommandLine;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void execute(JpegDecoderContext aJpegDecoderContext) {
      StegoDecodingAlgorithm stegoDecodingAlgorithm = StegoAlgorithmFactory.getDecoder(commandLine,
                                                                                       aJpegDecoderContext);
      DataContext  jpegStegoContext  = new JpegStegoDecoderContext(aJpegDecoderContext);
      File         outputStegoFile   = new File(outputStegoFilename);
      OutputStream outputStegoStream = null;

      try {
         try {
            recalculateDcCoefficients(aJpegDecoderContext);
            outputStegoStream = new BufferedOutputStream(new FileOutputStream(outputStegoFile));
            stegoDecodingAlgorithm.decode(jpegStegoContext, outputStegoStream);
         } finally {
            if (outputStegoStream != null) {
               outputStegoStream.close();
            }
         }
      } catch (Exception e) {
         JpegImageUtils.throwJpegImageExeption(e);
      }
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public String getName() {
      return "Stego Decoding";
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aJpegDecoderContext
    */
   @SuppressWarnings("unchecked")
   private void recalculateDcCoefficients(JpegDecoderContext aJpegDecoderContext) {
      List<Object>    results    = aJpegDecoderContext.getResult(HuffmanDecodingStep.class);
      List<JpegBlock> jpegBlocks = (List<JpegBlock>) results.get(0);
      int             prevDcY    = 0;
      int             prevDcCb   = 0;
      int             prevDcCr   = 0;

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
   }
}
