# Caso y requerimientos

Un restaurante pequeño necesita llevar un registro local de sus ventas y reservas. El personal de atención registra el consumo, verifica el estado de los pedidos y anota las reservas. La administración mantiene la lista de platos y su disponibilidad, el número y capacidad de las mesas y las existencias mínimas de los insumos.

## Requerimientos funcionales

| Requerimiento | Comprobación |
| --- | --- |
| Autenticar al personal y mostrar sus opciones | Administración ve los siete módulos. Atención accede a resumen, pedidos, historial y reservas. |
| Consultar el resumen | Ver los recuentos de platos, pedidos, mesas y reservas y hasta ocho pedidos recientes. |
| Administrar la carta | Crear, editar y eliminar platos. Marcar un plato como disponible y verificar que aparezca al registrar un pedido. |
| Registrar pedidos | Agregar platos y cantidades, ver los importes, registrar el pedido y ocupar la mesa cuando esté asignada. |
| Consultar el historial | Seleccionar un pedido, ver su detalle y actualizar su estado. Entregar o cancelar libera la mesa asignada. |
| Administrar mesas | Crear un número único con capacidad positiva, cambiar su disponibilidad y eliminar una mesa libre. |
| Gestionar reservas | Registrar cliente, fecha, hora y número de personas. Finalizar o cancelar una reserva confirmada y conservar su registro. |
| Controlar el inventario | Registrar insumos, editar su cantidad actual o eliminarlos. Mostrar la necesidad de reabastecer si la cantidad actual es igual o menor a la mínima. |
| Validar y comunicar el resultado | Mostrar mensajes si faltan datos, los valores son inválidos o se completó la acción. |

## Base de datos

El archivo `GestionRestaurante_H2.architect` representa el esquema contenido en `src/main/resources/schema_restaurante.sql`. Se puede abrir en SQL Power Architect mediante **File → Open Project**. El archivo tiene las siete tablas y la restricción de número único de mesa que existen en el esquema H2. Las referencias de pedido a mesa, detalle a pedido y detalle a plato se gestionan en las operaciones JDBC. `clientes` está prevista en el esquema. Los formularios de pedido y reserva guardan el nombre del cliente en su propio registro.

## Investigación adicional: verificación de contraseñas con PBKDF2

`AutenticacionService` compara un hash derivado con `PBKDF2WithHmacSHA256`. Cada cuenta de demostración tiene una sal distinta y la derivación usa 600 000 iteraciones. Al iniciar sesión, se deriva un hash con la contraseña ingresada, se compara con el valor configurado y se asigna el perfil si coinciden. La implementación utiliza las clases criptográficas de Java.

Las credenciales de la guía y del manual están destinadas a demostrar los perfiles en clase.

Referencia: [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2).

## Investigación aplicada en la carta

El buscador acepta fragmentos, mayúsculas y nombres escritos sin tilde. En `PlatoController`, `Normalizer` separa las letras de sus marcas de acentuación. El método `normalizar` quita esas marcas y pasa el texto a minúsculas. El controlador compara el nombre de cada plato con el texto ingresado mediante `contains`. Reutiliza la lista cargada y conserva la escritura original para mostrarla y guardarla.

Para la demostración, abre **Pedidos > Carta / Platos** con el perfil Administración. Si hace falta, registra **Ají de gallina**. Escribe **aji**, **AJÍ** y **gallina**. El plato aparece en los tres casos. Borra la búsqueda para recuperar la carta completa.

El modelo visual `GestionRestaurante_Modelo_Aplicacion.architect` muestra las asociaciones usadas por la aplicación. El archivo `GestionRestaurante_H2.architect` documenta por separado las restricciones físicas existentes en H2.
