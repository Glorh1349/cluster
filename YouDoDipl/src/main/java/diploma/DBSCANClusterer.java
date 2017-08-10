package diploma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import diploma.Coordinate;

public class DBSCANClusterer {
	
	private double epsilon = 1f;
	private int minimumNumberOfClusterMembers = 2;
	
	private ArrayList<Coordinate> inputValues = null;
	private ArrayList<Coordinate> coordinatesFilteredToBound = null;
	
	private HashSet<Coordinate> visitedPoints = new HashSet<Coordinate>();
	 
	public static final double R = 6371; // Earth Radius In kilometers
	
	// Bounds of co-ordinates to include
	private double minLat, minLng, maxLat, maxLng;
	
	// Haversine Distance Formula
	public double calculateDistance(Coordinate val1, Coordinate val2) {
		double lat1 = val1.getLatitude();
		double lat2 = val2.getLatitude();
		
		double lon1 = val1.getLongitude();
		double lon2 = val2.getLongitude();
		
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
	
	public DBSCANClusterer(final Collection<Coordinate> inputValues) {
		setInputValues(inputValues);
	}
	
	public DBSCANClusterer(final Collection<Coordinate> inputValues, int minNumElem, double maxDistance ) {
		setInputValues(inputValues);
		setMinimalNumberOfMembersForCluster(minNumElem);
		setMaxDistanceOfClusterMembers(maxDistance);
	}
	
	public void setInputValues(final Collection<Coordinate> collection) {
		this.inputValues = new ArrayList<Coordinate>(collection);
		this.coordinatesFilteredToBound = new ArrayList<Coordinate>();
	}
	
	public void setMinimalNumberOfMembersForCluster(final int num) {
		this.minimumNumberOfClusterMembers = num;
	}
	
	public void setMaxDistanceOfClusterMembers(final double maxDistance) {
		this.epsilon = maxDistance;
	}
	
	public void setBounds(final double minLat, final double minLng, final double maxLat, final double maxLng) {
		this.minLat = minLat;
		this.minLng = minLng;
		this.maxLat = maxLat;
		this.maxLng = maxLng;
	}
	
	private ArrayList<Coordinate> getNeighbours(final Coordinate inputValue) {
		ArrayList<Coordinate> neighbours = new ArrayList<Coordinate>();
		
		for(int i=0; i<coordinatesFilteredToBound.size(); i++) {
			Coordinate candidate = coordinatesFilteredToBound.get(i);
			
//			if( 
//					(candidate.getLatitude() < this.minLat) 
//					|| (candidate.getLatitude() > this.maxLat) 
//					|| (candidate.getLongitude() < this.minLng) 
//					|| (candidate.getLongitude() > this.maxLng) 
//			) {
//				continue;
//			}
			
			if(calculateDistance(inputValue, candidate) <= epsilon ) {
				neighbours.add(candidate);
			}
		}
		
		return neighbours;
	}
	
	private ArrayList<Coordinate> mergeRightToLeftCollection (final ArrayList<Coordinate> neighbours1, final ArrayList<Coordinate> neighbours2){
		for (int i=0; i<neighbours2.size(); i++) {
			Coordinate tempPt = neighbours2.get(i);
			if( ! (neighbours1.contains(tempPt)) ) {
				neighbours1.add(tempPt);
			}
		}
		
		return neighbours1;
	}
	
	public ArrayList<ArrayList<Coordinate>> performClustering() {
		
		ArrayList<ArrayList<Coordinate>> resultList = new ArrayList<ArrayList<Coordinate>>();
		
		visitedPoints.clear();
		
		ArrayList<Coordinate> neighbours;
		int index=0;
		
//		coordinatesFilteredToBound.clear();
		
//		String a = inputValues.toString();
//		System.out.println(a);
		
		for(int i=0; i<this.inputValues.size(); i++) {
			Coordinate c = this.inputValues.get(i);
			if( 
					(c.getLatitude() < this.minLat) 
					|| (c.getLatitude() > this.maxLat) 
					|| (c.getLongitude() < this.minLng) 
					|| (c.getLongitude() > this.maxLng) 
			) {
				continue;
			}
			
			coordinatesFilteredToBound.add(c);
		}
		
		while ( coordinatesFilteredToBound.size()  > index ) {
			Coordinate p = coordinatesFilteredToBound.get(index);
			
			if (!visitedPoints.contains(p)) {
				visitedPoints.add(p);
				
				neighbours = getNeighbours(p);
				
				if (neighbours.size() >= minimumNumberOfClusterMembers) {
					int ind=0;
					while (neighbours.size() > ind) {
						Coordinate r = neighbours.get(ind);
						if( !visitedPoints.contains(r) ) {
							visitedPoints.add(r);
							ArrayList<Coordinate> individualNeighbours = getNeighbours(r);
							if (individualNeighbours.size() >= minimumNumberOfClusterMembers) {
								neighbours = mergeRightToLeftCollection(neighbours, individualNeighbours);
							}
						}
						
						ind++;
					}
					resultList.add(neighbours);
				}
			}
			
			index++;
		}
		
		return resultList;
				
	}
	
}
