package es.javimolla.city.domain;

public class EquipmentField {
	private String name;
	private String alias;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EquipmentField)) {
			return false;
		}
		EquipmentField castOther = (EquipmentField) other;
		return castOther.name.equals(name);
	}
}
