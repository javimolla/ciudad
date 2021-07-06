-- This function generates the isochrone of a vertex from the OSM network
-- TODO: It uses 1.250 meters as a fixed distance, but it could receive it by parameter
-- TODO: It could also have a parameter of the radius applied to the alpha shape
CREATE OR REPLACE FUNCTION get_isochrone(vertex_id integer)
 RETURNS geometry
 LANGUAGE plpgsql
AS $FUNCTION$
DECLARE
isochrone geometry;
BEGIN
	DROP TABLE IF EXISTS isochrones_temp;
	CREATE TABLE isochrones_temp AS (
		WITH ways_lines AS (
			SELECT vertex_id AS gid, ST_Transform(l.the_geom, 25830) AS the_geom
			FROM pgr_drivingDistance('SELECT gid AS id, source, target, length_m AS cost FROM ways', vertex_id, 1250, false) AS foo join ways l ON l.gid = foo.edge
		), ways_points AS (
			SELECT gid, ST_StartPoint(the_geom) AS the_geom FROM ways_lines
			UNION
			SELECT gid, ST_EndPoint(the_geom) AS the_geom FROM ways_lines
		)
		SELECT gid, the_geom FROM ways_points
	);
	WITH alphashape AS (
		SELECT pgr_alphaShape('SELECT gid AS id, ST_X(the_geom) AS x, ST_Y(the_geom) AS y FROM isochrones_temp WHERE gid = ' || vertex_id)
	), alphapoints AS (
		SELECT ST_SetSRID(ST_MakePoint((pgr_alphaShape).x, (pgr_alphaShape).y), 25830) FROM alphashape
	), alphaline AS (
		SELECT ST_Makeline(ST_SetSRID) FROM alphapoints
	)
	SELECT ST_Buffer(ST_MakePolygon(ST_AddPoint(ST_Makeline, ST_StartPoint(ST_Makeline))), 30) AS the_geom FROM alphaline INTO isochrone;
	RETURN isochrone;
 EXCEPTION
	WHEN OTHERS THEN
		RETURN NULL;
END;
$FUNCTION$
;

-- This function finds the nearest vertex of the OSM network to the geometry of a portal, then it generates the isochrone by calling the get_isochrone function and, finally, stores the isochrone and its relationship with the portal on an intermediate table
-- TODO: It checks if the isochrone has already been generated and it does not generate it again. It could receive a parameter indicating if we want this check or not
CREATE OR REPLACE FUNCTION calculate_isochrones(geom geometry, geom_id integer)
 RETURNS boolean
 LANGUAGE plpgsql
AS $FUNCTION$
DECLARE
vertex_id int4;
origin_check int4;
BEGIN
	SELECT id::int4 FROM ways_vertices_pgr order by ST_Distance(the_geom, ST_Transform(geom, 4326)) LIMIT 1 INTO vertex_id;
	SELECT COUNT(*) FROM isochrones WHERE gid = vertex_id INTO origin_check;
	IF (origin_check = 0) THEN
        INSERT INTO isochrones (gid, the_geom) VALUES (vertex_id, get_isochrone(vertex_id));
	END IF;
	DELETE FROM isochrones_portals WHERE gid = geom_id;
	INSERT INTO isochrones_portals (gid, isochrone) VALUES (geom_id, vertex_id);
	RETURN true;
 EXCEPTION
	WHEN OTHERS THEN
		RETURN false;
END;
$FUNCTION$
;
