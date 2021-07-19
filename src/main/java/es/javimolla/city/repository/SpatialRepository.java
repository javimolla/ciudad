package es.javimolla.city.repository;

import java.util.List;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.crs.ProjectedCoordinateReferenceSystem;

import es.javimolla.city.entity.Feature;

public interface SpatialRepository {
    public ProjectedCoordinateReferenceSystem getETRS89CoordinateSystem();

    public List<Feature> findIntersectsByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
            String where) throws Exception;

    public List<Feature> findIntersectsByGeom(String table, Geometry<C2D> geometry, Integer distance,
            String where) throws Exception;

    public List<Feature> findContainsByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
            String where) throws Exception;

    public List<Feature> findContainsByGeom(String table, Geometry<C2D> geometry, Integer distance, String where)
            throws Exception;

    public List<Feature> findContainedByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
            String where) throws Exception;

    public List<Feature> findContainedByGeom(String table, Geometry<C2D> geometry, Integer distance, String where)
            throws Exception;

    public List<Feature> findByWhere(Feature feature, String where) throws Exception;

    public List<Feature> findByWhere(String table, String where) throws Exception;
}
