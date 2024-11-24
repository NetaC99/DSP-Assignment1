package org.example;

public class Main {
    public static void main(String[] args) {
        String line = "ToHTML\thttps://pdfobject.com/pdf/sample.pdf";

        String inputDirectory = "input";
        String outputDirectory = "output";

        pdfManager manager = new pdfManager(line);
        manager.execute(inputDirectory, outputDirectory);
    }
}
