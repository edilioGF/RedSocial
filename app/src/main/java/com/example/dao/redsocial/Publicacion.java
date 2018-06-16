package com.example.dao.redsocial;

public class Publicacion {
    private String titulo;
    private String descripcion;
    private String imagen;
    private String location;

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitulo() {

        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getLocation() {
        return location;
    }

    public Publicacion(String titulo, String descripcion, String imagen, String location) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.location = location;
    }

    public Publicacion() {
    }
}
