-- Example of a table that stores the city streets
CREATE TABLE streets (
	gid int4 NOT NULL,
	street_id int4 NOT NULL,
	street_name varchar(75) NOT NULL,
	CONSTRAINT streets_pkey PRIMARY KEY (gid)
);
CREATE UNIQUE INDEX streets_id_idx ON streets USING btree (street_id);

-- Example of a table that stores the city addresses
CREATE TABLE portals (
	gid int4 NOT NULL,
	street_id int4 NOT NULL,
	portal_number int4 NULL,
	the_geom geometry NULL,
	CONSTRAINT portals_pk PRIMARY KEY (gid),
	CONSTRAINT streets_portals_fk FOREIGN KEY (street_id) REFERENCES streets(street_id) ON DELETE CASCADE
);
CREATE INDEX portals_geom_idx ON portals USING GIST(the_geom);

-- This table stores the generated isochrones
CREATE TABLE isochrones (
	gid int4 NOT NULL,
	the_geom geometry NULL,
	CONSTRAINT isochrones_pk PRIMARY KEY (gid)
);
CREATE INDEX isochrones_geom_idx ON isochrones USING GIST(the_geom);

-- This table stores the relationship between the portal and its isochrone
CREATE TABLE isochrones_portals (
	gid int4 NOT NULL,
	isochrone int4 NOT NULL,
	CONSTRAINT isochrones_portals_pk PRIMARY KEY (gid, isochrone),
	CONSTRAINT isochrones_isochro_portals_fk FOREIGN KEY (isochrone) REFERENCES isochrones(gid) ON DELETE CASCADE,
	CONSTRAINT portals_isochro_portals_fk FOREIGN KEY (gid) REFERENCES portals(gid) ON DELETE CASCADE
);

-- This table stores the number of equipments by layer
CREATE TABLE isochrones_statistics (
	gid int4 NOT NULL,
	layer text NOT NULL,
	equipments int4 NOT NULL,
	CONSTRAINT isochrones_statistics_pk PRIMARY KEY (gid, layer),
	CONSTRAINT isochrones_isochro_stats_fk FOREIGN KEY (gid) REFERENCES isochrones(gid) ON DELETE CASCADE
);
