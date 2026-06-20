# 💣 Buscaminas JavaFX 💣

¡Bienvenido al repositorio de **Buscaminas JavaFX**! Esta es una recreación del clásico juego de lógica implementada en **Java** utilizando **JavaFX** para la interfaz gráfica de usuario (GUI). 

![Captura del Buscaminas](./screenshot.png)

El proyecto destaca por sincronizar de forma eficiente una arquitectura visual con matrices lógicas de estado, aplicando algoritmos recursivos clásicos del desarrollo de videojuegos.

---

##  Características Principales

* **Interfaz Gráfica Modular:** Diseñada mediante jerarquías de contenedores (`VBox`, `HBox` y `GridPane`).
* **Separación de Estilos con CSS:** Todo el apartado visual (casillas iniciales, reveladas, bombas, estados de victoria/derrota) se gestiona de forma desacoplada mediante hojas de estilo.
* **Algoritmo de Expansión Recursiva:** Apertura automatizada de zonas vacías colindantes cuando el jugador selecciona una casilla sin minas adyacentes.
* **Control Total del Estado:** Gestión completa de eventos de ratón para diferenciar la acción principal (clic izquierdo para descubrir) de la táctica (clic derecho para colocar banderas).
* **Persistencia y Reinicio Limpio:** Sistema robusto de reseteo que limpia estructuras lógicas y visuales, garantizando nuevas partidas sin fugas de estado anteriores.

---

##  Arquitectura Técnica y Estructura

### 1. Interfaz de Usuario (Layout)
La ventana principal se organiza mediante la siguiente jerarquía de nodos de JavaFX:

```text
VBox (Contenedor Raíz)
├── HBox (Panel Superior de Control)
│   ├── Label (lblInfo)         → Muestra el estado actual: "Jugando", "¡Victoria!" o "Derrota"
│   └── Button (btnReiniciar)   → Restablece por completo la partida
└── GridPane (Tablero Lógico)    → Matriz de 5x5 botones interactivos