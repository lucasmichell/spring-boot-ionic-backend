package com.lucasmichell.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasmichell.cursomc.domain.Categoria;
import com.lucasmichell.cursomc.domain.Pedido;
import com.lucasmichell.cursomc.repositories.CategoriaRepository;
import com.lucasmichell.cursomc.repositories.PedidoRepository;
import com.lucasmichell.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	
	public Pedido buscar(Integer id) 
	{  
		Optional<Pedido> obj = repo.findById(id);  
		return obj.orElseThrow(() -> 
			new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}	
}
