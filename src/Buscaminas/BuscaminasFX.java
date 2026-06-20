package Buscaminas;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BuscaminasFX extends Application {

    //----------------------------------------------
    // Variables de interfaz
    //----------------------------------------------
           
    Label lblInfo;       //muestra el estado del juego (Jugando, Victoria, Derrota)
    Button btnReiniciar; //boton para reinciar el juego
    //Se usara un layaut base tipo VBox
    VBox root;
    //El VBox se compone de un HBox (para label y boton superiores) y un GridPane
    HBox topRow;
    GridPane grid;       //este sera el tablero del juego de 5x5
    Scene scene;         //escenario principal de la aplicacion

    /*
    Con dichos elementos se estructura la interfaz grafica de la aplicacion:

    VBox (root)
        - HBox (topRow)
            - Label (lblInfo)
            - Button (btnReiniciar)
        - GridPane (grid) 
    */

    //imagenes para los botones (bomba y bandera)
    private Image imgBandera;
    private Image imgBomba;

    //----------------------------------------------
    // Variables del juego
    //----------------------------------------------
    private boolean[][] minas;
    private boolean[][] revelado;
    private boolean[][] bandera;
    private Button[][] botones; //aqui se almacenaran los botones del GridPane

    private final int TAM = 5;
    private final int NUM_MINAS = 5;

    private int casillasAbiertas = 0;  //para contabilizar las casillas que se van descubriendo

    //se crea el metodo abstracto obligatorio al heredar de la clase Application
    @Override
    public void start(Stage stage) throws Exception {

        //creamos los elementos de la parte superior del VBox 
        lblInfo = new Label("Estado: Jugando");

        btnReiniciar = new Button("Reiniciar");
            btnReiniciar.getStyleClass().add("boton");     //se le añade la clase css .boton
            btnReiniciar.setOnAction(e -> reiniciarJuego()); //se le añade al boton un evento mediante expresion lambda

        //la parte superior del VBox sera un HBox con dos elementos (label y boton reiniciar)
        topRow = new HBox();
            topRow.setPadding(new Insets(10));
            topRow.setSpacing(10);
            topRow.setAlignment(Pos.CENTER_LEFT);
        //se añaden al HBox la etiqueta y el boton
        topRow.getChildren().addAll(lblInfo, btnReiniciar);

        //creamos el GridPane 5x5 y le asignamos propiedades
        grid = new GridPane();
            grid.setPadding(new Insets(10)); //padding de los botones
            grid.setHgap(5);                 //gap horizontal
            grid.setVgap(5);                 //gap vertical

        //asignamos la ruta de las imagenes a las variables creadas tipo Image
        imgBandera = new Image(getClass().getResourceAsStream("/Buscaminas/bandera.png"));
        imgBomba = new Image(getClass().getResourceAsStream("/Buscaminas/bomba.png"));

        //inicializamos el array de botones
        botones = new Button[TAM][TAM];
        //mediante un for anidado se recorre el array bidimensional y se van insertando los botones
        for (int fila = 0; fila < TAM; fila++) {
            for (int columna = 0; columna < TAM; columna++) {
                //se instancia cada boton y se le asigna estilo y evento uno por uno
                Button casilla = new Button();            //se crea o instancia
                casilla.getStyleClass().add("casilla"); //se le aplica estilo css .casilla
                casilla.setPrefSize(50, 50);         //se les da un tamaño fijo de 50x50
                //se almacena el boton en la posicion correspondiente del array
                botones[fila][columna] = casilla;

                //a cada boton se le asigna un evento
                //se hace una copia de fila y columna ya que si no da error al llamar al Event
                //tambien podriamos haber usaro el metodo getRowIndex y getColumnIndex de GridPane
                int f = fila;
                int c = columna;
                //se usa el metodo explicado en la unidad 9
                casilla.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //se comprueba si el boton pulsado es el izquiero
                        if (event.getButton() == MouseButton.PRIMARY) {
                            descubrir(f, c);      //se descubre la posicion
                        //o si el boton pulsado es el derecho    
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            ponerBandera(f, c);   //se añade una bandera
                        }
                    }
                });
                //una vez creado el boton se inserta en el GridPane en su respectiva posicion (columna y fila)
                grid.add(casilla, columna, fila);
            }
        }

        //se añade al layout principal (VBox) la estructura completa que consiste en un HBox mas un GridPane
        root = new VBox();
            root.setSpacing(20);
            root.setPadding(new Insets(10));
        //se le añaden al VBox los elementos HBox y GridPane
        root.getChildren().addAll(topRow, grid);

        //se llama al metodo para reestablecer todos los valores
        reiniciarJuego();

        //creamos la escena principal y se le asignan las propiedades
        scene = new Scene(root, 300, 350);               //dimensiones
        scene.getStylesheets().add("/Buscaminas/Buscaminas.css"); //se enlaza a nuestro CSS
        stage.setScene(scene);                                      //se establece la escena principal
            stage.setTitle("Buscaminas");                     //se añade un titulo
            stage.setResizable(false);                          //se indica que la ventana no sea reajustable
            //se muestra el icono en la esquina de la ventana
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/Buscaminas/logoBuscaminas.png")));
        //finalmente se muestra todo
        stage.show();

    }
    
    public static void main(String[] args) {
        launch(args);
    }

    //----------------------------------------------
    // Colocar minas aleatorias
    //----------------------------------------------
    /**
     * Coloca las minas en posiciones aleatorias dentro del tablero.
     *
     * Se generan coordenadas aleatorias hasta alcanzar el número total de minas
     * definido en la constante NUM_MINAS.
     */
    private void inicializarMinas() {

        Random r = new Random();

        int minasColocadas = 0;

        while (minasColocadas < NUM_MINAS) {

            int f = r.nextInt(TAM);
            int c = r.nextInt(TAM);

            if (!minas[f][c]) {
                minas[f][c] = true;
                minasColocadas++;
            }
        }
    }

    //----------------------------------------------
    // Click izquierdo
    //----------------------------------------------
    /**
     * Descubre una casilla del tablero.
     *
     * Si la casilla contiene una mina, el jugador pierde la partida y se
     * muestran todas las minas. Si no contiene mina, se muestra el número de
     * minas adyacentes.
     *
     * También se controla la condición de victoria cuando todas las casillas
     * seguras han sido descubiertas.
     *
     * @param fila Fila de la casilla seleccionada.
     * @param col Columna de la casilla seleccionada.
     */
    private void descubrir(int fila, int col) {
        
        /*
        Cada vez que se pulsa el boton izquierdo se realizan las siguientes comprobaciones:
        - si ya ha sido previamente revelada
        - si existe una bandera 
        - si existe una mina
        En caso de que se cumpla una de las 3 condiciones se sale del metodo sin llegar a descubrir la posicion.
        */
        
        //si la posicion ya esta revelada, no hacemos nada (consultamos el array revelado)
        if (revelado[fila][col]) {
            return;
        }
        //se comprueba si ya tiene bandera, que tampoco hariamos nada (consultamos el array bandera)
        if (bandera[fila][col]) {
            return;
        }
        //se comprueba si hay una mina en la posicion (consultamos el array minas)
        //en el caso de que exista una bomba en dicha posicion:
        if (minas[fila][col]) {
            //se muestra la imagen de bomba mediante la creacion de un ImageView 
            ImageView boom = new ImageView(imgBomba);
                boom.setFitWidth(20);
                boom.setFitHeight(20);
            botones[fila][col].setGraphic(boom);          //añadimos la imagen bomba a ese boton concreto
            lblInfo.setText("Estado: Derrota");         //cambiamos el contenido del label a Derrota
            lblInfo.getStyleClass().add("derrota");         //cambiamos el estilo con la calse .derrota de nuestro CSS
            
            mostrarMinas();                                   //se decubre el tablero y se muestran todas las minas
            //se desactivan todas las posiciones para evitar que se siga interactuando con ellas
            for (int f = 0; f < TAM; f++) {
                for (int c = 0; c < TAM; c++) {
                    botones[f][c].setDisable(true);
                }
            }
            //se sale del metodo sin realizar mas comprobaciones
            return;
        }
        
                
        //si no se ha cumplido ninguna de las 3 condiciones previas se muestra el contenido de la posicion
        revelado[fila][col] = true;                          //se actualiza la posicion en el array revelado
        botones[fila][col].setDisable(true);             //se desactiva el boton de esa posicion en el tablero
        botones[fila][col].getStyleClass().add("abierta"); //se le asigna el estilo .abierta en nuestro CSS
        botones[fila][col].setGraphic(null);            //se quitan imagenes anteriores
        
        casillasAbiertas++;                                 //se contabiliza la casilla abierta

        

        //una vez descubierta la posicion pulsada se contabilizan las minas adyacentes
        int num = contarMinas(fila, col);
        //en caso de que hayan minas adyacentes se modifica el texto del boton con la cantidad de minas devueltas
        if (num > 0) {
            botones[fila][col].setText(String.valueOf(num));
        //en el caso contrario (no hay minas que rodeen la posicion) el texto permanece vacio y se descubren las adyacentes
        } else {
            botones[fila][col].setText("");
            //se procede a la apertura de manera expansiva (-1, 0 y 1 vertical y horizontal)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {

                    int numFila = fila + i;
                    int numColumna = col + j;
                    //se controla que la posicion no sea parte externa del tablero para no salirnos de los indices del array
                    if (numFila >= 0 && numFila < TAM && numColumna >= 0 && numColumna < TAM) {
                        if (!revelado[numFila][numColumna]) {
                            descubrir(numFila, numColumna);
                        }
                    }
                }
            }
        }

        
        //finalmente, cada vez que se descubre una posicion se comprueba si se ha ganado la partida comprobando las casillas abiertas
        if (casillasAbiertas == (TAM * TAM - NUM_MINAS)) {
            //se muestra mensaje en el label
            lblInfo.setText("Estado: Victoria");
            lblInfo.getStyleClass().add("victoria");         //cambiamos el estilo con la calse .victoria de nuestro CSS
            //se muestran las minas
            mostrarMinas();
            //se bloquea el tablero al igual que cuando se pierde
            for (int f = 0; f < TAM; f++) {
                for (int c = 0; c < TAM; c++) {
                    botones[f][c].setDisable(true);
                }
            }
        }
    }

    //----------------------------------------------
    // Click derecho (banderas)
    //----------------------------------------------
    /**
     * Coloca o elimina una bandera en la casilla seleccionada.
     *
     * Las banderas permiten al jugador marcar posibles ubicaciones de minas. No
     * se permite colocar banderas en casillas que ya han sido reveladas.
     *
     * @param fila Fila de la casilla seleccionada.
     * @param col Columna de la casilla seleccionada.
     */
    private void ponerBandera(int fila, int col) {

        /*
        Cada vez que se pulsa el boton derecho se realizan la siguiente comprobacion:
        - si ya ha sido previamente revelada
        En caso de que se cumpla la condicion se sale del metodo sin llegar a colocar la bandera.
        */
        
        //se comprueba que la casilla no este revelada
        if (revelado[fila][col]) {
            return;
        }

        //en caso de que no haya sido previamente colocada la bandera 
        if (!bandera[fila][col]) {
            //se actualiza el array bandera
            bandera[fila][col] = true;
            //se muestra la imagen de bandera mediante la creacion de un ImageView 
            ImageView flag = new ImageView(imgBandera);
                flag.setFitWidth(20);
                flag.setFitHeight(20);
            botones[fila][col].setGraphic(flag); //añadimos la imagen bomba a ese boton concreto

        //en caso de que ya haya sido previamente colocada la bandera    
        } else {
            //se elimina y se actualiza el array bandera 
            bandera[fila][col] = false;

            botones[fila][col].setGraphic(null); //se quitan imagenes anteriores
        }
    }

    //----------------------------------------------
    // Contar minas alrededor
    //----------------------------------------------
    /**
     * Cuenta el número de minas adyacentes a una casilla determinada.
     *
     * Se recorren las ocho posiciones vecinas alrededor de la casilla indicada
     * comprobando si contienen minas.
     *
     * @param f Fila de la casilla.
     * @param c Columna de la casilla.
     * @return Número de minas alrededor de la casilla.
     */
    private int contarMinas(int f, int c) {

        int contador = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                int nf = f + i;
                int nc = c + j;

                if (nf >= 0 && nf < TAM && nc >= 0 && nc < TAM) {

                    if (minas[nf][nc]) {
                        contador++;
                    }

                }
            }
        }

        return contador;
    }

    //----------------------------------------------
    // Mostrar todas las minas
    //----------------------------------------------
    /**
     * Revela todas las minas del tablero.
     *
     * Este método se utiliza cuando el jugador pierde o cuando gana la partida,
     * mostrando la posición de todas las minas existentes.
     */
    private void mostrarMinas() {

        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {

                if (minas[f][c]) {
                    //se muestra la imagen de bomba mediante la creacion de un ImageView 
                    ImageView boom = new ImageView(imgBomba);
                        boom.setFitWidth(20);
                        boom.setFitHeight(20);
                    botones[f][c].setGraphic(boom);          //añadimos la imagen bomba a ese boton concreto
                    botones[f][c].getStyleClass().add("mina"); //y se le añade el estilo .mina de nuestro CSS
                }
            }
        }
    }

    /**
     * Reinicia la partida actual.
     *
     * Se restablecen todas las estructuras de datos del juego, se limpian los
     * botones del tablero y se vuelven a colocar las minas en posiciones
     * aleatorias.
     */
    private void reiniciarJuego() {

        //se reinician todos los arrays
        minas = new boolean[TAM][TAM];
        revelado = new boolean[TAM][TAM];
        bandera = new boolean[TAM][TAM];
        casillasAbiertas = 0;

        //se limpian todos los botones del GridPane
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Button b = botones[f][c];
                    //se eliminan imagenes y textos de las casillas
                    b.setText("");
                    b.setGraphic(null);
                    b.setDisable(false);
                    //se eliminan los estilos CSS de los botones
                    b.getStyleClass().remove("abierta");
                    b.getStyleClass().remove("mina");
                    b.getStyleClass().remove("bandera");
            }
        }

        //colocamos de nuevo las minas
        inicializarMinas();

        //se cambia el texto de la eqiqueta al mensaje inicial y reseteamos estilos
        lblInfo.setText("Estado: Jugando");   //se resetea el mensaje
        lblInfo.getStyleClass().clear();            //se elimina posibles clases .derrota o .victoria que pueda tener aplicada
        lblInfo.getStyleClass().add("estado");    //se le añade la clase css .estado
    }
}


