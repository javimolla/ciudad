package es.javimolla.city.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.geolatte.geom.crs.CrsRegistry;
import org.geolatte.geom.crs.LinearUnit;
import org.geolatte.geom.crs.ProjectedCoordinateReferenceSystem;
import org.springframework.stereotype.Repository;

import es.javimolla.city.entity.Feature;
import es.javimolla.city.entity.FeatureGeomGid;
import es.javimolla.city.entity.FeatureGeomObjectId;
import es.javimolla.city.entity.FeatureShapeGid;
import es.javimolla.city.entity.FeatureShapeObjectId;
import es.javimolla.city.utils.EntityUtils;

@Repository
public class SpatialRepositoryImpl implements SpatialRepository {
	private static Map<String, Class<?>> featuresTables = new ConcurrentHashMap<>();

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public ProjectedCoordinateReferenceSystem getETRS89CoordinateSystem() {
		return CoordinateReferenceSystems.mkProjected(CrsRegistry.getCrsIdForEPSG(25830), LinearUnit.METER);
	}

	@Override
	public List<Feature> findIntersectsByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
			String where) throws Exception {
		return findByGeomImpl(feature, geometry, distance, "st_intersects", where);
	}

	@Override
	public List<Feature> findIntersectsByGeom(String table, Geometry<C2D> geometry, Integer distance,
			String where) throws Exception {
		return findByGeomImpl(table, getFeatureTable(table), geometry, distance, "st_intersects", where);
	}

	@Override
	public List<Feature> findContainsByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
			String where) throws Exception {
		return findByGeomImpl(feature, geometry, distance, "st_contains", where);
	}

	@Override
	public List<Feature> findContainsByGeom(String table, Geometry<C2D> geometry, Integer distance, String where)
			throws Exception {
		return findByGeomImpl(table, getFeatureTable(table), geometry, distance, "st_contains", where);
	}

	@Override
	public List<Feature> findContainedByGeom(Feature feature, Geometry<C2D> geometry, Integer distance,
			String where) throws Exception {
		return findByGeomImpl(feature, geometry, distance, "st_within", where);
	}

	@Override
	public List<Feature> findContainedByGeom(String table, Geometry<C2D> geometry, Integer distance, String where)
			throws Exception {
		return findByGeomImpl(table, getFeatureTable(table), geometry, distance, "st_within", where);
	}

	@Override
	public List<Feature> findByWhere(Feature feature, String where) throws Exception {
		return findByConditionImpl(feature, where);
	}

	@Override
	public List<Feature> findByWhere(String table, String where) throws Exception {
		return findByConditionImpl(table, getFeatureTable(table), where);
	}

	@SuppressWarnings("unchecked")
	private List<Feature> findByGeomImpl(Feature feature, Geometry<C2D> geometry, Integer distance, String method,
			String where) throws Exception {
		List<Object[]> results = entityManager.createQuery(String.format(
				"SELECT e, cast(st_distance(e.geom, :geometry) as float) AS distance FROM %s e WHERE %s(e.geom, st_buffer(:geometry, :distance)) = true %s ORDER BY distance",
				feature.getClass().getSimpleName(), method, where != null ? "AND " + where : ""))
				.setParameter("distance", distance != null ? distance : 0.0001)
				.setParameter("geometry", Wkt.toWkt(geometry).replaceFirst("SRID\\=25830\\;", "")).getResultList();

		return results.stream().map(f -> {
			try {
				EntityUtils.setProperty(f[0], "distance", f[1]);
			} catch (Exception e) {
			}
			return (Feature) f[0];
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<Feature> findByGeomImpl(String table, Feature feature, Geometry<C2D> geometry, Integer distance,
			String method, String where) throws Exception {
		if (geometry == null)
			return findByConditionImpl(table, feature, where);

		String geomColumn = feature.getGeomColumn(), gidColumn = feature.getGidColumn();
		List<String> extraFields = getExtraFields(feature, geomColumn, gidColumn);

		List<Object[]> found = entityManager.createNativeQuery(String.format(
				"SELECT st_astext(GEOM_COLUMN)::::text as geom, %s as gid, st_distance(GEOM_COLUMN, :geometry) AS distance %s FROM %s WHERE %s(GEOM_COLUMN, st_buffer(:geometry, :distance)) %s ORDER BY distance"
						.replaceAll("GEOM_COLUMN", geomColumn),
				gidColumn, extraFields.size() == 0 ? "" : ", " + String.join(", ", extraFields), table, method,
				where != null ? "AND " + where : ""))
				.setParameter("distance", distance != null ? distance : 0.0001).setParameter("geometry", geometry)
				.getResultList();

		extraFields.add(0, geomColumn);
		extraFields.add(1, gidColumn);
		extraFields.add(2, "distance");
		List<Feature> features = new ArrayList<>(found.size());
		for (Object[] find : found) {
			features.add(getFeature(feature, extraFields, find));
		}
		return features;
	}

	@SuppressWarnings("unchecked")
	private List<Feature> findByConditionImpl(Feature feature, String where) throws Exception {
		return entityManager.createQuery(String.format("SELECT e FROM %s e %s", feature.getClass().getSimpleName(),
				where != null ? "WHERE " + where : "")).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Feature> findByConditionImpl(String table, Feature feature, String where) throws Exception {
		String geomColumn = feature.getGeomColumn(), gidColumn = feature.getGidColumn();
		List<String> fieldsExtra = getExtraFields(feature, geomColumn, gidColumn);

		List<Object[]> found = entityManager.createNativeQuery(String.format(
				"SELECT st_astext(GEOM_COLUMN)::::text as geom, %s as gid %s FROM %s %s".replaceAll("GEOM_COLUMN",
						geomColumn),
				gidColumn, fieldsExtra.size() == 0 ? "" : ", " + String.join(", ", fieldsExtra), table,
				where != null ? "WHERE " + where : "")).getResultList();

		fieldsExtra.add(0, geomColumn);
		fieldsExtra.add(1, gidColumn);
		List<Feature> features = new ArrayList<>(found.size());
		for (Object[] find : found) {
			features.add(getFeature(feature, fieldsExtra, find));
		}
		return features;
	}

	@SuppressWarnings("unchecked")
	private Feature getFeatureTable(String table) throws InstantiationException, IllegalAccessException {
		Class<?> cls = featuresTables.get(table);
		if (cls != null) {
			return (Feature) cls.newInstance();
		}

		List<String> fields = entityManager
				.createNativeQuery(
						"SELECT lower(column_name) FROM information_schema.columns WHERE lower(table_name) = :table")
				.setParameter("table", table).getResultList();

		if (fields.contains("the_geom") && fields.contains("gid")) {
			featuresTables.put(table, FeatureGeomGid.class);
			return new FeatureGeomGid();
		}
		if (fields.contains("the_geom") && fields.contains("objectid")) {
			featuresTables.put(table, FeatureGeomObjectId.class);
			return new FeatureGeomObjectId();
		}
		if (fields.contains("shape") && fields.contains("gid")) {
			featuresTables.put(table, FeatureShapeGid.class);
			return new FeatureShapeGid();
		}

		featuresTables.put(table, FeatureShapeObjectId.class);
		return new FeatureShapeObjectId();
	}

	private List<String> getExtraFields(Feature feature, String gidColumn, String geomColumn) {
		List<String> extraFields = EntityUtils.getPropertyFields(feature.getClass());
		extraFields.remove(gidColumn);
		extraFields.remove(geomColumn);
		return extraFields;
	}

	private Feature getFeature(Feature feature, List<String> fields, Object[] data) throws Exception {
		Feature featureFound = feature.getClass().newInstance();
		data[0] = (Geometry<C2D>) Wkt.fromWkt((String) data[0], getETRS89CoordinateSystem());
		data[1] = (Integer) data[1];
		EntityUtils.fillEntity(featureFound, fields, data);
		return featureFound;
	}
}