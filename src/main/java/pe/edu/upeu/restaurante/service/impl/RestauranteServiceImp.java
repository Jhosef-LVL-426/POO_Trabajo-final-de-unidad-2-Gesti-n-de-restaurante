package pe.edu.upeu.restaurante.service.impl;
import pe.edu.upeu.restaurante.service.IRestauranteService;
import pe.edu.upeu.restaurante.repository.RestauranteRepository;
public class RestauranteServiceImp implements IRestauranteService {
    private final RestauranteRepository repository;
    public RestauranteServiceImp(RestauranteRepository repository){this.repository=repository;}
    @Override public String contar(String tabla){return String.valueOf(repository.contar(tabla));}
}
