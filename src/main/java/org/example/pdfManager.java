package org.example;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class pdfManager {
    String operation;
    String url;

    public pdfManager(String line){
        String[] arr = line.split("\t");
        this.operation = arr[0];
        this.url = arr[1];
    }

    public void execute(String inputDirectory, String outputDirectory) {
        String localPDF;
        switch (this.operation) {
            case "ToImage":
                localPDF = downloadPDF(url, inputDirectory);
                if (localPDF != null) 
                    convertToImage(localPDF, outputDirectory);
                break;

            case "ToText":
                localPDF = downloadPDF(url, inputDirectory);
                if (localPDF != null) 
                    convertToText(localPDF, outputDirectory);                
                break;

            case "ToHTML":
                localPDF = downloadPDF(url, inputDirectory);
                if (localPDF != null) 
                    convertToHTML(localPDF, outputDirectory);
                break;

            default:
                System.out.println("Unsupported operation: " + operation);
        }
    }

    // Downloads a PDF from a URL and saves it locally in the input directory
    private String downloadPDF(String urlString, String inputDirectory) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Ensure input directory exists
                File dir = new File(inputDirectory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Determine file name and path
                String filePath = inputDirectory + "/downloaded.pdf";
                try (InputStream inputStream = httpConnection.getInputStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("PDF downloaded to: " + filePath);
                return filePath;
            } else { 
                System.out.println("Failed to download PDF. HTTP Response Code: " + httpConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getOutputFilePath(String baseName, String extension, String outputDirectory) {
        return outputDirectory + "/" + baseName + "-first-page." + extension;
    }
    
    private void convertToImage(String pdfPath, String outputDirectory) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            File dir = new File(outputDirectory);
            if (!dir.exists()) dir.mkdirs();
    
            // Render only the first page as an image
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300);
            String baseName = new File(pdfPath).getName().replace(".pdf", "");
            String outputFileName = getOutputFilePath(baseName, "png", outputDirectory);
            ImageIO.write(image, "PNG", new File(outputFileName));
    
            System.out.println("First page saved as image to: " + outputFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToText(String pdfPath, String outputDirectory) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            org.apache.pdfbox.text.PDFTextStripper textStripper = new org.apache.pdfbox.text.PDFTextStripper();
    
            // Extract text from only the first page
            textStripper.setStartPage(1);
            textStripper.setEndPage(1);
            String text = textStripper.getText(document);
    
            /*File dir = new File(outputDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }*/
    
            // Save the text to a file
            String outputFilePath = getOutputFilePath(new File(pdfPath).getName().replace(".pdf", ""), "txt", outputDirectory);            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                writer.write(text);
            }
            System.out.println("First page text extracted and saved to: " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void convertToHTML(String pdfPath, String outputDirectory) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            org.apache.pdfbox.text.PDFTextStripper textStripper = new org.apache.pdfbox.text.PDFTextStripper();
    
            // Extract text from only the first page
            textStripper.setStartPage(1);
            textStripper.setEndPage(1);
            String text = textStripper.getText(document);
    
            // Wrap the text in basic HTML tags
            String htmlContent = "<!DOCTYPE html>\n<html>\n<head>\n<title>First Page</title>\n</head>\n<body>\n<pre>" +
                                 text + "</pre>\n</body>\n</html>";
    
            /*File dir = new File(outputDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }*/
            // Save the HTML to a file
            String outputFilePath = getOutputFilePath(new File(pdfPath).getName().replace(".pdf", ""), "html", outputDirectory);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                writer.write(htmlContent);
            }
    
            System.out.println("First page converted to HTML and saved to: " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
