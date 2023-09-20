package com.example.micasaapp.Model

class CategoriaModel {
    var id = 0
    var nombre   = ""
    var descripcion  = ""
    var imgPortada = 0

    constructor(nombre: String, descripcion: String, imgPortada: Int) {
        this.nombre = nombre
        this.descripcion = descripcion
        this.imgPortada = imgPortada
    }
}