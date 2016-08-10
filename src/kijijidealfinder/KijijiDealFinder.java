/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kijijidealfinder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Usman
 */
public class KijijiDealFinder {

    public static int MAX_PAGES=2;
    public static int MIN_PRICE=100;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, IOException {

        ArrayList<String> itemNames = new ArrayList<String>(); 
        ArrayList<Integer> itemMaxPrices = new ArrayList<Integer>(); 
        
               
        /*FIND THESE ITEMS WITH MAXPRICES FROM CONFIGURATION FILE*/
        readConfFile(itemNames, itemMaxPrices);

         /*PREPARE OUTPUT FILE*/
        PrintWriter writer = new PrintWriter("Kijiji_Deals.txt", "UTF-8");

        int itemN=0;
        for (itemN=0;itemN<itemNames.size();itemN++){
            findDeals(itemNames.get(itemN).toString(), writer, itemMaxPrices.get(itemN).intValue());
        }
        
        writer.close();
        System.out.println("Deals exported to Kijiji_Deals.txt!");
            
    }
    
    public static void findDeals(String input, PrintWriter writer, int maxPrice) throws MalformedURLException, IOException {
        
        input=input.replaceAll(" ", "-");
        drawLine(writer);
        writer.println("ALL KIJIJI SEARCH RESULTS FOR " + input + " under $" + maxPrice + ".");
        drawLine(writer);
        writer.println();
        
        /*READ THROUGH SEARCH RESULT PAGES*/
        int pageNo = 1;
        int detailsNo=0;
        String[][] details = new String[200][3];
        
        for (pageNo =1; pageNo<MAX_PAGES+1;pageNo++) {
                    /*READ PAGE {pageNo} of SEARCH RESULTS*/
                    String URL = new String();
                    URL = "http://www.kijiji.ca/b-gta-greater-toronto-area/" + input + "/page-" + pageNo + "/k0l1700272";

                    URL url = new URL(URL);

                    URLConnection con = url.openConnection();
                    InputStream is =con.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                    String line = null;                           
                    String price = new String();
                    String title = new String();
                    String link = new String();

                    while ((line = br.readLine()) != null) {
                    /*START-PARSE SOURCE HTML/CSS*/
                        
                        String lineHeading=line;
                        String compareHeading="            <div class=\"price\">";
                        if (lineHeading.equals(compareHeading)) {
                            
                            line = br.readLine();
                                price = line;
                            line = br.readLine();
                            line = br.readLine();
                            line = br.readLine();
                                link = line;
                            line = br.readLine();
                                title = line;
                                 
                                title=title.split("</a>")[0].trim(); 
                                price=price.split("</div>")[0].trim();
                                link=("http://www.kijiji.ca" + (link.split("\"")[1])).trim();
                                price= price.replaceAll(",", "");
                             
                                details[detailsNo][0] = title;
                                details[detailsNo][1] = price;
                                details[detailsNo][2] = link;

                             if (price != null && !price.isEmpty()) {
                             /*PRICE IS NUMERIC AND NON-EMPTY*/
                                 
                                        char dollarSign = price.charAt(0);
                                        if (dollarSign == '$') {

                                            double price_db = Double.parseDouble(price.substring(1,price.length()));
                                            if (price_db < maxPrice && price_db > MIN_PRICE) {
                                            /*MATCH FOUND WITHING PRICE RANGE. PRINT DETAILS.*/
                                                
                                                int indexOfDetails;
                                                for (indexOfDetails=0; indexOfDetails<3; indexOfDetails++){
                                                    //System.out.println(details[detailsNo][indexOfDetails]);
                                                    writer.println(details[detailsNo][indexOfDetails]);
                                                }
                                                 //System.out.println();
                                                 writer.println(); 
                                            }
                                        }
                             }
                             
                             detailsNo++;
                        }  
                    /*END-PARSE SOURCE HTML/CSS*/
                    }
        /*NEXT PAGE*/
        }
        
        writer.println();
        writer.println();
    }
    
    public static void readConfFile(ArrayList<String> itemNames, ArrayList<Integer> itemMaxPrices) throws FileNotFoundException, IOException {
        
            try(BufferedReader br = new BufferedReader(new FileReader("Items.conf"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            
            while (line != null && !line.isEmpty()) {
              
                String itemName = (line.split(",") [0]).trim()  ;
                Integer itemMaxPrice = Integer.parseInt( line.split(",")[1].trim())  ;
                
                System.out.println("Fetching results for " +itemName + "...");
                
                itemNames.add    (   (line.split(",") [0]).trim()                      );
                itemMaxPrices.add(   Integer.parseInt( line.split(",")[1].trim())    );

                line = br.readLine();
            }

        }
    }
    
    public static void drawLine (PrintWriter writer) {
        writer.println("---------------------------------------------------------------");
    }
    

}
