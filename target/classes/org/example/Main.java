package org.example;
public class Main {
    public static void main(String[] args) {
        String line = "ToHTML\thttp://www.thejewishcollection.com/passoverjokes.pdf";

        String inputDirectory = "input";
        String outputDirectory = "output";

        pdfManager manager = new pdfManager(line);
        manager.execute(inputDirectory, outputDirectory);
    }
}   
