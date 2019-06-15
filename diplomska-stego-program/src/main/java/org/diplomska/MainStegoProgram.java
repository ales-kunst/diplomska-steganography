package org.diplomska;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.StegoProgramParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Hello world!
 *
 */
public class MainStegoProgram implements StegoProgramParameters {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(MainStegoProgram.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */
   private String[] programParameters;

   /** Field description */
   private Options commandLineOptions;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aProgramParameters
    */
   public MainStegoProgram(String[] aProgramParameters) {
      programParameters  = aProgramParameters;
      commandLineOptions = new Options();
      buildCommandLineOptions();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    *
    * @param programParameters
    */
   public static void main(String[] programParameters) {
      MainStegoProgram mainStegoProgram = new MainStegoProgram(programParameters);
      CommandLine      commandLine      = mainStegoProgram.parseProgramParameters();
      String           message          = mainStegoProgram.checkProgramParameters(commandLine);

      if (message == null) {
         StegoProgram stegoProgram = new StegoProgram(commandLine);

         stegoProgram.process();
      } else {
         LOGGER.error(message);
      }
   }

   /**
    * Method description
    *
    *
    * @param aCommandLine
    *
    * @return
    */
   public String checkProgramParameters(CommandLine aCommandLine) {
      String resultMessage = null;

      if (aCommandLine.hasOption(JUST_COMPRESS_SHORT)) {
         if (!aCommandLine.hasOption(IN_FILE_SHORT) || !aCommandLine.hasOption(OUT_FILE_SHORT)) {
            resultMessage = "Should contain input and output file!";
         }
      } else {
         resultMessage = checkProgramParametersInStegoMode(aCommandLine);
      }

      return resultMessage;
   }

   /**
    * Method description
    *
    *
    * @param aCommandLine
    *
    * @return
    */
   public String checkProgramParametersInStegoMode(CommandLine aCommandLine) {
      String resultMessage = null;

      if (aCommandLine.hasOption(ENCODE_SHORT) && aCommandLine.hasOption(DECODE_SHORT)) {
         resultMessage = "Can not have both encode and decode parameter!";
      } else if (!aCommandLine.hasOption(ENCODE_SHORT) && !aCommandLine.hasOption(DECODE_SHORT)) {
         resultMessage = "Should contain one of encode or decode!";
      } else if (aCommandLine.hasOption(ENCODE_SHORT)) {
         if (!aCommandLine.hasOption(IN_FILE_SHORT) || !aCommandLine.hasOption(OUT_FILE_SHORT)) {
            resultMessage = "Should contain input and output file!";
         }
      } else if (aCommandLine.hasOption(DECODE_SHORT) && !aCommandLine.hasOption(IN_FILE_SHORT)) {
         resultMessage = "Should contain input file!";
      } else if (!aCommandLine.hasOption(ALGO_SHORT)) {
         resultMessage = "Should contain algorithm!";
      } else if (aCommandLine.hasOption(ENCODE_SHORT) && !aCommandLine.hasOption(IN_STEGO_FILE_SHORT)) {
         resultMessage = "In stego file option should be set!";
      } else if (aCommandLine.hasOption(DECODE_SHORT) && !aCommandLine.hasOption(OUT_STEGO_FILE_SHORT)) {
         resultMessage = "Out stego file option should be set!";
      }

      return resultMessage;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public CommandLine parseProgramParameters() {
      CommandLineParser parser     = new DefaultParser();
      CommandLine       resultLine = null;

      try {
         resultLine = parser.parse(commandLineOptions, programParameters);
      } catch (ParseException e) {
         JpegImageUtils.throwJpegImageExeption(e);;
      }

      return resultLine;
   }

   /**
    * Method description
    *
    */
   private void buildCommandLineOptions() {

      /*
       * stegano -e --encode
       * -d --decode
       * -i --input=FILE
       * -o --output=FILE
       * -a --algorithm=STRING (LSB-1, LSB-2, F5)
       */
      Option encode    = Option.builder(ENCODE_SHORT).longOpt(ENCODE_LONG).desc("Encode file").build();
      Option decode    = Option.builder(DECODE_SHORT).longOpt(DECODE_LONG).desc("Decode file").build();
      Option inputFile = Option.builder(IN_FILE_SHORT)
                               .longOpt(IN_FILE_LONG)
                               .hasArg()
                               .argName("INPUT_FILE")
                               .desc("Input file")
                               .build();
      Option inputStegoFile = Option.builder(IN_STEGO_FILE_SHORT)
                                    .longOpt(IN_STEGO_FILE_LONG)
                                    .hasArg()
                                    .argName("INPUT_STEGO_FILE")
                                    .desc("Input stego file")
                                    .build();
      Option outputStegoFile = Option.builder(OUT_STEGO_FILE_SHORT)
                                     .longOpt(OUT_STEGO_FILE_LONG)
                                     .hasArg()
                                     .argName("OUT_STEGO_FILE")
                                     .desc("Output stego file")
                                     .build();
      Option outputFile = Option.builder(OUT_FILE_SHORT)
                                .longOpt(OUT_FILE_LONG)
                                .hasArg()
                                .argName("OUTPUT_FILE")
                                .desc("Output file")
                                .build();;
      Option stegoAlgorithm = Option.builder(ALGO_SHORT)
                                    .longOpt(ALGO_LONG)
                                    .hasArg()
                                    .argName("STRING")
                                    .desc("Stego algorithm")
                                    .build();
      Option password = Option.builder(PASSWORD_SHORT)
                              .longOpt(PASSWORD_LONG)
                              .hasArg()
                              .argName("STRING")
                              .desc("Password for F5ME algorithm")
                              .build();
      Option justCompress = Option.builder(JUST_COMPRESS_SHORT)
                                  .longOpt(JUST_COMPRESS_LONG)
                                  .desc("Just compress file")
                                  .build();

      commandLineOptions.addOption(encode);
      commandLineOptions.addOption(decode);
      commandLineOptions.addOption(inputFile);
      commandLineOptions.addOption(outputFile);
      commandLineOptions.addOption(inputStegoFile);
      commandLineOptions.addOption(outputStegoFile);
      commandLineOptions.addOption(stegoAlgorithm);
      commandLineOptions.addOption(password);
      commandLineOptions.addOption(justCompress);
   }
}
