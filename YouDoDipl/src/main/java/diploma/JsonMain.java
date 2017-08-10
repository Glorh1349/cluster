package diploma;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;

import diploma.DBSCANClusterer;

import diploma.Cluster;

public class JsonMain extends PApplet {
	
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map;
	
	public void setup() {
		size(950, 600, P2D );
		map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		try {
			testCollecting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    // Initialize our clustering class with locations, minimum points in cluster and max Distance
	    DBSCANClusterer clusterer = new DBSCANClusterer(coordinates, 3, 2); // (20, 100)
	    
	    //Perform the clustering and save the returned clusters as a list
	    ArrayList<ArrayList<Coordinate>> clusters_raw= clusterer.performClustering();
	    
	    // Change the cluster list into array of object of our cluster class
	    // The clusters class is responsible for finding the center of the cluster and also the number of points inside the cluster
	    // It also exposes a method which returns javascript code for plotting the cluster as markers with numbers
	    ArrayList<Cluster> clusters = new ArrayList<>();
	    for(int i=0; i<clusters_raw.size(); i++) {
	    		Cluster c = new Cluster(clusters_raw.get(i));
	    		clusters.add(c);
	    }
	    
		map.addMarkers(clusters);
	}
	public void draw() {
		background(10);
		map.draw();
	}

	static List<Coordinate> coordinates = new ArrayList<>();
	
	private static final String ITEMS_NAME = "items";
	private static final String LATITUDE_PROPERTY = "latitude";
	private static final String LONGITUDE_PROPERTY = "longitude";
	private static final String CRASH_NAME = "em_type_name";
	
	static void parseCrashCoordinates(final JsonReader jsonReader, final ICoordinatesListener listener)
	        throws IOException {	
		
	    // Read { like the object begins.
		// If we don't read it or rread it wrong then exception will be thrown - that is the point of pull-method

		jsonReader.beginObject();
	    
	    // Looking for the object's next name of property and compare it with expected one
	    final String itemsName = jsonReader.nextName();
	    
	    if ( !itemsName.equals(ITEMS_NAME) ) {
	        // Not 'items'? No idea how to work with it, then better throw an exception
	        throw new MalformedJsonException(ITEMS_NAME + " expected but was " + itemsName);
	    }
	    
	    // Looking for [
	    jsonReader.beginArray();
	    
	    // And read every element of array
	    while ( jsonReader.hasNext() ) {
	    	
	        // Judging by scheme every element of array is object
	        jsonReader.beginObject();
	        double latitude = 0;
	        double longitude = 0;
	        String name = null;
	        
	        // Run through all properties of object
	        while ( jsonReader.hasNext() ) {
	        	// Now just look if they are what we know
	            final String property = jsonReader.nextName();
	           	           
	            	switch ( property ) {	            	
		            // latitude? Save it.
		            case LATITUDE_PROPERTY:
		                latitude = jsonReader.nextDouble();
		                break;
		            // longitude? Save it.
		            case LONGITUDE_PROPERTY:
		                longitude = jsonReader.nextDouble();
		                break;
		            // Save the type of crash    
		            case CRASH_NAME:
		            	name = jsonReader.nextString();
		            	break;
		            // Otherwise skip any values of property
		            default:
		                jsonReader.skipValue();
		                break;
		            }
	            }
	           
	        //Restrict Geolocations to only Russia
	        if ( !( latitude > 70.446677 || latitude < 49.177129 || longitude > 161.894782 || longitude < 32.97373 ) ){
	        	// Check if the object contains the exactly coordinates for crash with pedestrian
		        if (name.contains("Наезд на пешехода")) {
		        	// Just delegate our coordinates in handler
			        listener.onCoordinates(latitude, longitude);
		        }
	        }
	        
	        // And say, that we are done with current object 
	        jsonReader.endObject();
	    }
	    // Also close last ] пїЅ }
	    jsonReader.endArray();
	    jsonReader.endObject();
	}
	
	private static void readAndParse(final ICoordinatesListener listener)
	        throws IOException {
	    try ( final JsonReader jsonReader = new JsonReader(new BufferedReader(
	    		new InputStreamReader(
	    				new FileInputStream("C:\\Users\\Evgeny\\git\\cluster\\YouDoDipl\\json\\2015-crash.json")))) ) {
	        parseCrashCoordinates(jsonReader, listener);
	    }
	}
	
	// Output testing in console
	private static void testOutput()
	        throws IOException {
//	    readAndParse((lat, lng) -> System.out.println("(" + lat + "; " + lng + ")"));
	}

     // Collecting all coordinates in ArrayList.
	// Will JVM support increase? Perhaps, but not fact.
	private static void testCollecting()
	        throws IOException {
	  //  List<Coordinate> coordinates = new ArrayList<>();
	    readAndParse((lat, lng) -> coordinates.add(new Coordinate(lat, lng)));
	    System.out.println(coordinates.size());   
	}
	
	public static void main(String[] args)  {
	//	testOutput();
	 //   testCollecting();  
	    
	    System.out.println(coordinates.isEmpty());
	  
	    
	    
	    /*
	    // Initialize our clustering class with locations, minimum points in cluster and max Distance
	    DBSCANClusterer clusterer = new DBSCANClusterer(coordinates, 3, 2); // (20, 100)
	    
	    //Perform the clustering and save the returned clusters as a list
	    ArrayList<ArrayList<Coordinate>> clusters_raw= clusterer.performClustering();
	    
	    // Change the cluster list into array of object of our cluster class
	    // The clusters class is responsible for finding the center of the cluster and also the number of points inside the cluster
	    // It also exposes a method which returns javascript code for plotting the cluster as markers with numbers
	    ArrayList<Cluster> clusters = new ArrayList<>();
	    for(int i=0; i<clusters_raw.size(); i++) {
	    		Cluster c = new Cluster(clusters_raw.get(i));
	    		clusters.add(c);
	    }
	    
	    
	    */
	    PApplet.main(new String[] {"--present", "EarthQuakeMap"});

	}
	
}
