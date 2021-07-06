package es.javimolla.city.entity;

import org.geolatte.geom.C2D;
import org.geolatte.geom.Geometry;

public interface Feature {
    public Integer getGid();

    public void setGid(Integer gid);

    public Geometry<C2D> getGeom();

    public void setGeom(Geometry<C2D> geom);

    public void setGeom(String geom);

    public String getGeomColumn();

    public String getGidColumn();
}