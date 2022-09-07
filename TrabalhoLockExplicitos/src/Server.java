import java.util.concurrent.locks.ReentrantLock;

public class Server {


    private double bid;
    private String id;
    private String tipo;
    private double precoMax;
    private String estado;

    // novo
    private ReentrantLock lockServer;


    public Server(double bid, String id, String tipo, double precoMax, String estado) {
        this.bid = bid;
        this.id = id;
        this.tipo = tipo;
        this.precoMax = precoMax;
        this.estado = estado;
        this.lockServer = new ReentrantLock();
    }

    public double getPrecoMax() {
        return this.precoMax;
    }

    public double getBid() {
        return bid;
    }

    public String getTipo() {
        return tipo;
    }


    public void setBid(double bid) {
        this.bid = bid;
    }
    public void setEstado (String estado){this.estado =estado;}


    public String getEstado(){

        return estado;
    }
    @Override
    public String toString() {
        return "Server{" +
                "bid=" + bid +
                ", id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precoMax=" + precoMax +
                ", estado='" + estado + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   // novo
    public void lock(){
        System.out.println(Thread.currentThread().getName()+": adquirir lock do server "+this.id);
        this.lockServer.lock();
    }

    // novo
    public void unlock(){
        System.out.println(Thread.currentThread().getName()+": adquirir lock do server "+this.id);
        this.lockServer.unlock();
    }







}
