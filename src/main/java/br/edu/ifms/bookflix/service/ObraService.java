package br.edu.ifms.bookflix.service;

import br.edu.ifms.bookflix.model.Obra;

import br.edu.ifms.bookflix.repository.ObraRepository;

import br.edu.ifms.bookflix.dto.ObraDTO;

import br.edu.ifms.bookflix.projection.ObraView;

import br.edu.ifms.bookflix.service.exception.ObjectNotFoundException;
import br.edu.ifms.bookflix.service.exception.DataIntegrityException;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObraService {

	@Autowired
	private ObraRepository obrasRepository;
	private ObraDTO obrasDTO = new ObraDTO(); 
	
	public Obra find(Integer id) {
		Optional<Obra> objeto = obrasRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Obra não encontrada! Id: " + id));		
	}
	
	@Transactional
	public Obra insert (Obra objeto) {
		objeto.setId(null);
		obrasRepository.save(objeto);
		return objeto;	
	}
	
	public Obra update(Obra objetoEditado) {
		Obra objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return obrasRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id) {
		find(id);
		try {
			obrasRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public List<Obra> findAll() {
		return allObrasFound();
	}
	
	@Transactional
	public void deleteById(Integer id) {
		obrasRepository.deleteById(id);
	}
	
	public void save(Obra obra) {
		obrasRepository.saveAndFlush(obra);
	}
	
	public Obra findByIfsn(String ifsn) {
		Optional<Obra> objeto = obrasRepository.findByIfsn(ifsn); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Obra não encontrada! IFSN: " + ifsn));		
	}
    
    public Obra fromDTO(ObraDTO objetoDTO) {
    	return obrasDTO.fromDTO(objetoDTO);
    }
	
    public Obra fromNewDTO(ObraDTO objetoNewDTO) {
    	return obrasDTO.fromNewDTO(objetoNewDTO);
    }
	
	public List<ObraView> searchObra(String pesquisa){
		return obrasRepository.searchObra(unaccentedParam(pesquisa));
	}
	
	public List<ObraView> searchObraByIfsn(String ifsn) {
		return obrasRepository.searchObraByIfsn(unaccentedParam(ifsn));
	}
	
	public List<ObraView> searchObraByTitulo(String titulo) {
		return obrasRepository.searchObraByTitulo(unaccentedParam(titulo));
	}
	
	public List<ObraView> searchObraByArea(String area) {
		return obrasRepository.searchObraByArea(unaccentedParam(area));
	}
	
	public List<ObraView> searchObraByAno(String ano) {
		return obrasRepository.searchObraByAno(convertParamToInt(ano));
	} 

	private List<Obra> allObrasFound() {
		return obrasRepository.findAll();
	}
    
    public Obra obraWithoutSomeAtributes(Obra objeto) {
    	return obrasDTO.obraWithoutSomeAttributes(objeto);
    }
    
    private String unaccentedParam(String parameter) {
    	return Normalizer.normalize(parameter, Normalizer.Form.NFD)
    			.replaceAll("[^\\p{ASCII}]",  "");
    }
    
    public void intAnoParamaterValidator(String param) {
    	if(!param.matches("[0-9]+")) {
    		throw new IllegalArgumentException("O ano é formado apenas por números inteiros");
    	}
    }
    
    private int convertParamToInt(String param) {
    	String paramOnlyWithNumbers = param.replaceAll("[^0-9]", "");
    	return Integer.parseInt(paramOnlyWithNumbers);
    }
    
    public void stringParameterValidator(String param) {
    	if(param == null) {
    		throw new IllegalArgumentException("O parâmetro de busca não pode ser nulo");
    	}
    	if(param.isBlank()) {
    		throw new IllegalArgumentException("O parâmetro de busca não pode estar em branco");
    	}
    }
    
    public void intParamaterValidator(String param) {
    	if(!param.matches("[0-9]+")) {
    		throw new IllegalArgumentException("O parâmetro tem que ser um número inteiro");
    	}
    }
    
    public void validateObraId(Integer paramPathId, Integer obraBodyId) {
    	if(!paramPathId.equals(obraBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id da obra informada no corpo da solicitação");
    	}
    }
    
}
