package com.example.despensadesonia.DataClass;

public class Articulo {
    public int id;
    public String propietario;
    public String nombreProducto;
    public String cantAct;
    public String cantMin;
    public String fechaUltCompra;
    public String fechaCaducidadTop;

    public Articulo(int id, String propietario, String nombreProducto, String cantAct,
                    String cantMin, String fechaUltCompra, String fechaCaducidadTop) {
        this.id = id;
        this.propietario = propietario;
        this.nombreProducto = nombreProducto;
        this.cantAct = cantAct;
        this.cantMin = cantMin;
        this.fechaUltCompra = fechaUltCompra;
        this.fechaCaducidadTop = fechaCaducidadTop;
    }
}
