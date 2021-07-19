package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

@MappedSuperclass
public class FeatureGeomObjectId extends EntityObjectId implements Feature {
    @Column(name = "the_geom")
    private Geometry<C2D> geom;

    @Transient
    private Double distancia;

    @Override
    @JsonIgnore
    public Integer getGid() {
        return getObjectId();
    }

    @Override
    public void setGid(Integer gid) {
        this.setObjectId(gid);
    }

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
        return "the_geom";
    }

    @Override
    public String getGidColumn() {
        return "objectid";
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}