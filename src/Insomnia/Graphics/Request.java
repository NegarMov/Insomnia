package Insomnia.Graphics;

public class Request {

    private String method;
    private String name;

    public Request(String name, String method) {
        this.name = name;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
