package es.javimolla.city.domain;

import java.util.List;

public class EquipmentLayer {
	private String name;
	private String description;
	private List<EquipmentField> fields;
	private List<EquipmentInterval> intervals;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<EquipmentField> getFields() {
		return fields;
	}

	public void setFields(List<EquipmentField> fields) {
		this.fields = fields;
	}

	public List<EquipmentInterval> getIntervals() {
		return intervals;
	}

	public void setIntervals(List<EquipmentInterval> intervals) {
		this.intervals = intervals;
	}
}