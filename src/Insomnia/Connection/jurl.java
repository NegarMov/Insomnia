package Insomnia.Connection;

/**
 * The main class of the jurl program. Get input via args and run a new request through Request Manager.
 *
 * @author Negar Movaghatian
 */
public class jurl  {
    public static void main(String[] args) {
        RequestManager.setArgs(args);
        RequestManager.runInConsole();
    }
}
