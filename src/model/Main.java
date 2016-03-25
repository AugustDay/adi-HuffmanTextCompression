/*
 * TCSS 342: Project 2 - Compressed Literature
 * Main
 */

package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Launches the program.
 * @author Austin Ingraham
 * @version 15 February 2016
 */
public final class Main {
    
    /**
     * Runs the program.
     * @param theArgs command line arguments.
     */
    public static void main(final String[] theArgs) {
        final String[] testFiles = {"WarAndPeace.txt", "Ulysses.txt"};
        long inFileSize, outFileSize;
        for (final String s : testFiles) {
            //Get starting time and file size for statistics
            System.out.println("Opening: \"" + s + "\"");
            final long startTime = System.nanoTime();
            inFileSize = new File(s).length(); 
            
            //Read input, build tree, and write output
            final String input = readInput(s);            
            final CodingTree encode = new CodingTree(input);
            outFileSize = writeOutput(encode);
            
            //Get end time, print statistics 
            final long endTime = System.nanoTime();
            printStats(startTime, endTime, inFileSize, outFileSize);
            System.out.println();
        }
        System.out.println("Complete."); 
    }
    
    /**
     * Reads the input of a file with the given filename. 
     * @param theFilename Filename of a text file to open
     * @return String contents of file
     */
    private static String readInput(final String theFilename) {
        final FileReader inputStream;
        final StringBuilder sb = new StringBuilder();

        try {
            
            inputStream = new FileReader(theFilename);
            final BufferedReader bufferedStream = new BufferedReader(inputStream);
            
            String line = bufferedStream.readLine();
            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = bufferedStream.readLine();
            }
            bufferedStream.close();
        } catch (final FileNotFoundException e) {
            System.out.println("FileNotFound!");
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println("IOException!");
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    /**
     * Writes the compressed output of the Huffman compression process to a file.
     * @param theEncode the Tree to be written
     * @return fileSize the size of this file in bytes (long)
     */
    private static long writeOutput(final CodingTree theEncode) {
        final String fileName = "out.txt";
        long fileSize = 0;
        try {
            final byte[] buffer = theEncode.getBytes();
            final FileOutputStream outputStream = new FileOutputStream(fileName);
            
            outputStream.write(buffer);
            fileSize = new File(fileName).length(); //gets size in bytes
            
            outputStream.close();       
        } catch (final FileNotFoundException e) {
            System.out.println("FileNotFound! ");
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println("IOException! ");
            e.printStackTrace();
        }        
        return fileSize;
    }
    
    /**
     * Prints the statistical analysis of this run of the program.
     * @param theStartTime value in nanoseconds when this run was started
     * @param theEndTime value in nanoseconds when this run was ended
     * @param theInSize size in bytes of the input file
     * @param theOutSize size in bytes of the compressed output
     */
    private static void printStats(final long theStartTime, final long theEndTime, 
                                   final long theInSize, final long theOutSize) {
        System.out.println("Uncompressed file size: " + theInSize + " bytes.");
        System.out.println("Compressed file size: " + theOutSize + " bytes.");
        System.out.println("Compression ration: " 
                          + (int) (theOutSize * 100.0 / theInSize + 0.5) + "%.");
        System.out.println("Running time: " + ((theEndTime - theStartTime) / 1000000) 
                          + " milliseconds.");
    
    }
}
