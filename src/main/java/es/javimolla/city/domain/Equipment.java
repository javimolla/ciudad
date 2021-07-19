package es.javimolla.city.domain;

import java.util.Map;

import es.javimolla.city.entity.Feature;

public class Equipment {
	private Feature feature;
	private Map<EquipmentField, Object> fieldsValues;

	public Equipment(Feature feature, Map<EquipmentField, Object> fieldsValues) {
		this.feature = feature;
		this.fieldsValues = fieldsValues;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Map<EquipmentField, Object> getFieldsValues() {
		return fieldsValues;
	}

	public void setFieldsValues(Map<EquipmentField, Object> fieldsValues) {
		this.fieldsValues = fieldsValues;
	}
}
