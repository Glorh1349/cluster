package diploma;

public final class Coordinate {
	private final double latitude;
    private final double longitude;

     Coordinate(final double latitude, final double longitude) {
        this.latitude = latitude; 
        this.longitude = longitude;
    }
     
    public String toString() {
    		return "["+latitude+" , "+longitude+"]"; 
    }
    
    public double getLatitude () {
    		return this.latitude;
    }
    public double getLongitude () {
		return this.longitude;
    }
}
