package com.lucasmichell.cursomc.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucasmichell.cursomc.domain.Categoria;
import com.lucasmichell.cursomc.domain.Cidade;
import com.lucasmichell.cursomc.domain.Cliente;
import com.lucasmichell.cursomc.domain.Endereco;
import com.lucasmichell.cursomc.domain.enums.TipoCliente;
import com.lucasmichell.cursomc.dto.ClienteDTO;
import com.lucasmichell.cursomc.dto.ClienteNewDTO;
import com.lucasmichell.cursomc.repositories.ClienteRepository;
import com.lucasmichell.cursomc.repositories.EnderecoRepository;
import com.lucasmichell.cursomc.services.exceptions.DataIntegrityException;
import com.lucasmichell.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
		
	public Cliente find(Integer id) 
	{  
		Optional<Cliente> obj = repo.findById(id);  
		return obj.orElseThrow(() -> 
			new ObjectNotFoundException("Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}	
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);		
		enderecoRepository.saveAll(obj.getEnderecos());
		
		return obj;		
	}
	
	
	public List<Cliente> findAll() 
	{  
		return repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String direction, String orderBy){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage,org.springframework.data.domain.Sort.Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}	
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj,obj);
		return repo.save(newObj);
	}
	
	public void delete (Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente por que há entidades relacionadas");
		}		
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(),null,null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli =  new Cliente(null, objDto.getNome(), objDto.getEmail(),objDto.getCpfOucnpj(),TipoCliente.toEnum(objDto.getTipo()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(),objDto.getNumero(),objDto.getComplemento(),objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2() != null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}			
		if(objDto.getTelefone3() != null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}			
		
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
