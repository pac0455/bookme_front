package com.example.frontendapp.data.local.filtros

import com.example.frontendapp.data.model.Negocio.Negocio

interface FiltroNegocio {
    fun aplicar(lista: List<Negocio>): List<Negocio> //Lista virgen de negocios
}

//Logica de fitro por nombre
class FiltroPorNombre(private val nombre: String) : FiltroNegocio {
    override fun aplicar(lista: List<Negocio>): List<Negocio> {
        return lista.filter { it.nombre.contains(nombre, ignoreCase = true) }
    }
}
//Logica de fitro por categoria
class FiltroPorCategoria(private val categoria: String) : FiltroNegocio {
    override fun aplicar(lista: List<Negocio>): List<Negocio> {
        return lista.filter {
            it.let{
                categoria.contains(categoria, ignoreCase = true)
            }
        }
    }
}

class FiltroContext {
    private val filtros = mutableListOf<FiltroNegocio>()

    fun agregarFiltro(filtro: FiltroNegocio) {
        filtros.add(filtro)
    }

    fun removerFiltro(filtro: FiltroNegocio) {
        filtros.remove(filtro)
    }

    fun limpiarFiltros() {
        filtros.clear()
    }

    fun ejecutar(listaOriginal: List<Negocio>): List<Negocio> {
        //Se devuelven todos los filtros aplicados
        return filtros.fold(listaOriginal) { acc, filtro -> filtro.aplicar(acc) }
    }
}
