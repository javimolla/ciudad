package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

@MappedSuperclass
public class FeatureShapeGid extends EntityGid implements Feature {
    @Column(name = "shape")
    private Geometry<C2D> geom;

    @Transient
    private Double distancia;

    @Override
    @JsonIgnore
    public Geometry<C2D> getGeom() {
        return geom;
    }

    @Override
    public void setGeom(Geometry<C2D> geom) {
        this.geom = geom;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setGeom(String geom) {
        this.geom = (Geometry<C2D>) Wkt.fromWkt(geom);
    }

    @Override
    public String getGeomColumn() {
        return "shape";
    }

    @Override
    public String getGidColumn() {
        return "gid";
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}
