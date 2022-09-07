import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Serializable {

    private String id;
    private String tipo;
    private double precoMax;
    private String estado;
    private String user;

    private ReentrantLock lockServer;

    public Server( String id, String tipo, double precoMax, String estado, String user) {
        this.id = id;
        this.tipo = tipo;
        this.precoMax = precoMax;
        this.estado = estado;
        this.user=user;
        this.lockServer = new ReentrantLock();
    }

    public double getPrecoMax() {
        return this.precoMax;
    }

    public String getTipo() {
        return tipo;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setEstado (String estado){this.estado =estado;}

    public String getEstado(){

        return estado;
    }


    public String toString() {
        return "Server{" +
                ", id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precoMax=" + precoMax +
                ", estado='" + estado + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void lock(){
        System.out.println(Thread.currentThread().getName()+": adquirir lock do server "+this.id);
        this.lockServer.lock();
    }

    public void unlock(){
        System.out.println(Thread.currentThread().getName()+": adquirir lock do server "+this.id);
        this.lockServer.unlock();
    }
}
