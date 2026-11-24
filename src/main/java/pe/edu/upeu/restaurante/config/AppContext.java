package pe.edu.upeu.restaurante.config;

import java.util.HashMap;
import java.util.Map;
import pe.edu.upeu.restaurante.repository.*;
import pe.edu.upeu.restaurante.service.*;
import pe.edu.upeu.restaurante.service.impl.*;
import pe.edu.upeu.restaurante.controller.*;

/** El mismo contenedor de repositorios, servicios y controladores de SysVentas. */
public class AppContext {
    // Singleton: conserva una sola instancia del contenedor durante la ejecución.
    private static AppContext instance;
    public static synchronized AppContext getInstance() {
        if(instance==null) instance=new AppContext();
        return instance;
    }
    // Inyección de dependencias: relaciona cada interfaz/clase con su objeto.
    private final Map<Class<?>,Object> contenedor=new HashMap<>();
    private AppContext(){registrarRepositorios();registrarServicios();registrarControladores();}
    // Repositorio: acceso a las tablas mediante consultas SQL.
    private void registrarRepositorios(){
        registrar(PlatoRepository.class,new PlatoRepository());
        registrar(MesaRepository.class,new MesaRepository());
        registrar(PedidoRepository.class,new PedidoRepository());
        registrar(ReservaRepository.class,new ReservaRepository());
        registrar(InsumoRepository.class,new InsumoRepository());
        registrar(RestauranteRepository.class,new RestauranteRepository());
    }
    // Servicio: lógica del restaurante que llama al repositorio.
    private void registrarServicios(){
        registrar(AutenticacionService.class, new AutenticacionService());
        registrar(IPlatoService.class,new PlatoServiceImp(getBean(PlatoRepository.class)));
        registrar(IMesaService.class,new MesaServiceImp(getBean(MesaRepository.class)));
        registrar(IPedidoService.class,new PedidoServiceImp(getBean(PedidoRepository.class)));
        registrar(IReservaService.class,new ReservaServiceImp(getBean(ReservaRepository.class)));
        registrar(IInsumoService.class,new InsumoServiceImp(getBean(InsumoRepository.class)));
        registrar(IRestauranteService.class,new RestauranteServiceImp(getBean(RestauranteRepository.class)));
    }
    // Controlador: conecta eventos de las vistas FXML con los servicios.
    private void registrarControladores(){
        registrar(LoginController.class,new LoginController(getBean(AutenticacionService.class)));
        registrar(MainGuiController.class,new MainGuiController());
        registrar(ResumenController.class,new ResumenController(getBean(IRestauranteService.class),getBean(IPedidoService.class)));
        registrar(PlatoController.class,new PlatoController(getBean(IPlatoService.class)));
        registrar(PedidoController.class,new PedidoController(getBean(IPedidoService.class),getBean(IPlatoService.class),getBean(IMesaService.class)));
        registrar(HistorialController.class,new HistorialController(getBean(IPedidoService.class)));
        registrar(MesaController.class,new MesaController(getBean(IMesaService.class)));
        registrar(ReservaController.class,new ReservaController(getBean(IReservaService.class)));
        registrar(InsumoController.class,new InsumoController(getBean(IInsumoService.class)));
    }
    private void registrar(Class<?> tipo,Object bean){contenedor.put(tipo,bean);}
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo){
        Object bean=contenedor.get(tipo);
        if(bean==null) bean=contenedor.values().stream().filter(b->tipo.isAssignableFrom(b.getClass())).findFirst()
            .orElseThrow(()->new IllegalStateException("Bean no registrado: "+tipo.getName()));
        return (T)bean;
    }
}
