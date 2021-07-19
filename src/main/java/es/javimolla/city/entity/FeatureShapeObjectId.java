package es.javimolla.city.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;

@MappedSuperclass
public class FeatureShapeObjectId extends EntityObjectId implements Feature {
    @Column(name = "shape")
    private Geometry<C2D> geom;

    @Transient
    private Double distancia;

    @Override
    public Integer getGid() {
        return getObjectId();
    }

    @Override
    public void setGid(Integer gid) {
        this.setObjectId(gid);
    }

    @Override
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
        return "objectid";
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}
