# Correspondencia con SysVentas (dev-Sesion11)

| SysVentas | Gestión de Restaurante |
|---|---|
| `SysVentas.java` carga `login.fxml` | `RestauranteApp.java` carga `login.fxml` |
| `AppContext` registra repositorios, servicios y controladores | `config/AppContext` con la misma responsabilidad |
| `FXMLLoader.setControllerFactory(appContext::getBean)` | La misma conexión FXML/controlador |
| `MainGuiController` y vistas FXML | `MainGuiController` con menú y siete vistas FXML |
| `ProductoController`, `IProductoService`, `ProductoServiceImp`, `ProductoRepository` | `PlatoController`, `IPlatoService`, `PlatoServiceImp`, `PlatoRepository` |
| Venta, detalle, carrito | Pedido, detalle y `PedidoItemDTO` |
| `AbstractJpaRepository` y `SqlHelper` (JDBC) | Clases copiadas del avance y adaptadas al paquete |
| `DatabaseConfig` (HikariCP + H2 + DDL) | Mismo patrón con base `restaurante` y esquema propio |
| `TableViewHelper` | Se conserva el helper para tablas del proyecto del restaurante |

## Funciones conservadas

1. Resumen: cantidad de platos, pedidos, mesas, reservas y últimos pedidos.
2. Carta: alta de platos, edición con doble clic, eliminación y disponibilidad.
3. Pedidos: selección de mesa libre, selección de plato, cantidad, cliente y total; registro y detalle.
4. Historial: vista de pedidos, consulta del detalle y actualización de estados.
5. Mesas: alta, modificación de disponibilidad y eliminación de mesas libres.
6. Reservas: alta y cancelación.
7. Inventario: alta, listado con alerta por stock mínimo y eliminación.

## Correcciones técnicas respecto del ZIP recibido

- La lógica que antes estaba mezclada en `RestauranteApp` se encuentra ahora en las capas correspondientes.
- Se eliminaron las consultas SQL directas de los controladores.
- Cada vista tiene un controlador cargado por `AppContext`.
- Los pedidos conservan los IDs de los platos y se guardan con `BigDecimal` y una transacción.
- La configuración de persistencia sigue la de SysVentas sin alterar el contenido funcional del restaurante.

La entrega incorpora los perfiles de Administración y Atención mediante el acceso existente. Se mantuvo la estructura de los siete módulos.

## Ajustes finales de organización

- Solo se conserva `src/main/resources/schema_restaurante.sql` para crear las tablas; se quitó `database.sql` de la raíz por estar duplicado.
- Los cinco modelos y `PedidoItemDTO` se escribieron como clases Java con constructores y getters/setters, sin `record`, igual que el estilo de modelos utilizado en SysVentas. No se añadieron nuevas bibliotecas.
- La hoja de estilos permanece en `src/main/resources/css/style.css` y las pantallas en `src/main/resources/view/`.
