# MoneyTracker - Control de Gastos Personales

MoneyTracker es una aplicación de Android para el registro y control de gastos e ingresos personales. Cuenta con almacenamiento local persistente, configuración de usuario y consulta de tasas de cambio en tiempo real.

## ✨ Funcionalidades Principales

- **Dashboard Personalizado:**
  - **Resumen del Mes:** Visualiza tus ingresos, gastos y el balance actual de un solo vistazo.
  - **Progreso de Presupuesto:** Una barra de progreso te muestra qué tan cerca estás de tu presupuesto mensual, con alertas visuales si superas ciertos umbrales.
  - **Transacciones Recientes:** Acceso rápido a tus últimos movimientos registrados.
  - **Saludo Personalizado:** Muestra el nombre del usuario para una experiencia más amigable.

- **Gestión de Transacciones:**
  - **Añadir Transacciones:** Un formulario intuitivo permite registrar nuevos ingresos o gastos, seleccionando monto, categoría, fecha y método de pago.
  - **Categorías Dinámicas:** Las categorías se cargan desde la base de datos y se filtran automáticamente según si se registra un ingreso o un gasto.

- **Estadísticas Visuales:**
  - **Gráfico de Gastos:** Un gráfico circular (`PieChart`) muestra la distribución de tus gastos por categoría, permitiéndote entender a dónde va tu dinero.
  - **Promedio Diario:** Calcula y muestra tu gasto promedio diario para el mes actual.
  - **Desglose por Categoría:** Una lista detallada complementa el gráfico con los montos y porcentajes exactos de cada categoría.

- **Tasas de Cambio en Tiempo Real:**
  - **Consulta a API:** Se conecta a `ExchangeRate-API` para obtener las últimas tasas de cambio con respecto a una moneda base (USD por defecto).
  - **Manejo de Conectividad:** Muestra un indicador de carga y maneja posibles errores si no hay conexión a internet.

- **Configuración del Usuario:**
  - **Configuración Inicial:** En el primer inicio, la app solicita al usuario su nombre para personalizar la experiencia.
  - **Pantalla de Configuración:** Permite al usuario modificar su nombre y establecer un presupuesto mensual personalizado en cualquier momento. Los cambios se guardan y reflejan inmediatamente en toda la aplicación.

## 🛠️ Stack Técnico y Librerías

- **Lenguaje:** Java
- **Arquitectura:** Single-Activity con Fragments, asegurando una navegación fluida y una gestión de estado eficiente.
- **Base de Datos:** SQLite para el almacenamiento local y persistente de todas las transacciones y categorías a través de una clase `DatabaseHelper`.
- **Preferencias:** `SharedPreferences` para guardar datos ligeros como el nombre de usuario, el presupuesto y el estado de la configuración inicial.
- **Networking:**
  - **Retrofit:** Para realizar llamadas a la API REST de forma declarativa y segura.
  - **Gson:** Para la serialización/deserialización automática de objetos JSON.
- **UI & Diseño:**
  - **Material Components:** Para construir una interfaz de usuario moderna y coherente (Bottom Navigation, Cards, FABs, etc.).
  - **RecyclerView:** Para mostrar listas eficientes de transacciones y estadísticas.
- **Gráficos:**
  - **MPAndroidChart:** Para la creación del gráfico circular interactivo en la pantalla de estadísticas.

## 🏗️ Arquitectura

La aplicación está estructurada para ser modular y escalable:

- **`MainActivity.java`**: Actúa como el controlador principal que gestiona la navegación entre los diferentes `Fragment`.
- **Paquete `ui`**: Contiene todos los componentes de la interfaz de usuario (Fragments y Activities).
- **Paquete `database`**: Encapsula toda la lógica de la base de datos en la clase `DatabaseHelper`.
- **Paquete `api`**: Centraliza la configuración de Retrofit, los modelos de datos de la API y la interfaz del servicio.
- **Paquete `adapters`**: Contiene los adaptadores necesarios para los `RecyclerViews` de la aplicación.
- **Paquete `models`**: Define los objetos del dominio (POJOs) como `Transaccion` y `Categoria`.

## ⚙️ Configuración y Ejecución

1. Clona este repositorio.
2. Abre el proyecto en Android Studio.
3. **Importante:** La clave de la API de ExchangeRate-API se encuentra actualmente hardcodeada en `api/ExchangeRateApiService.java`. Reemplázala con tu propia clave si es necesario.
4. Sincroniza el proyecto con los archivos de Gradle para descargar todas las dependencias.
5. Ejecuta la aplicación en un emulador o dispositivo físico.

