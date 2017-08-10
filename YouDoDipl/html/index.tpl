<!DOCTYPE html>
<html>
   <head>
      <title>Clustering Of Accident Data</title>
      <meta name="viewport" content="initial-scale=1.0">
      <meta charset="utf-8">
      <style>
         /* Always set the map height explicitly to define the size of the div
         * element that contains the map. */
         #map {
         height: 100%;
         }
         /* Optional: Makes the sample page fill the window. */
         html, body {
         height: 100%;
         margin: 0;
         padding: 0;
         }
      </style>
      <script src='https://cdn.rawgit.com/googlemaps/js-marker-clusterer/gh-pages/src/markerclusterer.js'></script>  
   </head>
   <body>
      <div id="map"></div>
      <script>
        
        var map;
        var markers=[];
        var circles=[];
        var timeout; 

        var zoom_relations = {

            "3": {
              minPoints: 50,
              maxDistance: 50,
              radius: 100000
            }
            ,"4": {
              minPoints: 29,
              maxDistance: 30,
              radius: 85000
            }
            ,"5": {
              minPoints: 19,
              maxDistance: 17,
              radius: 50000
            }
            ,"6": {
              minPoints: 13,
              maxDistance: 14,
              radius: 42000
            }
            ,"7": {
              minPoints: 6,
              maxDistance: 12,
              radius: 35000
            }
            ,"8": {
              minPoints: 3,
              maxDistance: 9,
              radius: 22000
            }
            ,"9": {
              minPoints: 3,
              maxDistance: 6,
              radius: 4800
            }
            ,"10": {
              minPoints: 2,
              maxDistance: 3,
              radius: 2700
            }
            ,"11": {
              minPoints: 2,
              maxDistance: 1.5,
              radius: 1300
            }
            ,"12": {
              minPoints: 1,
              maxDistance: 0.6,
              radius: 500
            },

        }

        function initMap() {

            // Fix the map initially at center of russia and make whole of Russia visible
            // Also, disable user from zooming out more than level 3 as it will span the whole world and we need only Russia
            // Diable the option to switch to Street View too
            var mapOptions = { 
                              zoom: 10,
                              center: new google.maps.LatLng(55.7558, 37.6173),
                              clickableIcons: false,
                              minZoom: 3,
                              maxZoom: 12,
                              streetViewControl: false
                            };
            map = new google.maps.Map(document.getElementById('map'), mapOptions);

            google.maps.event.addListener(map, 'idle', function () { 

                window.clearTimeout(timeout); 

                timeout = window.setTimeout(function () { 
                          getMarkersToPlotFromJava();
                        }); 
            }, 500)
        };

        function getMarkersToPlotFromJava (){

            // Get Bounding Coordinates of currently visible map
            var bounds = map.getBounds();
            var ne = bounds.getNorthEast(); // LatLng of the north-east corner
            var sw = bounds.getSouthWest(); // LatLng of the south-west corder

            // Define the bound requirements for clusters
            var minLat = sw.lat();
            var minLng = sw.lng();
            var maxLat = ne.lat();
            var maxLng = ne.lng();

            //Get zoom level of map
            var zoom = map.getZoom();
            // Define the minimum points and maximum distance requirements based on the current zoom level of map
            var minPoints = 2;
            var maxDistance = 3;


            // Remove all the current markers
            for(i=0; i<markers.length; i++){
                markers[i].setMap(null);
            }
            for(i=0; i<circles.length; i++){
                circles[i].setMap(null);
            }
            markers = [];
            circles = [];

            // Call the Java code to calculate clusters data, and save the returned clusters into a variable
            var markers_data = eval( window.java.mapStateChanged( minLat, minLng, maxLat, maxLng, zoom_relations[zoom].minPoints, zoom_relations[zoom].maxDistance ) );
            
            for( var i=0; i<markers_data.length; i++ ){
              var position = markers_data[i];
              var icon = "data:image/svg+xml,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%2238%22%20height%3D%2238%22%20viewBox%3D%220%200%2038%2038%22%3E%3Cpath%20fill%3D%22%23a22%22%20stroke%3D%22%23ccc%22%20stroke-width%3D%22.5%22%20d%3D%22M34.305%2016.234c0%208.83-15.148%2019.158-15.148%2019.158S3.507%2025.065%203.507%2016.1c0-8.505%206.894-14.304%2015.4-14.304%208.504%200%2015.398%205.933%2015.398%2014.438z%22%2F%3E%3Ctext%20transform%3D%22translate%2819%2018.5%29%22%20fill%3D%22%23fff%22%20style%3D%22font-family%3A%20Arial%2C%20sans-serif%3Bfont-weight%3Abold%3Btext-align%3Acenter%3B%22%20font-size%3D%2212%22%20text-anchor%3D%22middle%22%3E" 
                          + position[2]
                        + "%3C%2Ftext%3E%3C%2Fsvg%3E";

              if(zoom > 11) icon = null; //Default to marker with no number if at city zom level

              markers.push( new google.maps.Marker({
                              position: new google.maps.LatLng( position[0], position[1] ),
                              map: map,
                              title: position[2],
                              text: position[2],
                              icon: icon
                          }) 
              );
              var circle = new google.maps.Circle({
                map: map,
                radius: zoom_relations[zoom].radius,
                fillColor: '#AA0000',
                strokeWeight: 1,
                strokeColor: '#ccc',
                strokeOpacity: 0.5
              });
              circle.bindTo('center', markers[markers.length - 1], 'position');
              circles.push(circle);
            }
        }

      </script>
      <script src="https://maps.googleapis.com/maps/api/js?callback=initMap" async defer></script>
   </body>
</html>