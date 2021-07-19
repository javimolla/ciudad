package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class EntityGid {
	@Id
	@Column(name = "gid")
	private Integer gid;
}
