

import java.io.File;

import beans.AppInfo;
import crawler.GooglePlayStoreCrawlerInfo;



public class Main{

		public static void main(String args[]) throws Exception{
			
			// webDriverFile is the driver file for Chrome browser
			// if you use Firefox you can set webDriverFile to null
		    File webDriverFile = new File("C:/Users/Computer/Desktop/java libraries/chromedriver_win32/chromedriver.exe");
	
			final String baseLink = "https://play.google.com/store/apps/details?id=";
			final String language = "&hl=en";
			
			// Package names for a list of apps
			String[] packageNames = new String[]{"com.lucasdnd.bitclock16",
												 "com.ketchapp.stack", 
												 "com.sonymobile.lifelog"};

			// browserChoice is the browser you want to use browserChoice = "Chrome | Firefox"
			String browserChoice = "Firefox";
			
			// Creating a new crawler
			GooglePlayStoreCrawlerInfo googlePlayStoreCrawler = new GooglePlayStoreCrawlerInfo(browserChoice, webDriverFile);
				
			
			for (int i = 0; i<packageNames.length;i++){
				// appLink is the link of the app's page on Google Play
				String appLink = baseLink+packageNames[i]+language;
				System.out.println("Connecting to "+appLink);
				
				
				// Getting app related information
				AppInfo appInfo = googlePlayStoreCrawler.getAppInformation(appLink);
				
				// Outputting results
				System.out.println("PACKAGE NAME: "+packageNames[i]);
				if (appInfo!=null){
					System.out.println("CURRENT VERSION: "+appInfo.getCurrentVersion());
					System.out.println("LAST UPDATE: "+appInfo.getLastUpdate());
					System.out.flush();
				}else{
					System.out.println("DOES NOT EXIST");
					System.out.flush();
				}

			}
			
			// closing the web driver
			googlePlayStoreCrawler.closeDriver();
	
			
		}

                        
                    
}


