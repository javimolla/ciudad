package es.javimolla.city.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.NaturalId;

import lombok.Data;

@Embeddable
@Data
public class IsochroneStatisticPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@NaturalId
	@Column(name = "gid")
	private Integer gid;	
	@NaturalId
	@Column(name = "layer")
	private String layer;
}
