package es.javimolla.city.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.javimolla.city.domain.Equipment;
import es.javimolla.city.domain.EquipmentField;
import es.javimolla.city.domain.EquipmentLayer;
import es.javimolla.city.entity.Feature;
import es.javimolla.city.entity.Isochrone;
import es.javimolla.city.utils.CollectionUtils;

@Repository
@Transactional
public class EquipmentRepository {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SpatialRepositoryImpl spatialRepositoryImpl;

	public List<Equipment> findAllByIsochrone(EquipmentLayer layer, Isochrone isochrone) throws Exception {
		return getEquipments(layer,
				spatialRepositoryImpl.findIntersectsByGeom(layer.getName(), isochrone.getGeom(), null, null));
	}

	public List<Equipment> findAllByGeom(EquipmentLayer layer, Geometry<C2D> geom) throws Exception {
		return getEquipments(layer, spatialRepositoryImpl.findIntersectsByGeom(layer.getName(), geom,
				Isochrone.DISTANCE_WALKING, null));
	}

	private List<Equipment> getEquipments(EquipmentLayer layer, List<Feature> features) {
		String sqlFields = String.join(", ", getFieldsNames(layer.getFields()));
		return features.stream().map(feature -> getEquipment(layer, sqlFields, feature)).collect(Collectors.toList());
	}

	private List<String> getFieldsNames(List<EquipmentField> fields) {
		return fields.stream().map(EquipmentField::getName).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Equipment getEquipment(EquipmentLayer layer, String sqlFields, Feature feature) {
		return new Equipment(feature, (Map<EquipmentField, Object>) CollectionUtils.listsToMap(layer.getFields(),
				getAllValuesByFeature(layer.getName(), sqlFields, feature)));
	}

	@SuppressWarnings("unchecked")
	private List<Object> getAllValuesByFeature(String table, String sqlFields, Feature feature) {
		List<Object> values = entityManager.createNativeQuery(String.format("select %s from %s where %s = %d",
				sqlFields, table, feature.getGidColumn(), feature.getGid())).getResultList();
		if (values.get(0) instanceof Object[]) {
			return Arrays.asList((Object[]) values.get(0));
		} else {
			return values;
		}
	}
}